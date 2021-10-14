package brownshome.unreasonableodds.packets.converters;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.Universe;

public final class UniverseIdConverter implements Converter<Universe.Id> {
	private static final InetSocketAddressConverter ADDRESS_CONVERTER = new InetSocketAddressConverter();

	@Override
	public void write(ByteBuffer buffer, Universe.Id object) {
		ADDRESS_CONVERTER.write(buffer, object.creator());
		buffer.putInt(object.number());
	}

	@Override
	public Universe.Id read(ByteBuffer buffer) {
		return new Universe.Id(ADDRESS_CONVERTER.read(buffer), buffer.getInt());
	}

	@Override
	public int size(Universe.Id object) {
		return ADDRESS_CONVERTER.size(object.creator()) + Integer.BYTES;
	}

	@Override
	public boolean isSizeExact(Universe.Id object) {
		return ADDRESS_CONVERTER.isSizeExact(object.creator());
	}

	@Override
	public boolean isSizeConstant() {
		return ADDRESS_CONVERTER.isSizeConstant();
	}
}
