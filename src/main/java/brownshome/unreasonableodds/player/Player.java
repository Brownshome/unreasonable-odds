package brownshome.unreasonableodds.player;

/**
 * A named member of a session
 */
public interface Player extends Comparable<Player> {
	/**
	 * The name of the player
	 * @return a string
	 */
	String name();

	@Override
	default int compareTo(Player o) {
		return name().compareTo(o.name());
	}
}
