package brownshome.unreasonableodds.network;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.network.packets.RequestTimeSyncPacket;
import brownshome.unreasonableodds.packets.StartGamePacket;

public final class UDPSessionPlayer extends SessionPlayer {
	private final NetworkControlledPlayer networkPlayer;
	private final UDPConnection connection;

	private Instant timeSyncRoot;
	private final CompletableFuture<Duration> timeOffset = new CompletableFuture<>();

	static UDPSessionPlayer makeHost(String name, UDPConnection connection) {
		return new UDPSessionPlayer(name, true, true, connection);
	}

	static UDPSessionPlayer makeClient(String name, UDPConnection connection) {
		return new UDPSessionPlayer(name, false, false, connection);
	}

	UDPSessionPlayer(String name, boolean isHost, boolean isReady, UDPConnection connection) {
		super(name, isHost, isReady);

		networkPlayer = new NetworkControlledPlayer();
		this.connection = connection;
	}

	public NetworkControlledPlayer networkPlayer() {
		return networkPlayer;
	}

	/**
	 * This is populated with the offset of this player's clock compared to the host session clock
	 *
	 * @return a future of this clock
	 */
	public CompletableFuture<Duration> timeOffset() {
		return timeOffset;
	}

	public void startTimeSync() {
		timeSyncRoot = Instant.now();
		connection.send(new RequestTimeSyncPacket());
	}

	public void completeTimeSync(Instant remoteTime) {
		var outgoingDelta = Duration.between(timeSyncRoot, remoteTime);
		var returnDelta = Duration.between(remoteTime, Instant.now());

		var clockOffset = outgoingDelta.minus(returnDelta).dividedBy(2);
		var latency = outgoingDelta.plus(returnDelta).dividedBy(2);

		timeOffset.complete(clockOffset);

		/*
		 * TODO james.brown [12-10-2021] often times will be set 'correctly' so the real clock offset will be as close to
		 *                               zero as is possible. This method should check if the clock offset is within the
		 *                               margin of error for synchronisation and accept it as zero. This sequence is to
		 *                               guard against issues such as someone setting their internal clock wrong, not attempting
		 *                               to correct inaccuracies in OS time, which will often be lower than we can reach
		 *                               through this networking abstraction anyway.
		 */
	}

	public void startGame(Instant timeToStart, Rules rules) {
		connection.connect(UDPSession.gameSchema());
		connection.send(new StartGamePacket(timeToStart.plus(timeOffset.getNow(null)), rules));
	}
}
