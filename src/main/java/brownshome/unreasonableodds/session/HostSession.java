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
	private final Map<InetSocketAddress, String> names;

	public HostSession(String name, int port, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(List.of(new BaseSchema(),
				new UDPSchema(),
				new UnreasonableOddsSchema()), port), executor);

		LOGGER.log(System.Logger.Level.INFO, "Listening on port {0}", port);

		this.names = new HashMap<>();
	}

	public Multiverse startGame(Rules rules) {
		return null;
	}

	public void setPlayerName(InetSocketAddress address, String name) {
		names.put(address, name);

		var setNamesPacket = new SetNamesPacket(players());

		for (var e : names.entrySet()) {
			connectionManager().getOrCreateConnection(e.getKey()).send(setNamesPacket);
		}
	}

	@Override
	public void name(String name) {
		super.name(name);

		var setNamesPacket = new SetNamesPacket(players());

		for (var address : names.keySet()) {
			connectionManager().getOrCreateConnection(address).send(setNamesPacket);
		}
	}

	@Override
	public List<String> players() {
		var result = new ArrayList<>(names.values());
		result.add(name());
		result.sort(null);

		return result;
	}

	public void removePlayer(InetSocketAddress address) {
		if (names.remove(address) == null) {
			throw new IllegalStateException("Address %s is not part of this session".formatted(address));
		}
	}

	@Override
	public void close() {
		var leavePacket = new LeaveSessionPacket();

		for (var address : names.keySet()) {
			connectionManager().getOrCreateConnection(address).send(leavePacket);
		}

		super.close();
	}
}
