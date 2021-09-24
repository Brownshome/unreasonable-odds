package brownshome.unreasonableodds.gdx;

import browngu.logging.Logger;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main extends Game {
	private ApplicationResources resources;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;

		new LwjglApplication(new Main(), config);
	}

	@Override
	public void create () {
		resources = new ApplicationResources();

		setScreen(new CreateMultiverseScreen(resources));
	}

	@Override
	public void render() {
		super.render();

		if (getScreen() instanceof SubScreen screen && screen.nextScreen() != screen) {
			setScreen(screen.nextScreen());
		}
	}

	@Override
	public void dispose () {
		super.dispose();

		resources.dispose();
	}
}
