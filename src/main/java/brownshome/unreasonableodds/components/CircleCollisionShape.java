package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

import static brownshome.unreasonableodds.math.VectorUtils.cross;

/**
 * A circle collision shape.
 * @param position the center of the circle
 * @param radius the radius of the circle
 */
public record CircleCollisionShape(Vec2 position, double radius) implements CollisionShape {
	public CircleCollisionShape {
		assert radius > 0;
	}

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
		return position.distanceSq(circle.position) < (radius + circle.radius) * (radius + circle.radius);
	}

	@Override
	public SweptCollision sweptCollision(CollisionShape shape, Vec2 sweep) {
		return switch (shape) {
			case CircleCollisionShape circle -> sweptCollision(circle, sweep);

			default -> {
				MVec2 reverse = sweep.copy();
				reverse.negate();
				yield shape.sweptCollision(this, reverse).reverse();
			}
		};
	}

	public SweptCollision sweptCollision(CircleCollisionShape circle, Vec2 sweep) {
		assert !doesCollideWith(circle);

		// This forms a quadratic equation in t.
		double sweepLength = sweep.lengthSq();
		double combinedRadius = radius + circle.radius;
		assert combinedRadius != 0;

		assert sweepLength != 0;

		MVec2 relativePosition = circle.position.copy();
		relativePosition.subtract(position);

		double cross = cross(sweep, relativePosition);
		double contactPointSplit = Math.sqrt(sweepLength * combinedRadius * combinedRadius - cross * cross);

		// This catches NaN, and the 'kiss but don't collide case'. Both of which are considered not collisions
		if (!(contactPointSplit > 0.0)) {
			return null;
		}

		double contactPointOffset = -relativePosition.dot(sweep);

		double t = contactPointOffset < contactPointSplit
				? contactPointOffset + contactPointSplit
				: contactPointOffset - contactPointSplit;

		if (t >= sweepLength || t < 0) {
			return null;
		}

		t /= sweepLength;

		relativePosition.scaleAdd(sweep, t);
		MVec2 normal = relativePosition.copy();
		normal.normalize();
		relativePosition.scale(radius / combinedRadius);
		relativePosition.add(position);

		return new SweptCollision(t, relativePosition, normal);
	}
}
