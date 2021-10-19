package brownshome.unreasonableodds.session;

import java.util.Collection;
import java.util.List;

import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.player.LobbyPlayer;

/**
 * Represents the current lobby session. This is used to start a new game session when the lobby starts. Lobbies are
 * full of players, the current lobby may or may not be associated with a particular player.
 *
 *
 */
public interface LobbySession extends Session {
	/**
	 * A sorted list of all players in the current lobby
	 * @return a list of players
	 */
	@Override
	Collection<? extends LobbyPlayer> players();

	/**
	 * Called when players change. This is used for UI and networking updates
	 */
	default void onPlayersChanged() { }

	/**
	 * Sets the rules of the current lobby
	 * @param rules the rules
	 */
	void rules(Rules rules);
}
