package brownshome.unreasonableodds.gdx.entities;

import java.nio.ByteBuffer;
import java.time.Duration;

import brownshome.unreasonableodds.Rules;
import brownshome.unreasonableodds.Universe;
import brownshome.unreasonableodds.components.Position;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.gdx.components.RenderComponent;
import brownshome.unreasonableodds.gdx.components.Renderable;
import brownshome.unreasonableodds.session.Id;
import brownshome.unreasonableodds.session.NetworkGameSession;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.math.Affine2;

public class GdxPlayerCharacter extends PlayerCharacter implements Renderable {
	private static final TextureRegionCache REGION_CACHE = new TextureRegionCache("character");
	private static final Vec2 SIZE = Vec2.of(CHARACTER_RADIUS * 2, CHARACTER_RADIUS * 2);

	private final RenderComponent renderComponent;
	private final boolean isMainCharacter;

	protected GdxPlayerCharacter(Position position,
	                             Vec2 velocity,
	                             Id playerId,
	                             Duration timeTravelEnergy,
	                             RenderComponent renderComponent,
	                             boolean isMainCharacter) {
		super(position, velocity, playerId, timeTravelEnergy);

		this.renderComponent = renderComponent;
		this.isMainCharacter = isMainCharacter;
	}

	public GdxPlayerCharacter(ByteBuffer buffer, ApplicationResources resources) {
		super(buffer);

		this.renderComponent = createRenderComponent(position(), resources);

		var session = NetworkGameSession.get();
		this.isMainCharacter = session.sessionId().equals(playerId().sessionId());
	}

	public GdxPlayerCharacter(Position position,
	                          Vec2 velocity,
	                          Id playerId,
	                          Duration timeTravelEnergy,
	                          ApplicationResources resources,
	                          boolean isMainCharacter) {
		this(position, velocity, playerId, timeTravelEnergy, createRenderComponent(position, resources), isMainCharacter);
	}

	private static RenderComponent createRenderComponent(Position position, ApplicationResources resources) {
		MVec2 renderPosition = position.position().copy();
		renderPosition.add(-CHARACTER_RADIUS, -CHARACTER_RADIUS);

		return new RenderComponent(resources,
				REGION_CACHE.getTextureRegion(resources.atlas()),
				SIZE,
				new Position(renderPosition, position.orientation()));
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
		return new GdxPlayerCharacter(position(), velocity(), playerId(), energy, renderComponent, isMainCharacter);
	}

	@Override
	protected GdxPlayerCharacter withPosition(Position position) {
		MVec2 renderPosition = position.position().copy();
		renderPosition.add(-CHARACTER_RADIUS, -CHARACTER_RADIUS);

		return new GdxPlayerCharacter(position,
				velocity(),
				playerId(),
				timeTravelEnergy(),
				renderComponent.withPosition(new Position(renderPosition, position.orientation())),
				isMainCharacter);
	}

	@Override
	protected GdxPlayerCharacter withVelocity(Vec2 velocity) {
		return new GdxPlayerCharacter(position(), velocity, playerId(), timeTravelEnergy(), renderComponent, isMainCharacter);
	}

	@Override
	public void addToBuilder(Universe.Builder builder) {
		super.addToBuilder(builder);

		var gdxBuilder = (GdxUniverse.Builder) builder;
		gdxBuilder.addRenderable(this);

		if (isMainCharacter) {
			gdxBuilder.flagUniverseAsActive();
		}
	}
}
