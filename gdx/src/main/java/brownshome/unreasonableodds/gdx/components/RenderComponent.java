package brownshome.unreasonableodds.gdx.components;

import brownshome.unreasonableodds.components.Position;

import brownshome.vecmath.Vec2;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;

import brownshome.unreasonableodds.gdx.ApplicationResources;

public record RenderComponent(ApplicationResources resources, TextureRegion region, Vec2 size, Position position) {
	public void render() {
		var position = position();
		var transform = new Affine2();
		var size = size();

		transform.setToRotation((float) position.orientation().cos(), (float) position.orientation().sin());
		transform.preTranslate((float) position.position().x(), (float) position.position().y());

		resources().batch().draw(region(), (float) size.x(), (float) size.y(), transform);
	}
}
