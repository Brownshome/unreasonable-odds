package brownshome.unreasonableodds.player;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.packets.lobby.RequestTimeSyncPacket;

/**
 * A lobby player that represents a client on the host session.
 */
public class ImportedLobbyPlayer extends NetworkLobbyPlayer {
	/**
	 * The connection to the client session
	 */
	private final UDPConnection connection;

	/**
	 * The time at which the sync packet was sent
	 */
	private Instant timeSyncRoot;
	private Duration timeOffset;
	private Duration latency;

	private final CompletableFuture<Void> timeSyncComplete = new CompletableFuture<>();

	protected ImportedLobbyPlayer(String name, int number, UDPConnection connection) {
		super(name, false, false, new Id(connection.address(), number));

		this.connection = connection;
	}

	public final UDPConnection connection() {
		return connection;
	}

	/**
	 * Signals to this client to start the time-synchronisation process
	 */
	public final void timeSync() {
		timeSyncRoot = Instant.now();
		connection.send(new RequestTimeSyncPacket(null));
	}

	public final CompletableFuture<Void> timeSyncComplete() {
		return timeSyncComplete;
	}

	public final Duration timeOffset() {
		return timeOffset;
	}

	public final Duration latency() {
		return latency;
	}

	public final void completeTimeSync(Instant remoteTime) {
		var outgoingDelta = Duration.between(timeSyncRoot, remoteTime);
		var returnDelta = Duration.between(remoteTime, Instant.now());

		timeOffset = outgoingDelta.minus(returnDelta).dividedBy(2);
		latency = outgoingDelta.plus(returnDelta).dividedBy(2);

		timeSyncComplete.complete(null);

		/*
		 * TODO james.brown [12-10-2021] often times will be set 'correctly' so the real clock offset will be as close to
		 *                               zero as is possible. This method should check if the clock offset is within the
		 *                               margin of error for synchronisation and accept it as zero. This sequence is to
		 *                               guard against issues such as someone setting their internal clock wrong, not attempting
		 *                               to correct inaccuracies in OS time, which will often be lower than we can reach
		 *                               through this networking abstraction anyway.
		 */
	}
}
