package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.netcode.udp.UDPSchema;
import brownshome.unreasonableodds.Multiverse;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.net.*;

/**
 * Represents a hosted game that other people can connect to
 */
public class HostSession extends UDPSession {
	private static final System.Logger LOGGER = System.getLogger(HostSession.class.getModule().getName());
	private final Map<InetSocketAddress, Player> players;

	public HostSession(String name, int port, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(List.of(new BaseSchema(),
				new UDPSchema(),
				new UnreasonableOddsSchema()), port), executor);

		LOGGER.log(System.Logger.Level.INFO, "Listening on port {0}", port);

		this.players = new HashMap<>();
	}

	public Multiverse startGame(Rules rules) {
		return null;
	}

	public void setPlayerName(InetSocketAddress address, String name) {
		players.compute(address, (a, existing) -> {
			if (existing != null) {
				existing.name(name);
				return existing;
			} else {
				return Player.makeClient(name);
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
	public List<Player> players() {
		var result = new ArrayList<>(players.values());
		result.add(Player.makeHost(name));
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
