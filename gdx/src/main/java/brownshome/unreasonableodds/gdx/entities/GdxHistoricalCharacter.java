package brownshome.unreasonableodds.gdx.entities;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.HistoricalCharacter;
import brownshome.unreasonableodds.gdx.GdxUniverse;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.math.Affine2;

public class GdxHistoricalCharacter extends HistoricalCharacter implements Renderable {
	private final RenderComponent renderComponent;

	protected GdxHistoricalCharacter(Position position, Vec2 velocity, RenderComponent renderComponent) {
		super(position, velocity);
		this.renderComponent = renderComponent;
	}

	public RenderComponent renderComponent() {
		return renderComponent;
	}

	@Override
	public void render(Affine2 transform) {
		renderComponent.render(transform);
	}

	@Override
	protected HistoricalCharacter withVelocity(Vec2 velocity) {
		return new GdxHistoricalCharacter(position(), velocity, renderComponent);
	}

	@Override
	protected GdxHistoricalCharacter withPosition(Position position) {
		MVec2 renderPosition = position.position().copy();
		renderPosition.add(-CHARACTER_RADIUS, -CHARACTER_RADIUS);

		return new GdxHistoricalCharacter(position,
				velocity(),
				renderComponent.withPosition(new Position(renderPosition, position.orientation())));
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);
		((GdxUniverse.Builder) builder).addRenderable(this);
	}
}
