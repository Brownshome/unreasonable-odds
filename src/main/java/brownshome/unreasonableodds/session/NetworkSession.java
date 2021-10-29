package brownshome.unreasonableodds.session;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.ToIntFunction;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.annotation.converter.Converter;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.packets.game.GameSchema;
import brownshome.unreasonableodds.packets.lobby.LobbySchema;
import brownshome.unreasonableodds.packets.session.SessionSchema;
import brownshome.unreasonableodds.player.NetworkPlayer;

public abstract class NetworkSession implements AutoCloseable, Session {
	private static final ThreadLocal<NetworkSession> THREAD_SESSION = new ThreadLocal<>();

	public static NetworkSession get() {
		return THREAD_SESSION.get();
	}

	/**
	 * This converter reads from the thread-local storage. No data is actually sent.
	 */
	protected static class SessionConverter<SESSION extends NetworkSession> implements Converter<SESSION> {
		@Override
		public void write(ByteBuffer buffer, NetworkSession object) {
			assert object == null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public SESSION read(ByteBuffer buffer) {
			return (SESSION) get();
		}

		@Override
		public int size(SESSION object) {
			return 0;
		}

		@Override
		public boolean isSizeExact(SESSION object) {
			return true;
		}

		@Override
		public boolean isSizeConstant() {
			return true;
		}
	}

	public static final class NetworkSessionConverter extends SessionConverter<NetworkSession> { }

	private final UDPConnectionManager connectionManager;

	private SessionId sessionId = null;

	private final Map<InetSocketAddress, SessionId> sessionIds;

	protected NetworkSession(UDPConnectionManager connectionManager, Executor executor) {
		this(connectionManager, new HashMap<>());
		connectionManager.registerExecutor("default", executor, Integer.MAX_VALUE);
	}

	protected NetworkSession(UDPConnectionManager connectionManager, Map<InetSocketAddress, SessionId> sessionIds) {
		this.connectionManager = connectionManager;
		this.sessionIds = sessionIds;
	}

	public final void markThreadAsSessionThread() {
		THREAD_SESSION.set(this);
	}

	@Override
	public abstract Collection<? extends NetworkPlayer> players();

	protected UDPConnectionManager connectionManager() {
		return connectionManager;
	}

	protected static List<Schema> allSchema() {
		return List.of(new BaseSchema(), new SessionSchema(), new LobbySchema(), new GameSchema());
	}

	public void sessionLeft(SessionId sessionId) { }

	@Override
	public void close() {
		connectionManager.closeAsync();
	}

	public void sessionId(int id) {
		if (sessionId != null) {
			throw new IllegalStateException();
		}

		sessionId = SessionId.createLocal(id);
	}

	public final SessionId sessionId() {
		assert sessionId != null;
		return sessionId;
	}

	public final SessionId sessionId(InetSocketAddress address) {
		var result = sessionIds.get(address);

		if (result == null) {
			throw new IllegalArgumentException("There is no session registered for address %s".formatted(address));
		}

		return result;
	}

	@Override
	public String toString() {
		return sessionId != null ? "Session " + sessionId : "Session (no id)";
	}

	protected final Map<InetSocketAddress, SessionId> sessionIds() {
		return sessionIds;
	}
}
