package brownshome.unreasonableodds.components;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.packets.converters.Vec2Converter;
import brownshome.vecmath.*;

/**
 * A component that represents a position and orientation
 */
public record Position(Vec2 position, Rot2 orientation) implements Networkable {
	public Position(ByteBuffer buffer) {
		this(Vec2Converter.INSTANCE.read(buffer),
				Rot2.of(Vec2Converter.INSTANCE.read(buffer)));
	}

	@Override
	public void write(ByteBuffer buffer) {
		Vec2Converter.INSTANCE.write(buffer, position);
		Vec2Converter.INSTANCE.write(buffer, orientation);
	}

	@Override
	public int size() {
		return Vec2Converter.INSTANCE.size(position) + Vec2Converter.INSTANCE.size(orientation);
	}

	@Override
	public boolean isSizeExact() {
		return Vec2Converter.INSTANCE.isSizeExact(position) && Vec2Converter.INSTANCE.isSizeExact(orientation);
	}

	@Override
	public boolean isSizeConstant() {
		return Vec2Converter.INSTANCE.isSizeConstant();
	}
}
