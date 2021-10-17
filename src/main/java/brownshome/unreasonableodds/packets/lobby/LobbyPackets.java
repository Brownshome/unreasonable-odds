package brownshome.unreasonableodds.packets.lobby;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.packets.converters.LobbyPlayerProxy;
import brownshome.unreasonableodds.packets.converters.RulesConverter;
import brownshome.unreasonableodds.player.NetworkPlayer;
import brownshome.unreasonableodds.session.ClientLobbySession;
import brownshome.unreasonableodds.session.HostLobbySession;

final class LobbyPackets {
	private static final System.Logger LOGGER = System.getLogger(LobbyPackets.class.getModule().toString());

	private LobbyPackets() { }

	/**
	 * Sets the name of a player, creating the player if it does not exist
	 * @param connection the connection
	 * @param name the name of the player
	 */
	@DefinePacket(name = "SetName")
	@MakeReliable
	static void setName(@ConnectionParam Connection<?> connection,
						@UseConverter(HostLobbySession.HostLobbySessionConverter.class) HostLobbySession session,
	                    byte number,
						String name) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Set the name of %s to %s".formatted(connection.address(), name));

		var udpConnection = (UDPConnection) connection;
		var id = new NetworkPlayer.Id(udpConnection.address(), Byte.toUnsignedInt(number));

		session.getOrMakePlayer(id).name(name);
	}

	/**
	 * Sets the ready-state of a player
	 * @param connection the connection
	 * @param ready the ready state of this player
	 */
	@DefinePacket(name = "SetReadyState")
	@MakeReliable
	static void setReady(@ConnectionParam Connection<?> connection,
	                    @UseConverter(HostLobbySession.HostLobbySessionConverter.class) HostLobbySession session,
	                    byte number,
	                    boolean ready) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Set the ready state of %s to %s".formatted(connection.address(), ready));

		var udpConnection = (UDPConnection) connection;
		var id = new NetworkPlayer.Id(udpConnection.address(), Byte.toUnsignedInt(number));

		session.getPlayer(id).ready(ready);
	}

	/**
	 * Updates the client player list
	 * @param players the player list
	 */
	@DefinePacket(name = "SendPlayers")
	@MakeReliable
	static void setPlayers(@ConnectionParam Connection<?> connection,
	                           @UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                           Collection<LobbyPlayerProxy> players) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Received client name list %s".formatted(players));

		var udpConnection = (UDPConnection) connection;
		session.exportedPlayers(players.stream().map(proxy -> proxy.toExported(udpConnection.address())).collect(Collectors.toList()));
	}

	@DefinePacket(name = "SetRules")
	@MakeReliable
	static void setRules(@UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                     @UseConverter(RulesConverter.class) Rules rules) {
		session.rules(rules);
	}
}
