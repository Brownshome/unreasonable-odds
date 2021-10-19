package brownshome.unreasonableodds.packets.converters;

import java.net.*;
import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;

public class InetSocketAddressConverter implements Converter<InetSocketAddress> {
	public static final InetSocketAddressConverter INSTANCE = new InetSocketAddressConverter();

	@Override
	public void write(ByteBuffer buffer, InetSocketAddress object) {
		byte[] address = object.getAddress().getAddress();
		buffer.put((byte) address.length).put(address).putInt(object.getPort());
	}

	@Override
	public InetSocketAddress read(ByteBuffer buffer) {
		byte[] address = new byte[Byte.toUnsignedInt(buffer.get())];
		buffer.get(address);

		try {
			return new InetSocketAddress(InetAddress.getByAddress(address), buffer.getInt());
		} catch (UnknownHostException uhe) {
			throw new IllegalArgumentException("%d is not a valid address length".formatted(address.length));
		}
	}

	@Override
	public int size(InetSocketAddress object) {
		return Byte.BYTES + object.getAddress().getAddress().length + Integer.BYTES;
	}

	@Override
	public boolean isSizeExact(InetSocketAddress object) {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
