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
import brownshome.unreasonableodds.network.*;
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

	/**
	 *
	 * @param localPlayers players whose actions are controlled by this session, and are hosted in this session
	 * @param network the network information about this universe
	 * @param epoch the time that the multiverse comes into existence
	 * @param random a random number generator used to generate the universe
	 * @return a new multiverse
	 */
	public Multiverse createMultiverse(Collection<Player> localPlayers, MultiverseNetwork network, Instant epoch, Random random) {
		var map = generateStaticMap(random);
		var universes = new ArrayList<Universe>();
		int number = 0;

		for (var player : localPlayers) {
			var character = entities().createPlayerCharacter(createSpawnPosition(random), Vec2.ZERO, player, initialJumpEnergy());
			universes.add(createUniverse(new Universe.Id(network.address(), number++), List.of(map, character), epoch));
		}

		if (network != null) {
			for (var player : network.remotePlayers()) {
				var character = entities().createPlayerCharacter(createSpawnPosition(random), Vec2.ZERO, initialJumpEnergy(), player);
				universes.add(createUniverse(new Universe.Id(network.address(), number++), List.of(map, character), epoch));
			}
		}

		return createMultiverse(universes, network);
	}

	protected Multiverse createMultiverse(List<Universe> universes, MultiverseNetwork network) {
		return new Multiverse(this, universes, network);
	}

	protected Universe createUniverse(Universe.Id id, List<Entity> initialEntities, Instant epoch) {
		var builder = universeBuilder(id, epoch);

		for (Entity e : initialEntities) {
			e.addToBuilder(builder);
		}

		return builder.build();
	}

	protected Universe.Builder universeBuilder(Universe.Id id, Instant epoch) {
		return Universe.createEmptyUniverse(id, epoch).builder(Duration.ZERO);
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
