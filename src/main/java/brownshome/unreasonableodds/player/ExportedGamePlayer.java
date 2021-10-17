package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.PlayerCharacter;

public class ExportedGamePlayer extends NetworkGamePlayer implements ControllingPlayer {
	private final CharacterController controller;

	private Universe.Id hostUniverseId;

	public static ExportedGamePlayer exportPlayer(LocalGamePlayer player, Universe.Id hostUniverse) {
		return new ExportedGamePlayer(player.name(), player.id(), player.controller(), hostUniverse);
	}

	protected ExportedGamePlayer(String name, Id id, CharacterController controller, Universe.Id hostUniverseId) {
		super(name, id);

		this.controller = controller;
		this.hostUniverseId = hostUniverseId;
	}

	public void hostUniverseId(Universe.Id hostUniverse) {
		this.hostUniverseId = hostUniverse;
	}

	@Override
	public final CharacterController controller() {
		return controller;
	}
}
