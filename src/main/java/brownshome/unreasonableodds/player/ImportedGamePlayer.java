package brownshome.unreasonableodds.player;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.packets.game.*;

/**
 * A player that is controlled by session other than this one but hosted in this session
 */
public class ImportedGamePlayer extends NetworkGamePlayer {
	private final UDPConnection controllingConnection;
	private final Duration offset, latency;

	private Collection<Entity> newEntities;
	private PlayerCharacter next;

	public static ImportedGamePlayer create(ImportedLobbyPlayer lobbyPlayer) {
		return new ImportedGamePlayer(lobbyPlayer.name(),
				lobbyPlayer.id(),
				lobbyPlayer.connection(),
				lobbyPlayer.timeOffset(),
				lobbyPlayer.latency());
	}

	protected ImportedGamePlayer(String name, Id id, UDPConnection controllingConnection, Duration offset, Duration latency) {
		super(name, id);

		this.controllingConnection = controllingConnection;
		this.offset = offset;
		this.latency = latency;
		this.newEntities = Collections.emptyList();
		this.next = null;
	}

	public final void pushUniverse(Universe universe) {
		controllingConnection.send(new UniversePacket(null, universe));
	}

	public PlayerCharacter next(Universe.UniverseStep step) {
		newEntities.forEach(e -> e.addToBuilder(step.builder()));
		newEntities = Collections.emptyList();

		return next;
	}

	protected void next(PlayerCharacter next, Collection<Entity> newEntities) {
		this.next = next;
		this.newEntities = newEntities;
	}

	public void startGame(Universe initialUniverse) {
		controllingConnection.send(new SetEpochPacket(null, initialUniverse.beginning()));
		pushUniverse(initialUniverse);
	}
}
