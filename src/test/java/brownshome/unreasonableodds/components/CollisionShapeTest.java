package brownshome.unreasonableodds.components;

import brownshome.vecmath.Vec2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionShapeTest {
	private static final double ACCURACY = 1e-6;

	private static void assertCollisionPointEquals(CollisionShape.Point expected, CollisionShape.Point actual, double accuracy) {
		assertEquals(expected.position().x(), actual.position().x(), accuracy);
		assertEquals(expected.position().y(), actual.position().y(), accuracy);

		assertEquals(expected.normal().x(), actual.normal().x(), accuracy);
		assertEquals(expected.normal().y(), actual.normal().y(), accuracy);
	}

	@Test
	void circleCircleCollision() {
		CollisionShape a = new CircleCollisionShape(Vec2.ZERO, 1.0);
		CollisionShape b = new CircleCollisionShape(Vec2.of(2.0, 0.0), 2.0);
		CollisionShape c = new CircleCollisionShape(Vec2.of(2.0, 4.5), 2.5);
		CollisionShape d = new CircleCollisionShape(Vec2.of(3.0, 4.0), 4.0);
		CollisionShape e = new CircleCollisionShape(Vec2.of(3.0, 4.0), 4.1);

		assertTrue(a.doesCollideWith(b));
		assertTrue(b.doesCollideWith(a));

		assertFalse(b.doesCollideWith(c));
		assertFalse(c.doesCollideWith(b));

		assertFalse(a.doesCollideWith(c));
		assertFalse(c.doesCollideWith(a));

		assertFalse(a.doesCollideWith(d));
		assertFalse(d.doesCollideWith(a));

		assertTrue(a.doesCollideWith(e));
		assertTrue(e.doesCollideWith(a));
	}

	@Test
	void circleCircleSweptCollisionAtStart() {
		CollisionShape a = new CircleCollisionShape(Vec2.of(1.0, 0.0), 1.0);
		CollisionShape.Point result;

		result = a.sweptCollision(new CircleCollisionShape(Vec2.of(1.0, -2.0), 1.0), Vec2.of(0.0, 1.0));
		assertCollisionPointEquals(new CollisionShape.Point(Vec2.of(1.0, -1.0), Vec2.of(0.0, -1.0)), result, ACCURACY);
	}

	@Test
	void circleCircleSweptNoCollisionAway() {
		CollisionShape a = new CircleCollisionShape(Vec2.of(1.0, 0.0), 1.0);
		CollisionShape.Point result;

		result = a.sweptCollision(new CircleCollisionShape(Vec2.of(2.0, -2.0), 1.0), Vec2.of(0.0, -1.0));
		assertNull(result);
	}

	@Test
	void circleCircleSweptNoCollision() {
		CollisionShape a = new CircleCollisionShape(Vec2.of(1.0, 0.0), 1.0);
		CollisionShape.Point result;

		result = a.sweptCollision(new CircleCollisionShape(Vec2.of(2.0, -2.0), 1.0), Vec2.of(1.0, 0.0));
		assertNull(result);
	}
}