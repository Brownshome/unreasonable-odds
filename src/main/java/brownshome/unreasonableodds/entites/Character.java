package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;

/**
 * The main protagonist of the game
 */
public abstract class Character<THIS extends Character<?>> extends Entity<THIS> {
	private final Position position;

	/**
	 * Creates a non-root character
	 * @param root the root
	 * @param position the position of this character
	 */
	protected Character(THIS root, Position position) {
		super(root);

		this.position = position;
	}

	/**
	 * Creates a root character
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

			JumpScar.create(position.position(), step);
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
	}

	/**
	 * The position of this character
	 * @return the position
	 */
	public Position position() {
		return position;
	}
}
