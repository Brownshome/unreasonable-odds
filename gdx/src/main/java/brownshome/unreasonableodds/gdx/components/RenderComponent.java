package brownshome.unreasonableodds.gdx.components;

import brownshome.unreasonableodds.components.Position;

import brownshome.vecmath.Vec2;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

import brownshome.unreasonableodds.gdx.ApplicationResources;

public record RenderComponent(ApplicationResources resources, TextureRegion region, Vec2 size, Position position) {
	public RenderComponent {
		assert resources != null;
		assert region != null;
		assert size != null;
		assert position != null;
	}

	public void render(Affine2 transform) {
		var position = position();
		var localTransform = new Affine2();
		var size = size();

		localTransform.setToRotation((float) position.orientation().cos(), (float) position.orientation().sin());
		localTransform.preTranslate((float) position.position().x(), (float) position.position().y());
		localTransform.preMul(transform);

		resources().batch().draw(region(), (float) size.x(), (float) size.y(), localTransform);
	}

	public RenderComponent withPosition(Position position) {
		return new RenderComponent(resources, region, size, position);
	}
}
