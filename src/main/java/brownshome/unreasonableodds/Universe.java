package brownshome.unreasonableodds;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.history.History;

/**
 * A single universe in the multiverse. This is an immutable object
 */
public final class Universe {
	private final Instant now;

	private final List<Entity<?>> entities;
	private final History history;

	private Universe(Universe parent, Duration stepSize, ArrayList<Entity<?>> entities) {
		this.now = parent.now.plus(stepSize);
		this.history = parent.history.expandHistory(this);
		this.entities = entities;
	}

	/**
	 * Returns the history of this universe
	 * @return the history of this universe
	 */
	public History history() {
		return history;
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
		public final UniverseStep withHandler(Consumer<? super Entity<?>> c) {
			return new UniverseStep(multiverseStep) {
				@Override
				public void addEntity(Entity<?> entity) {
					c.accept(entity);
				}
			};
		}

		/**
		 * Creates a new universe by time-travelling back into the past
		 * @param instant the instant to travel back to
		 * @return a step object than can be used to interact with the new universe
		 */
		public final UniverseStep timeTravel(Instant instant) {
			return history().getUniverse(instant, multiverse()).step(multiverseStep);
		}

		/**
		 * Adds a new entity to this universe, triggering any handlers registered on this step
		 * @param entity the entity to add
		 */
		public abstract void addEntity(Entity<?> entity);

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
		var newEntities = new ArrayList<Entity<?>>();

		var step = new UniverseStep(multiverseStep) {
			@Override
			public void addEntity(Entity<?> entity) {
				newEntities.add(entity);
			}
		};

		multiverseStep.addUniverse(new Universe(this, multiverseStep.stepSize(), newEntities));

		for (var s : entities) {
			s.step(step);
		}

		return step;
	}

	/**
	 * The time that is considered 'now' for this universe object. These times may or may not be related to real time
	 * in any way.
	 * @return now
	 */
	public Instant now() {
		return now;
	}

	/**
	 * The time that this universe was created. These times may or may not be related to real time
	 * in any way.
	 * @return the beginning of the universe
	 */
	public Instant beginning() {
		return Instant.EPOCH;
	}
}
