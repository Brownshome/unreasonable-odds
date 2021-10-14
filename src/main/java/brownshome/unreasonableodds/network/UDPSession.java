package brownshome.unreasonableodds.network;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.network.packets.SessionSchema;
import brownshome.unreasonableodds.packets.GameSchema;

abstract class UDPSession extends Session {
	private final UDPConnectionManager connectionManager;
	private final Map<Universe.Id, InetSocketAddress> universeMapping = new HashMap<>();

	protected UDPSession(String name, UDPConnectionManager connectionManager, Executor executor) {
		super(name);

		this.connectionManager = connectionManager;
		connectionManager.registerExecutor("default", executor, 1);
	}

	protected static List<Schema> sessionSchema() {
		return List.of(new BaseSchema(), new SessionSchema());
	}

	protected static List<Schema> gameSchema() {
		/*
		 * TODO james.brown [13-10-2021] Move the lobby specific schema into its own schema
		 */
		return List.of(new BaseSchema(), new SessionSchema(), new GameSchema());
	}

	protected static List<Schema> allSchema() {
		return List.of(new BaseSchema(), new SessionSchema(), new GameSchema());
	}

	protected final UDPConnectionManager connectionManager() {
		return connectionManager;
	}

	public void claimUniverse(Universe.Id universe) {
		setUniverseHost(universe, connectionManager.address());
	}

	/**
	 * Registers a new universe ID as being owned by the
	 * @param address the address of the session claiming the universe
	 * @param universe the id of the universe
	 */
	public void setUniverseHost(Universe.Id universe, InetSocketAddress address) {
		universeMapping.compute(universe, (id, oldAddress) -> {
			if (oldAddress == null && !address.equals(universe.creator())) {
				throw new IllegalArgumentException("A session cannot claim a new universe id that was not created by that session.");
			}

			return address;
		});
	}

	@Override
	public void close() {
		connectionManager.closeAsync();
	}
}
