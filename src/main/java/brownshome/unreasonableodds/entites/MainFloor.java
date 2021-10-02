package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.tile.Tile;

public class MainFloor extends Entity {
	private final Tile[] tiles;

	public MainFloor(Tile[] tiles) {
		this.tiles = tiles;
	}

	protected final Tile[] tiles() {
		return tiles;
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);

		for (var tile : tiles) {
			builder.addCollision(tile);
		}
	}
}
