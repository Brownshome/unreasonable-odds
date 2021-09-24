package brownshome.unreasonableodds;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.history.History;

/**
 * A single universe in the multiverse. This is an immutable object
 */
public class Universe {
	private final Instant now;
	private final List<Entity> entities;
	private final History history;

	protected Universe(Instant now, List<Entity> entities, History previousHistory) {
		this.now = now;
		this.entities = entities;
		this.history = previousHistory.expandHistory(this);
	}

	public static Universe createUniverse(Instant epoch, List<Entity> entities) {
		return new Universe(epoch, entities, History.blankHistory());
	}

	/**
	 * Returns the history of this universe
	 * @return the history of this universe
	 */
	public final History history() {
		return history;
	}

	public Universe createHistoricalUniverse() {
		var newEntities = new ArrayList<Entity>(entities.size());
		for (var e : entities) {
			var historical = e.createHistoricalEntity();

			if (historical != null) {
				newEntities.add(historical);
			}
		}

		return createNextUniverse(now, newEntities);
	}

	/**
	 * A single step of the universe
	 */
	public abstract class UniverseStep {
		private final Multiverse.MultiverseStep multiverseStep;

		private UniverseStep(Multiverse.MultiverseStep multiverseStep) {
			this.multiverseStep = multiverseStep;
		}

		/**
		 * The length of this step
		 * @return a duration greater than zero
		 */
		public final Duration stepSize() {
			return multiverseStep.stepSize();
		}

		/**
		 * The number of seconds in this step
		 * @return a double
		 */
		public final double seconds() {
			return multiverseStep.seconds();
		}

		/**
		 * The universe this step is for
		 * @return the universe
		 */
		public final Universe universe() {
			return Universe.this;
		}

		/**
		 * The parent multiverse
		 * @return the multiverse
		 */
		public final Multiverse multiverse() {
			return multiverseStep.multiverse();
		}

		/**
		 * A reference to the parent multiverse step that initiated this one
		 * @return the parent multiverse step
		 */
		public final Multiverse.MultiverseStep multiverseStep() {
			return multiverseStep;
		}

		/**
		 * Makes a new step object with a handler that is called whenever an entity is added to this universe
		 * @param c the handler
		 * @return the new step object
		 */
		public final UniverseStep withHandler(Consumer<? super Entity> c) {
			return new UniverseStep(multiverseStep) {
				@Override
				public void addEntity(Entity entity) {
					c.accept(entity);
				}
			};
		}

		/**
		 * Creates a new universe by time-travelling back into the past
		 * @param instant the instant to travel back to
		 * @return the universe
		 */
		public final Universe timeTravel(Instant instant) {
			return history().getUniverse(instant, multiverse());
		}

		/**
		 * Adds a new entity to this universe, triggering any handlers registered on this step
		 * @param entity the entity to add
		 */
		public abstract void addEntity(Entity entity);

		/**
		 * The rules of this game
		 * @return the rules
		 */
		public final Rules rules() {
			return multiverseStep.rules();
		}
	}

	/**
	 * Steps this universe forward within the context of a parent multiverse step
	 * @param multiverseStep the step
	 * @return a universe step that can be used to further interact with the newly created universe
	 */
	public UniverseStep step(Multiverse.MultiverseStep multiverseStep) {
		var newEntities = new ArrayList<Entity>();
		var step = createUniverseStep(multiverseStep, newEntities::add);

		multiverseStep.addUniverse(createNextUniverse(now.plus(multiverseStep.stepSize()), newEntities));

		for (var s : entities) {
			s.step(step);
		}

		return step;
	}

	protected UniverseStep createUniverseStep(Multiverse.MultiverseStep multiverseStep, Consumer<Entity> newEntities) {
		return new UniverseStep(multiverseStep) {
			@Override
			public void addEntity(Entity entity) {
				newEntities.accept(entity);
			}
		};
	}

	protected Universe createNextUniverse(Instant now, List<Entity> newEntities) {
		return new Universe(now, newEntities, history);
	}

	/**
	 * The time that is considered <em>now</em> for this universe object. These times may or may not be related to real time
	 * in any way.
	 * @return now
	 */
	public final Instant now() {
		return now;
	}

	/**
	 * The time that this universe was created. These times may or may not be related to real time
	 * in any way.
	 * @return the beginning of the universe
	 */
	public final Instant beginning() {
		return history.beginning();
	}
}
