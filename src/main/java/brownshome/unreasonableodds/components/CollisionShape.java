package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
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
	 * Queries if this object collides with the given object. Two objects are considered to have collided if there
	 * is a definite overlap. Two touching objects are not considered to collide.
	 *
	 * @param shape the shape to query
	 * @return true if they do collide, false if they do not.
	 */
	boolean doesCollideWith(CollisionShape shape);

	/**
	 * The result of a swept collision.
	 *
	 * @param sweep the portion of the sweep ray that was swept along
	 * @param contact the contact point
	 * @param normal the normal of the surface of the collided object
	 */
	record SweptCollision(double sweep, Vec2 contact, Vec2 normal) {
		public SweptCollision reverse() {
			MVec2 reversed = normal.copy();
			reversed.negate();
			return new SweptCollision(sweep, contact, normal);
		}
	}

	/**
	 * Finds the collision point between this object and a swept other object. If two objects just touch at the end of the
	 * sweep they are not considered to have collided.
	 * @param shape the shape to sweep
	 * @param sweep the vector over which the sweep occurs
	 * @return a collision point, or null if no collision occurred over the course of the sweep
	 */
	SweptCollision sweptCollision(CollisionShape shape, Vec2 sweep);
}
