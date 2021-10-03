package brownshome.unreasonableodds.generation;

public interface TileType {
	/**
	 * A version of this tile reflected about the x-axis
	 * @return the tile
	 */
	TileType reflect();

	/**
	 * A version of this tile rotated a quarter turn anti-clockwise
	 * @return the tile
	 */
	TileType rotate();
}
