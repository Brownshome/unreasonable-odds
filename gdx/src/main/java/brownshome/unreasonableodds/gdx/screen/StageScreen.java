package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StageScreen extends SubScreen {
	private final Stage stage;

	public StageScreen(ApplicationResources resources) {
		stage = new Stage(new ScreenViewport(), resources.batch());
	}

	public final Stage stage() {
		return stage;
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
