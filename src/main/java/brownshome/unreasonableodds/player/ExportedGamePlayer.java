package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.session.Id;

public class ExportedGamePlayer extends ControllingNetworkPlayer {
	private final CharacterController controller;
	private final Id hostUniverseId;

	public static ExportedGamePlayer exportPlayer(ControllingNetworkPlayer player, Id hostUniverse) {
		return new ExportedGamePlayer(player.name(), player.id(), player.controller(), hostUniverse);
	}

	protected ExportedGamePlayer(String name, Id id, CharacterController controller, Id hostUniverseId) {
		super(name, id);

		this.controller = controller;
		this.hostUniverseId = hostUniverseId;
	}

	@Override
	public final CharacterController controller() {
		return controller;
	}

	public final Id hostUniverseId() {
		return hostUniverseId;
	}
}
