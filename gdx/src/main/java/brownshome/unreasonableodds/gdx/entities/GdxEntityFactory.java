package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.player.GamePlayer;
import brownshome.vecmath.Vec2;

public class GdxEntityFactory extends EntityFactory {
	private final ApplicationResources resources;

	public GdxEntityFactory(ApplicationResources resources) {
		this.resources = resources;
	}

	protected final ApplicationResources resources() {
		return resources;
	}

	@Override
	public GdxPlayerCharacter createPlayerCharacter(Position position, Vec2 velocity, Duration timeTravelEnergy, GamePlayer player) {
		return GdxPlayerCharacter.createCharacter(position, velocity, player, timeTravelEnergy, resources);
	}

	@Override
	public GdxHistoricalCharacter createHistoricalCharacter(Position position, Vec2 velocity) {
		return GdxHistoricalCharacter.create(position, velocity, resources);
	}

	public GdxHistoricalCharacter createHistoricalCharacter(Position position, Vec2 velocity, RenderComponent renderComponent) {
		return new GdxHistoricalCharacter(position, velocity, renderComponent);
	}

	@Override
	public JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new GdxJumpScar(position, jumpScarDuration, resources);
	}
}
