package brownshome.unreasonableodds.session;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Networkable;

public record Id(SessionId sessionId, int number) implements Networkable {
	public Id(ByteBuffer buffer) {
		this(new SessionId(buffer), Byte.toUnsignedInt(buffer.get()));
	}

	@Override
	public void write(ByteBuffer buffer) {
		sessionId.write(buffer);

		assert number >= 0 && number < (1 << Byte.SIZE);
		buffer.put((byte) number);
	}

	@Override
	public int size() {
		return sessionId.size() + Byte.BYTES;
	}

	@Override
	public boolean isSizeExact() {
		return sessionId.isSizeExact();
	}

	@Override
	public boolean isSizeConstant() {
		return sessionId.isSizeConstant();
	}
}
