package brownshome.unreasonableodds.net;

import java.time.Instant;

import brownshome.netcode.annotation.DefinePacket;
import brownshome.netcode.annotation.MakeReliable;
import brownshome.netcode.annotation.converter.UseConverter;
import brownshome.unreasonableodds.session.ClientSession;
import brownshome.unreasonableodds.session.net.InstantConverter;

final class GamePackets {
	private GamePackets() { }

	@DefinePacket(name = "StartGame")
	@MakeReliable
	static void startGame(@UseConverter(InstantConverter.class) Instant startTime) {
		ClientSession.getHost().startGame(startTime);
	}
}
