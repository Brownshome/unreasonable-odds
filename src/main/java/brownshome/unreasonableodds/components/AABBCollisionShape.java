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

	@Override
	public SweptCollision sweptCollision(CollisionShape shape, Vec2 sweep) {
		return switch (shape) {
			case AABBCollisionShape aabb -> sweptCollision(aabb, sweep);
			case PointCollisionShape point -> sweptCollision(point, sweep);
			default -> CollisionShape.super.sweptCollision(shape, sweep);
		};
	}

	public SweptCollision sweptCollision(AABBCollisionShape aabb, Vec2 sweep) {
		assert !doesCollideWith(aabb);

		MVec2 scale = Vec2.of(1.0 / sweep.x(), 1.0 / sweep.y());

		MVec2 positiveDistance = lesserExtent.copy();
		positiveDistance.subtract(aabb.greaterExtent);
		positiveDistance.scale(scale);

		MVec2 negativeDistance = greaterExtent.copy();
		negativeDistance.subtract(aabb.lesserExtent);
		negativeDistance.scale(scale);

		double tx = Math.min(positiveDistance.x(), negativeDistance.x());
		if (tx >= 1.0 || Double.isNaN(tx)) {
			return null;
		}

		double ty = Math.min(positiveDistance.y(), negativeDistance.y());
		if (ty >= 1.0 || Double.isNaN(ty)) {
			return null;
		}

		double t;
		Vec2 normal;
		if (tx > ty) {
			t = tx;

			if (sweep.x() > 0.0) {
				normal = Vec2.of(-1.0, 0.0);
			} else {
				normal = Vec2.of(1.0, 0.0);
			}
		} else {
			t = ty;

			if (sweep.y() > 0.0) {
				normal = Vec2.of(0.0, -1.0);
			} else {
				normal = Vec2.of(0.0, 1.0);
			}
		}

		if (t < 0.0 || t >= positiveDistance.x() && t >= negativeDistance.x() || t >= positiveDistance.y() && t >= negativeDistance.y()) {
			// We have will miss the AABB, the ranges of collision on each axis don't overlap
			return null;
		}

		return new SweptCollision(t, normal);
	}

	public SweptCollision sweptCollision(PointCollisionShape point, Vec2 sweep) {
		assert !doesCollideWith(point);

		MVec2 scale = Vec2.of(1.0 / sweep.x(), 1.0 / sweep.y());

		MVec2 positiveDistance = lesserExtent.copy();
		positiveDistance.subtract(point.position());
		positiveDistance.scale(scale);

		MVec2 negativeDistance = greaterExtent.copy();
		negativeDistance.subtract(point.position());
		negativeDistance.scale(scale);

		double tx = Math.min(positiveDistance.x(), negativeDistance.x());
		if (tx >= 1.0 || Double.isNaN(tx)) {
			return null;
		}

		double ty = Math.min(positiveDistance.y(), negativeDistance.y());
		if (ty >= 1.0 || Double.isNaN(ty)) {
			return null;
		}

		double t;
		Vec2 normal;
		if (tx > ty) {
			t = tx;
			assert sweep.x() != 0.0;
			normal = Vec2.of(-Math.signum(sweep.x()), 0.0);
		} else {
			t = ty;
			assert sweep.y() != 0.0;
			normal = Vec2.of(0.0, -Math.signum(sweep.y()));
		}

		if (t < 0.0 || t >= positiveDistance.x() && t >= negativeDistance.x() || t >= positiveDistance.y() && t >= negativeDistance.y()) {
			// We missed, the ranges of collision on each axis don't overlap
			return null;
		}

		return new SweptCollision(t, normal);
	}
}
