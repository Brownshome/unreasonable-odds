package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.time.Instant;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.collision.CollisionDetector;
import brownshome.unreasonableodds.components.CollisionShape;
import brownshome.unreasonableodds.components.*;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

/**
 * The main protagonist of the game
 */
public abstract class Character extends Entity implements Positioned, Collidable {
	/**
	 * The radius of the collision shape for character objects
	 */
	protected static final double CHARACTER_RADIUS = 0.05;
	protected static final double CHARACTER_ACCELERATION = 5.0;
	protected static final double CHARACTER_MAX_SPEED = 0.5;

	private final Position position;
	private final Vec2 velocity;
	private final CollisionShape collisionShape;

	/**
	 * Creates a character
	 * @param position the position of this character
	 * @param collisionShape the collision shape to use
	 */
	protected Character(Position position, Vec2 velocity, CollisionShape collisionShape) {
		this.position = position;
		this.collisionShape = collisionShape;
		this.velocity = velocity;
	}

	/**
	 * Creates a character with the default circle collision shape
	 * @param position the position of this character
	 */
	protected Character(Position position, Vec2 velocity) {
		this(position, velocity, makeCollisionShape(position));
	}

	private static CollisionShape makeCollisionShape(Position position) {
		return new CircleCollisionShape(position.position(), CHARACTER_RADIUS);
	}

	/**
	 * The position of this character
	 * @return the position
	 */
	public final Position position() {
		return position;
	}

	public final Vec2 velocity() {
		return velocity;
	}

	@Override
	public final CollisionShape collisionShape() {
		return collisionShape;
	}

	/**
	 * The view and actions that can be taken with respect to this character, only one action method must be called
	 */
	public class Actions {
		private final Universe.UniverseStep step;

		private Character nextCharacter = null;
		private boolean actionTaken = false;

		/**
		 * Creates a new actions object for this player
		 * @param step the contextual step that these actions will take place in
		 */
		protected Actions(Universe.UniverseStep step) {
			assert step != null;

			this.step = step;
		}

		/**
		 * Sets the final outcome of these actions. This must be called once and only once on this object
		 * @param character the final character outcome, this can be null if no character should exist in the new
		 *                  universe
		 */
		protected final void setNext(Character character) {
			assert !actionTaken;
			actionTaken = true;
			nextCharacter = character;
		}

		/**
		 * Jumps the character out of this universe.
		 */
		protected final void jumpOutOfUniverse() {
			rules().entities().createJumpScar(position().position(), step.rules().jumpScarDuration()).addToBuilder(step.builder());
			setNext(null);
		}

		/**
		 * Finishes moves for this step
		 * @param movementDirection the direction to move. If the length of this vector is greater than 1.0 it will be normalized
		 */
		public final void finaliseMove(Vec2 movementDirection) {
			double l = movementDirection.lengthSq();

			if (l > 1.0) {
				var tmp = movementDirection.copy();
				tmp.normalize();
				movementDirection = tmp;
			} else if (l == 0.0) {
				double v = velocity.lengthSq();

				double thisFrameDeltaV = CHARACTER_ACCELERATION * step.seconds();
				if (v <= thisFrameDeltaV * thisFrameDeltaV) {
					setNext(withVelocity(Vec2.ZERO));
					return;
				} else {
					var tmp = velocity.copy();
					tmp.normalize();
					tmp.scale(-1.0);
					movementDirection = tmp;
				}
			}

			var newVelocity = velocity.copy();
			newVelocity.scaleAdd(movementDirection, step.seconds() * CHARACTER_ACCELERATION);
			double lengthSquared = newVelocity.lengthSq();

			// Limit max speed
			if (lengthSquared > CHARACTER_MAX_SPEED * CHARACTER_MAX_SPEED) {
				newVelocity.scale(CHARACTER_MAX_SPEED / Math.sqrt(lengthSquared));
			}

			setNext(withVelocity(newVelocity));
		}

		/**
		 * Gets the character to be added to the next universe
		 * @return the character to add to the next universe
		 */
		protected final Character next() {
			return nextCharacter;
		}

		/**
		 * Returns the step object
		 * @return step
		 */
		protected final Universe.UniverseStep step() {
			return step;
		}

		/**
		 * The universe this action will take place in
		 * @return the universe
		 */
		protected final Universe universe() {
			return step.universe();
		}

		/**
		 * The multiverse
		 * @return the multiverse
		 */
		protected final Multiverse multiverse() {
			return step.multiverse();
		}

		/**
		 * The rules of the game
		 * @return the rules
		 */
		public final Rules rules() {
			return step.rules();
		}

		/**
		 * The current time in this universe
		 * @return the time
		 */
		public final Instant now() {
			return universe().now();
		}
	}

	protected Actions createActions(Universe.UniverseStep step) {
		return new Actions(step);
	}

	@Override
	protected Entity nextEntity(Universe.UniverseStep step) {
		var preMoveEntity = createActions(step).next();

		if (preMoveEntity == null) {
			return null;
		}

		MVec2 move = preMoveEntity.velocity.copy();
		move.scale(step.seconds());

		MVec2 velocity = preMoveEntity.velocity.copy();

		// Project away components for each object we hit
		while (move.lengthSq() != 0.0) {
			var callback = new CollisionDetector.SweptCollisionCallback() {
				CollisionShape.SweptCollision firstCollision = null;

				@Override
				public void call(CollisionShape.SweptCollision sweptCollision, Collidable collidable) {
					if (firstCollision == null || firstCollision.sweep() < sweptCollision.sweep()) {
						firstCollision = sweptCollision;
					}
				}
			};

			step.universe().collisionDetector().forEachCollidingShapeSwept(collisionShape, move, callback);

			if (callback.firstCollision == null) {
				break;
			}

			var normal = callback.firstCollision.normal();
			velocity.scaleAdd(normal, -normal.dot(velocity));

			move.set(velocity);
			move.scale(step.seconds());
		}

		move.add(preMoveEntity.position.position());
		return preMoveEntity.withPosition(new Position(move, preMoveEntity.position.orientation())).withVelocity(velocity);
	}

	/**
	 * Creates a new character identical to this one, but with a given velocity
	 * @param velocity the velocity
	 * @return a new character
	 */
	protected abstract Character withVelocity(Vec2 velocity);

	protected abstract Character withPosition(Position position);
}
