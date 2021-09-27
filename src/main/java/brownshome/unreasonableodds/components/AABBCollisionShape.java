package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

/**
 * Represents an axis-aligned bounding box.
 * @param greaterExtent the greater coordinates of each axis
 * @param lesserExtent the lesser coordinates of each axis
 */
public record AABBCollisionShape(Vec2 lesserExtent, Vec2 greaterExtent) implements CollisionShape {
	@Override
	public boolean doesCollideWith(CollisionShape shape) {
		return switch (shape) {
			// Shapes that we know how to collide with
			case AABBCollisionShape aabb -> doesCollideWith(aabb);
			case CircleCollisionShape circle -> doesCollideWith(circle);

			// A custom shape, hope they know how to collide with us...
			default -> shape.doesCollideWith(this);
		};
	}

	public boolean doesCollideWith(AABBCollisionShape aabb) {
		return lesserExtent.x() < aabb.greaterExtent.x() && aabb.lesserExtent.x() < greaterExtent.x()
				&& lesserExtent.y() < aabb.greaterExtent.y() && aabb.lesserExtent.y() < greaterExtent.y();
	}

	public boolean doesCollideWith(CircleCollisionShape circle) {
		// project the center of the circle onto the AABB
		MVec2 proj = circle.position().copy();

		if (proj.x() < lesserExtent.x()) {
			proj.x(lesserExtent.x());
		} else if (proj.x() > greaterExtent.x()) {
			proj.x(greaterExtent.x());
		}

		if (proj.y() < lesserExtent.y()) {
			proj.y(lesserExtent.y());
		} else if (proj.y() > greaterExtent.y()) {
			proj.y(greaterExtent.y());
		}

		// test that point against the circle
		return proj.distanceSq(circle.position()) < circle.radius() * circle.radius();
	}
}
