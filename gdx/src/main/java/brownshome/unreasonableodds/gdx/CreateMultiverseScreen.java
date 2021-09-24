package brownshome.unreasonableodds.gdx;

import java.util.List;

import brownshome.unreasonableodds.entites.Character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;

final class CreateMultiverseScreen extends SubScreen {
	private final ApplicationResources resources;
	private final OrthographicCamera camera;

	CreateMultiverseScreen(ApplicationResources resources) {
		this.resources = resources;
		camera = new OrthographicCamera();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		resources.batch().setProjectionMatrix(camera.combined);

		resources.batch().begin();
		resources.font().draw(resources.batch(),
				"Press to start a new multiverse...",
				0,
				height() / 2,
				width(),
				Align.center,
				true);
		resources.batch().end();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		nextScreen(new MultiverseScreen(resources, new GdxRules(resources)));

		return true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		camera.setToOrtho(false, width, height);
		camera.update();
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void hide() { }

	@Override
	public void dispose() { }
}
