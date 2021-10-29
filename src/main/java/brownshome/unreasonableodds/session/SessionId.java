package brownshome.unreasonableodds.session;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

import brownshome.netcode.annotation.converter.Networkable;

/**
 * The id of a session
 * @param number the number of the session
 * @param address the address of the session, or null if this is a local session or the address is unknown
 */
public record SessionId(int number, InetSocketAddress address) implements Networkable {
	public static final int HOST_NUMBER = 0;

	public static SessionId createLocal(int number) {
		return new SessionId(number, null);
	}

	public SessionId withAddress(InetSocketAddress address) {
		return new SessionId(number, address);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SessionId other && other.number == number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public String toString() {
		return Integer.toString(number);
	}

	public SessionId(ByteBuffer buffer) {
		this(Byte.toUnsignedInt(buffer.get()), null);
	}

	@Override
	public void write(ByteBuffer buffer) {
		assert number >= 0 && number < (1 << Byte.SIZE);
		buffer.put((byte) number);
	}

	@Override
	public int size() {
		return Byte.BYTES;
	}

	@Override
	public boolean isSizeExact() {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}

	public boolean isHost() {
		return number == HOST_NUMBER;
	}
}
