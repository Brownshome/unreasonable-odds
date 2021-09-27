package brownshome.unreasonableodds.components;

import brownshome.vecmath.Vec2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionShapeTest {
	@Test
	void circleCircleCollision() {
		CollisionShape a = new CircleCollisionShape(Vec2.ZERO, 1.0);
		CollisionShape b = new CircleCollisionShape(Vec2.of(2.0, 0.0), 2.0);
		CollisionShape c = new CircleCollisionShape(Vec2.of(2.0, 4.5), 2.5);

		assertTrue(a.doesCollideWith(b));
		assertTrue(b.doesCollideWith(a));

		assertFalse(b.doesCollideWith(c));
		assertFalse(c.doesCollideWith(b));

		assertFalse(a.doesCollideWith(c));
		assertFalse(c.doesCollideWith(a));
	}

	// todo fill in the rest of these test cases. One for each collision type
}