package brownshome.unreasonableodds.session;

import java.util.concurrent.Executor;

import brownshome.netcode.udp.UDPConnectionManager;

abstract class UDPSession extends Session {
	private final UDPConnectionManager connectionManager;

	protected UDPSession(String name, UDPConnectionManager connectionManager, Executor executor) {
		super(name);

		this.connectionManager = connectionManager;
		connectionManager.registerExecutor("default", executor, 1);
	}

	protected final UDPConnectionManager connectionManager() {
		return connectionManager;
	}

	@Override
	public void close() {
		connectionManager.closeAsync();
	}
}
