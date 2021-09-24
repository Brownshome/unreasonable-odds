package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.time.Instant;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.components.Positioned;
import brownshome.vecmath.Vec2;

/**
 * The main protagonist of the game
 */
public abstract class Character extends Entity implements Positioned {
	private final Position position;

	/**
	 * Creates a character
	 * @param position the position of this character
	 */
	protected Character(Position position) {
		this.position = position;
	}

	/**
	 * The view and actions that can be taken with respect to this character, only one action method must be called
	 */
	public class Actions {
		private final Universe.UniverseStep step;
		private boolean actionTaken = false;

		Actions(Universe.UniverseStep step) {
			assert step != null;

			this.step = step;
		}

		/**
		 * Jumps the character out of this universe.
		 */
		final void jumpOutOfUniverse() {
			checkFinalAction();

			step.addEntity(createJumpScar(position().position(), step.rules().jumpScarDuration()));
		}

		/**
		 * An assertion check that validates that only one final action is taken
		 */
		final void checkFinalAction() {
			assert !actionTaken;
			actionTaken = true;
		}

		/**
		 * States that there will be no further actions made this step
		 */
		public final void endStep() {
			checkFinalAction();

			Character.super.step(step);
		}

		/**
		 * Returns the step object
		 * @return step
		 */
		final Universe.UniverseStep step() {
			return step;
		}

		/**
		 * The universe this action will take place in
		 * @return the universe
		 */
		final Universe universe() {
			return step.universe();
		}

		/**
		 * The multiverse
		 * @return the multiverse
		 */
		final Multiverse multiverse() {
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

	protected JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new JumpScar(position, jumpScarDuration);
	}

	/**
	 * The position of this character
	 * @return the position
	 */
	public final Position position() {
		return position;
	}
}
