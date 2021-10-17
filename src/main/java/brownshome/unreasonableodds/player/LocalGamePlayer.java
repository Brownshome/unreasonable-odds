package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.CharacterController;

public class LocalGamePlayer extends NetworkGamePlayer implements ControllingPlayer {
	private final CharacterController controller;

	public static LocalGamePlayer create(NetworkLobbyPlayer lobbyPlayer, CharacterController controller) {
		return new LocalGamePlayer(lobbyPlayer.name(), lobbyPlayer.id(), controller);
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
