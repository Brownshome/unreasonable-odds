package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;

public class ErrorScreen extends StageScreen {
	private final Throwable throwable;

	public ErrorScreen(Throwable throwable, ApplicationResources resources) {
		super(resources);
		this.throwable = throwable;
	}
}
