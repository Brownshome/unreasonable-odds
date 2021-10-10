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
public class ClientSession extends UDPSession {
	private final UDPConnection connection;

	private List<String> players;

	public ClientSession(String name, InetSocketAddress address, Executor executor) throws IOException {
		super(name, new UDPConnectionManager(List.of(new BaseSchema(),
				new UDPSchema(),
				new UnreasonableOddsSchema())), executor);

		this.players = new ArrayList<>();

		connection = connectionManager().getOrCreateConnection(address);
		connection.connect();
		connection.send(new SetNamePacket(name));
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
	public void close() {
		connection.send(new LeaveSessionPacket());
		super.close();
	}
}
