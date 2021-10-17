package brownshome.unreasonableodds.packets.converters;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.Universe;

public final class UniverseIdConverter implements Converter<Universe.Id> {
	@Override
	public void write(ByteBuffer buffer, Universe.Id object) {
		InetSocketAddressConverter.INSTANCE.write(buffer, object.creator());
		buffer.putInt(object.number());
	}

	@Override
	public Universe.Id read(ByteBuffer buffer) {
		return new Universe.Id(InetSocketAddressConverter.INSTANCE.read(buffer), buffer.getInt());
	}

	@Override
	public int size(Universe.Id object) {
		return InetSocketAddressConverter.INSTANCE.size(object.creator()) + Integer.BYTES;
	}

	@Override
	public boolean isSizeExact(Universe.Id object) {
		return InetSocketAddressConverter.INSTANCE.isSizeExact(object.creator());
	}

	@Override
	public boolean isSizeConstant() {
		return InetSocketAddressConverter.INSTANCE.isSizeConstant();
	}
}
