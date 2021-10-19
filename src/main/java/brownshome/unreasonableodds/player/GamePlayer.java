package brownshome.unreasonableodds.player;

public abstract class GamePlayer implements Player {
	private final String name;

	protected GamePlayer(String name) {
		this.name = name;
	}

	@Override
	public final String name() {
		return name;
	}
}
