package brownshome.unreasonableodds.generation;

/**
 * A direction definition that is used by the wave collapse generation system
 */
public enum Direction {
	UP(0, 1), LEFT(-1, 0), DOWN(0, -1), RIGHT(1, 0);

	public final int x, y;

	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the direction opposite this one
	 * @return the reflected direction
	 */
	public Direction opposite() {
		return Direction.values()[(ordinal() + 2) % Direction.values().length];
	}

	/**
	 * Gets the direction counter-clockwise from this one
	 * @return the rotated direction
	 */
	public Direction rotate() {
		return Direction.values()[(ordinal() + 1) % Direction.values().length];
	}
}
