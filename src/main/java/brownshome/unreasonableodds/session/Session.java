package brownshome.unreasonableodds.session;

import java.util.Collection;

import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.player.Player;

public interface Session {
	Collection<? extends Player> players();

	/**
	 * The rules of the current lobby
	 * @return the rules
	 */
	Rules rules();
}
