package brownshome.unreasonableodds.network;

import java.net.InetSocketAddress;
import java.util.List;

public abstract class Session implements AutoCloseable {
	private static final ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<>();

	public static Session getHost() {
		return THREAD_SESSION.get();
	}

	public final void markThreadAsSessionThread() {
		THREAD_SESSION.set(this);
	}

	private String name = "";
	private MultiverseNetwork network;

	protected Session(String name) {
		this.name = name;
	}

	public final boolean hasGameStarted() {
		return network != null;
	}

	public final MultiverseNetwork network() {
		return network;
	}

	public final String name() {
		return name;
	}

	public void name(String name) {
		assert name != null;

		this.name = name;
	}

	public abstract List<SessionPlayer> players();

	@Override
	public abstract void close();
}
