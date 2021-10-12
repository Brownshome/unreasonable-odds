package brownshome.unreasonableodds.session;

import java.util.List;
import java.util.concurrent.Executor;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.netcode.udp.UDPSchema;
import brownshome.unreasonableodds.net.GameSchema;
import brownshome.unreasonableodds.session.net.SessionSchema;

abstract class UDPSession extends Session {
	private final UDPConnectionManager connectionManager;

	protected static final class UDPSessionPlayer extends SessionPlayer {
		private final NetworkControlledPlayer networkPlayer;

		public static UDPSessionPlayer makeHost(String name) {
			return new UDPSessionPlayer(name, true, true);
		}

		public static UDPSessionPlayer makeClient(String name) {
			return new UDPSessionPlayer(name, false, false);
		}

		public UDPSessionPlayer(String name, boolean isHost, boolean isReady) {
			super(name, isHost, isReady);

			networkPlayer = new NetworkControlledPlayer();
		}

		public NetworkControlledPlayer networkPlayer() {
			return networkPlayer;
		}
	}

	protected UDPSession(String name, UDPConnectionManager connectionManager, Executor executor) {
		super(name);

		this.connectionManager = connectionManager;
		connectionManager.registerExecutor("default", executor, 1);
	}

	protected static List<Schema> sessionSchema() {
		return List.of(new BaseSchema(), new UDPSchema(), new SessionSchema());
	}

	protected static List<Schema> gameSchema() {
		return List.of(new BaseSchema(), new UDPSchema(), new GameSchema());
	}

	protected static List<Schema> allSchema() {
		return List.of(new BaseSchema(), new UDPSchema(), new SessionSchema(), new GameSchema());
	}

	protected final UDPConnectionManager connectionManager() {
		return connectionManager;
	}

	@Override
	public void close() {
		connectionManager.closeAsync();
	}
}
