package brownshome.unreasonableodds.gdx.logger;

import browngu.logging.Logger;
import browngu.logging.Severity;
import com.badlogic.gdx.ApplicationLogger;

public class GdxLogger implements ApplicationLogger {
	@Override
	public void log(String tag, String message) {
		Logger.logger().log(Severity.INFO, "[%s] %s", tag, message);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		Logger.logger().log(Severity.INFO, exception, "[%s] %s", tag, message);
	}

	@Override
	public void error(String tag, String message) {
		Logger.logger().log(Severity.ERROR, "[%s] %s", tag, message);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		Logger.logger().log(Severity.ERROR, exception, "[%s] %s", tag, message);
	}

	@Override
	public void debug(String tag, String message) {
		Logger.logger().log(Severity.DEBUG, "[%s] %s", tag, message);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		Logger.logger().log(Severity.DEBUG, exception, "[%s] %s", tag, message);
	}
}
