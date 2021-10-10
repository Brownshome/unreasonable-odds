package brownshome.unreasonableodds.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public record ApplicationResources(
		SpriteBatch batch,
		BitmapFont font,
		TextureAtlas atlas,
		Skin skin
) implements Disposable {
	public ApplicationResources() {
		this(new SpriteBatch(),
				new BitmapFont(),
				new TextureAtlas("packed-textures.atlas"),
				new Skin(Gdx.files.internal("uiskin.json")));
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
