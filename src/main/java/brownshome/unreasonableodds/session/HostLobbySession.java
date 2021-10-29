package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.game.CreateGameSessionPacket;
import brownshome.unreasonableodds.packets.lobby.*;
import brownshome.unreasonableodds.packets.session.LeaveSessionPacket;
import brownshome.unreasonableodds.player.*;

public abstract class HostLobbySession extends NetworkLobbySession implements LocalPlayerLobbySession {
	private static final System.Logger LOGGER = System.getLogger(HostLobbySession.class.getModule().getName());

	public static final class HostLobbySessionConverter extends SessionConverter<HostLobbySession> {}

	public static HostLobbySession get() {
		return (HostLobbySession) NetworkSession.get();
	}

	private Rules rules;
	private final NetworkLobbyPlayer localPlayer;

	private final Map<Id, ImportedLobbyPlayer> players;
	private final AtomicInteger nextSessionId = new AtomicInteger(SessionId.HOST_NUMBER);

	{
		sessionId(allocateSessionId());
		assert sessionId().number() == SessionId.HOST_NUMBER;
	}

	protected HostLobbySession(Rules rules, String name, int port, Executor executor) throws IOException {
		super(new UDPConnectionManager(allSchema(), port), executor);

		this.rules = rules;
		this.localPlayer = new NetworkLobbyPlayer(name, true, true, allocatePlayerId()) {
			@Override
			public void name(String name) {
				super.name(name);
				onPlayersChanged();
			}

			@Override
			public void ready(boolean ready) {
				super.ready(ready);
				onPlayersChanged();
			}
		};
		this.players = new HashMap<>();

		LOGGER.log(System.Logger.Level.INFO, "Listening on port {0}", port);
	}

	@Override
	public Collection<? extends NetworkLobbyPlayer> players() {
		var result = new ArrayList<NetworkLobbyPlayer>(players.values());
		result.add(localPlayer);
		return result;
	}

	@Override
	public Rules rules() {
		return rules;
	}

	@Override
	public void rules(Rules rules) {
		this.rules = rules;

		var packet = new SetRulesPacket(null, rules);
		forAllClients(c -> c.connection().send(packet));
	}

	@SuppressWarnings("unchecked")
	public void onPlayersChanged() {
		var packet  = new SendPlayersPacket(null, (Collection<NetworkLobbyPlayer>) players());
		forAllClients(c -> c.connection().send(packet));
	}

	private void forAllClients(Consumer<? super ImportedLobbyPlayer> action) {
		for (var v : players.values()) {
			action.accept(v);
		}
	}

	@Override
	public NetworkLobbyPlayer localPlayer() {
		return localPlayer;
	}

	public final ImportedLobbyPlayer getOrMakePlayer(Id id, InetSocketAddress address) {
		return players.computeIfAbsent(id, i -> createNewPlayer(i, address));
	}

	public final ImportedLobbyPlayer player(Id id) {
		return players.get(id);
	}

	public SessionId getOrMakeSessionId(InetSocketAddress address) {
		return sessionIds().computeIfAbsent(address, a -> {
			var id = allocateSessionId();

			assert id >= 0 && id < (1 << Byte.SIZE);

			var connection = connectionManager().getOrCreateConnection(address);
			connection.send(new SetSessionIdPacket(null, (byte) id));
			connection.send(new SetRulesPacket(null, rules));

			return new SessionId(id, a);
		});
	}

	private int allocateSessionId() {
		return nextSessionId.getAndIncrement();
	}

	@Override
	public final void sessionLeft(SessionId sessionId) {
		if (!players.keySet().removeIf(id -> id.sessionId().equals(sessionId))) {
			throw new IllegalStateException("Session %s is not connected to this session".formatted(sessionId));
		}

		onPlayersChanged();
	}

	private ImportedLobbyPlayer createNewPlayer(Id id, InetSocketAddress address) {
		return new ImportedLobbyPlayer(id.toString(), id, connectionManager().getOrCreateConnection(address)) {
			@Override
			public void name(String name) {
				super.name(name);
				onPlayersChanged();
			}

			@Override
			public void ready(boolean ready) {
				super.ready(ready);
				onPlayersChanged();
			}
		};
	}

	public void startGame(Random random) {
		for (var player : players.values()) {
			// Wait for the sync send and then receive
			/*
			 * TODO james.brown [12-10-2021] Retry the time-sync system if the packets fail to send. This will need
			 *                               sequence numbers of some sort to reject 'slow' time-sync packets that are
			 *                               out of date.
			 */

			player.timeSync();
		}

		CompletableFuture.allOf(players.values().stream()
				.map(importedLobbyPlayer -> importedLobbyPlayer.timeSyncComplete().thenCompose(v -> {
					importedLobbyPlayer.connection().connect(NetworkGameSession.gameSchema());
					return importedLobbyPlayer.connection().send(new CreateGameSessionPacket(null, sessionIds().values()));
				})).toArray(CompletableFuture[]::new)
		).thenRun(() -> {
			// At this point all client have created game-session are and ready to go!
			var epoch = rules.gameStartTime();

			var builder = gameSessionBuilder(sessionIds());
			builder.addPlayer(LocalGamePlayer.create(localPlayer, localController()));

			for (var player : players.values()) {
				builder.addPlayer(new NetworkGamePlayer(player.name(), player.id()));
			}

			var gameSession = builder.build();
			gameSession.markThreadAsSessionThread();

			gameSession.map(rules.generateStaticMap(random));

			var universes = new ArrayList<Universe>();
			for (var player : gameSession.players()) {
				var universe = rules.createUniverse(gameSession, player.id(), epoch, random);

				if (player instanceof LocalGamePlayer) {
					universes.add(universe);
				} else {
					var connection = player(player.id()).connection();
					player.startGame(universe, connection);
				}
			}

			gameSession.startGame(rules.createMultiverse(universes, gameSession, epoch));
		});
	}

	@Override
	public void close() {
		var packet = new LeaveSessionPacket(null);
		forAllClients(c -> c.connection().send(packet));
		super.close();
	}
}
