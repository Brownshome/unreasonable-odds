package brownshome.unreasonableodds;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import brownshome.unreasonableodds.entites.Steppable;
import brownshome.unreasonableodds.history.History;

/**
 * A single universe in the multiverse. This is an immutable object
 */
public final class Universe {
	private final Instant now;

	private final List<Steppable> steppables;
	private final History history;

	private Universe(Universe parent, Duration stepSize, ArrayList<Steppable> steppables) {
		this.now = parent.now.plus(stepSize);
		this.history = parent.history.expandHistory(this);
		this.steppables = steppables;
	}

	public History history() {
		return history;
	}

	public abstract class UniverseStep {
		private final Multiverse.MultiverseStep multiverseStep;

		public UniverseStep(Multiverse.MultiverseStep multiverseStep) {
			this.multiverseStep = multiverseStep;
		}

		public final Duration stepSize() {
			return multiverseStep.stepSize();
		}

		public final Universe universe() {
			return Universe.this;
		}

		public final Multiverse multiverse() {
			return multiverseStep.multiverse();
		}

		public final Multiverse.MultiverseStep multiverseStep() {
			return multiverseStep;
		}

		public void addUniverse(Universe universe) {
			multiverseStep.addUniverse(universe);
		}

		public final UniverseStep withHandler(Consumer<? super Steppable> c) {
			return new UniverseStep(multiverseStep) {
				@Override
				public void addSteppable(Steppable steppable) {
					c.accept(steppable);
				}
			};
		}

		public final UniverseStep withUniverseHandler(Consumer<? super Universe> c) {
			var parent = UniverseStep.this;

			return new UniverseStep(multiverseStep) {
				@Override
				public void addSteppable(Steppable steppable) {
					parent.addSteppable(steppable);
				}

				@Override
				public void addUniverse(Universe universe) {
					c.accept(universe);
				}
			};
		}

		/**
		 * Creates a new universe by time-travelling back into the past
		 * @param instant the instant to travel back to
		 * @return a step object than can be used to interact with the new universe
		 */
		public final UniverseStep timeTravel(Instant instant) {
			return history().getUniverse(instant).step(multiverseStep);
		}

		public abstract void addSteppable(Steppable steppable);

		public final Rules rules() {
			return multiverseStep.rules();
		}
	}

	public UniverseStep step(Multiverse.MultiverseStep multiverseStep) {
		var newSteppables = new ArrayList<Steppable>();

		var step = new UniverseStep(multiverseStep) {
			@Override
			public void addSteppable(Steppable steppable) {
				newSteppables.add(steppable);
			}
		};

		multiverseStep.addUniverse(new Universe(this, multiverseStep.stepSize(), newSteppables));

		for (var s : steppables) {
			s.step(step);
		}

		return step;
	}

	public Instant now() {
		return now;
	}

	public Instant beginning() {
		return Instant.EPOCH;
	}

	public static Universe interpolate(Universe past, double t, Universe future) {
		// How the hell to do this?
		return past;
	}
}
