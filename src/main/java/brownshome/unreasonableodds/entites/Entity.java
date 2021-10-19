package brownshome.unreasonableodds.entites;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.Universe;

/**
 * An entity in the universe. This is an immutable object. It is not strictly networkable, as it cannot be constructed.
 * Therefore, a converter must be used.
 */
public abstract class Entity implements Networkable {
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

	protected abstract int id();

	static int readId(ByteBuffer buffer) {
		return Short.toUnsignedInt(buffer.getShort());
	}

	@Override
	public void write(ByteBuffer buffer) {
		int id = id();
		assert Short.toUnsignedInt((short) id) == id;

		buffer.putShort((short) id);
	}

	@Override
	public int size() {
		return Short.BYTES;
	}

	@Override
	public boolean isSizeExact() {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return true;
	}
}
