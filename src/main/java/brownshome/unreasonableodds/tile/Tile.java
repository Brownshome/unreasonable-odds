package brownshome.unreasonableodds.tile;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.Universe;

public interface Tile extends Networkable {
	void addToBuilder(Universe.Builder builder);
	int id();

	@Override
	default void write(ByteBuffer buffer) {
		int id = id();
		assert Short.toUnsignedInt((short) id) == id;

		buffer.putShort((short) id);
	}

	@Override
	default int size() {
		return Short.BYTES;
	}

	@Override
	default boolean isSizeExact() {
		return true;
	}

	@Override
	default boolean isSizeConstant() {
		return false;
	}
}
