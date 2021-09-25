package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.unreasonableodds.history.BranchRecord;
import brownshome.unreasonableodds.history.History;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.math.Affine2;

public class GdxUniverse extends Universe implements Renderable {
	private static final TextureRegionCache TEXTURE_REGION_CACHE = new TextureRegionCache("universe-background");
	private static final Vec2 SIZE = Vec2.of(1.0, 1.0);

	private final List<Renderable> renderables;
	private final RenderComponent renderComponent;

	protected GdxUniverse(Instant now, List<Entity> entities, History previousHistory, BranchRecord branchRecord, List<Renderable> renderables, RenderComponent renderComponent) {
		super(now, entities, previousHistory, branchRecord);

		this.renderables = renderables;
		this.renderComponent = renderComponent;
	}

	public static GdxUniverse createEmptyUniverse(Instant beginning, ApplicationResources resources) {
		var renderComponent = new RenderComponent(resources, TEXTURE_REGION_CACHE.getTextureRegion(resources.atlas()), SIZE, new Position(Vec2.ZERO, Rot2.IDENTITY));

		return new GdxUniverse(beginning, Collections.emptyList(), History.blankHistory(), BranchRecord.blankRecord(beginning), Collections.emptyList(), renderComponent);
	}

	public final List<Renderable> renderables() {
		return renderables;
	}

	@Override
	public RenderComponent renderComponent() {
		return renderComponent;
	}

	public class Builder extends Universe.Builder {
		private final List<Renderable> renderables;

		public Builder(Instant now, BranchRecord branchRecord) {
			super(now, branchRecord);

			renderables = new ArrayList<>();
		}

		public final void addRenderable(Renderable renderable) {
			renderables.add(renderable);
		}

		@Override
		public Universe build() {
			return new GdxUniverse(now(), entities(), history(), branchRecord(), renderables, renderComponent);
		}
	}

	@Override
	protected Builder builder(Instant now, BranchRecord branchRecord) {
		return new Builder(now, branchRecord);
	}

	public final void render(Affine2 transform) {
		renderComponent.render(transform);

		for (Renderable renderable : renderables) {
			renderable.render(transform);
		}
	}
}
