package brownshome.unreasonableodds.player;

import java.nio.ByteBuffer;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.session.Id;

/**
 * A lobby player in a networked environment. This is networkable, but care must be taken, as the concrete type
 * in the packet will be the created at the other end.
 */
public class NetworkLobbyPlayer extends LobbyPlayer implements NetworkPlayer, Networkable {
	private final boolean host;
	private final Id id;

	public NetworkLobbyPlayer(ByteBuffer data) {
		this(NetworkUtils.readString(data), data.get(), new Id(data));
	}

	private NetworkLobbyPlayer(String name, int flags, Id id) {
		this(name, flags != 0, flags == 2, id);

		if (flags > 2) {
			throw new IllegalArgumentException("Illegal player flags %d".formatted(flags));
		}
	}

	protected NetworkLobbyPlayer(String name, boolean ready, boolean host, Id id) {
		super(name, ready);

		this.host = host;
		this.id = id;

		assert !host || ready : "All hosts must be ready";
	}

	@Override
	public void ready(boolean ready) {
		assert !host || ready : "All hosts must be ready";

		super.ready(ready);
	}

	public final boolean host() {
		return host;
	}

	@Override
	public Id id() {
		return id;
	}

	@Override
	public String toString() {
		if (host()) {
			return "[Host] " + name();
		} else if (ready()) {
			return "[Ready] " + name();
		} else {
			return name();
		}
	}

	@Override
	public void write(ByteBuffer buffer) {
		NetworkUtils.writeString(buffer, name());

		byte flags;
		if (host()) {
			flags = 2;
		} else if (ready()) {
			flags = 1;
		} else {
			flags = 0;
		}
		buffer.put(flags);

		id().write(buffer);
	}

	@Override
	public int size() {
		return NetworkUtils.calculateSize(name()).size() + Byte.BYTES + id().size();
	}

	@Override
	public boolean isSizeExact() {
		return NetworkUtils.calculateSize(name()).exact() && id().isSizeExact();
	}

	@Override
	public boolean isSizeConstant() {
		return NetworkUtils.calculateSize(name()).constant() && id().isSizeConstant();
	}
}
