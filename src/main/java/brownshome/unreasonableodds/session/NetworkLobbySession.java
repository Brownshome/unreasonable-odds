package brownshome.unreasonableodds.session;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.packets.lobby.LobbySchema;
import brownshome.unreasonableodds.packets.session.SessionSchema;
import brownshome.unreasonableodds.player.*;

public abstract class NetworkLobbySession extends NetworkSession implements LobbySession {
	public static final class NetworkLobbySessionConverter extends SessionConverter<NetworkLobbySession> { }

	public static NetworkLobbySession get() {
		return (NetworkLobbySession) NetworkSession.get();
	}

	private final AtomicInteger playerId = new AtomicInteger();

	protected NetworkLobbySession(UDPConnectionManager connectionManager, Executor executor) {
		super(connectionManager, executor);
	}

	@Override
	public abstract Collection<? extends NetworkLobbyPlayer> players();

	protected static List<Schema> lobbySchema() {
		return List.of(new BaseSchema(), new SessionSchema(), new LobbySchema());
	}

	protected final NetworkPlayer.Id allocatePlayerId() {
		return new NetworkPlayer.Id(connectionManager().address(), playerId.getAndIncrement());
	}
}
