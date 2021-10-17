package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.vecmath.Vec2;

public class Vec2Converter implements Converter<Vec2> {
	public static final Vec2Converter INSTANCE = new Vec2Converter();

	@Override
	public void write(ByteBuffer buffer, Vec2 object) {
		buffer.putDouble(object.x()).putDouble(object.y());
	}

	@Override
	public Vec2 read(ByteBuffer buffer) {
		return Vec2.of(buffer.getDouble(), buffer.getDouble());
	}

	@Override
	public int size(Vec2 object) {
		return Double.BYTES * 2;
	}

	@Override
	public boolean isSizeExact(Vec2 object) {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}
}
