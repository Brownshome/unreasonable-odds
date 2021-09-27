package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.time.Instant;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.CollisionShape;
import brownshome.unreasonableodds.components.*;
import brownshome.vecmath.Vec2;

/**
 * The main protagonist of the game
 */
public abstract class Character extends Entity implements Positioned, Collidable {
	/**
	 * The radius of the collision shape for character objects
	 */
	protected static final double CHARACTER_RADIUS = 0.1;

	private final Position position;
	private final CollisionShape collisionShape;

	/**
	 * Creates a character
	 * @param position the position of this character
	 * @param collisionShape the collision shape to use
	 */
	protected Character(Position position, CollisionShape collisionShape) {
		this.position = position;
		this.collisionShape = collisionShape;
	}

	/**
	 * Creates a character with the default circle collision shape
	 * @param position the position of this character
	 */
	protected Character(Position position) {
		this(position, new CircleCollisionShape(position.position(), CHARACTER_RADIUS));
	}

	/**
	 * The position of this character
	 * @return the position
	 */
	public final Position position() {
		return position;
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
			createJumpScar(position().position(), step.rules().jumpScarDuration()).addToBuilder(step.builder());
			setNext(null);
		}

		/**
		 * Finishes moves for this step
		 * @param movementDirection the direction to move. If the length of this vector is greater than 1.0 it will be normalized
		 */
		public final void finaliseMove(Vec2 movementDirection) {
			double l = movementDirection.lengthSq();
			if (l == 0) {
				setNext(Character.this);
			} else {
				if (l > 1.0) {
					var tmp = movementDirection.copy();
					tmp.normalize();
					movementDirection = tmp;
				}

				var newPosition = position.position().copy();
				newPosition.scaleAdd(movementDirection, step.seconds());

				setNext(withPosition(new Position(newPosition, position.orientation())));
			}
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

	/**
	 * Creates a jump scar at the given position and with the given duration remaining
	 * @param position the position to create the jump scar
	 * @param jumpScarDuration the duration remaining of the jump scar
	 * @return the jump scar
	 */
	protected JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new JumpScar(position, jumpScarDuration);
	}

	/**
	 * Creates a new character identical to this one, but with a given position
	 * @param position the position
	 * @return a new character
	 */
	protected abstract Character withPosition(Position position);

	@Override
	public void addToBuilder(Universe.Builder builder) {
		builder.addCollision(this);

		super.addToBuilder(builder);
	}
}
