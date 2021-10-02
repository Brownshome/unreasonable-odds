package brownshome.unreasonableodds;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.entites.tile.Tile;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * A collection of rules for the game
 */
public class Rules {
	private final Instant epoch = Instant.now();

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

	public Multiverse createMultiverse(Collection<Player> players) {
		return createMultiverse(players, new Random());
	}

	protected Duration initialJumpEnergy() {
		return Duration.ZERO;
	}

	public MainFloor createFloor() {
		return new MainFloor(new Tile[] {
				Tile.makeTile(Vec2.of(0.4, 0.4), Vec2.of(0.6, 0.6))
		});
	}

	public Multiverse createMultiverse(Collection<Player> players, Random random) {
		var initialEntities = new ArrayList<Entity>();

		for (var player : players) {
			initialEntities.add(createPlayerCharacter(createSpawnPosition(random), player, initialJumpEnergy()));
		}

		initialEntities.add(createFloor());

		return createMultiverse(createUniverse(initialEntities));
	}

	protected Multiverse createMultiverse(Universe baseUniverse) {
		return Multiverse.createMultiverse(this, List.of(baseUniverse));
	}

	protected Universe.Builder universeBuilder() {
		return Universe.createEmptyUniverse(epoch()).builder(Duration.ZERO);
	}

	protected final Universe createUniverse(List<Entity> initialEntities) {
		var builder = universeBuilder();

		for (Entity e : initialEntities) {
			e.addToBuilder(builder);
		}

		return builder.build();
	}

	/**
	 * The time the multiverse will have been created at
	 * @return the time of creation
	 */
	public Instant epoch() {
		return epoch;
	}

	protected PlayerCharacter createPlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return PlayerCharacter.createCharacter(position, Vec2.ZERO, player, timeTravelEnergy);
	}

	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.of(random.nextDouble(), random.nextDouble()), Rot2.IDENTITY);
	}

	public double energyGainRate() {
		return 1.0;
	}
}
