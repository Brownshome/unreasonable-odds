package brownshome.unreasonableodds.packets.game;

import java.net.InetSocketAddress;
import java.util.List;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.converters.InetSocketAddressConverter;
import brownshome.unreasonableodds.packets.converters.UniverseConverter;
import brownshome.unreasonableodds.session.*;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "CreateGameSession")
	static void createGameSession(@ConnectionParam Connection<?> connection,
	                              @UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session) {
		var udpConnection = (UDPConnection) connection;
		session.makeGameSession(udpConnection);
	}

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                      @UseConverter(UniverseConverter.class) Universe universe) {
		session.startGame(session.rules().createMultiverse(List.of(universe), session, universe.beginning()));
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
