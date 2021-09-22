package brownshome.unreasonableodds;

import java.time.Duration;

public class Rules {
	/**
	 * The amount of time-energy taken to jump between two universes next to each-other
	 * @return the duration required
	 */
	public Duration timePerUniverseJump() {
		return Duration.ofSeconds(20);
	}

	public Duration jumpScarDuration() {
		return Duration.ofSeconds(10);
	}
}
