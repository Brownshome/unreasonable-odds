package brownshome.unreasonableodds.gdx.session;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.LobbySession;
import brownshome.unreasonableodds.session.NetworkSession;

public interface GdxLobbySession extends LobbySession {
	static GdxLobbySession get() {
		return (GdxLobbySession) NetworkSession.get();
	}

	ApplicationResources applicationResources();
}
