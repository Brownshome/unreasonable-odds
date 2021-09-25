package brownshome.unreasonableodds.gdx;

import java.util.List;

import brownshome.unreasonableodds.*;
import com.badlogic.gdx.math.Affine2;

public class GdxMultiverse extends Multiverse {
	protected GdxMultiverse(Rules rules, List<Universe> universes) {
		super(rules, universes);
	}

	public static GdxMultiverse createMultiverse(Rules rules, List<Universe> universes) {
		return new GdxMultiverse(rules, universes);
	}

	public void render() {
		Affine2 transform = new Affine2();

		for (var universe : universes()) {
			((GdxUniverse) universe).render(transform);

			transform.translate(1.0f, 0f);
		}
	}
}
