package brownshome.unreasonableodds.net;

import java.util.List;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.session.*;

final class Packets {
	private static final System.Logger LOGGER = System.getLogger(Packets.class.getModule().toString());

	@DefinePacket(name = "SetName")
	@MakeReliable
	@MakeOrdered({ "SetName" })
	void setName(@ConnectionParam Connection<?> connection, String name) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Set the name of %s to %s".formatted(connection.address(), name));

		var session = (HostSession) Session.getHost();
		var udpConnection = (UDPConnection) connection;

		session.setPlayerName(udpConnection.address(), name);
	}

	@DefinePacket(name = "SetNames")
	@MakeReliable
	@MakeOrdered({ "SetNames" })
	void setClientNames(List<String> names) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Receive client name list %s".formatted(names));

		var session = (ClientSession) Session.getHost();
		session.players(names);
	}

	@DefinePacket(name = "LeaveSession")
	@MakeReliable
	@MakeOrdered({ "SetName", "SetNames", "LeaveSession" })
	void leaveSession(@ConnectionParam Connection<?> connection) {
		switch (Session.getHost()) {
			case HostSession session -> session.removePlayer(((UDPConnection) connection).address());
			case ClientSession session -> session.hostLeft();
			default -> throw new IllegalStateException("No session currently running");
		}
	}
}
