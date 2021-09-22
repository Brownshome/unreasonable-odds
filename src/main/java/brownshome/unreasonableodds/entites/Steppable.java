package brownshome.unreasonableodds.entites;

import java.util.function.Consumer;

import brownshome.unreasonableodds.Universe;

@FunctionalInterface
public interface Steppable extends Consumer<Universe.UniverseStep> {
	Steppable NO_OPERATION = step -> {};

	/**
	 * Steps this object forward
	 *
	 * @param step an object containing information about this step
	 */
	void step(Universe.UniverseStep step);

	@Override
	default void accept(Universe.UniverseStep step) {
		step(step);
	}
}
