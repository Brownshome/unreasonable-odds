package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.entites.Character;
import brownshome.unreasonableodds.gdx.*;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.Player;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;

import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import com.badlogic.gdx.math.Affine2;

public class GdxPlayerCharacter extends PlayerCharacter implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("character");
	private static final Vec2 SIZE = Vec2.of(0.1, 0.1);

	private final RenderComponent renderComponent;

	protected GdxPlayerCharacter(Position position, Vec2 velocity, Player player, Duration timeTravelEnergy, RenderComponent renderComponent) {
		super(position, velocity, player, timeTravelEnergy);

		this.renderComponent = renderComponent;
	}

	public static GdxPlayerCharacter createCharacter(Position position, Vec2 velocity, Player player, Duration timeTravelEnergy, ApplicationResources resources) {
		MVec2 renderPosition = position.position().copy();
		renderPosition.add(-CHARACTER_RADIUS, -CHARACTER_RADIUS);

		return new GdxPlayerCharacter(position, velocity, player, timeTravelEnergy, new RenderComponent(resources,
				REGION_CACHE.getTextureRegion(resources.atlas()),
				SIZE,
				new Position(renderPosition, position.orientation())));
	}

	public final RenderComponent renderComponent() {
		return renderComponent;
	}

	@Override
	public void render(Affine2 transform) {
		renderComponent.render(transform);
	}

	@Override
	protected JumpScar createJumpScar(Vec2 position, Duration jumpScarDuration) {
		return new GdxJumpScar(position, jumpScarDuration, renderComponent.resources());
	}

	@Override
	protected PlayerCharacter withTimeTravelEnergy(Duration energy) {
		return new GdxPlayerCharacter(position(), velocity(), player(), energy, renderComponent);
	}

	@Override
	protected GdxPlayerCharacter withPosition(Position position) {
		MVec2 renderPosition = position.position().copy();
		renderPosition.add(-CHARACTER_RADIUS, -CHARACTER_RADIUS);

		return new GdxPlayerCharacter(position,
				velocity(),
				player(),
				timeTravelEnergy(),
				renderComponent.withPosition(new Position(renderPosition, position.orientation())));
	}

	@Override
	protected Character withVelocity(Vec2 velocity) {
		return new GdxPlayerCharacter(position(), velocity, player(), timeTravelEnergy(), renderComponent);
	}

	@Override
	protected HistoricalCharacter createHistoricalCharacter() {
		return new GdxHistoricalCharacter(position(), velocity(), renderComponent);
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);

		var gdxBuilder = (GdxUniverse.Builder) builder;
		gdxBuilder.addRenderable(this);

		if (player() instanceof GdxPlayer) {
			// This is the keyboard controlled one
			gdxBuilder.flagUniverseAsActive();
		}
	}
}
