package brownshome.unreasonableodds.player;

public abstract class NetworkGamePlayer extends GamePlayer implements NetworkPlayer {
	private final Id id;

	protected NetworkGamePlayer(String name, Id id) {
		super(name);

		this.id = id;
	}

	@Override
	public final Id id() {
		return id;
	}
}
