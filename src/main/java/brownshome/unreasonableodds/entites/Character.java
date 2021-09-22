package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.*;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * The main protagonist of the game
 */
public abstract class Character extends Entity {
	protected Character(Vec2 position, Rot2 orientation) {
		super(position, orientation);
	}

	/**
	 * The view and actions that can be taken with respect to this character
	 */
	public class Actions {
		private Universe.UniverseStep step;

		protected Actions(Universe.UniverseStep step) {
			assert step != null;

			this.step = step;
		}

		/**
		 * Jumps the character out of this universe. This must not be called more than once
		 */
		protected final void jumpOutOfUniverse() {
			step.addSteppable(new JumpScar(position(), step.rules().jumpScarDuration()));
		}

		public final void endStep() {
			Character.super.step(step);
		}

		/**
		 * Returns the step object
		 * @return step
		 */
		protected final Universe.UniverseStep step() {
			return step;
		}

		protected final Universe universe() {
			return step.universe();
		}

		protected final Multiverse multiverse() {
			return step.multiverse();
		}

		public final Rules rules() {
			return step.rules();
		}
	}
}
