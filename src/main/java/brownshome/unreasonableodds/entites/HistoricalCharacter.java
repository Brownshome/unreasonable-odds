package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * A version of the protagonist that is controlled by historical data
 */
public final class HistoricalCharacter extends Character {
	HistoricalCharacter(Vec2 position, Rot2 orientation) {
		super(position, orientation);
	}

	@Override
	public void step(Universe.UniverseStep step) {
		var actions = new Actions(step);

		actions.endStep();
	}
}
