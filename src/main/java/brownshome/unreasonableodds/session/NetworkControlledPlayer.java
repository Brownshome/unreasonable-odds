package brownshome.unreasonableodds.session;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.vecmath.Vec2;

public class NetworkControlledPlayer implements Player {
	@Override
	public void performActions(PlayerCharacter.PlayerActions actions) {
		actions.finaliseMove(Vec2.ZERO);
	}
}
