package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.session.NetworkGameSession;

public sealed class UniverseConverter implements Converter<Universe> {
	public static final class WithMap extends UniverseConverter {
		@Override
		public Universe read(ByteBuffer buffer) {
			var session = NetworkGameSession.get();
			return session.rules().readUniverseWithMap(buffer);
		}

		@Override
		public void write(ByteBuffer buffer, Universe object) {
			object.writeWithMap(buffer);
		}
	}

	@Override
	public void write(ByteBuffer buffer, Universe object) {
		object.write(buffer);
	}

	@Override
	public Universe read(ByteBuffer buffer) {
		var session = NetworkGameSession.get();
		return session.rules().readUniverse(buffer);
	}

	@Override
	public int size(Universe object) {
		return object.size();
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
