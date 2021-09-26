package brownshome.unreasonableodds.components;

import brownshome.vecmath.Vec2;

public interface CollisionShape {
	Vec2 lesserExtent();
	Vec2 greaterExtent();
	boolean doesCollideWith(CollisionShape shape);
}
