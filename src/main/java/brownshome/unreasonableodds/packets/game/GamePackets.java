package brownshome.unreasonableodds.packets.game;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Random;

import brownshome.netcode.annotation.DefinePacket;
import brownshome.netcode.annotation.MakeReliable;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.packets.converters.*;
import brownshome.unreasonableodds.session.NetworkGameSession;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "SetEpoch")
	@MakeReliable
	static void setEpoch(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                     @UseConverter(InstantConverter.class) Instant startTime) {
		session.rules().createMultiverse(session, startTime, new Random());
	}

	@DefinePacket(name = "Universe")
	static void universe(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                     @UseConverter(UniverseConverter.class) Universe universe) {
		session.universe(universe);
	}

	@DefinePacket(name = "SetUniverseHost")
	@MakeReliable
	static void setUniverseHost(@UseConverter(NetworkGameSession.NetworkGameSessionConverter.class) NetworkGameSession session,
	                            @UseConverter(UniverseIdConverter.class) Universe.Id universe,
	                            @UseConverter(InetSocketAddressConverter.class) InetSocketAddress address) {
		session.setUniverseHost(universe, address);
	}
}
