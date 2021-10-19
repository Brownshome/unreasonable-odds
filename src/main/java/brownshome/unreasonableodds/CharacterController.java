package brownshome.unreasonableodds;

import brownshome.unreasonableodds.entites.PlayerCharacter;

/**
 * A functional interface used to pass player actions to the game
 */
@FunctionalInterface
public interface CharacterController {
	/**
	 * Performs actions
	 * @param actions an object giving the player information and allowing them to perform actions. One and only one
	 *                action may be performed on this object.
	 */
	void performActions(PlayerCharacter.PlayerActions actions);
}
