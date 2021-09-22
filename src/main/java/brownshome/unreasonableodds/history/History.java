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
	private final List<Universe> view;

	private History(List<Universe> history, List<Universe> view) {
		this.history = history;
		this.view = view;
	}

	/**
	 * Creates a blank history
	 * @return a history with no universes in it
	 */
	public static History blankHistory() {
		return new History(new LinkedList<>(), Collections.emptyList());
	}

	/**
	 * Adds a new universe to the history
	 * @param universe the universe to add
	 * @return a new history
	 */
	public History expandHistory(Universe universe) {
		if (history.size() != view.size()) {
			// A new branching history, copy the existing chain
			return copyHistory().expandHistory(universe);
		}

		history.add(universe);
		return new History(history, history.subList(0, history.size()));
	}

	private History copyHistory() {
		return new History(new LinkedList<>(view), view);
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

		for (Universe future : view) {
			var futureDistance = Duration.between(when, future.now());
			if (!futureDistance.isNegative()) {
				if (futureDistance.isZero()) {
					return future;
				}

				assert past != null : "Time must not be before the earliest entry in the history";

				return multiverse.stepDisconnectedUniverse(past, pastDistance);
			}

			past = future;
			pastDistance = futureDistance.negated();
		}

		throw new IllegalArgumentException("Time in the future");
	}
}
