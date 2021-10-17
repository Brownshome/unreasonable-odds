package brownshome.unreasonableodds.session;

import brownshome.unreasonableodds.player.LobbyPlayer;

/**
 * A lobby that has a single player associated with it
 */
public interface LocalPlayerLobbySession extends LobbySession {
	LobbyPlayer localPlayer();
}
