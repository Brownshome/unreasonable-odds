package brownshome.unreasonableodds.history;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

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

	public static History blankHistory() {
		return new History(new LinkedList<>(), Collections.emptyList());
	}

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

	public Universe getUniverse(Instant when) {
		Universe past = null;
		Duration pastDistance = null;

		for (var iterator = view.listIterator(); iterator.hasNext(); ) {
			var future = iterator.next();

			var futureDistance = Duration.between(when, future.now());
			if (!futureDistance.isNegative()) {
				if (futureDistance.isZero()) {
					return future;
				}

				assert past != null : "Time must not be before the earliest entry in the history";

				double totalDistance = futureDistance.plus(pastDistance).toNanos();
				double weight = pastDistance.toNanos() / totalDistance;

				return Universe.interpolate(past, weight, future);
			}

			past = future;
			pastDistance = futureDistance.negated();
		}

		throw new IllegalArgumentException("Time in the future");
	}
}
