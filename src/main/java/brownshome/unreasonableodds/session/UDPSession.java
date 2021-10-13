package brownshome.unreasonableodds.session;

import java.util.List;
import java.util.concurrent.Executor;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.net.GameSchema;
import brownshome.unreasonableodds.session.net.SessionSchema;

abstract class UDPSession extends Session {
	private final UDPConnectionManager connectionManager;

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

	@Override
	public void close() {
		connectionManager.closeAsync();
	}
}
