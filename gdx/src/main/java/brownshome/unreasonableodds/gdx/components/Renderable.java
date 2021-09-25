package brownshome.unreasonableodds.gdx.components;

import com.badlogic.gdx.math.Affine2;

public interface Renderable {
	RenderComponent renderComponent();

	default void render(Affine2 transform) {
		renderComponent().render(transform);
	}
}
