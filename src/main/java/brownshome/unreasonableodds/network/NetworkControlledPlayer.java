package brownshome.unreasonableodds.network;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.vecmath.Vec2;

/**
 * A player that is controlled by actions of a networked counterpart
 */
public class NetworkControlledPlayer implements Player {
	@Override
	public void performActions(PlayerCharacter.PlayerActions actions) {
		actions.finaliseMove(Vec2.ZERO);
	}
}
