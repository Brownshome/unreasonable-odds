package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import brownshome.unreasonableodds.Multiverse;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Entity;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.unreasonableodds.history.History;

public class GdxUniverse extends Universe {
	protected GdxUniverse(Instant now, List<Entity> entities, History previousHistory) {
		super(now, entities, previousHistory);
	}

	public static GdxUniverse createUniverse(Instant beginning, List<Entity> entities) {
		return new GdxUniverse(beginning, entities, History.blankHistory());
	}

	@Override
	protected GdxUniverse createNextUniverse(Instant now, List<Entity> newEntities) {
		return new GdxUniverse(now, newEntities, history());
	}

	@Override
	protected UniverseStep createUniverseStep(Multiverse.MultiverseStep multiverseStep, Consumer<Entity> newEntities) {
		return super.createUniverseStep(multiverseStep, newEntities.andThen(this::renderEntity));
	}

	protected void renderEntity(Entity entity) {
		if (entity instanceof Renderable renderable) {
			renderable.renderComponent().render();
		}
	}
}
