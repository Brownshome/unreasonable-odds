package brownshome.unreasonableodds.gdx;

import brownshome.unreasonableodds.Multiverse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * A screen that displays a given multiverse
 */
class MultiverseScreen extends SubScreen {
	private final ApplicationResources resources;

	MultiverseScreen(ApplicationResources resources, Multiverse multiverse) {
		this.resources = resources;
	}

	@Override
	public void show() { }

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void hide() { }

	@Override
	public void dispose() {	}
}
