package brownshome.unreasonableodds.math;

import brownshome.vecmath.Vec2;

public final class VectorUtils {
	private VectorUtils() {}

	public static double cross(Vec2 a, Vec2 b) {
		return a.x() * b.y() - a.y() * b.x();
	}
}
