package brownshome.unreasonableodds.net;

import java.util.List;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.session.*;

final class Packets {
	private static final System.Logger LOGGER = System.getLogger(Packets.class.getModule().toString());

	private Packets() { }

	/**
	 * Sets the name of a player, creating the player if it does not exist
	 * @param connection the connection
	 * @param name the name of the player
	 */
	@DefinePacket(name = "SetName")
	@MakeReliable
	@MakeOrdered({ "SetName" })
	static void setName(@ConnectionParam Connection<?> connection, String name) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Set the name of %s to %s".formatted(connection.address(), name));

		var session = (HostSession) Session.getHost();
		var udpConnection = (UDPConnection) connection;

		session.setPlayerName(udpConnection.address(), name);
	}

	/**
	 * Sets the ready-state of a player
	 * @param connection the connection
	 * @param ready the ready state of this player
	 */
	@DefinePacket(name = "SetReadyState")
	@MakeReliable
	@MakeOrdered({ "SetReadyState" })
	static void setReadyState(@ConnectionParam Connection<?> connection, boolean ready) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Set the ready state of %s to %s".formatted(connection.address(), ready));

		var session = (HostSession) Session.getHost();
		var udpConnection = (UDPConnection) connection;

		session.setReadyState(udpConnection.address(), ready);
	}

	/**
	 * Updates the client player list
	 * @param players the player list
	 */
	@DefinePacket(name = "SendPlayers")
	@MakeReliable
	@MakeOrdered({ "SendPlayers" })
	static void setClientNames(@UseConverter(Session.PlayerConverter.class) List<Session.Player> players) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Received client name list %s".formatted(players));

		var session = (ClientSession) Session.getHost();
		session.players(players);
	}

	/**
	 * Notifies the client or host that the other end of this connection has left the session
	 * @param connection the other end that has left
	 */
	@DefinePacket(name = "LeaveSession")
	@MakeReliable
	@MakeOrdered({ "SetName", "SetReadyState", "SendPlayers", "LeaveSession" })
	static void leaveSession(@ConnectionParam Connection<?> connection) {
		switch (Session.getHost()) {
			case HostSession session -> session.removePlayer(((UDPConnection) connection).address());
			case ClientSession session -> session.hostLeft();
			default -> throw new IllegalStateException("No session currently running");
		}
	}
}
