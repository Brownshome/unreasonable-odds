package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;

/**
 * A version of the protagonist that is controlled by historical data
 */
public final class HistoricalCharacter extends Character<HistoricalCharacter> {
	private HistoricalCharacter(HistoricalCharacter root, Position position) {
		super(root, position);
	}

	@Override
	public void step(Universe.UniverseStep step) {
		var actions = new Actions(step);

		actions.endStep();
	}
}
