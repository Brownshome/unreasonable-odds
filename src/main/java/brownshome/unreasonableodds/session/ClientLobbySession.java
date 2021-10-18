package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;

import brownshome.netcode.udp.UDPConnection;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.packets.lobby.SetNamePacket;
import brownshome.unreasonableodds.packets.lobby.SetReadyStatePacket;
import brownshome.unreasonableodds.packets.session.LeaveSessionPacket;
import brownshome.unreasonableodds.player.*;

public abstract class ClientLobbySession extends NetworkLobbySession implements LocalPlayerLobbySession {
	public static final class ClientLobbySessionConverter extends SessionConverter<ClientLobbySession> {}

	private final UDPConnection hostConnection;

	private List<NetworkLobbyPlayer> players;

	private Id localPlayerId;
	private final ExportedLobbyPlayer localPlayer;

	private Rules rules;

	protected ClientLobbySession(String name, InetSocketAddress address, Executor executor) throws IOException {
		super(new UDPConnectionManager(allSchema()), executor);

		this.hostConnection = connectionManager().getOrCreateConnection(address);
		this.players = new ArrayList<>();
		this.localPlayer = new ExportedLobbyPlayer(name, false, false, null) {
			@Override
			public Id id() {
				assert localPlayerId != null : "Session number must be set before calling Id";
				return localPlayerId;
			}

			@Override
			public void name(String name) {
				super.name(name);
				uploadName();
				onPlayersChanged();
			}

			@Override
			public void ready(boolean ready) {
				super.ready(ready);
				uploadReady();
				onPlayersChanged();
			}
		};

		this.rules = null;

		hostConnection.connect(lobbySchema());
		uploadName();
	}

	@Override
	public void sessionId(int id) {
		super.sessionId(id);
		localPlayerId = allocatePlayerId();
	}

	@Override
	public final Collection<? extends NetworkLobbyPlayer> players() {
		var result = new ArrayList<>(players);
		result.add(localPlayer);
		result.sort(null);

		return result;
	}

	public final void players(List<NetworkLobbyPlayer> players) {
		players.removeIf(p -> p.id().sessionId() == sessionId());

		this.players = players;
		onPlayersChanged();
	}

	@Override
	public final Rules rules() {
		return rules;
	}

	@Override
	public void rules(Rules rules) {
		this.rules = rules;
	}

	@Override
	public final NetworkLobbyPlayer localPlayer() {
		return localPlayer;
	}

	private void uploadName() {
		hostConnection.send(new SetNamePacket(null, (byte) 0, localPlayer.name()));
	}

	private void uploadReady() {
		hostConnection.send(new SetReadyStatePacket(null, (byte) 0, localPlayer.ready()));
	}

	public void startGame(Instant startTime, UDPConnection universeRegistrar) {
		var builder = gameSessionBuilder();
		builder.makeClientSession(universeRegistrar);

		builder.addPlayer(LocalGamePlayer.create(localPlayer, localController()));

		for (var player : players) {
			builder.addPlayer(NetworkGamePlayer.create(player));
		}

		rules().createMultiverse(builder.build(), startTime, new Random());
	}

	@Override
	public void close() {
		hostConnection.send(new LeaveSessionPacket(null));
		super.close();
	}
}
