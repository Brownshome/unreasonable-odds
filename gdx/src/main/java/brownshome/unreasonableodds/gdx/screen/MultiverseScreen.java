package brownshome.unreasonableodds.gdx.screen;

import java.time.Duration;
import java.util.List;

import brownshome.unreasonableodds.Multiverse;
import brownshome.unreasonableodds.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * A screen that displays a given multiverse
 */
public class MultiverseScreen extends SubScreen {
	public static final float SIZE_IN_PIXELS = 512f;

	private final ApplicationResources resources;
	private final GdxMultiverse multiverse;
	private final OrthographicCamera camera;
	private final GdxPlayer player;

	MultiverseScreen(ApplicationResources resources, GdxMultiverse multiverse, GdxPlayer player) {
		this.resources = resources;
		this.player = player;
		this.multiverse = multiverse;
		this.camera = new OrthographicCamera();
	}

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
		super.resize(width, height);

		if (width > height) {
			camera.setToOrtho(false, SIZE_IN_PIXELS * width / height, SIZE_IN_PIXELS);
		} else {
			camera.setToOrtho(false, SIZE_IN_PIXELS, SIZE_IN_PIXELS * height / width);
		}

		camera.position.set(0f, 0f, 0f);
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
