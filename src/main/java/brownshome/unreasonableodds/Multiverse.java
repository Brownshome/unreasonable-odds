package brownshome.unreasonableodds;

import java.time.Duration;
import java.util.*;

import brownshome.unreasonableodds.entites.Entity;

/**
 * The base class for the game, representing a collection of universes
 */
public class Multiverse {
	private final Rules rules;
	private final MultiverseNetwork network;

	private List<Universe> universes;

	protected Multiverse(Rules rules, List<Universe> universes, MultiverseNetwork network) {
		assert rules != null;
		assert universes != null;

		this.rules = rules;
		this.universes = universes;
		this.network = network;
	}

	public final List<Universe> universes() {
		return universes;
	}

	/**
	 * The rules of this game
	 * @return the rules
	 */
	public final Rules rules() {
		return rules;
	}

	protected final MultiverseNetwork network() {
		assert isNetworked();
		return network;
	}

	protected final boolean isNetworked() {
		return network != null;
	}

	/**
	 * A single step of the multiverse
	 */
	public abstract class MultiverseStep {
		private final Duration stepSize;

		private MultiverseStep(Duration stepSize) {
			this.stepSize = stepSize;
		}

		/**
		 * The length of this step
		 * @return a duration greater than zero
		 */
		public final Duration stepSize() {
			return stepSize;
		}

		/**
		 * The number of seconds in this step
		 * @return a double
		 */
		public final double seconds() {
			return stepSize.toNanos() / 1e9;
		}

		/**
		 * The parent multiverse
		 * @return the multiverse
		 */
		public final Multiverse multiverse() {
			return Multiverse.this;
		}

		/**
		 * The rules of this game
		 * @return the rules
		 */
		public final Rules rules() {
			return rules;
		}

		/**
		 * Adds a universe to this multiverse. The universe will not be stepped this step
		 * @param universe the universe to add
		 */
		public abstract void addUniverse(Universe universe);

		/**
		 * Steps a given entity into a universe
		 * @param universe the universe to interact with
		 * @param entity the entity to step into the universe
		 */
		public abstract void stepInUniverse(Universe universe, Entity entity);
	}

	/**
	 * Steps all universes within this multiverse forward
	 * @param stepSize the amount to step
	 */
	public final void step(Duration stepSize) {
		record ExternalSteps(Universe.UniverseStep step, List<Entity> entities) {
			ExternalSteps() {
				this(null, new ArrayList<>());
			}

			ExternalSteps(Universe.UniverseStep step) {
				this(step, Collections.emptyList());
			}

			ExternalSteps addStep(Universe.UniverseStep step) {
				assert this.step == null;

				for (var s : entities) {
					s.step(step);
				}

				return new ExternalSteps(step);
			}

			void addEntity(Entity s) {
				if (step != null) {
					s.step(step);
				} else {
					entities.add(s);
				}
			}
		}

		Map<Universe, ExternalSteps> externalSteps = new HashMap<>();

		var newUniverses = new ArrayList<Universe>();
		var step = new MultiverseStep(stepSize) {
			@Override
			public void addUniverse(Universe universe) {
				newUniverses.add(universe);
			}

			@Override
			public void stepInUniverse(Universe universe, Entity entity) {
				externalSteps.compute(universe, (u, external) -> {
					if (external == null) {
						external = new ExternalSteps();
					}

					external.addEntity(entity);

					return external;
				});
			}
		};

		for (var universe : universes) {
			var universeStep = universe.step(step);
			externalSteps.compute(universe, (u, external) -> external != null ? external.addStep(universeStep) : new ExternalSteps(universeStep));
		}

		for (var externalStep : externalSteps.values()) {
			var nextUniverse = externalStep.step.builder().build();
			if (nextUniverse != null) {
				newUniverses.add(nextUniverse);
			}
		}

		universes = newUniverses;
	}

	/**
	 * Returns the set of all reachable universes from the given on within a number of jumps
	 * @param universe the origin universe
	 * @param jumps the number of jumps
	 * @return the set of all reachable universes. This set will not contain the provided universe
	 */
	public final Set<Universe> reachableUniverses(Universe universe, int jumps) {
		if (jumps == 0) {
			return Collections.emptySet();
		}

		var result = new HashSet<>(universes);
		result.remove(universe);

		return result;
	}

	/**
	 * Steps a single universe without any interaction with any other universe. No attempt may be made to create more
	 * than a single universe and stepping into another universe is also not permitted.
	 * @param origin the universe to step
	 * @param stepSize the duration to step for
	 * @return the newly stepped universe
	 */
	public final Universe stepDisconnectedUniverse(Universe origin, Duration stepSize) {
		var disconnectedStep = new MultiverseStep(stepSize) {
			@Override
			public void addUniverse(Universe universe) {
				throw new UnsupportedOperationException("New universes cannot be created from a historical step");
			}

			@Override
			public void stepInUniverse(Universe universe, Entity entity) {
				throw new UnsupportedOperationException("New universes cannot be created from a historical step");
			}
		};

		var result = origin.step(disconnectedStep).builder().build();

		assert result != null : "At the time stepped to, the universe was destroyed";

		return result;
	}
}
