package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;
import java.time.Instant;

import brownshome.netcode.annotation.converter.Converter;

public final class InstantConverter implements Converter<Instant> {
	public static final InstantConverter INSTANCE = new InstantConverter();

	@Override
	public void write(ByteBuffer buffer, Instant object) {
		buffer.putLong(object.getEpochSecond()).putInt(object.getNano());
	}

	@Override
	public Instant read(ByteBuffer buffer) {
		return Instant.ofEpochSecond(buffer.getLong(), buffer.getInt());
	}

	@Override
	public int size(Instant object) {
		return Long.BYTES + Integer.BYTES;
	}

	@Override
	public boolean isSizeExact(Instant object) {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}
}
