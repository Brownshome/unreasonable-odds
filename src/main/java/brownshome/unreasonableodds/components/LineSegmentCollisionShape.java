package brownshome.unreasonableodds.components;

import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

public record LineSegmentCollisionShape(Vec2 from, Vec2 to) implements CollisionShape {
	@Override
	public Vec2 lesserExtent() {
		return Vec2.of(Math.min(from.x(), to.x()), Math.min(from.y(), to.y()));
	}

	@Override
	public Vec2 greaterExtent() {
		return Vec2.of(Math.max(from.x(), to.x()), Math.max(from.y(), to.y()));
	}

	@Override
	public boolean doesCollideWith(CollisionShape shape) {
		return switch (shape) {
			case LineSegmentCollisionShape lineSegment -> doesCollideWith(lineSegment);
			case CircleCollisionShape circle -> doesCollideWith(circle);
			case AABBCollisionShape aabb -> doesCollideWith(aabb);

			default -> shape.doesCollideWith(this);
		};
	}

	public boolean doesCollideWith(AABBCollisionShape aabb) {
		Vec2 l = aabb.lesserExtent();
		Vec2 g = aabb.greaterExtent();

		final int TOP = 1, BOTTOM = 2, LEFT = 4, RIGHT = 8;

		int toSector = 0;
		if (to.x() < l.x()) {
			toSector |= LEFT;
		} else if (to.x() > g.x()) {
			toSector = RIGHT;
		}

		if (to.y() < l.y()) {
			toSector += BOTTOM;
		} else if (to.y() > g.y()) {
			toSector += TOP;
		}

		int fromSector = 0;
		if (from.x() < l.x()) {
			fromSector = LEFT;
		} else if (from.x() > g.x()) {
			fromSector = RIGHT;
		}

		if (from.y() < l.y()) {
			fromSector += BOTTOM;
		} else if (from.y() > g.y()) {
			fromSector += RIGHT;
		}

		// One of the ends is inside the aabb
		if (toSector == 0 || fromSector == 0) {
			return true;
		}

		// The ends share an edge slice, no intersection
		if ((toSector & fromSector) != 0) {
			return false;
		}

		// The two corners that we bisect
		MVec2 a, b;
		switch (toSector) {
			case TOP -> {
				a = Vec2.of(l.x(), g.y());
				b = g.copy();
			}
			case BOTTOM -> {
				a = l.copy();
				b = Vec2.of(g.x(), l.y());
			}
			case LEFT -> {
				a = Vec2.of(l.x(), g.y());
				b = l.copy();
			}
			case RIGHT -> {
				a = Vec2.of(g.x(), l.y());
				b = g.copy();
			}
			case TOP | LEFT, BOTTOM | RIGHT -> {
				a = l.copy();
				b = g.copy();
			}
			case TOP | RIGHT, BOTTOM | LEFT -> {
				a = Vec2.of(l.x(), g.y());
				b = Vec2.of(g.x(), l.y());
			}
			default -> throw new AssertionError();
		}

		// Bisect
		MVec2 startToA, startToB, endToA, endToB;
		startToA = a.copy();
		endToA = a;
		startToB = b.copy();
		endToB = b;

		startToA.subtract(from);
		endToA.subtract(to);
		startToB.subtract(from);
		endToB.subtract(to);

		return cross(startToA, endToA) * cross(endToB, startToB) > 0;
	}

	public boolean doesCollideWith(LineSegmentCollisionShape line) {
		// If the shape formed by the four corners of this shape is convex then the two lines collide
		MVec2 startToEnd = to.copy();
		startToEnd.subtract(line.from);

		MVec2 startToStart = from.copy();
		startToEnd.subtract(line.from);

		MVec2 endToEnd = to.copy();
		startToEnd.subtract(line.to);

		MVec2 endToStart = from.copy();
		startToEnd.subtract(line.to);

		// Check if the angles at each corner are all < 180 deg (taking into account reflections)
		return cross(startToEnd, startToStart) * cross(endToStart, endToEnd) > 0
				|| cross(startToEnd, endToEnd) * cross(endToStart, startToStart) > 0;
	}

	public boolean doesCollideWith(CircleCollisionShape circle) {
		MVec2 startToCircle = circle.position().copy();
		startToCircle.subtract(from);

		MVec2 endToCircle = circle.position().copy();
		endToCircle.subtract(to);

		MVec2 ray = to.copy();
		ray.subtract(from);

		double radiusSquared = circle.radius() * circle.radius();

		// Check collision with end points
		if (ray.dot(startToCircle) < 0) {
			return startToCircle.lengthSq() < radiusSquared;
		}

		if (ray.dot(endToCircle) > 0) {
			return endToCircle.length() < radiusSquared;
		}

		// Check the distance of the circle to the line
		double cross = cross(startToCircle, endToCircle);
		return cross * cross < radiusSquared * ray.lengthSq();
	}

	private static double cross(Vec2 a, Vec2 b) {
		return a.x() * b.y() - b.x() * a.y();
	}
}
