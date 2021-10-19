package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;
import java.time.Duration;

import brownshome.netcode.annotation.converter.Converter;

public class DurationConverter implements Converter<Duration> {
	public static final DurationConverter INSTANCE = new DurationConverter();

	@Override
	public void write(ByteBuffer buffer, Duration object) {
		buffer.putLong(object.getSeconds()).putInt(object.getNano());
	}

	@Override
	public Duration read(ByteBuffer buffer) {
		return Duration.ofSeconds(buffer.getLong(), buffer.getInt());
	}

	@Override
	public int size(Duration object) {
		return Long.BYTES + Integer.BYTES;
	}

	@Override
	public boolean isSizeExact(Duration object) {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}
}
