package brownshome.unreasonableodds;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.netcode.NetworkUtils;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.generation.FloorTileGenerator;
import brownshome.unreasonableodds.generation.TileType;
import brownshome.unreasonableodds.tile.Tile;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * A collection of rules for the game
 */
public abstract class Rules {
	private final EntityFactory entityFactory;

	protected Rules(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public final EntityFactory entities() {
		return entityFactory;
	}

	/**
	 * The amount of time-energy taken to jump between two universes next to each-other
	 * @return the duration required
	 */
	public Duration timePerUniverseJump() {
		return Duration.ofSeconds(20);
	}

	/**
	 * How long a jump scar will remain in the game world before expiring
	 * @return the lifetime of a jump scar
	 */
	public Duration jumpScarDuration() {
		return Duration.ofSeconds(10);
	}

	protected Duration initialJumpEnergy() {
		return Duration.ZERO;
	}

	public double energyGainRate() {
		return 1.0;
	}

	public Instant gameStartTime() {
		return Instant.now().plusSeconds(5);
	}

	// *************** GENERATION ***************** //

	protected abstract TileType[][] createArchetype();

	protected int generatorAreaSize() {
		return 2;
	}

	protected abstract TileType[][] createInitialGrid();

	protected StaticMap generateStaticMap(Random random) {
		var generator = new FloorTileGenerator(generatorAreaSize(), createArchetype());
		var grid = createInitialGrid();

		generator.generateGrid(grid, random);

		List<Tile> tiles = new ArrayList<>();
		for (int y = 0; y < grid.length; y++) for (int x = 0; x < grid[y].length; x++) {
			var tile = grid[y][x].createTile(x, y);

			if (tile != null) {
				tiles.add(tile);
			}
		}

		return entities().createStaticMap(tiles);
	}

	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.of(random.nextDouble(), random.nextDouble()), Rot2.IDENTITY);
	}

	// *************** CREATE MULTIVERSE ***************** //

	public Multiverse createMultiverse(Collection<Player> players) {
		return createMultiverse(players, null, gameStartTime(), new Random());
	}

	public Multiverse createMultiverse(Collection<Player> players, MultiverseNetwork network, Instant epoch) {
		return createMultiverse(players, network, epoch, new Random());
	}

	protected Multiverse createMultiverse(Collection<Player> players, MultiverseNetwork network, Instant epoch, Random random) {
		var initialEntities = new ArrayList<Entity>();

		for (var player : players) {
			initialEntities.add(entities().createPlayerCharacter(createSpawnPosition(random), Vec2.ZERO, player, initialJumpEnergy()));
		}

		initialEntities.add(generateStaticMap(random));

		return createMultiverse(List.of(createUniverse(initialEntities, epoch)), network);
	}

	protected Multiverse createMultiverse(List<Universe> universes, MultiverseNetwork network) {
		return new Multiverse(this, universes, network);
	}

	protected Universe createUniverse(List<Entity> initialEntities, Instant epoch) {
		var builder = universeBuilder(epoch);

		for (Entity e : initialEntities) {
			e.addToBuilder(builder);
		}

		return builder.build();
	}

	protected Universe.Builder universeBuilder(Instant epoch) {
		return Universe.createEmptyUniverse(epoch).builder(Duration.ZERO);
	}

	// *************** NETWORKING ***************** //

	public void write(ByteBuffer buffer) {
		NetworkUtils.writeString(buffer, networkClassName());
	}

	/**
	 * Returns the amount of data required to represent this rules object
	 * @return the size in bytes (this may be an overestimate)
	 */
	public int size() {
		return NetworkUtils.calculateSize(networkClassName()).size();
	}

	public String networkClassName() {
		return getClass().getName();
	}
}
