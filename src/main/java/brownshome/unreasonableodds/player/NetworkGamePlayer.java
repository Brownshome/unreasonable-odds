package brownshome.unreasonableodds.player;

import java.util.concurrent.CompletableFuture;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.game.CreateGameSessionPacket;
import brownshome.unreasonableodds.packets.game.StartGamePacket;
import brownshome.unreasonableodds.session.Id;

public class NetworkGamePlayer extends GamePlayer implements NetworkPlayer {
	private final Id id;
	private final CompletableFuture<Void> gameStarted = new CompletableFuture<>();

	public static NetworkGamePlayer create(NetworkLobbyPlayer player) {
		return new NetworkGamePlayer(player.name(), player.id());
	}

	protected NetworkGamePlayer(String name, Id id) {
		super(name);

		this.id = id;
	}

	protected final CompletableFuture<Void> gameStarted() {
		return gameStarted;
	}

	public void startGame(Universe initialUniverse, UDPConnection connection) {
		connection.send(new CreateGameSessionPacket(null))
				.thenCompose(v -> connection.send(new StartGamePacket(null, initialUniverse)))
				.thenAccept(gameStarted::complete);
	}

	@Override
	public final Id id() {
		return id;
	}
}
