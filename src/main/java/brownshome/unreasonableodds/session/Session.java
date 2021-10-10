package brownshome.unreasonableodds.session;

import java.util.List;

public abstract class Session implements AutoCloseable {
	private static final ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<>();

	public static Session getHost() {
		return THREAD_SESSION.get();
	}

	public final void markThreadAsSessionThread() {
		THREAD_SESSION.set(this);
	}

	protected String name = "";

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

	@Override
	public abstract void close();
}
