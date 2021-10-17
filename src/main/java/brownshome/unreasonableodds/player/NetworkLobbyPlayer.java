package brownshome.unreasonableodds.player;

public abstract class NetworkLobbyPlayer extends LobbyPlayer implements NetworkPlayer {
	private final boolean host;
	private final Id id;

	protected NetworkLobbyPlayer(String name, boolean ready, boolean host, Id id) {
		super(name, ready);

		this.host = host;
		this.id = id;

		assert !host || ready : "All hosts must be ready";
	}

	@Override
	public void ready(boolean ready) {
		assert !host || ready : "All hosts must be ready";

		super.ready(ready);
	}

	public final boolean host() {
		return host;
	}

	@Override
	public final Id id() {
		return id;
	}
}
