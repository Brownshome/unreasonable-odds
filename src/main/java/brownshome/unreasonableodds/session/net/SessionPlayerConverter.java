package brownshome.unreasonableodds.session.net;

import java.nio.ByteBuffer;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.session.SessionPlayer;

public final class SessionPlayerConverter implements Converter<SessionPlayer> {
	@Override
	public void write(ByteBuffer byteBuffer, SessionPlayer player) {
		NetworkUtils.writeString(byteBuffer, player.name());

		byte flags;
		if (player.isHost()) {
			flags = 2;
		} else if (player.isReady()) {
			flags = 1;
		} else {
			flags = 0;
		}

		byteBuffer.put(flags);
	}

	@Override
	public SessionPlayer read(ByteBuffer buffer) {
		var name = NetworkUtils.readString(buffer);

		var flags = buffer.get();
		if (flags > 2) {
			throw new IllegalArgumentException("Illegal player flags %d".formatted(flags));
		}

		return new SessionPlayer(name, flags == 2, flags != 0);
	}

	@Override
	public int size(SessionPlayer player) {
		return NetworkUtils.calculateSize(player.name()).size();
	}

	@Override
	public boolean isSizeExact(SessionPlayer player) {
		return false;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
