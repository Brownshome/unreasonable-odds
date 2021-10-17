package brownshome.unreasonableodds.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executor;

import brownshome.netcode.udp.UDPConnection;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.packets.lobby.*;
import brownshome.unreasonableodds.packets.session.LeaveSessionPacket;
import brownshome.unreasonableodds.player.ExportedLobbyPlayer;
import brownshome.unreasonableodds.player.NetworkLobbyPlayer;

public class ClientLobbySession extends NetworkLobbySession implements LocalPlayerLobbySession {
	public static final class ClientLobbySessionConverter extends SessionConverter<ClientLobbySession> {}

	private final UDPConnection hostConnection;

	private List<ExportedLobbyPlayer> players;
	private final NetworkLobbyPlayer localPlayer;

	private Rules rules;

	protected ClientLobbySession(String name, InetSocketAddress address, Executor executor) throws IOException {
		super(new UDPConnectionManager(allSchema()), executor);


		this.hostConnection = connectionManager().getOrCreateConnection(address);
		this.players = new ArrayList<>();
		this.localPlayer = new NetworkLobbyPlayer(name, false, false, allocatePlayerId()) {
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
	public final Collection<? extends NetworkLobbyPlayer> players() {
		var result = new ArrayList<NetworkLobbyPlayer>(players);
		result.add(localPlayer);
		result.sort(null);

		return result;
	}

	public final void exportedPlayers(List<ExportedLobbyPlayer> players) {
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
		hostConnection.send(new SetNamePacket(null, (byte) localPlayer.id().number(), localPlayer.name()));
	}

	private void uploadReady() {
		hostConnection.send(new SetReadyStatePacket(null, (byte) localPlayer.id().number(), localPlayer.ready()));
	}

	@Override
	public void close() {
		hostConnection.send(new LeaveSessionPacket(null));
		super.close();
	}
}
