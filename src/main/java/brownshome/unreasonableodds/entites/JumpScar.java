package brownshome.unreasonableodds.entites;

import java.time.Duration;

import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.Universe;

/**
 * An indicator that a jump occurred here in the past
 */
public final class JumpScar extends Entity<JumpScar> {
	private final Duration lifetime;
	private final Vec2 position;

	private JumpScar(Vec2 position, Duration lifetime) {
		this.position = position;
		this.lifetime = lifetime;
	}

	private JumpScar(JumpScar root, Vec2 position, Duration lifetime) {
		super(root);

		this.position = position;
		this.lifetime = lifetime;
	}

	/**
	 * Creates a new jump-scar at the specific location
	 * @param position the location
	 * @param step the step in which to create the jump scar
	 */
	public static void create(Vec2 position, Universe.UniverseStep step) {
		step.addEntity(new JumpScar(position, step.rules().jumpScarDuration()));
	}

	/**
	 * The amount of time before the scar fades
	 * @return the remaining life of this scar
	 */
	public Duration lifetime() {
		return lifetime;
	}

	@Override
	public void step(Universe.UniverseStep step) {
		if (lifetime.compareTo(step.stepSize()) >= 0) {
			step.addEntity(new JumpScar(root(), position, lifetime.minus(step.stepSize())));
		}
	}
}
