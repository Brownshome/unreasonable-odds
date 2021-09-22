package brownshome.unreasonableodds.entites;

import java.time.Duration;

import brownshome.unreasonableodds.Universe;

/**
 * An entity in the universe. This is an immutable object
 */
public abstract class Entity<THIS extends Entity<?>> {
	private final THIS root;

	/**
	 * Creates an entity
	 * @param root the entity object that this one was derived from. This must be a root entity
	 */
	protected Entity(THIS root) {
		assert root.isRoot();

		this.root = root;
	}

	/**
	 * Creates a root entity
	 */
	@SuppressWarnings("unchecked")
	protected Entity() {
		root = (THIS) this;
	}

	/**
	 * Checks if this entity is the same as the other given entity. Two entities are considered the same if they came
	 * from the same root-object. This can be used to compare across time and across universes
	 * @param other the other entity, may not be null
	 * @return if the two entities share a root
	 */
	public final boolean sameEntity(Entity<THIS> other) {
		return root == other.root;
	}

	/**
	 * Returns the entity that originally created this one
	 * @return the root
	 */
	public final THIS root() {
		return root;
	}

	/**
	 * Checks if this entity has no parent
	 * @return true if this is a root entity
	 */
	public final boolean isRoot() {
		return root == this;
	}

	/**
	 * Steps this object forward
	 *
	 * @param step an object containing information about this step
	 */
	public void step(Universe.UniverseStep step) {
		step.addEntity(this);
	}
}
