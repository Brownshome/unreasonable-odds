package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

import static brownshome.unreasonableodds.math.VectorUtils.cross;

public record PointCollisionShape(Vec2 position) implements CollisionShape {
	@Override
	public Vec2 lesserExtent() {
		return position;
	}

	@Override
	public Vec2 greaterExtent() {
		return position;
	}

	@Override
	public boolean doesCollideWith(CollisionShape shape) {
		return switch (shape) {
			case PointCollisionShape ignored -> false;
			case CircleCollisionShape circle -> doesCollideWith(circle);
			case AABBCollisionShape aabb -> doesCollideWith(aabb);

			default -> shape.doesCollideWith(this);
		};
	}

	public boolean doesCollideWith(CircleCollisionShape circle) {
		return position.distanceSq(circle.position()) < circle.radius() * circle.radius();
	}

	public boolean doesCollideWith(AABBCollisionShape aabb) {
		return position.x() > aabb.lesserExtent().x() && position.x() < aabb.greaterExtent().x()
				&& position.y() > aabb.lesserExtent().y() && position.y() < aabb.greaterExtent().y();
	}

	@Override
	public SweptCollision sweptCollision(CollisionShape shape, Vec2 sweep) {
		return switch (shape) {
			case PointCollisionShape ignored -> null;
			case CircleCollisionShape circle -> sweptCollision(circle, sweep);
			case AABBCollisionShape aabb -> sweptCollision(aabb, sweep);

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
		assert sweepLength != 0;

		MVec2 relativePosition = circle.position().copy();
		relativePosition.subtract(position);

		double cross = cross(sweep, relativePosition);
		double contactPointSplit = Math.sqrt(sweepLength * circle.radius() * circle.radius() - cross * cross);

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
		relativePosition.add(position);

		return new SweptCollision(t, relativePosition, normal);
	}

	public SweptCollision sweptCollision(AABBCollisionShape aabb, Vec2 sweep) {
		assert !doesCollideWith(aabb);

		MVec2 scale = Vec2.of(1.0 / sweep.x(), 1.0 / sweep.y());

		MVec2 positiveDistance = aabb.lesserExtent().copy();
		positiveDistance.subtract(position);
		positiveDistance.scale(scale);

		MVec2 negativeDistance = position.copy();
		negativeDistance.subtract(aabb.greaterExtent());
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

		if (t >= positiveDistance.x() && t >= negativeDistance.x() || t >= positiveDistance.y() && t >= positiveDistance.y()) {
			// We have will miss the AABB, the ranges of collision on each axis don't overlap
			return null;
		}

		MVec2 point = position.copy();
		point.scaleAdd(sweep, t);

		return new SweptCollision(t, point, normal);
	}
}
