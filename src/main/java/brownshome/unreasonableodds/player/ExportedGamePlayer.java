package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.session.Id;

public class ExportedGamePlayer extends NetworkGamePlayer implements ControllingPlayer {
	private final CharacterController controller;

	private Id hostUniverseId;

	public static ExportedGamePlayer exportPlayer(LocalGamePlayer player, Id hostUniverse) {
		return new ExportedGamePlayer(player.name(), player.id(), player.controller(), hostUniverse);
	}

	protected ExportedGamePlayer(String name, Id id, CharacterController controller, Id hostUniverseId) {
		super(name, id);

		this.controller = controller;
		this.hostUniverseId = hostUniverseId;
	}

	public void hostUniverseId(Id hostUniverse) {
		this.hostUniverseId = hostUniverse;
	}

	@Override
	public final CharacterController controller() {
		return controller;
	}
}
