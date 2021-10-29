package brownshome.unreasonableodds.packets.converters;

import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.session.*;

public class EntityConverter implements Converter<Entity> {
	public static final EntityConverter INSTANCE = new EntityConverter();

	/**
	 * An entity converter that does not send the whole map, and the cached copy
	 */
	public static final class CompressMap extends EntityConverter {
		public static final CompressMap INSTANCE = new CompressMap();

		@Override
		public void write(ByteBuffer buffer, Entity object) {
			if (object.id() == KnownEntities.STATIC_MAP.id()) {
				buffer.putShort((short) KnownEntities.STATIC_MAP.id());
				return;
			}

			super.write(buffer, object);
		}

		@Override
		public Entity read(ByteBuffer buffer) {
			int id = Short.toUnsignedInt(buffer.getShort());

			if (id == KnownEntities.STATIC_MAP.id()) {
				var map = NetworkGameSession.get().map();
				assert map != null;
				return map;
			}

			var entityFactory = NetworkSession.get().rules().entities();
			return entityFactory.read(id, buffer);
		}

		@Override
		public boolean isSizeExact(Entity object) {
			return !(object instanceof StaticMap) && super.isSizeExact(object);
		}
	}

	@Override
	public void write(ByteBuffer buffer, Entity object) {
		int id = object.id();
		assert Short.toUnsignedInt((short) id) == id;

		buffer.putShort((short) id);

		object.write(buffer);
	}

	@Override
	public Entity read(ByteBuffer buffer) {
		int id = Short.toUnsignedInt(buffer.getShort());

		var entityFactory = NetworkSession.get().rules().entities();
		var result = entityFactory.read(id, buffer);

		if (result instanceof StaticMap map) {
			assert NetworkGameSession.get().map() == null;
			NetworkGameSession.get().map(map);
		}

		return result;
	}

	@Override
	public int size(Entity object) {
		return object.size() + Short.BYTES;
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
