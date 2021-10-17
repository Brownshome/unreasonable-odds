package brownshome.unreasonableodds.gdx.entities;

import java.time.Duration;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.entites.Character;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.player.ControllingPlayer;
import brownshome.unreasonableodds.player.GamePlayer;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.*;

import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import com.badlogic.gdx.math.Affine2;

public class GdxPlayerCharacter extends PlayerCharacter implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("character");
	private static final Vec2 SIZE = Vec2.of(CHARACTER_RADIUS * 2, CHARACTER_RADIUS * 2);

	private final RenderComponent renderComponent;

	protected GdxPlayerCharacter(Position position,
	                             Vec2 velocity,
	                             GamePlayer player,
	                             Duration timeTravelEnergy,
	                             RenderComponent renderComponent) {
		super(position, velocity, player, timeTravelEnergy);

		this.renderComponent = renderComponent;
	}

	public static GdxPlayerCharacter createCharacter(Position position,
	                                                 Vec2 velocity,
	                                                 GamePlayer player,
	                                                 Duration timeTravelEnergy,
	                                                 ApplicationResources resources) {
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
	public GdxHistoricalCharacter createHistoricalEntity(Rules rules) {
		return ((GdxEntityFactory) rules.entities()).createHistoricalCharacter(position(), velocity(), renderComponent);
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
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);

		var gdxBuilder = (GdxUniverse.Builder) builder;
		gdxBuilder.addRenderable(this);

		if (player() instanceof ControllingPlayer controllingPlayer && controllingPlayer.controller() instanceof GdxCharacterController) {
			// This is the keyboard controlled one
			gdxBuilder.flagUniverseAsActive();
		}
	}
}
