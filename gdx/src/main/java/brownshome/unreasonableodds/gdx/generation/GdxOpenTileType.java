package brownshome.unreasonableodds.gdx.generation;

import brownshome.unreasonableodds.generation.TileType;
import brownshome.unreasonableodds.tile.Tile;

public class GdxOpenTileType implements TileType {
	@Override
	public TileType reflect() {
		return this;
	}

	@Override
	public TileType rotate() {
		return this;
	}

	@Override
	public Tile createTile(int x, int y) {
		return null;
	}
}
