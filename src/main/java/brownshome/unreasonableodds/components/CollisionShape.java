package brownshome.unreasonableodds.components;

import brownshome.vecmath.Vec2;

public interface CollisionShape {
	/**
	 * Gets the lesser extent of the AABB of this object
	 * @return the exclusive lesser edge of the AABB
	 */
	Vec2 lesserExtent();

	/**
	 * Gets the greater extent of the AABB of this object
	 * @return the exclusive greater edge of the AABB
	 */
	Vec2 greaterExtent();

	/**
	 * Queries if this object collides with the given object.
	 *
	 * @param shape the shape to query
	 * @return true if they do collide, false if they do not.
	 */
	boolean doesCollideWith(CollisionShape shape);
}
