package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.Universe;

public final class UniverseConverter implements Converter<Universe> {
	@Override
	public void write(ByteBuffer buffer, Universe object) {

	}

	@Override
	public Universe read(ByteBuffer buffer) {
		return null;
	}

	@Override
	public int size(Universe object) {
		return 0;
	}

	@Override
	public boolean isSizeExact(Universe object) {
		return false;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
