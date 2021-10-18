package brownshome.unreasonableodds.packets.lobby;

import java.time.Instant;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.ConnectionParam;
import brownshome.netcode.annotation.DefinePacket;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.packets.converters.InstantConverter;
import brownshome.unreasonableodds.session.*;

final class TimeSyncPackets {
	private TimeSyncPackets() { }

	@DefinePacket(name = "RequestTimeSync")
	static void syncTime(@ConnectionParam Connection<?> connection,
	                     @UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session) {
		connection.send(new TimeSyncPacket(null, (byte) session.localPlayer().id().number(), Instant.now()));
	}

	@DefinePacket(name = "TimeSync")
	static void syncTime(@ConnectionParam Connection<?> connection,
						 @UseConverter(HostLobbySession.HostLobbySessionConverter.class) HostLobbySession session,
	                     byte id,
						 @UseConverter(InstantConverter.class) Instant now) {
		var udpConnection = (UDPConnection) connection;
		session.player(new Id(session.sessionId(udpConnection.address()), Byte.toUnsignedInt(id)))
				.completeTimeSync(now);
	}
}
