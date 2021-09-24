package brownshome.unreasonableodds.gdx.entities;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.HistoricalCharacter;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;

public class GdxHistoricalCharacter extends HistoricalCharacter implements Renderable {
	private final RenderComponent renderComponent;

	protected GdxHistoricalCharacter(Position position, RenderComponent renderComponent) {
		super(position);
		this.renderComponent = renderComponent;
	}

	@Override
	public RenderComponent renderComponent() {
		return renderComponent;
	}

	@Override
	protected GdxHistoricalCharacter withPosition(Position position) {
		return new GdxHistoricalCharacter(position, renderComponent.withPosition(position));
	}
}
