package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.udp.*;
import brownshome.unreasonableodds.net.*;

/**
 * Represents a client game that connects to a hosted game
 */
public class ClientSession extends Session {
	private final UDPConnectionManager connectionManager;
	private final UDPConnection connection;

	private List<String> players;

	public ClientSession(String name, InetSocketAddress address, Executor executor) throws IOException {
		super(name);

		this.players = new ArrayList<>();

		connectionManager = new UDPConnectionManager(List.of(new BaseSchema(),
				new UDPSchema(),
				new UnreasonableOddsSchema()));
		connectionManager.registerExecutor("default", executor, 1);

		connection = connectionManager.getOrCreateConnection(address);
		connection.connect();

		name(name());
	}

	@Override
	public void name(String name) {
		super.name(name);
		connection.send(new SetNamePacket(name));
	}

	public void players(List<String> players) {
		this.players = players;
	}

	@Override
	public final List<String> players() {
		return players;
	}

	@Override
	public final InetSocketAddress address() {
		return connectionManager.address();
	}

	@Override
	public void leave() {
		connection.send(new LeaveSessionPacket());
		connectionManager.close();
	}

	public void hostLeft() {
		connectionManager.close();
	}
}
