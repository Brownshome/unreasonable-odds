package brownshome.unreasonableodds.entites;

import java.time.Duration;
import java.util.List;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.tile.Tile;
import brownshome.vecmath.Vec2;

/**
 * A factory for creating entities that do not have a root entity to derive their type from.
 */
public class EntityFactory {
	public PlayerCharacter createPlayerCharacter(Position position, Vec2 velocity, Player player, Duration timeTravelEnergy) {
		return new PlayerCharacter(position, velocity, player, timeTravelEnergy);
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
}
