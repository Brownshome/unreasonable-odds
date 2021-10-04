package brownshome.unreasonableodds.generation;

import java.util.*;

/**
 * A description of a possible grid-cell in the generation
 */
public class LocalArea {
	private static final double LOW_APPROXIMATION_CUTOFF = 1e-16;

	/**
	 * An approximation of -xln x that is correct when close to zero. In particular, 0 ln 0 is well-defined as 0.
	 * @param probability the probability
	 * @return -x ln x
	 */
	private static double lowApproximateEntropy(double probability) {
		final double BIG_NUMBER = 2e8;

		return BIG_NUMBER * (probability - Math.pow(probability, 1 + 1 / BIG_NUMBER));
	}

	private final double probability, individualEntropy;

	private final int index;

	/**
	 * Stores the compatible areas in each direction
	 */
	private final EnumMap<Direction, BitSet> compatibility;

	/**
	 * Creates a possible grid-cell
	 * @param index the index of this grid cell in the array passed to {@link WaveCollapseGridGenerator#WaveCollapseGridGenerator(int, int, LocalArea[])}
	 * @param probability the probability of this grid cell being chosen
	 */
	public LocalArea(int index, double probability) {
		assert probability >= 0.0 && probability <= 1.0;

		this.index = index;
		this.individualEntropy = (probability < LOW_APPROXIMATION_CUTOFF) ? lowApproximateEntropy(probability) : (-probability * Math.log(probability));
		this.probability = probability;

		this.compatibility = new EnumMap<>(Direction.class);
		for (var d : Direction.values()) {
			compatibility.put(d, new BitSet());
		}
	}

	/**
	 * The probability of this grid-cell being chosen
	 * @return the probability of this grid-cell being chosen
	 */
	public final double probability() {
		return probability;
	}

	final double individualEntropy() {
		return individualEntropy;
	}

	public final int index() {
		return index;
	}

	final BitSet compatibility(Direction d) {
		return compatibility.get(d);
	}

	/**
	 * Sets that it is compatible for b to be next to a in direction dir
	 *
	 * @param a   the source area
	 * @param dir the direction of the next area
	 * @param b   the next area
	 */
	public static void makeCompatible(LocalArea a, Direction dir, LocalArea b) {
		a.compatibility(dir).set(b.index);
		b.compatibility(dir.opposite()).set(a.index);
	}

	@Override
	public String toString() {
		return "Area %d".formatted(index);
	}
}
