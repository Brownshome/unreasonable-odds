package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.gdx.entities.GdxPlayerCharacter;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

public final class GdxRules extends Rules {
	private final ApplicationResources resources;

	public GdxRules(ApplicationResources resources) {
		this.resources = resources;
	}

	@Override
	public PlayerCharacter createPlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return GdxPlayerCharacter.createCharacter(position, player, timeTravelEnergy, resources);
	}

	@Override
	protected Universe createUniverse(List<Entity> initialEntities) {
		return GdxUniverse.createUniverse(epoch(), initialEntities);
	}

	@Override
	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.ZERO, Rot2.IDENTITY);
	}
}
