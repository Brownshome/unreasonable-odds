package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.util.List;

import brownshome.unreasonableodds.Multiverse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * A screen that displays a given multiverse
 */
class MultiverseScreen extends SubScreen {
	private final ApplicationResources resources;
	private final GdxMultiverse multiverse;
	private final OrthographicCamera camera;
	private final GdxPlayer player;

	MultiverseScreen(ApplicationResources resources, GdxRules rules) {
		this.resources = resources;
		this.player = new GdxPlayer();
		this.multiverse = (GdxMultiverse) rules.createMultiverse(List.of(player));
		this.camera = new OrthographicCamera();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		resources.batch().begin();
		resources.batch().setProjectionMatrix(camera.combined);

		multiverse.step(Duration.ofNanos((long) (delta * 1e9)));
		multiverse.render();

		resources.batch().end();
	}

	@Override
	public void resize(int width, int height) {
		if (width > height) {
			camera.setToOrtho(false, (float) width / height, 1f);
		} else {
			camera.setToOrtho(false, 1f, (float) height / width);
		}

		camera.position.set(0.5f, 0.5f, 0f);
		camera.update();
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void hide() { }

	@Override
	public void dispose() {	}

	@Override
	public boolean keyDown(int keycode) {
		return switch (keycode) {
			case Input.Keys.F -> {
				player.timeTravel();
				yield true;
			}

			default -> false;
		};
	}
}
