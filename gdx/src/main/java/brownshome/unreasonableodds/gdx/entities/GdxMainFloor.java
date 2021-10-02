package brownshome.unreasonableodds.gdx.entities;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.MainFloor;
import brownshome.unreasonableodds.entites.tile.Tile;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.gdx.components.Renderable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

public class GdxMainFloor extends MainFloor implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("wall");

	private final TextureRegion wallTexture;
	private final ApplicationResources resources;

	protected GdxMainFloor(Tile[] tiles, TextureRegion wallTexture, ApplicationResources resources) {
		super(tiles);
		this.wallTexture = wallTexture;
		this.resources = resources;
	}

	public static GdxMainFloor createMainFloor(Tile[] tiles, ApplicationResources resources) {
		return new GdxMainFloor(tiles, REGION_CACHE.getTextureRegion(resources.atlas()), resources);
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);
		((GdxUniverse.Builder) builder).addRenderable(this);
	}

	@Override
	public void render(Affine2 transform) {
		Affine2 localTransform = new Affine2();

		for (var tile : tiles()) {
			var scale = tile.greaterExtent().copy();
			scale.subtract(tile.lesserExtent());

			localTransform.setToTranslation((float) tile.lesserExtent().x(),
					(float) tile.lesserExtent().y());
			localTransform.preMul(transform);

			resources.batch().draw(wallTexture, (float) scale.x(), (float) scale.y(), localTransform);
		}
	}
}
