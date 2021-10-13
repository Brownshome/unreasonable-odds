package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.session.net.LeaveSessionPacket;
import brownshome.unreasonableodds.session.net.SendPlayersPacket;

/**
 * Represents a hosted game that other people can connect to
 */
public class HostSession extends UDPSession {
	private static final System.Logger LOGGER = System.getLogger(HostSession.class.getModule().getName());

	public static HostSession getHost() {
		return (HostSession) Session.getHost();
	}

	private final Map<InetSocketAddress, UDPSessionPlayer> players;

	public HostSession(String name, int port, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(allSchema(), port), executor);

		LOGGER.log(System.Logger.Level.INFO, "Listening on port {0}", port);

		this.players = new HashMap<>();
	}

	public CompletableFuture<Multiverse> startGame(Rules rules) {
		// Re-negotiate the schema to the game schema
		var startTime = CompletableFuture.allOf(players.values()
				.stream().map(UDPSessionPlayer::timeOffset).toArray(CompletableFuture[]::new))
				.thenApply(v -> rules.gameStartTime());

		for (var player : players.values()) {
			// Wait for the sync send and then receive
			/*
			 * TODO james.brown [12-10-2021] Retry the time-sync system if the packets fail to send. This will need
			 *                               sequence numbers of some sort to reject 'slow' time-sync packets that are
			 *                               out of date.
			 */

			player.startTimeSync();
			startTime.thenAccept(s -> player.startGame(s, rules));
		}

		return startTime.thenApply(s -> rules.createMultiverse(createPlayers(), new MultiverseNetwork(), s));
	}

	public void completeTimeSync(InetSocketAddress address, Instant remoteTime) {
		players.get(address).completeTimeSync(remoteTime);
	}

	protected List<Player> createPlayers() {
		var result = new ArrayList<Player>();

		for (var player : players.values()) {
			result.add(player.networkPlayer());
		}

		return result;
	}

	public void setPlayerName(InetSocketAddress address, String name) {
		players.compute(address, (a, existing) -> {
			if (existing != null) {
				existing.name(name);
				return existing;
			} else {
				return UDPSessionPlayer.makeClient(name, connectionManager().getOrCreateConnection(a));
			}
		});

		onPlayersChanged();
	}

	public void setReadyState(InetSocketAddress address, boolean ready) {
		players.get(address).setReady(ready);

		onPlayersChanged();
	}

	@Override
	public void name(String name) {
		super.name(name);

		onPlayersChanged();
	}

	public void removePlayer(InetSocketAddress address) {
		if (players.remove(address) == null) {
			throw new IllegalStateException("Address %s is not part of this session".formatted(address));
		}

		onPlayersChanged();
	}

	protected void onPlayersChanged() {
		var sendPlayersPacket = new SendPlayersPacket(players());

		for (var address : players.keySet()) {
			connectionManager().getOrCreateConnection(address).send(sendPlayersPacket);
		}
	}

	@Override
	public List<SessionPlayer> players() {
		var result = new ArrayList<SessionPlayer>(players.values());
		result.add(UDPSessionPlayer.makeHost(name()));
		result.sort(null);

		return result;
	}

	@Override
	public void close() {
		var leavePacket = new LeaveSessionPacket();

		for (var address : players.keySet()) {
			connectionManager().getOrCreateConnection(address).send(leavePacket);
		}

		super.close();
	}
}
