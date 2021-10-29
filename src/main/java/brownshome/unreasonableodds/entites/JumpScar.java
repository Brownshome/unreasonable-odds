package brownshome.unreasonableodds.entites;

import java.nio.ByteBuffer;
import java.time.Duration;

import brownshome.unreasonableodds.packets.converters.DurationConverter;
import brownshome.unreasonableodds.packets.converters.Vec2Converter;
import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.Universe;

/**
 * An indicator that a jump occurred here in the past
 */
public class JumpScar extends Entity {
	private final Vec2 position;
	private final Duration lifetime;

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
			return withLifetime(lifetime.minus(step.stepSize()));
		}

		return null;
	}

	protected JumpScar withLifetime(Duration lifetime) {
		return new JumpScar(position, lifetime);
	}

	protected JumpScar(ByteBuffer buffer) {
		this(Vec2Converter.INSTANCE.read(buffer), DurationConverter.INSTANCE.read(buffer));
	}

	@Override
	public int id() {
		return KnownEntities.JUMP_SCAR.id();
	}

	@Override
	public void write(ByteBuffer buffer) {
		super.write(buffer);

		Vec2Converter.INSTANCE.write(buffer, position);
		DurationConverter.INSTANCE.write(buffer, lifetime);
	}
}
