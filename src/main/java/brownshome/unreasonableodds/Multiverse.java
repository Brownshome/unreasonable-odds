package brownshome.unreasonableodds;

import java.time.Duration;
import java.util.*;

import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.entites.Steppable;

public final class Multiverse {
	private final Rules rules;

	private final List<PlayerCharacter> playerCharacters;
	private List<Universe> universes;

	Multiverse(Rules rules, List<PlayerCharacter> playerCharacters, List<Universe> universes) {
		this.rules = rules;
		this.playerCharacters = playerCharacters;
		this.universes = universes;
	}

	public abstract class MultiverseStep {
		private final Duration stepSize;

		private MultiverseStep(Duration stepSize) {
			this.stepSize = stepSize;
		}

		public final Duration stepSize() {
			return stepSize;
		}

		public final Multiverse multiverse() {
			return Multiverse.this;
		}

		public final Rules rules() {
			return rules;
		}

		public abstract void addUniverse(Universe universe);

		/**
		 * Gets a step object for the provided universe for interacting with it
		 * @param universe the universe to interact with
		 * @param steppable the object to step in the new universe
		 */
		public abstract void stepInUniverse(Universe universe, Steppable steppable);
	}

	public void step(Duration stepSize) {
		record ExternalSteps(Universe.UniverseStep step, List<Steppable> externalObjects) {
			ExternalSteps() {
				this(null, new ArrayList<>());
			}

			ExternalSteps(Universe.UniverseStep step) {
				this(step, Collections.emptyList());
			}

			ExternalSteps addStep(Universe.UniverseStep step) {
				assert this.step == null;

				for (var s : externalObjects) {
					s.step(step);
				}

				return new ExternalSteps(step);
			}

			void addSteppable(Steppable s) {
				if (step != null) {
					s.step(step);
				} else {
					externalObjects.add(s);
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
			public void stepInUniverse(Universe universe, Steppable steppable) {
				externalSteps.compute(universe, (u, external) -> {
					if (external == null) {
						external = new ExternalSteps();
					}

					external.addSteppable(steppable);

					return external;
				});
			}
		};

		for (var universe : universes) {
			var universeStep = universe.step(step);
			externalSteps.compute(universe, (u, external) -> external != null ? external.addStep(universeStep) : new ExternalSteps(universeStep));
		}

		universes = newUniverses;
	}

	public Rules rules() {
		return rules;
	}

	/**
	 * Returns the set of all reachable universes from the given on within a number of jumps
	 * @param universe the origin universe
	 * @param jumps the number of jumps
	 * @return the set of all reachable universes. This set will not contain the provided universe
	 */
	public Set<Universe> reachableUniverses(Universe universe, int jumps) {
		if (jumps == 0) {
			return Collections.emptySet();
		}

		var result = new HashSet<>(universes);
		result.remove(universe);

		return result;
	}
}
