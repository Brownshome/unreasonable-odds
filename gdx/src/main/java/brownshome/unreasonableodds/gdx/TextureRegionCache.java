package brownshome.unreasonableodds.gdx;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class TextureRegionCache {
	private final String regionName;

	private TextureAtlas atlas;
	private TextureRegion cachedRegion = null;

	public TextureRegionCache(String regionName) {
		this.regionName = regionName;
	}

	public TextureRegion getTextureRegion(TextureAtlas atlas) {
		if (this.atlas == atlas) {
			return cachedRegion;
		}

		this.atlas = atlas;

		return cachedRegion = atlas.findRegion(regionName);
	}
}
