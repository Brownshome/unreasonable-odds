package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.vecmath.Vec2;

/**
 * A version of the protagonist that is controlled by historical data
 */
public class HistoricalCharacter extends Character {
	protected HistoricalCharacter(Position position) {
		super(position);
	}

	@Override
	protected HistoricalCharacter withPosition(Position position) {
		return new HistoricalCharacter(position);
	}

	@Override
	protected HistoricalCharacter nextEntity(Universe.UniverseStep step) {
		var actions = new Actions(step);
		actions.finaliseMove(Vec2.ZERO);
		return (HistoricalCharacter) actions.next();
	}
}
