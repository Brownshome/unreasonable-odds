package brownshome.unreasonableodds.entites.tile;

import brownshome.unreasonableodds.components.*;
import brownshome.vecmath.Vec2;

public class Tile implements Collidable {
	private final AABBCollisionShape aabb;

	protected Tile(AABBCollisionShape aabb) {
		this.aabb = aabb;
	}

	public static Tile makeTile(Vec2 lower, Vec2 greater) {
		return new Tile(new AABBCollisionShape(lower, greater));
	}

	public final Vec2 lesserExtent() {
		return aabb.lesserExtent();
	}

	public final Vec2 greaterExtent() {
		return aabb.greaterExtent();
	}

	@Override
	public CollisionShape collisionShape() {
		return aabb;
	}
}
