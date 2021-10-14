package brownshome.unreasonableodds.packets;

import java.net.InetSocketAddress;
import java.time.Instant;

import brownshome.netcode.annotation.*;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.network.*;
import brownshome.unreasonableodds.packets.converters.*;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@UseConverter(InstantConverter.class) Instant startTime, @UseConverter(RulesConverter.class) Rules rules) {
		ClientSession.getHost().startGame(rules, startTime);
	}

	@DefinePacket(name = "Universe")
	static void universe(@UseConverter(UniverseConverter.class) Universe universe) {
		ClientSession.getHost().network().setUniverse(universe);
	}

	@DefinePacket(name = "SetUniverseHost")
	@MakeReliable
	static void setUniverseHost(@UseConverter(UniverseIdConverter.class) Universe.Id universe, @UseConverter(InetSocketAddressConverter.class) InetSocketAddress address) {
		switch (Session.getHost()) {
			case ClientSession session -> session.setUniverseHost(universe, address);
			case HostSession session -> session.setUniverseHost(universe, address);
			default -> throw new AssertionError("Unexpected session: " + Session.getHost());
		}
	}
}
