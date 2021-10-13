package brownshome.unreasonableodds.gdx;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.gdx.entities.GdxEntityFactory;
import brownshome.unreasonableodds.gdx.generation.GdxClosedTileType;
import brownshome.unreasonableodds.gdx.generation.GdxOpenTileType;
import brownshome.unreasonableodds.gdx.session.GdxSession;
import brownshome.unreasonableodds.generation.TileType;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

public final class GdxRules extends Rules {
	private final ApplicationResources resources;

	public GdxRules(ApplicationResources resources) {
		super(new GdxEntityFactory(resources));

		this.resources = resources;
	}

	public GdxRules(ByteBuffer buffer) {
		this(GdxSession.getHost().applicationResources());
	}

	@Override
	protected GdxUniverse.Builder universeBuilder(Instant epoch) {
		return (GdxUniverse.Builder) GdxUniverse.createEmptyUniverse(epoch, resources).builder(Duration.ZERO);
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
