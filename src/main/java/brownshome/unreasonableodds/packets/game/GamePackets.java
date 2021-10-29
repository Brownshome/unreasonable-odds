package brownshome.unreasonableodds.packets.game;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

import brownshome.netcode.Connection;
import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.history.BranchRecord;
import brownshome.unreasonableodds.packets.converters.EntityConverter;
import brownshome.unreasonableodds.packets.converters.UniverseConverter;
import brownshome.unreasonableodds.player.ImportedGamePlayer;
import brownshome.unreasonableodds.session.*;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "CreateGameSession")
	@MakeReliable
	static void createGameSession(@ConnectionParam Connection<?> connection,
	                              @UseConverter(ClientLobbySession.ClientLobbySessionConverter.class) ClientLobbySession session,
	                              Collection<SessionId> sessionIds) {
		var address = (InetSocketAddress) connection.address();

		sessionIds.add(new SessionId(SessionId.HOST_NUMBER, address));

		session.makeGameSession(sessionIds).markThreadAsSessionThread();
	}

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                      @UseConverter(UniverseConverter.WithMap.class) Universe universe) {
		session.startGame(session.rules().createMultiverse(List.of(universe), session, universe.beginning()));
	}

	@DefinePacket(name = "Universe")
	@MakeOrdered({ "ExportEntity", "Universe" })
	static void universe(@ConnectionParam Connection<?> connection,
	                     @UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                     @UseConverter(UniverseConverter.class) Universe universe) {
		var address = (InetSocketAddress) connection.address();
		session.receiveRemoteUniverse(universe, session.sessionId(address));
	}

	@DefinePacket(name = "RegisterNewUniverse")
	@MakeReliable
	static void registerNewUniverse(@ConnectionParam Connection<?> connection,
									@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                                Id universe,
	                                BranchRecord branchRecord) {
		var address = (InetSocketAddress) connection.address();
		session.registerNewUniverse(universe, branchRecord, session.sessionId(address));
	}

	@DefinePacket(name = "ExportEntity")
	@MakeReliable
	static void exportEntity(@ConnectionParam Connection<?> connection,
	                             @UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                             Id universe,
	                             @UseConverter(EntityConverter.class) Entity entity) {
		var player = (PlayerCharacter) entity;
		var address = (InetSocketAddress) connection.address();
		var playerId = player.playerId();

		if (!playerId.sessionId().equals(session.sessionId(address))) {
			throw new IllegalArgumentException("Universe jump commands must come from the controlling session");
		}

		session.importEntity(universe, player);
	}

	@DefinePacket(name = "PlayerUpdate")
	@MakeOrdered({ "PlayerUpdate" })
	static void playerUpdate(@ConnectionParam Connection<?> connection,
	                         @UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                         short playerId,
	                         @UseConverter(EntityConverter.class) List<Entity> newEntities) {
		var address = (InetSocketAddress) connection.address();
		var sessionId = session.sessionId(address);
		var id = new Id(sessionId, Short.toUnsignedInt(playerId));
		var importedPlayer = (ImportedGamePlayer) session.player(id);
		
		importedPlayer.setNext(newEntities);
	}
}
