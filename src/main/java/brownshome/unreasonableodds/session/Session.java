package brownshome.unreasonableodds.session;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.converter.Converter;

public abstract class Session implements AutoCloseable {
	private static final ThreadLocal<Session> THREAD_SESSION = new ThreadLocal<>();

	public static Session getHost() {
		return THREAD_SESSION.get();
	}

	public final void markThreadAsSessionThread() {
		THREAD_SESSION.set(this);
	}

	public static final class PlayerConverter implements Converter<Player> {
		@Override
		public void write(ByteBuffer byteBuffer, Player player) {
			NetworkUtils.writeString(byteBuffer, player.name);

			byte flags;
			if (player.isHost) {
				flags = 2;
			} else if (player.isReady) {
				flags = 1;
			} else {
				flags = 0;
			}

			byteBuffer.put(flags);
		}

		@Override
		public Player read(ByteBuffer buffer) {
			var name = NetworkUtils.readString(buffer);

			var flags = buffer.get();
			if (flags > 2) {
				throw new IllegalArgumentException("Illegal player flags %d".formatted(flags));
			}

			return new Player(name, flags == 2, flags != 0);
		}

		@Override
		public int size(Player player) {
			return NetworkUtils.calculateSize(player.name).size();
		}

		@Override
		public boolean isSizeExact(Player player) {
			return false;
		}

		@Override
		public boolean isSizeConstant() {
			return false;
		}
	}

	public static final class Player implements Comparable<Player> {
		public static Player makeHost(String name) {
			return new Player(name, true, true);
		}

		public static Player makeClient(String name) {
			return new Player(name, false, false);
		}

		private final boolean isHost;

		private String name;
		private boolean isReady;

		public Player(String name, boolean isHost, boolean isReady) {
			assert name != null;
			assert !isHost || isReady : "All hosts are ready";

			this.name = name;
			this.isHost = isHost;
			this.isReady = isReady;
		}

		public void setReady(boolean ready) {
			isReady = ready;
		}

		public boolean isReady() {
			return isReady;
		}

		public void name(String name) {
			assert name != null;

			this.name = name;
		}

		public String name() {
			return name;
		}

		public boolean isHost() {
			return isHost;
		}

		@Override
		public int compareTo(Player o) {
			if (o.isHost != isHost) {
				return Boolean.compare(o.isHost, isHost);
			}

			return name.compareTo(o.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, isHost, isReady);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Player player
					&& player.name.equals(name)
					&& (player.isHost == isHost)
					&& (player.isReady == isReady);
		}

		@Override
		public String toString() {
			if (isHost) {
				return "[Host] " + name;
			}

			if (isReady) {
				return "[Ready] " + name;
			}

			return name;
		}
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

	public abstract List<Player> players();

	@Override
	public abstract void close();
}
