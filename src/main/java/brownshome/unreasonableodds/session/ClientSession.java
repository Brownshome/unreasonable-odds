package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import brownshome.netcode.udp.*;
import brownshome.unreasonableodds.session.net.*;

/**
 * Represents a client game that connects to a hosted game
 */
public class ClientSession extends UDPSession {
	private final UDPConnection connection;

	private List<SessionPlayer> players;
	private boolean isReady;

	public ClientSession(String name, InetSocketAddress address, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(allSchema()), executor);

		this.players = new ArrayList<>();

		connection = connectionManager().getOrCreateConnection(address);
		connection.connect(sessionSchema());
		connection.send(new SetNamePacket(name));
		isReady = false;
	}

	@Override
	public void name(String name) {
		super.name(name);
		connection.send(new SetNamePacket(name));
	}

	public final boolean isReady() {
		return isReady;
	}

	public void setReady(boolean ready) {
		if (isReady != ready) {
			isReady = ready;
			connection.send(new SetReadyStatePacket(ready));
		}
	}

	public void players(List<SessionPlayer> players) {
		this.players = players;
	}

	@Override
	public final List<SessionPlayer> players() {
		return players;
	}

	@Override
	public void close() {
		connection.send(new LeaveSessionPacket());
		super.close();
	}

	/**
	 * Called when the host has left the session
	 */
	public void hostLeft() {
		/* Do nothing as the host will close the connection from its end */
	}
}
