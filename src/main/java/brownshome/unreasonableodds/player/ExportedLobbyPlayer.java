package brownshome.unreasonableodds.player;

import java.nio.ByteBuffer;

import brownshome.unreasonableodds.session.Id;

public class ExportedLobbyPlayer extends NetworkLobbyPlayer {
	public static ExportedLobbyPlayer create(String name, boolean ready, boolean host, Id id) {
		return new ExportedLobbyPlayer(name, ready, host, id);
	}

	public ExportedLobbyPlayer(ByteBuffer data) {
		super(data);
	}

	protected ExportedLobbyPlayer(String name, boolean ready, boolean host, Id id) {
		super(name, ready, host, id);
	}
}
