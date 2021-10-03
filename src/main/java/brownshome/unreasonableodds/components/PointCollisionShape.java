package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

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
			case CircleCollisionShape circle -> new CircleCollisionShape(position, 0.0).sweptCollision(circle, sweep);
			case AABBCollisionShape aabb -> new AABBCollisionShape(position, position).sweptCollision(aabb, sweep);
			default -> CollisionShape.super.sweptCollision(shape, sweep);
		};
	}
}
