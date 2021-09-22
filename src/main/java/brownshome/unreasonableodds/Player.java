package brownshome.unreasonableodds;

import brownshome.unreasonableodds.entites.PlayerCharacter;

@FunctionalInterface
public interface Player {
	void performActions(PlayerCharacter.PlayerActions actions);
}
