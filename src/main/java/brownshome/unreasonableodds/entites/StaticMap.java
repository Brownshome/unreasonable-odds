package brownshome.unreasonableodds.entites;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.AABBCollisionShape;
import brownshome.unreasonableodds.components.Collidable;
import brownshome.unreasonableodds.tile.*;
import brownshome.vecmath.Vec2;

/**
 * Represents unchanging elements on the map layout
 */
public class StaticMap extends Entity {
	private final List<Tile> tiles;
	private final List<Collidable> walls;

	protected StaticMap(List<Tile> tiles) {
		this.tiles = tiles;
		this.walls = createWalls();
	}

	private static List<Collidable> createWalls() {
		return List.of(
				new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Vec2.of(0.0, Double.POSITIVE_INFINITY)),
				new AABBCollisionShape(Vec2.of(1.0, Double.NEGATIVE_INFINITY), Vec2.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)),
				new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Vec2.of(Double.POSITIVE_INFINITY, 0.0)),
				new AABBCollisionShape(Vec2.of(Double.NEGATIVE_INFINITY, 1.0), Vec2.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY))
		);
	}

	@Override
	public int id() {
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

	protected StaticMap(ByteBuffer buffer) {
		int numberOfTiles = Short.toUnsignedInt(buffer.getShort());

		tiles = new ArrayList<>(numberOfTiles);
		for (int i = 0; i < numberOfTiles; i++) {
			int id = Short.toUnsignedInt(buffer.getShort());
			tiles.add(readTile(id, buffer));
		}

		this.walls = createWalls();
	}

	@Override
	public void write(ByteBuffer buffer) {
		super.write(buffer);

		assert tiles.size() < 1 << (Short.SIZE);
		buffer.putShort((short) tiles.size());

		for (var t : tiles) {
			t.write(buffer);
		}
	}

	@Override
	public int size() {
		return super.size() + Short.BYTES + tiles.stream().mapToInt(Tile::size).sum();
	}

	@Override
	public boolean isSizeExact() {
		return super.isSizeExact() && tiles.stream().allMatch(Tile::isSizeExact);
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}

	protected Tile readTile(int id, ByteBuffer buffer) {
		return switch (KnownTiles.values()[id]) {
			case CLOSED -> new ClosedTile(buffer);
		};
	}
}
