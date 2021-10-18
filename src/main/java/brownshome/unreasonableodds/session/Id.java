package brownshome.unreasonableodds.session;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Networkable;

public record Id(int sessionId, int number) implements Networkable {
	public Id(ByteBuffer buffer) {
		this(Byte.toUnsignedInt(buffer.get()), Byte.toUnsignedInt(buffer.get()));
	}

	@Override
	public void write(ByteBuffer buffer) {
		assert sessionId >= 0 && sessionId < (1 << Byte.SIZE);
		buffer.put((byte) sessionId);

		assert number >= 0 && number < (1 << Byte.SIZE);
		buffer.put((byte) number);
	}

	@Override
	public int size() {
		return Byte.BYTES + Byte.BYTES;
	}

	@Override
	public boolean isSizeExact() {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}
}
