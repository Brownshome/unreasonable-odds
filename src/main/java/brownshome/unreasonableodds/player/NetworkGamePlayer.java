package brownshome.unreasonableodds.player;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.game.StartGamePacket;
import brownshome.unreasonableodds.session.Id;

public class NetworkGamePlayer extends GamePlayer implements NetworkPlayer {
	private final Id id;

	public NetworkGamePlayer(String name, Id id) {
		super(name);

		this.id = id;
	}

	public void startGame(Universe initialUniverse, UDPConnection connection) {
		connection.send(new StartGamePacket(null, initialUniverse));
	}

	@Override
	public final Id id() {
		return id;
	}
}
