package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.session.Id;

public abstract class ControllingNetworkPlayer extends NetworkGamePlayer implements ControllingPlayer {
	protected ControllingNetworkPlayer(String name, Id id) {
		super(name, id);
	}
}
