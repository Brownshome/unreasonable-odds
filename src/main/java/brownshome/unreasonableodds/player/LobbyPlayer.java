package brownshome.unreasonableodds.player;

public class LobbyPlayer implements Player {
	private boolean ready;
	private String name;

	protected LobbyPlayer(String name, boolean ready) {
		assert name != null;

		this.ready = ready;
		this.name = name;
	}

	public void ready(boolean ready) {
		this.ready = ready;
	}

	public final boolean ready() {
		return ready;
	}

	public void name(String name) {
		assert name != null;

		this.name = name;
	}

	@Override
	public final String name() {
		return name;
	}
}
