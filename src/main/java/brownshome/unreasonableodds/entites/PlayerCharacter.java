package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.Universe;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * A version of the protagonist of the game that is currently being controlled by the
 * player.
 */
public final class PlayerCharacter extends Character {
	private final Player player;
	private final Duration timeTravelEnergy;

	private PlayerCharacter(Player player, Duration timeTravelEnergy, Vec2 position, Rot2 orientation) {
		super(position, orientation);
		this.player = player;
		this.timeTravelEnergy = timeTravelEnergy;
	}

	private PlayerCharacter withTimeTravelEnergy(Duration energy) {
		return new PlayerCharacter(player, energy, position(), orientation());
	}

	/**
	 * The view and actions that the player can take with respect to this controllable character.
	 */
	public final class PlayerActions extends Actions {
		private PlayerActions(Universe.UniverseStep step) {
			super(step);
		}

		/**
		 * Time travel to a specified time, creating a new universe there.
		 * The instant must be before {@link Universe#now()} and the same or after {@link #earliestTimeTravelLocation()}
		 * @param instant the time to travel to
		 */
		public void timeTravel(Instant instant) {
			assert instant.isBefore(universe().now()) && !earliestTimeTravelLocation().isAfter(instant);

			var distance = Duration.between(instant, universe().now());
			withTimeTravelEnergy(timeTravelEnergy.minus(distance)).step(step().timeTravel(instant));
			jumpOutOfUniverse();
		}

		/**
		 * Travel to a specified universe at the current time.
		 * Universe must be one of the universes returned by {@link #reachableUniverses()}.
		 * @param universe the universe to travel to
		 */
		public void jumpUniverse(Universe universe) {
			assert reachableUniverses().contains(universe) && timeTravelEnergy.compareTo(rules().timePerUniverseJump()) >= 0;

			step().multiverseStep().stepInUniverse(universe, withTimeTravelEnergy(timeTravelEnergy.minus(rules().timePerUniverseJump())));
			jumpOutOfUniverse();
		}

		public Instant earliestTimeTravelLocation() {
			var limit = universe().now().minus(timeTravelEnergy);

			return limit.isBefore(universe().beginning())
					? universe().beginning()
					: limit;
		}

		public Set<Universe> reachableUniverses() {
			long jumps = timeTravelEnergy.dividedBy(rules().timePerUniverseJump());

			if (jumps > Integer.MAX_VALUE) {
				jumps = Integer.MAX_VALUE;
			}

			return multiverse().reachableUniverses(universe(), (int) jumps);
		}
	}

	@Override
	public void step(Universe.UniverseStep step) {
		player.performActions(new PlayerActions(step));
	}
}
