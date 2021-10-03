package brownshome.unreasonableodds.gdx.tile;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.AABBCollisionShape;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.unreasonableodds.tile.ClosedTile;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.math.Affine2;

public class GdxClosedTile extends ClosedTile implements Renderable {
	private static final TextureRegionCache TEXTURE_REGION_CACHE = new TextureRegionCache("wall");

	private final RenderComponent renderComponent;

	protected GdxClosedTile(AABBCollisionShape aabb, RenderComponent renderComponent) {
		super(aabb);
		this.renderComponent = renderComponent;
	}

	public static GdxClosedTile createTile(Vec2 lesser, Vec2 greater, ApplicationResources resources) {
		var scale = greater.copy();
		scale.subtract(lesser);

		return new GdxClosedTile(new AABBCollisionShape(lesser, greater),
				new RenderComponent(resources,
						TEXTURE_REGION_CACHE.getTextureRegion(resources.atlas()),
						scale,
						new Position(lesser, Rot2.IDENTITY)));
	}

	@Override
	public void render(Affine2 transform) {
		renderComponent.render(transform);
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);
		((GdxUniverse.Builder) builder).addRenderable(this);
	}
}
