package brownshome.unreasonableodds.player;

import java.net.InetSocketAddress;

/**
 *
 */
public class ExportedLobbyPlayer extends NetworkLobbyPlayer {
	public static ExportedLobbyPlayer create(String name, boolean ready, boolean host, Id id) {
		return new ExportedLobbyPlayer(name, ready, host, id);
	}

	protected ExportedLobbyPlayer(String name, boolean ready, boolean host, Id id) {
		super(name, ready, host, id);
	}
}
