package brownshome.unreasonableodds.gdx;

import java.time.Instant;
import java.util.*;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.collision.CollisionDetector;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.unreasonableodds.history.BranchRecord;
import brownshome.unreasonableodds.history.History;
import brownshome.unreasonableodds.session.Id;
import brownshome.vecmath.Rot2;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.math.Affine2;

public class GdxUniverse extends Universe implements Renderable {
	private static final TextureRegionCache TEXTURE_REGION_CACHE = new TextureRegionCache("universe-background");
	private static final Vec2 SIZE = Vec2.of(1.0, 1.0);

	private final List<Renderable> renderables;
	private final RenderComponent renderComponent;
	private final boolean isActive;

	protected GdxUniverse(Id id,
	                      Instant now,
	                      List<Entity> entities,
	                      History previousHistory,
	                      BranchRecord branchRecord,
	                      CollisionDetector collisionDetector,
	                      List<Renderable> subComponents,
	                      RenderComponent renderComponent,
	                      boolean isActive) {
		super(id, now, entities, previousHistory, branchRecord, collisionDetector);

		this.renderables = subComponents;
		this.renderComponent = renderComponent;
		this.isActive = isActive;
	}

	public static GdxUniverse createEmptyUniverse(Id id, Instant beginning, ApplicationResources resources) {
		var renderComponent = new RenderComponent(resources, TEXTURE_REGION_CACHE.getTextureRegion(resources.atlas()), SIZE, new Position(Vec2.ZERO, Rot2.IDENTITY));

		return new GdxUniverse(id,
				beginning,
				Collections.emptyList(),
				History.blankHistory(),
				BranchRecord.blankRecord(beginning),
				CollisionDetector.createDetector(),
				Collections.emptyList(),
				renderComponent,
				true);
	}

	public final List<Renderable> renderables() {
		return renderables;
	}

	public final RenderComponent renderComponent() {
		return renderComponent;
	}

	public final boolean isActive() {
		return isActive;
	}

	public class Builder extends Universe.Builder {
		private final List<Renderable> renderables;
		private boolean isActive = false;

		public Builder(Instant now, BranchRecord branchRecord) {
			super(now, branchRecord);

			renderables = new ArrayList<>();
		}

		public final void addRenderable(Renderable renderable) {
			renderables.add(renderable);
		}

		public void flagUniverseAsActive() {
			assert !isActive;
			isActive = true;
		}

		@Override
		public Universe build() {
			return new GdxUniverse(id(), now(), entities(), history(), branchRecord(), collisionDetector(), renderables, renderComponent, isActive);
		}
	}

	@Override
	protected Builder builder(Instant now, BranchRecord branchRecord) {
		return new Builder(now, branchRecord);
	}

	public final void render(Affine2 transform) {
		renderComponent.render(transform);

		for (var renderable : renderables) {
			renderable.render(transform);
		}
	}
}
