package brownshome.unreasonableodds.network;

import java.net.InetSocketAddress;
import java.util.*;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;

/**
 * Holds networking information about a multiverse
 */
public final class MultiverseNetwork {
	private final UDPSession session;

	/**
	 * Players that are controlled by another session but simulated in this session
	 */
	private final Map<InetSocketAddress, PlayerCharacterNetwork> remotePlayers;

	private static final class ExportedPlayer {
		final Player player;
		Universe universe;

		ExportedPlayer(Player player) {
			this.player = player;
		}
	}

	/**
	 * Players that are controlled by this session but simulated in another session
	 */
	private final Map<Universe.Id, ExportedPlayer> exportedPlayers;

	public static final class Builder {
		private final UDPSession session;

		private final Map<InetSocketAddress, PlayerCharacterNetwork> remotePlayers = new HashMap<>();
		private final Map<Universe.Id, ExportedPlayer> exportedPlayers = new HashMap<>();

		public Builder(ClientSession session) {
			this.session = session;
		}

		public Builder(HostSession session) {
			this.session = session;
		}

		public MultiverseNetwork build() {
			return new MultiverseNetwork(session, remotePlayers, exportedPlayers);
		}

		public void addRemotePlayer(InetSocketAddress address, PlayerCharacterNetwork player) {
			remotePlayers.put(address, player);
		}

		public void addExportedPlayer(Universe.Id universe, Player player) {
			exportedPlayers.put(universe, new ExportedPlayer(player));
		}
	}

	private MultiverseNetwork(UDPSession session,
	                          Map<InetSocketAddress, PlayerCharacterNetwork> remotePlayers,
	                          Map<Universe.Id, ExportedPlayer> exportedPlayers) {
		this.session = session;
		this.remotePlayers = remotePlayers;
		this.exportedPlayers = exportedPlayers;
	}

	public InetSocketAddress address() {
		return session.connectionManager().address();
	}

	public Collection<PlayerCharacterNetwork> remotePlayers() {
		return remotePlayers.values();
	}

	public void setNextEntityForPlayer(InetSocketAddress remoteAddress, PlayerCharacter player, Collection<Entity> newEntities) {
		remotePlayers.get(remoteAddress).setNext(player, newEntities);
	}

	public void setUniverse(Universe universe) {
		exportedPlayers.get(universe.id()).universe = universe;
	}

	public void registerNewUniverse(Universe universe) {
		session.claimUniverse(universe.id());
	}
}
