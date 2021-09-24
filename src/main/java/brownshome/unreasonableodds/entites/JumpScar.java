package brownshome.unreasonableodds.entites;

import java.time.Duration;

import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.Universe;

/**
 * An indicator that a jump occurred here in the past
 */
public class JumpScar extends Entity {
	private final Duration lifetime;
	private final Vec2 position;

	protected JumpScar(Vec2 position, Duration lifetime) {
		this.position = position;
		this.lifetime = lifetime;
	}

	/**
	 * The amount of time before the scar fades
	 * @return the remaining life of this scar
	 */
	public final Duration lifetime() {
		return lifetime;
	}

	/**
	 * The position of this jump scar
	 * @return the position
	 */
	public Vec2 position() {
		return position;
	}

	@Override
	protected JumpScar nextEntity(Universe.UniverseStep step) {
		if (lifetime.compareTo(step.stepSize()) >= 0) {
			return new JumpScar(position, lifetime.minus(step.stepSize()));
		}

		return null;
	}
}
