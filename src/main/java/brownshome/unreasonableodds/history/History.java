package brownshome.unreasonableodds.history;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.Multiverse;
import brownshome.unreasonableodds.Universe;

/**
 * Stores a series of events that have occurred
 */
public final class History {
	private final List<Universe> history;
	private final int size;

	private History(List<Universe> history, int size) {
		this.history = history;
		this.size = size;
	}

	/**
	 * Creates a blank history
	 * @return a history with no universes in it
	 */
	public static History blankHistory() {
		return new History(new LinkedList<>(), 0);
	}

	/**
	 * Adds a new universe to the history
	 * @param universe the universe to add
	 * @return a new history
	 */
	public History expandHistory(Universe universe) {
		if (history.size() != size) {
			// A new branching history, copy the existing chain
			return copyHistory().expandHistory(universe);
		}

		history.add(universe);
		return new History(history, history.size());
	}

	private History copyHistory() {
		return new History(new LinkedList<>(history.subList(0, size)), size);
	}

	/**
	 * Gets a universe at a specified time
	 * @param when the time to retrieve; this must be within the range that this history covers inclusive
	 * @param multiverse the surrounding multiverse
	 * @return a universe
	 */
	public Universe getUniverse(Instant when, Multiverse multiverse) {
		Universe past = null;
		Duration pastDistance = null;

		for (Universe future : history.subList(0, size)) {
			var futureDistance = Duration.between(when, future.now());
			if (!futureDistance.isNegative()) {
				if (futureDistance.isZero()) {
					return future.createHistoricalUniverse();
				}

				assert past != null : "Time must not be before the earliest entry in the history";

				return multiverse.stepDisconnectedUniverse(past.createHistoricalUniverse(), pastDistance);
			}

			past = future;
			pastDistance = futureDistance.negated();
		}

		throw new IllegalArgumentException("Time in the future");
	}

	/**
	 * Gets the beginning of this history
	 * @return the beginning
	 */
	public Instant beginning() {
		return history.get(0).now();
	}
}
