package brownshome.unreasonableodds.gdx.screen;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import brownshome.unreasonableodds.Multiverse;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.network.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Affine2;

/**
 * A screen that displays a given multiverse
 */
public class MultiverseScreen extends SubScreen {
	public static final float SIZE_IN_PIXELS = 512f;
	private static final float UNIVERSE_SIZE = SIZE_IN_PIXELS * 12 / 16;
	private static final float INTER_UNIVERSE_STRIDE = SIZE_IN_PIXELS;

	private final ApplicationResources resources;
	private final Multiverse multiverse;
	private final OrthographicCamera camera;
	private final GdxPlayer player;

	MultiverseScreen(ApplicationResources resources, Multiverse multiverse, GdxPlayer player) {
		this.resources = resources;
		this.player = player;
		this.multiverse = multiverse;
		this.camera = new OrthographicCamera();
	}

	MultiverseScreen(ApplicationResources resources, GdxRules rules) {
		this.resources = resources;
		this.player = new GdxPlayer();
		this.multiverse = rules.createMultiverse(List.of(player), null, Instant.now(), new Random());
		this.camera = new OrthographicCamera();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		resources.batch().begin();
		resources.batch().setProjectionMatrix(camera.combined);

		multiverse.step(Duration.ofNanos((long) (delta * 1e9)));

		Affine2 transform = new Affine2();

		var universes = multiverse.universes();
		universes.sort(null);

		int index = 0;
		for (var universe : universes) {
			if (((GdxUniverse) universe).isActive()) {
				break;
			}

			index++;
		}

		assert index != universes.size();

		transform.scale(UNIVERSE_SIZE, UNIVERSE_SIZE);
		transform.translate(-0.5f, -0.5f);
		transform.preTranslate(-INTER_UNIVERSE_STRIDE * index, 0f);

		for (var universe : universes) {
			((GdxUniverse) universe).render(transform);

			transform.preTranslate(INTER_UNIVERSE_STRIDE, 0f);
		}

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
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		if (Session.getHost() != null) {
			Session.getHost().close();
		}
	}

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
