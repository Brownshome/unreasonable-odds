package brownshome.unreasonableodds.gdx.generation;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.tile.GdxClosedTile;
import brownshome.unreasonableodds.generation.TileType;
import brownshome.unreasonableodds.tile.Tile;
import brownshome.vecmath.Vec2;

public final class GdxClosedTileType implements TileType {
	private final ApplicationResources resources;

	public GdxClosedTileType(ApplicationResources resources) {
		this.resources = resources;
	}

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
		return new GdxClosedTile(Vec2.of(x / 8.0, y / 8.0), Vec2.of((x + 1.0) / 8.0, (y + 1.0) / 8.0), resources);
	}
}
