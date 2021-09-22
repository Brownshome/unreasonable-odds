package brownshome.unreasonableodds;

import java.time.Duration;

/**
 * A collection of rules for the game
 */
public class Rules {
	/**
	 * The amount of time-energy taken to jump between two universes next to each-other
	 * @return the duration required
	 */
	public Duration timePerUniverseJump() {
		return Duration.ofSeconds(20);
	}

	/**
	 * How long a jump scar will remain in the game world before expiring
	 * @return the lifetime of a jump scar
	 */
	public Duration jumpScarDuration() {
		return Duration.ofSeconds(10);
	}
}
