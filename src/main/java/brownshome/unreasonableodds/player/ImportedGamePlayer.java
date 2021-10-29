package brownshome.unreasonableodds.player;

import java.util.Collection;
import java.util.Collections;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.packets.game.UniversePacket;
import brownshome.unreasonableodds.session.Id;

/**
 * A player that is controlled by session other than this one but hosted in this session
 */
public class ImportedGamePlayer extends NetworkGamePlayer {
	private final UDPConnection controllingConnection;

	private Collection<Entity> newEntities;

	public static ImportedGamePlayer create(NetworkGamePlayer player, UDPConnection controllingConnection) {
		return new ImportedGamePlayer(player.name(), player.id(), controllingConnection);
	}

	protected ImportedGamePlayer(String name, Id id, UDPConnection controllingConnection) {
		super(name, id);

		this.controllingConnection = controllingConnection;
		this.newEntities = null;
	}

	public final void pushUniverse(Universe universe) {
		controllingConnection.send(new UniversePacket(null, universe));
	}

	/**
	 * Steps this imported player, returning true if there are actions to step
	 * @param step the parent step
	 * @return true if a new set of entities were added to the universe, false otherwise
	 */
	public boolean step(Universe.UniverseStep step) {
		if (newEntities == null) {
			return false;
		} else {
			newEntities.forEach(e -> e.addToBuilder(step.builder()));
			newEntities = null;
			return true;
		}
	}

	public void setNext(Collection<Entity> newEntities) {
		this.newEntities = newEntities;
	}
}
