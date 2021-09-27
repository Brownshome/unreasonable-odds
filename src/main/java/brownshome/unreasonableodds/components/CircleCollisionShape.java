package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

/**
 * A circle collision shape.
 * @param position the center of the circle
 * @param radius the radius of the circle
 */
public record CircleCollisionShape(Vec2 position, double radius) implements CollisionShape {
	@Override
	public Vec2 lesserExtent() {
		MVec2 extent = position.copy();
		extent.add(-radius, -radius);
		return extent;
	}

	@Override
	public Vec2 greaterExtent() {
		MVec2 extent = position.copy();
		extent.add(radius, radius);
		return extent;
	}

	@Override
	public boolean doesCollideWith(CollisionShape shape) {
		return switch (shape) {
			// Shapes that we know how to collide with
			case CircleCollisionShape circle -> doesCollideWith(circle);

			// Shapes that know how to collide with us (and can skip type checks)
			case AABBCollisionShape aabb -> aabb.doesCollideWith(this);

			// A custom shape, hope they know how to collide with us...
			default -> shape.doesCollideWith(this);
		};
	}

	public boolean doesCollideWith(CircleCollisionShape circle) {
		return position.distanceSq(circle.position) < radius * radius + circle.radius * circle.radius;
	}
}
