package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.session.Id;

public class LocalGamePlayer extends NetworkGamePlayer implements ControllingPlayer {
	private final CharacterController controller;

	public static LocalGamePlayer create(NetworkPlayer player, CharacterController controller) {
		return new LocalGamePlayer(player.name(), player.id(), controller);
	}

	protected LocalGamePlayer(String name, Id id, CharacterController controller) {
		super(name, id);

		this.controller = controller;
	}

	@Override
	public final CharacterController controller() {
		return controller;
	}
}
