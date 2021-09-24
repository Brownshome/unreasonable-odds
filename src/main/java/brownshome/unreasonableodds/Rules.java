package brownshome.unreasonableodds;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
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

	public Multiverse createMultiverse(Collection<Player> players, Random random) {
		var initialPlayers = players.stream().<Entity>map(p -> createPlayerCharacter(createSpawnPosition(random), p, initialJumpEnergy())).toList();

		return createMultiverse(createUniverse(initialPlayers));
	}

	protected Multiverse createMultiverse(Universe baseUniverse) {
		return Multiverse.createMultiverse(this, List.of(baseUniverse));
	}

	protected Universe createUniverse(List<Entity> initialEntities) {
		return Universe.createUniverse(epoch(), initialEntities);
	}

	/**
	 * The time the multiverse will have been created at
	 * @return the time of creation
	 */
	public Instant epoch() {
		return epoch;
	}

	protected PlayerCharacter createPlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return PlayerCharacter.createCharacter(position, player, timeTravelEnergy);
	}

	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.of(random.nextDouble(), random.nextDouble()), Rot2.IDENTITY);
	}
}
