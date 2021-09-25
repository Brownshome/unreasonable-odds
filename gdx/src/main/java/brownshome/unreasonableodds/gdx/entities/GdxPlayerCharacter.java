package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.gdx.*;
import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;

import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;

public class GdxPlayerCharacter extends PlayerCharacter implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("character");
	private static final Vec2 SIZE = Vec2.of(0.1, 0.1);

	private final RenderComponent renderComponent;

	protected GdxPlayerCharacter(Position position, Player player, Duration timeTravelEnergy, RenderComponent renderComponent) {
		super(position, player, timeTravelEnergy);

		this.renderComponent = renderComponent;
	}

	public static GdxPlayerCharacter createCharacter(Position position, Player player, Duration timeTravelEnergy, ApplicationResources resources) {
		return new GdxPlayerCharacter(position, player, timeTravelEnergy, new RenderComponent(resources,
				REGION_CACHE.getTextureRegion(resources.atlas()),
				SIZE,
				position));
	}

	@Override
	public final RenderComponent renderComponent() {
		return renderComponent;
	}

	@Override
	protected JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new GdxJumpScar(position, jumpScarDuration, renderComponent.resources());
	}

	@Override
	protected PlayerCharacter withTimeTravelEnergy(Duration energy) {
		return new GdxPlayerCharacter(position(), player(), energy, renderComponent);
	}

	@Override
	protected GdxPlayerCharacter withPosition(Position position) {
		return new GdxPlayerCharacter(position, player(), timeTravelEnergy(), renderComponent.withPosition(position));
	}

	@Override
	protected HistoricalCharacter createHistoricalCharacter() {
		return new GdxHistoricalCharacter(position(), renderComponent);
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);
		((GdxUniverse.Builder) builder).addRenderable(this);
	}
}
