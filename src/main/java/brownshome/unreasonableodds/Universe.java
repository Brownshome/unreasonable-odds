package brownshome.unreasonableodds;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.netcode.annotation.converter.Converter;
import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.collision.CollisionDetector;
import brownshome.unreasonableodds.components.Collidable;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.history.BranchRecord;
import brownshome.unreasonableodds.history.History;
import brownshome.unreasonableodds.packets.converters.EntityConverter;
import brownshome.unreasonableodds.packets.converters.InstantConverter;
import brownshome.unreasonableodds.player.ExportedGamePlayer;
import brownshome.unreasonableodds.session.Id;

/**
 * A single universe in the multiverse. This is an immutable object.
 */
public class Universe implements Networkable {
	private final Instant now;
	private final List<Entity> entities;
	private final CollisionDetector collisionDetector;

	private final History history;
	private final BranchRecord branchRecord;
	private final Id id;

	protected Universe(Id id, Instant now, List<Entity> entities, History previousHistory, BranchRecord branchRecord, CollisionDetector collisionDetector) {
		this.id = id;
		this.now = now;
		this.entities = entities;
		this.history = previousHistory.expandHistory(this);
		this.branchRecord = branchRecord;
		this.collisionDetector = collisionDetector;
	}

	public static Universe createEmptyUniverse(Id id, Instant epoch) {
		return new Universe(id, epoch, Collections.emptyList(), History.blankHistory(), BranchRecord.blankRecord(epoch), CollisionDetector.createDetector());
	}

	public final Id id() {
		return id;
	}

	/**
	 * Returns the history of this universe
	 * @return the history of this universe
	 */
	public final History history() {
		return history;
	}

	public final BranchRecord branchRecord() {
		return branchRecord;
	}

	public final CollisionDetector collisionDetector() {
		return collisionDetector;
	}

	public Universe createHistoricalUniverse(Rules rules) {
		Builder builder = newBranchBuilder();
		for (var e : entities) {
			var historical = e.createHistoricalEntity(rules);

			if (historical != null) {
				historical.addToBuilder(builder);
			}
		}

		return builder.build();
	}

	/**
	 * Creates a builder for a universe offset in time from this one by a given amount
	 *
	 * @param id the id of the new universe
	 * @param offset the offset duration
	 * @return a builder
	 */
	public final Builder builder(Id id, Duration offset) {
		return builder(id, now.plus(offset), branchRecord);
	}

	public final Builder newBranchBuilder() {
		return builder(id, now, branchRecord.newBranch(Duration.between(beginning(), now)));
	}

	protected Builder builder(Id id, Instant now, BranchRecord branchRecord) {
		return new Builder(now, branchRecord);
	}

	public class Builder {
		private final List<Entity> entities;
		private final Instant now;
		private final BranchRecord branchRecord;
		private final Set<Collidable> collidables;

		protected Builder(Instant now, BranchRecord branchRecord) {
			this.now = now;
			this.entities = new ArrayList<>();
			this.collidables = new HashSet<>();
			this.branchRecord = branchRecord;
		}

		/**
		 * Adds a new entity to this universe
		 * @param entity the entity to add
		 */
		public final void addEntity(Entity entity) {
			entities.add(entity);
		}

		public final void addCollision(Collidable c) {
			collidables.add(c);
		}

		/**
		 * The list of entities in the new universe
		 * @return the list of entities in the new universe
		 */
		protected final List<Entity> entities() {
			return entities;
		}

		protected final Instant now() {
			return now;
		}

		protected final BranchRecord branchRecord() {
			return branchRecord;
		}

		protected final CollisionDetector collisionDetector() {
			var old = new HashSet<>(collisionDetector.collidables());
			if (old.equals(collidables)) {
				return collisionDetector;
			} else {
				return CollisionDetector.createDetector(new ArrayList<>(collidables));
			}
		}

		/**
		 * Builds the universe
		 * @return the new universe
		 */
		public Universe build() {
			return new Universe(id, now, entities, history, branchRecord, collisionDetector());
		}
	}

	/**
	 * A single step of the universe
	 */
	public final class UniverseStep {
		private final Multiverse.MultiverseStep multiverseStep;
		private final Builder builder;

