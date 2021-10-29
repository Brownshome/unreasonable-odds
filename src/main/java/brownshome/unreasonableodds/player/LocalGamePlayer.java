package brownshome.unreasonableodds.player;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.session.Id;

public class LocalGamePlayer extends ControllingNetworkPlayer {
	private final CharacterController controller;

	public static LocalGamePlayer create(NetworkPlayer player, CharacterController controller) {
		return new LocalGamePlayer(player.name(), player.id(), controller);
	}

	protected LocalGamePlayer(String name, Id id, CharacterController controller) {
		super(name, id);

		this.controller = controller;
	}

	@Override
	public final void startGame(Universe initialUniverse, UDPConnection connection) {
		throw new AssertionError("This method must not be called for local players");
	}

	@Override
	public final CharacterController controller() {
		return controller;
	}
}
