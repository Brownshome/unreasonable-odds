package brownshome.unreasonableodds.entites;

import java.nio.ByteBuffer;
import java.util.*;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.AABBCollisionShape;
import brownshome.unreasonableodds.components.Collidable;
import brownshome.unreasonableodds.tile.Tile;
import brownshome.vecmath.Vec2;

/**
 * Represents unchanging elements on the map layout
 */
public class StaticMap extends Entity {
	private final List<Tile> tiles;
	private final List<Collidable> walls;

	protected StaticMap(List<Tile> tiles) {
		this.tiles = tiles;

		walls = new ArrayList<>();

		walls.add(new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Vec2.of(0.0, Double.POSITIVE_INFINITY)));
		walls.add(new AABBCollisionShape(Vec2.of(1.0, Double.NEGATIVE_INFINITY), Vec2.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)));
		walls.add(new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Vec2.of(Double.POSITIVE_INFINITY, 0.0)));
		walls.add(new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, 1.0), Vec2.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)));
	}

	@Override
	protected int id() {
		return KnownEntities.STATIC_MAP.id();
	}

	protected final List<Tile> tiles() {
		return tiles;
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);

		walls.forEach(builder::addCollision);

		for (var tile : tiles) {
			tile.addToBuilder(builder);
		}
	}
}
