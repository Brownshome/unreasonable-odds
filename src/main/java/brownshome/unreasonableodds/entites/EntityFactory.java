package brownshome.unreasonableodds.entites;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.player.GamePlayer;
import brownshome.unreasonableodds.session.Id;
import brownshome.unreasonableodds.session.NetworkGameSession;
import brownshome.unreasonableodds.tile.Tile;
import brownshome.vecmath.Vec2;

/**
 * A factory for creating entities that do not have a root entity to derive their type from.
 */
public class EntityFactory {
	public PlayerCharacter createPlayerCharacter(Position position, Vec2 velocity, Duration timeTravelEnergy, Id playerId) {
		return new PlayerCharacter(position, velocity, playerId, timeTravelEnergy);
	}

	public HistoricalCharacter createHistoricalCharacter(Position position, Vec2 velocity) {
		return new HistoricalCharacter(position, velocity);
	}

	public JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new JumpScar(position, jumpScarDuration);
	}

	public StaticMap createStaticMap(List<Tile> tiles) {
		return new StaticMap(tiles);
	}

	// **************** NETWORK READ AND WRITE ********************

	public Entity read(int id, ByteBuffer buffer) {
		return switch (KnownEntities.values()[id]) {
			case HISTORICAL_CHARACTER -> new HistoricalCharacter(buffer);
			case JUMP_SCAR -> new JumpScar(buffer);
			case PLAYER_CHARACTER -> new PlayerCharacter(buffer);
			case STATIC_MAP -> Objects.requireNonNullElseGet(NetworkGameSession.get().map(), () -> new StaticMap(buffer));
		};
	}
}
