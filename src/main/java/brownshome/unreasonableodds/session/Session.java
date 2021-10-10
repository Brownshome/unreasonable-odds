package brownshome.unreasonableodds.session;

import java.net.InetSocketAddress;
import java.util.List;

public abstract class Session {
	private String name = "";

	private static final ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<>();

	public static Session getHost() {
		return THREAD_SESSION.get();
	}

	public void markThreadAsSessionThread() {
		THREAD_SESSION.set(this);
	}

	protected Session(String name) {
		this.name = name;
	}

	public final String name() {
		return name;
	}

	public void name(String name) {
		assert name != null;

		this.name = name;
	}

	public abstract List<String> players();

	public abstract InetSocketAddress address();

	public abstract void leave();
}
