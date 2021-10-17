package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.session.*;

public class EntityConverter implements Converter<Entity> {
	@Override
	public void write(ByteBuffer buffer, Entity object) {
		object.write(buffer);
	}

	@Override
	public Entity read(ByteBuffer buffer) {
		var entityFactory = NetworkSession.get().rules().entities();
		return entityFactory.read(buffer);
	}

	@Override
	public int size(Entity object) {
		return object.size();
	}

	@Override
	public boolean isSizeExact(Entity object) {
		return object.isSizeExact();
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
