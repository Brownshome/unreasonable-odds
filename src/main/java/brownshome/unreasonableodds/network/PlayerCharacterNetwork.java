package brownshome.unreasonableodds.network;

import java.util.*;

import brownshome.netcode.Connection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.packets.UniversePacket;

/**
 * Holds networking information about a player character
 */
public class PlayerCharacterNetwork {
	private final Connection<?> connection;

	private Collection<Entity> newEntities;
	private PlayerCharacter next;

	public PlayerCharacterNetwork(Connection<?> connection) {
		this.connection = connection;
		newEntities = new ArrayList<>();
	}

	public void pushUniverse(Universe universe) {
		connection.send(new UniversePacket(universe));
	}

	public PlayerCharacter nextPlayer(Universe.UniverseStep step) {
		newEntities.forEach(e -> e.addToBuilder(step.builder()));

		newEntities = Collections.emptyList();

		return next;
	}

	protected void setNext(PlayerCharacter player, Collection<Entity> newEntities) {
		next = player;
		this.newEntities = newEntities;
	}
}
