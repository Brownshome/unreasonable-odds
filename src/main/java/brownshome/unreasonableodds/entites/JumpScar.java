package brownshome.unreasonableodds.entites;

import java.time.Duration;

import brownshome.unreasonableodds.Universe;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

public class JumpScar extends Entity {
	private final Duration lifetime;

	public JumpScar(Vec2 position, Duration lifetime) {
		super(position, Rot2.IDENTITY);

		this.lifetime = lifetime;
	}

	public Duration lifetime() {
		return lifetime;
	}

	@Override
	public void step(Universe.UniverseStep step) {
		if (lifetime.compareTo(step.stepSize()) >= 0) {
			step.addSteppable(new JumpScar(position(), lifetime.minus(step.stepSize())));
		}
	}
}
