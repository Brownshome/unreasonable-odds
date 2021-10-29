package brownshome.unreasonableodds.gdx.entities;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.tile.GdxClosedTile;
import brownshome.unreasonableodds.player.GamePlayer;
import brownshome.unreasonableodds.session.Id;
import brownshome.unreasonableodds.session.NetworkGameSession;
import brownshome.unreasonableodds.tile.*;
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
	public GdxPlayerCharacter createPlayerCharacter(Position position, Vec2 velocity, Duration timeTravelEnergy, Id playerId) {
		return new GdxPlayerCharacter(position, velocity, playerId, timeTravelEnergy, resources, true);
	}

	@Override
	public GdxHistoricalCharacter createHistoricalCharacter(Position position, Vec2 velocity) {
		return new GdxHistoricalCharacter(position, velocity, resources);
	}

	public GdxHistoricalCharacter createHistoricalCharacter(Position position, Vec2 velocity, RenderComponent renderComponent) {
		return new GdxHistoricalCharacter(position, velocity, renderComponent);
	}

	@Override
	public JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new GdxJumpScar(position, jumpScarDuration, resources);
	}

	@Override
	public Entity read(int id, ByteBuffer buffer) {
		return switch (KnownEntities.values()[id]) {
			case HISTORICAL_CHARACTER -> new GdxHistoricalCharacter(buffer, resources);
			case JUMP_SCAR -> new GdxJumpScar(buffer, resources);
			case PLAYER_CHARACTER -> new GdxPlayerCharacter(buffer, resources);
			case STATIC_MAP -> Objects.requireNonNullElseGet(NetworkGameSession.get().map(), () -> new StaticMap(buffer) {
				@Override
				protected Tile readTile(int id, ByteBuffer buffer) {
					return switch (KnownTiles.values()[id]) {
						case CLOSED -> new GdxClosedTile(buffer, resources);
					};
				}
			});
		};
	}
}
