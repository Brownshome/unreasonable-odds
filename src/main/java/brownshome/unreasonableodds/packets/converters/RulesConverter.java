package brownshome.unreasonableodds.packets.converters;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import brownshome.netcode.NetworkUtils;
import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.session.NetworkSession;

public final class RulesConverter implements Converter<Rules> {
	@Override
	public void write(ByteBuffer buffer, Rules object) {
		object.write(buffer);
	}

	@Override
	public Rules read(ByteBuffer buffer) {
		try {
			// Use the current session classloader
			return (Rules) NetworkSession.get()
					.getClass().getClassLoader()
					.loadClass(NetworkUtils.readString(buffer))
					.getConstructor(ByteBuffer.class).newInstance(buffer);
		} catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch(InvocationTargetException e) {
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
