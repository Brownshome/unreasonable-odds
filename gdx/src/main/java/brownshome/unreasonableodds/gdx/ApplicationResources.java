package brownshome.unreasonableodds.gdx;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Disposable;

public record ApplicationResources(SpriteBatch batch, BitmapFont font, TextureAtlas atlas) implements Disposable {
	public ApplicationResources() {
		this(new SpriteBatch(), new BitmapFont(), new TextureAtlas("packed-textures.atlas"));
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