		private UniverseStep(Multiverse.MultiverseStep multiverseStep, Builder builder) {
			this.multiverseStep = multiverseStep;
			this.builder = builder;
		}

		private UniverseStep(Id id, Multiverse.MultiverseStep multiverseStep) {
			this(multiverseStep, Universe.this.builder(id, multiverseStep.stepSize()));
		}

		/**
		 * The length of this step
		 * @return a duration greater than zero
		 */
		public Duration stepSize() {
			return multiverseStep.stepSize();
		}

		/**
		 * The number of seconds in this step
		 * @return a double
		 */
		public double seconds() {
			return multiverseStep.seconds();
		}

		/**
		 * The universe this step is for
		 * @return the universe
		 */
		public Universe universe() {
			return Universe.this;
		}

		/**
		 * The parent multiverse
		 * @return the multiverse
		 */
		public Multiverse multiverse() {
			return multiverseStep.multiverse();
		}

		/**
		 * A reference to the parent multiverse step that initiated this one
		 * @return the parent multiverse step
		 */
		public Multiverse.MultiverseStep multiverseStep() {
			return multiverseStep;
		}

		/**
		 * Creates a new universe by time-travelling back into the past
		 * @param instant the instant to travel back to
		 * @return the universe
		 */
		public Universe timeTravel(Instant instant) {
			return history().getUniverse(instant, multiverse());
		}

		/**
		 * The rules of this game
		 * @return the rules
		 */
		public Rules rules() {
			return multiverseStep.rules();
		}

		public Builder builder() {
			return builder;
		}
	}

	/**
	 * Steps this universe forward within the context of a parent multiverse step
	 * @param multiverseStep the step
	 * @return a universe step that can be used to further interact with the newly created universe
	 */
	public UniverseStep step(Multiverse.MultiverseStep multiverseStep) {
		return step(id(), multiverseStep);
	}

	/**
	 * Steps this universe forward within the context of a parent multiverse step, allocating it a new ID
	 * @param multiverseStep the step
	 * @return a universe step that can be used to further interact with the newly created universe
	 */
	public UniverseStep stepNew(Multiverse.MultiverseStep multiverseStep) {
		return step(multiverseStep.multiverse().allocateUniverseId(), multiverseStep);
	}

	public List<Entity> exportedStep(Multiverse.MultiverseStep multiverseStep, ExportedGamePlayer player) {
		var step = new UniverseStep(id, multiverseStep);

		for (var s : entities) {
			if (s instanceof PlayerCharacter playerCharacter && playerCharacter.playerId().equals(player.id())) {
				playerCharacter.step(step);
			}
		}

		return step.builder().entities();
	}

	/**
	 * Steps this universe forward within the context of a parent multiverse step
	 * @param id the ID of the new universe
	 * @param multiverseStep the step
	 * @return a universe step that can be used to further interact with the newly created universe
	 */
	protected UniverseStep step(Id id, Multiverse.MultiverseStep multiverseStep) {
		var step = new UniverseStep(id, multiverseStep);

		for (var s : entities) {
			s.step(step);
		}

		return step;
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

	public final List<Entity> entities() {
		return entities;
	}

	@Override
	public String toString() {
		return "Universe (%d entities) @ %s".formatted(entities.size(), now);
	}

	@Override
	public void write(ByteBuffer buffer) {
		write(buffer, EntityConverter.CompressMap.INSTANCE);
	}

	public void writeWithMap(ByteBuffer buffer) {
		write(buffer, EntityConverter.INSTANCE);
	}

	protected void write(ByteBuffer buffer, Converter<Entity> entityConverter) {
		id.write(buffer);
		InstantConverter.INSTANCE.write(buffer, now);

		int length = entities.size();
		assert length < (1 << Short.SIZE);
		buffer.putShort((short) length);

		for (var entity : entities) {
			entityConverter.write(buffer, entity);
		}
	}

	@Override
	public int size() {
		int size = 0;

		size += id.size();
		size += InstantConverter.INSTANCE.size(now);
		size += Short.BYTES;

		for (var entity : entities) {
			size += entity.size();
		}

		return size;
	}

	@Override
	public boolean isSizeExact() {
		return false;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
