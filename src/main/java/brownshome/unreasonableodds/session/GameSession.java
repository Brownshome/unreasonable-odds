package brownshome.unreasonableodds.session;

import java.util.Collection;

import brownshome.unreasonableodds.player.*;

/**
 * A current session of a game, tied to a particular multiverse
 */
public interface GameSession extends Session {
	Id allocateUniverseId();

	Player player(Id id);

	@Override
	Collection<? extends GamePlayer> players();
}
