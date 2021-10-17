package brownshome.unreasonableodds.packets.converters;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.player.*;

public record LobbyPlayerProxy(String name, int id, boolean host, boolean ready) implements Networkable {
	@Override
	public void write(ByteBuffer byteBuffer) {
		NetworkUtils.writeString(byteBuffer, name());

		assert Byte.toUnsignedInt((byte) id) == id;
		byteBuffer.put((byte) id);

		byte flags;
		if (host) {
			flags = 2;
		} else if (ready) {
			flags = 1;
		} else {
			flags = 0;
		}

		byteBuffer.put(flags);
	}

	public LobbyPlayerProxy(NetworkLobbyPlayer player) {
		this(player.name(), player.id().number(), player.host(), player.ready());
	}

	public LobbyPlayerProxy(ByteBuffer buffer) {
		this(NetworkUtils.readString(buffer), buffer.get(), buffer.get());
	}

	private LobbyPlayerProxy(String name, int id, int flags) {
		this(name, id, flags == 2, flags != 0);

		if (flags > 2) {
			throw new IllegalArgumentException("Illegal player flags %d".formatted(flags));
		}
	}

	@Override
	public int size() {
		return NetworkUtils.calculateSize(name()).size() + Byte.BYTES + Byte.BYTES;
	}

	@Override
	public boolean isSizeExact() {
		return false;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}

	public ExportedLobbyPlayer toExported(InetSocketAddress address) {
		return ExportedLobbyPlayer.create(name, ready, host, new NetworkPlayer.Id(address, id));
	}
}
