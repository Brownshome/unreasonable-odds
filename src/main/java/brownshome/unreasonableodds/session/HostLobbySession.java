package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.packets.converters.LobbyPlayerProxy;
import brownshome.unreasonableodds.packets.lobby.SendPlayersPacket;
import brownshome.unreasonableodds.packets.lobby.SetRulesPacket;
import brownshome.unreasonableodds.packets.session.LeaveSessionPacket;
import brownshome.unreasonableodds.player.*;

public class HostLobbySession extends NetworkLobbySession implements LocalPlayerLobbySession {
	private static final System.Logger LOGGER = System.getLogger(HostLobbySession.class.getModule().getName());

	public static final class HostLobbySessionConverter extends SessionConverter<HostLobbySession> {}

	public static HostLobbySession get() {
		return (HostLobbySession) NetworkSession.get();
	}

	private Rules rules;
	private final NetworkLobbyPlayer localPlayer;

	private final Map<NetworkPlayer.Id, ImportedLobbyPlayer> players;

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

	public void onPlayersChanged() {
		var packet  = new SendPlayersPacket(null, players().stream().map(LobbyPlayerProxy::new).collect(Collectors.toList()));
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

	public final ImportedLobbyPlayer getOrMakePlayer(NetworkPlayer.Id id) {
		return players.computeIfAbsent(id, this::createNewPlayer);
	}

	public final ImportedLobbyPlayer getPlayer(NetworkPlayer.Id id) {
		return players.get(id);
	}

	@Override
	public final void sessionLeft(InetSocketAddress address) {
		if (!players.keySet().removeIf(id -> id.address().equals(address))) {
			throw new IllegalStateException("%s is not connected to this session".formatted(address));
		}

		onPlayersChanged();
	}

	private ImportedLobbyPlayer createNewPlayer(NetworkPlayer.Id id) {
		return new ImportedLobbyPlayer(id.toString(), id.number(), connectionManager().getOrCreateConnection(id.address())) {
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

	public void startGame(CharacterController localController, Random random) {
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
				.map(ImportedLobbyPlayer::timeSyncComplete).toArray(CompletableFuture[]::new)
		).thenAccept(v -> {
			var builder = new NetworkGameSession.Builder(connectionManager(), rules);

			builder.addPlayer(LocalGamePlayer.create(localPlayer, localController));

			for (var player : players.values()) {
				builder.addPlayer(ImportedGamePlayer.create(player));
			}

			var gameSession = builder.build();
			gameSession.markThreadAsSessionThread();
			rules.createMultiverse(gameSession, rules.gameStartTime(), random);
		});
	}

	@Override
	public void close() {
		var packet = new LeaveSessionPacket(null);
		forAllClients(c -> c.connection().send(packet));
		super.close();
	}
}
