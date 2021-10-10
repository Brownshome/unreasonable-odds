package brownshome.unreasonableodds.gdx.screen;

import com.badlogic.gdx.*;

public abstract class SubScreen implements Screen, InputProcessor {
	private Screen nextScreen = this;
	private float width, height;

	protected final void nextScreen(Screen screen) {
		nextScreen = screen;
	}

	public final Screen nextScreen() {
		return nextScreen;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected final float width() {
		return width;
	}

	protected final float height() {
		return height;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
