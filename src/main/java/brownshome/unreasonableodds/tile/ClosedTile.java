package brownshome.unreasonableodds.tile;

import java.nio.ByteBuffer;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.AABBCollisionShape;
import brownshome.unreasonableodds.packets.converters.Vec2Converter;
import brownshome.vecmath.Vec2;

public class ClosedTile implements Tile {
	private final AABBCollisionShape aabb;

	public ClosedTile(AABBCollisionShape aabb) {
		this.aabb = aabb;
	}

	public ClosedTile(Vec2 lower, Vec2 greater) {
		this(new AABBCollisionShape(lower, greater));
	}

	public final Vec2 lesserExtent() {
		return aabb.lesserExtent();
	}

	public final Vec2 greaterExtent() {
		return aabb.greaterExtent();
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		builder.addCollision(aabb);
	}

	public ClosedTile(ByteBuffer buffer) {
		this(Vec2Converter.INSTANCE.read(buffer), Vec2Converter.INSTANCE.read(buffer));
	}

	@Override
	public int id() {
		return KnownTiles.CLOSED.id();
	}

	@Override
	public void write(ByteBuffer buffer) {
		Tile.super.write(buffer);

		Vec2Converter.INSTANCE.write(buffer, aabb.lesserExtent());
		Vec2Converter.INSTANCE.write(buffer, aabb.greaterExtent());
	}

	@Override
	public int size() {
		return Tile.super.size() + Vec2Converter.INSTANCE.size(aabb.lesserExtent()) + Vec2Converter.INSTANCE.size(aabb.greaterExtent());
	}

	@Override
	public boolean isSizeExact() {
		return Tile.super.isSizeExact()
				&& Vec2Converter.INSTANCE.isSizeExact(aabb.lesserExtent())
				&& Vec2Converter.INSTANCE.isSizeExact(aabb.greaterExtent());
	}
}
