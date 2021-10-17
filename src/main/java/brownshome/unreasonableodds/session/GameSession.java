package brownshome.unreasonableodds.session;

import java.util.Collection;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.player.GamePlayer;

/**
 * A current session of a game, tied to a particular multiverse
 */
public interface GameSession extends Session {
	Universe.Id allocateUniverseId();

	@Override
	Collection<? extends GamePlayer> players();
}
