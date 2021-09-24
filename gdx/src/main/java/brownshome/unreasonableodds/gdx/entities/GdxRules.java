package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.PlayerCharacter;

import brownshome.unreasonableodds.gdx.ApplicationResources;

public final class GdxRules extends Rules {
	private final ApplicationResources resources;

	public GdxRules(ApplicationResources resources) {
		this.resources = resources;
	}

	@Override
	public PlayerCharacter createPlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return GdxPlayerCharacter.createCharacter(position, player, timeTravelEnergy, resources);
	}
}
