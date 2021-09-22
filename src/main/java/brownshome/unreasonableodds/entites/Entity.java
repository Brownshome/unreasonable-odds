package brownshome.unreasonableodds.entites;

import brownshome.unreasonableodds.Universe;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

/**
 * A physical entity in the universe. This is an immutable object
 */
public abstract class Entity implements Steppable {
	private final Vec2 position;
	private final Rot2 orientation;

	protected Entity(Vec2 position, Rot2 orientation) {
		this.position = position;
		this.orientation = orientation;
	}

	public Vec2 position() {
		return position;
	}

	public Rot2 orientation() {
		return orientation;
	}

	@Override
	public void step(Universe.UniverseStep step) {
		step.addSteppable(this);
	}
}
