package brownshome.unreasonableodds.packets.game;

import java.net.InetSocketAddress;
import java.time.Instant;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.converters.*;
import brownshome.unreasonableodds.session.*;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@ConnectionParam Connection<?> connection,
	                      @UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                      @UseConverter(InstantConverter.class) Instant startTime) {

		var udpConnection = (UDPConnection) connection;
		session.startGame(startTime, udpConnection);
	}

	@DefinePacket(name = "Universe")
	@MakeOrdered({ "StartGame" })
	static void universe(@ConnectionParam Connection<?> connection,
	                     @UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                     @UseConverter(UniverseConverter.class) Universe universe) {

		var address = (InetSocketAddress) connection.address();
		session.universe(universe, address);
	}

	@DefinePacket(name = "SetUniverseHost")
	@MakeReliable
	@MakeOrdered({ "StartGame" })
	static void setUniverseHost(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                            Id universe,
	                            @UseConverter(InetSocketAddressConverter.class) InetSocketAddress address) {
		session.setUniverseHost(universe, address);
	}
}
