package brownshome.unreasonableodds.packets.session;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.session.ClientLobbySession;
import brownshome.unreasonableodds.session.NetworkSession;

final class SessionManagement {
	private static final System.Logger LOGGER = System.getLogger(SessionManagement.class.getModule().toString());

	private SessionManagement() { }

	/**
	 * Notifies the session of another connection session closing the connection. The connection will be closed shortly after
	 * this packet is sent.
	 * @param connection the connection
	 * @param session the current session
	 */
	@DefinePacket(name = "LeaveSession")
	@MakeReliable
	static void leaveSession(@ConnectionParam Connection<?> connection,
	                         @UseConverter(NetworkSession.NetworkSessionConverter.class) NetworkSession session) {
		LOGGER.log(System.Logger.Level.TRACE, "Address {0} left the session", connection.address());

		session.sessionLeft(session.sessionId(((UDPConnection) connection).address()));
	}
}
