package brownshome.unreasonableodds.player;

import brownshome.unreasonableodds.session.Id;

public class NetworkGamePlayer extends GamePlayer implements NetworkPlayer {
	private final Id id;

	public static NetworkGamePlayer create(NetworkLobbyPlayer player) {
		return new NetworkGamePlayer(player.name(), player.id());
	}

	protected NetworkGamePlayer(String name, Id id) {
		super(name);

		this.id = id;
	}

	@Override
	public final Id id() {
		return id;
	}
}
