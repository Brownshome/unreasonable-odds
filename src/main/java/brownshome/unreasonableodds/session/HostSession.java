package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
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
	private final Map<InetSocketAddress, UDPSessionPlayer> players;

	public HostSession(String name, int port, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(allSchema(), port), executor);

		LOGGER.log(System.Logger.Level.INFO, "Listening on port {0}", port);

		this.players = new HashMap<>();
	}

	public CompletableFuture<Multiverse> startGame(Rules rules) {
		// Re-negotiate the schema to the game schema
		var schemas = gameSchema();

		List<CompletableFuture<Void>> renegotiateFutures = new ArrayList<>();
		for (var address : players.keySet()) {
			renegotiateFutures.add(connectionManager().getOrCreateConnection(address).connect(schemas));
		}

		return CompletableFuture.allOf(renegotiateFutures.toArray(CompletableFuture[]::new))
				.thenApply(unused -> rules.createMultiverse(createPlayers()));
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
				return UDPSessionPlayer.makeClient(name);
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
		result.add(UDPSessionPlayer.makeHost(name));
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
