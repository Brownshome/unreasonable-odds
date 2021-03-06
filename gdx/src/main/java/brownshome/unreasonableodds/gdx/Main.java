package brownshome.unreasonableodds.gdx;

import browngu.logging.Logger;
import browngu.logging.Output;
import brownshome.unreasonableodds.gdx.logger.GdxLogger;
import brownshome.unreasonableodds.gdx.screen.SubScreen;
import brownshome.unreasonableodds.gdx.screen.TopMenuScreen;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main extends Game {
	private ApplicationResources resources;

	public static void main(String... arg) {
		Logger.logger().setLoggingOutputs(new Output(System.out, System.Logger.Level.INFO.getSeverity()));

		Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
			Logger.logger().log(exception, "Uncaught exception", exception);
			System.exit(-1);
		});

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;

		new LwjglApplication(new Main(), config) {
			@Override
			public void setApplicationLogger(ApplicationLogger applicationLogger) {
				// Override their choice of logger
				logLevel = LOG_DEBUG;
				super.setApplicationLogger(new GdxLogger());
			}
		};
	}

	@Override
	public void create () {
		resources = new ApplicationResources();

		setScreen(new TopMenuScreen(resources));
	}

	@Override
	public void render() {
		super.render();

		if (getScreen() instanceof SubScreen screen && screen.nextScreen() != screen) {
			setScreen(screen.nextScreen());
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		resources.dispose();
	}
}
