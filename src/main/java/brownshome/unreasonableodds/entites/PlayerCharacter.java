package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;

/**
 * A version of the protagonist of the game that is currently being controlled by the
 * player.
 */
public class PlayerCharacter extends Character {
	private final Player player;
	private final Duration timeTravelEnergy;

	protected PlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		super(position);

		this.player = player;
		this.timeTravelEnergy = timeTravelEnergy;
	}

	public static PlayerCharacter createCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return new PlayerCharacter(position, player, timeTravelEnergy);
	}

	public Player player() {
		return player;
	}

	public Duration timeTravelEnergy() {
		return timeTravelEnergy;
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
			var newUniverseStep = step()
					.timeTravel(instant)
					.step(step().multiverseStep());

			withTimeTravelEnergy(timeTravelEnergy.minus(distance))
					.step(newUniverseStep);

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

		/**
		 * The earliest time that can be travelled to
		 * @return the early limit of travel
		 */
		public Instant earliestTimeTravelLocation() {
			var limit = universe().now().minus(timeTravelEnergy);

			return limit.isBefore(universe().beginning())
					? universe().beginning()
					: limit;
		}

		/**
		 * Gets the set of universes that can be visited
		 * @return the set of reachable universes
		 */
		public Set<Universe> reachableUniverses() {
			long jumps = timeTravelEnergy.dividedBy(rules().timePerUniverseJump());

			if (jumps > Integer.MAX_VALUE) {
				jumps = Integer.MAX_VALUE;
			}

			return multiverse().reachableUniverses(universe(), (int) jumps);
		}
	}

	@Override
	protected PlayerCharacter nextEntity(Universe.UniverseStep step) {
		var actions = new PlayerActions(step);
		player.performActions(actions);
		var next = (PlayerCharacter) actions.next();

		if (next == null) {
			return null;
		}

		double nanosGained = step.stepSize().toNanos() * step.rules().energyGainRate();
		return next.withTimeTravelEnergy(next.timeTravelEnergy.plus(Duration.ofNanos((long) nanosGained)));
	}

	@Override
	public HistoricalCharacter createHistoricalEntity() {
		return createHistoricalCharacter();
	}

	protected PlayerCharacter withTimeTravelEnergy(Duration energy) {
		return new PlayerCharacter(position(), player, energy);
	}

	@Override
	protected PlayerCharacter withPosition(Position position) {
		return new PlayerCharacter(position, player, timeTravelEnergy);
	}

	protected HistoricalCharacter createHistoricalCharacter() {
		return new HistoricalCharacter(position());
	}
}
