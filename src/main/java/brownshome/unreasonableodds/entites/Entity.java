package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.Universe;

/**
 * An entity in the universe. This is an immutable object
 */
public abstract class Entity {
	/**
	 * Returns the next entity, and performing any other stepping activities as needed.
	 * @param step an object containing information about this step
	 * @return the stepped entity, or null if the entity should not be included in the next universe
	 */
	protected Entity nextEntity(Universe.UniverseStep step) {
		return this;
	}

	public Entity createHistoricalEntity(Rules rules) {
		return this;
	}

	/**
	 * Steps this object forward
	 *
	 * @param step an object containing information about this step
	 */
	public final void step(Universe.UniverseStep step) {
		var next = nextEntity(step);

		if (next != null) {
			next.addToBuilder(step.builder());
		}
	}

	/**
	 * Adds this entity to the provided builder
	 * @param builder the builder to add to
	 */
	public void addToBuilder(Universe.Builder builder) {
		builder.addEntity(this);
	}
}
