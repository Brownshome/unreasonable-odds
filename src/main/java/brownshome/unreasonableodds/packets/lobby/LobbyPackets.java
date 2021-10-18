package brownshome.unreasonableodds.packets.lobby;

import java.util.Collection;
import java.util.List;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.netcode.udp.UDPConnection;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.packets.converters.RulesConverter;
import brownshome.unreasonableodds.player.*;
import brownshome.unreasonableodds.session.*;

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

		int sessionId = session.getOrMakeSessionId(udpConnection.address());
		Id id = new Id(sessionId, Byte.toUnsignedInt(number));

		session.getOrMakePlayer(id, udpConnection.address()).name(name);
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

		int sessionId = session.sessionId(udpConnection.address());
		Id id = new Id(sessionId, Byte.toUnsignedInt(number));

		session.player(id).ready(ready);
	}

	/**
	 * Updates the client player list
	 * @param players the player list
	 */
	@DefinePacket(name = "SendPlayers")
	@MakeReliable
	static void setPlayers(@UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                       Collection<NetworkLobbyPlayer> players) {
		LOGGER.log(System.Logger.Level.TRACE, () -> "Received client name list %s".formatted(players));

		session.players((List<NetworkLobbyPlayer>) players);
	}

	@DefinePacket(name = "SetRules")
	@MakeReliable
	static void setRules(@UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                     @UseConverter(RulesConverter.class) Rules rules) {
		session.rules(rules);
	}

	@DefinePacket(name = "SetSessionId")
	@MakeReliable
	static void setSessionId(@UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                         byte sessionId) {
		session.sessionId(Byte.toUnsignedInt(sessionId));
	}
}
