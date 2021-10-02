package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.tile.Tile;
import brownshome.unreasonableodds.gdx.entities.GdxMainFloor;
import brownshome.unreasonableodds.gdx.entities.GdxPlayerCharacter;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

public final class GdxRules extends Rules {
	private final ApplicationResources resources;

	public GdxRules(ApplicationResources resources) {
		this.resources = resources;
	}

	@Override
	public GdxPlayerCharacter createPlayerCharacter(Position position, Player player, Duration timeTravelEnergy) {
		return GdxPlayerCharacter.createCharacter(position, Vec2.ZERO, player, timeTravelEnergy, resources);
	}

	@Override
	protected GdxUniverse.Builder universeBuilder() {
		return (GdxUniverse.Builder) GdxUniverse.createEmptyUniverse(epoch(), resources).builder(Duration.ZERO);
	}

	@Override
	protected GdxMultiverse createMultiverse(Universe baseUniverse) {
		return GdxMultiverse.createMultiverse(this, List.of(baseUniverse));
	}

	@Override
	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.ZERO, Rot2.IDENTITY);
	}

	@Override
	public GdxMainFloor createFloor() {
		return GdxMainFloor.createMainFloor(new Tile[] {
				Tile.makeTile(Vec2.of(0.4, 0.4), Vec2.of(0.6, 0.6))
		}, resources);
	}
}
