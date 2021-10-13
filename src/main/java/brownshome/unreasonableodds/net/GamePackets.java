package brownshome.unreasonableodds.net;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.time.Instant;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.DefinePacket;
import brownshome.netcode.annotation.MakeReliable;
import brownshome.netcode.annotation.converter.Converter;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.session.ClientSession;
import brownshome.unreasonableodds.session.net.InstantConverter;

final class GamePackets {
	private GamePackets() { }

	static final class RulesConverter implements Converter<Rules> {
		@Override
		public void write(ByteBuffer buffer, Rules object) {
			object.write(buffer);
		}

		@Override
		public Rules read(ByteBuffer buffer) {
			try {
				// Use the current session classloader
				return (Rules) ClientSession.getHost()
						.getClass().getClassLoader()
						.loadClass(NetworkUtils.readString(buffer))
						.getConstructor(ByteBuffer.class).newInstance(buffer);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Failed to construct rules class", e);
			}
		}

		@Override
		public int size(Rules object) {
			return object.size();
		}

		@Override
		public boolean isSizeExact(Rules object) {
			return false;
		}

		@Override
		public boolean isSizeConstant() {
			return false;
		}
	}

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@UseConverter(InstantConverter.class) Instant startTime, @UseConverter(RulesConverter.class) Rules rules) {
		ClientSession.getHost().startGame(rules, startTime);
	}
}
