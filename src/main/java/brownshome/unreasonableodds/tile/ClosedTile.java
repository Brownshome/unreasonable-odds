package brownshome.unreasonableodds.tile;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.AABBCollisionShape;
import brownshome.vecmath.Vec2;

public class ClosedTile implements Tile {
	private final AABBCollisionShape aabb;

	protected ClosedTile(AABBCollisionShape aabb) {
		this.aabb = aabb;
	}

	public static Tile createTile(Vec2 lower, Vec2 greater) {
		return new ClosedTile(new AABBCollisionShape(lower, greater));
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
}
