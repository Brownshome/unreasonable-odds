package brownshome.unreasonableodds.entites;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.packets.converters.DurationConverter;
import brownshome.unreasonableodds.player.*;
import brownshome.unreasonableodds.session.*;
import brownshome.vecmath.Vec2;

/**
 * A version of the protagonist of the game that is currently being controlled by the
 * player.
 */
public class PlayerCharacter extends Character {
	/**
	 * Holds networking information about the player character
	 */
	private final Id playerId;
	private final Duration timeTravelEnergy;

	protected PlayerCharacter(Position position, Vec2 velocity, Id playerId, Duration timeTravelEnergy) {
		super(position, velocity);

		this.playerId = playerId;
		this.timeTravelEnergy = timeTravelEnergy;
	}

	@Override
	public final int id() {
		return KnownEntities.PLAYER_CHARACTER.id();
	}

	public final Id playerId() {
		return playerId;
	}

	public final Duration timeTravelEnergy() {
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
					.stepNew(step().multiverseStep());

			withTimeTravelEnergy(timeTravelEnergy.minus(distance))
					.step(newUniverseStep);

			step().multiverseStep().addUniverse(newUniverseStep.builder().build());

			jumpOutOfUniverse();
		}

		/**
		 * Travel to a specified universe at the current time.
		 * Universe must be one of the universes returned by {@link #leftJumpId()} or {@link #rightJumpId()}.
		 * @param universe the universe to travel to
		 */
		public void jumpUniverse(Id universe) {
			assert canJump();
			assert universe.equals(leftJumpId()) || universe.equals(rightJumpId());

			var newEntity = withTimeTravelEnergy(timeTravelEnergy.minus(rules().timePerUniverseJump()));

			step().multiverseStep().stepInUniverse(universe, newEntity);
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

		public boolean canJump() {
			return timeTravelEnergy.compareTo(rules().timePerUniverseJump()) >= 0;
		}

		public Id leftJumpId() {
			if (!canJump()) {
				return null;
			}

			var ids = multiverse().allUniverseIds();
			int index = ids.indexOf(universe().id());

			assert index >= 0;

			return index - 1 >= 0 ? ids.get(index - 1) : null;
		}

		public Id rightJumpId() {
			if (!canJump()) {
				return null;
			}

			var ids = multiverse().allUniverseIds();
			int index = ids.indexOf(universe().id());

			assert index >= 0;

			return index + 1 < ids.size() ? ids.get(index + 1) : null;
		}
	}

	@Override
	protected Actions createActions(Universe.UniverseStep step) {
		var player = (ControllingPlayer) step.multiverse().session().player(playerId);

		var actions = new PlayerActions(step);
		player.controller().performActions(actions);
		return actions;
	}

	@Override
	protected PlayerCharacter nextEntity(Universe.UniverseStep step) {
		var player = step.multiverse().session().player(playerId);

		if (player instanceof ImportedGamePlayer imported) {
			imported.pushUniverse(step.universe());

			// If there is no step then keep the same entity
			return imported.step(step) ? null : this;
		} else {
			var next = (PlayerCharacter) super.nextEntity(step);

			if (next == null) {
				return null;
			}

			double nanosGained = step.stepSize().toNanos() * step.rules().energyGainRate();
			return next.withTimeTravelEnergy(next.timeTravelEnergy.plus(Duration.ofNanos((long) nanosGained)));
		}
	}

	@Override
	public HistoricalCharacter createHistoricalEntity(Rules rules) {
		return rules.entities().createHistoricalCharacter(position(), velocity());
	}

	protected PlayerCharacter withTimeTravelEnergy(Duration energy) {
		return new PlayerCharacter(position(), velocity(), playerId, energy);
	}

	@Override
	protected PlayerCharacter withPosition(Position position) {
		return new PlayerCharacter(position, velocity(), playerId, timeTravelEnergy);
	}

	@Override
	protected PlayerCharacter withVelocity(Vec2 velocity) {
		return new PlayerCharacter(position(), velocity, playerId, timeTravelEnergy);
	}

	public PlayerCharacter(ByteBuffer buffer) {
		super(buffer);

		var session = NetworkGameSession.get();
		this.playerId = session.player(new Id(buffer)).id();
		this.timeTravelEnergy = DurationConverter.INSTANCE.read(buffer);
	}

	@Override
	public void write(ByteBuffer buffer) {
		super.write(buffer);

		playerId.write(buffer);
		DurationConverter.INSTANCE.write(buffer, timeTravelEnergy);
	}

	@Override
	public int size() {
		return super.size() + playerId.size() + DurationConverter.INSTANCE.size(timeTravelEnergy);
	}

	@Override
	public boolean isSizeExact() {
		return super.isSizeExact() && playerId.isSizeExact() && DurationConverter.INSTANCE.isSizeExact(timeTravelEnergy);
	}

	@Override
	public boolean isSizeConstant() {
		return super.isSizeConstant() && playerId.isSizeConstant() && DurationConverter.INSTANCE.isSizeConstant();
	}
}
