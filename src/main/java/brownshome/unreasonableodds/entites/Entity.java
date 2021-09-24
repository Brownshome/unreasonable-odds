package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;

/**
 * An entity in the universe. This is an immutable object
 */
public abstract class Entity {
	/**
	 * Steps this object forward
	 *
	 * @param step an object containing information about this step
	 */
	public void step(Universe.UniverseStep step) {
		step.addEntity(this);
	}
}
