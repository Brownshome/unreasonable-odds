package brownshome.unreasonableodds.gdx.session;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.Session;

public interface GdxSession {
	static GdxSession getHost() {
		return (GdxSession) Session.getHost();
	}

	ApplicationResources applicationResources();
}
