package brownshome.unreasonableodds.session.net;

import java.time.Instant;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.ConnectionParam;
import brownshome.netcode.annotation.DefinePacket;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.session.HostSession;

final class TimeSyncPackets {
	private TimeSyncPackets() { }

	@DefinePacket(name = "RequestTimeSync")
	static void syncTime(@ConnectionParam Connection<?> connection) {
		connection.send(new TimeSyncPacket(Instant.now()));
	}

	@DefinePacket(name = "TimeSync")
	static void syncTime(@ConnectionParam Connection<?> connection, @UseConverter(InstantConverter.class) Instant now) {
		var session = HostSession.getHost();
		var udpConnection = (UDPConnection) connection;

		session.completeTimeSync(udpConnection.address(), now);
	}
}
