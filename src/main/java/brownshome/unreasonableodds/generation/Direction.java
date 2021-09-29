package brownshome.unreasonableodds.generation;

/**
 * A direction definition that is used by the wave collapse generation system
 */
public enum Direction {
	UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

	public final int x, y;

	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Direction opposite() {
		return Direction.values()[(ordinal() + 2) % Direction.values().length];
	}
}
