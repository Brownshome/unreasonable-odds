package brownshome.unreasonableodds.network;

import java.util.Objects;

public class SessionPlayer implements Comparable<SessionPlayer> {
	public static SessionPlayer makeHost(String name) {
		return new SessionPlayer(name, true, true);
	}

	public static SessionPlayer makeClient(String name) {
		return new SessionPlayer(name, false, false);
	}

	private final boolean isHost;

	private String name;
	private boolean isReady;

	public SessionPlayer(String name, boolean isHost, boolean isReady) {
		assert name != null;
		assert !isHost || isReady : "All hosts are ready";

		this.name = name;
		this.isHost = isHost;
		this.isReady = isReady;
	}

	public final void setReady(boolean ready) {
		isReady = ready;
	}

	public final boolean isReady() {
		return isReady;
	}

	public final void name(String name) {
		assert name != null;

		this.name = name;
	}

	public final String name() {
		return name;
	}

	public final boolean isHost() {
		return isHost;
	}

	@Override
	public final int compareTo(SessionPlayer o) {
		if (o.isHost != isHost) {
			return Boolean.compare(o.isHost, isHost);
		}

		return name.compareTo(o.name);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(name, isHost, isReady);
	}

	@Override
	public final boolean equals(Object obj) {
		return obj instanceof SessionPlayer player
				&& player.name.equals(name)
				&& (player.isHost == isHost)
				&& (player.isReady == isReady);
	}

	@Override
	public final String toString() {
		if (isHost) {
			return "[Host] " + name;
		}

		if (isReady) {
			return "[Ready] " + name;
		}

		return name;
	}

}
