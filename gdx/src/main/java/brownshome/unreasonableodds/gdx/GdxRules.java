package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.gdx.entities.GdxPlayerCharacter;
import brownshome.unreasonableodds.gdx.generation.GdxClosedTileType;
import brownshome.unreasonableodds.gdx.generation.GdxOpenTileType;
import brownshome.unreasonableodds.generation.TileType;
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
	protected Position createSpawnPosition(Random random) {
		return new Position(Vec2.ZERO, Rot2.IDENTITY);
	}

	@Override
	protected TileType[][] createArchetype() {
		var open = new GdxOpenTileType();
		var close = new GdxClosedTileType(resources);

		return new TileType[][] {
				{ open, open, open, open },
				{ open, close, close, open },
				{ open, close, close, open },
				{ open, open, open, open },
		};
	}

	@Override
	protected TileType[][] createInitialGrid() {
		return new TileType[8][8];
	}
}
