package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.vecmath.Vec2;

/**
 * A version of the protagonist that is controlled by historical data
 */
public class HistoricalCharacter extends Character {
	protected HistoricalCharacter(Position position, Vec2 velocity) {
		super(position, velocity);
	}

	@Override
	protected HistoricalCharacter withVelocity(Vec2 velocity) {
		return new HistoricalCharacter(position(), velocity);
	}

	@Override
	protected Character withPosition(Position position) {
		return new HistoricalCharacter(position, velocity());
	}

	@Override
	protected Actions createActions(Universe.UniverseStep step) {
		var actions = super.createActions(step);
		actions.finaliseMove(Vec2.ZERO);
		return actions;
	}
}
