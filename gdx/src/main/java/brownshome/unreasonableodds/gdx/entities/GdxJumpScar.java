package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.JumpScar;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;

public class GdxJumpScar extends JumpScar implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("jump-scar");
	private static final Vec2 SCAR_SIZE = Vec2.of(40, 40);

	private final RenderComponent renderComponent;

	protected GdxJumpScar(Vec2 position, Duration jumpScarDuration, ApplicationResources resources) {
		super(position, jumpScarDuration);

		this.renderComponent = new RenderComponent(resources,
				REGION_CACHE.getTextureRegion(resources.atlas()),
				SCAR_SIZE,
				new Position(position, Rot2.IDENTITY));
	}

	@Override
	public final RenderComponent renderComponent() {
		return renderComponent;
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);
		((GdxUniverse.Builder) builder).addRenderable(this);
	}
}
