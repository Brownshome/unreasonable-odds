package brownshome.unreasonableodds.gdx;

import java.util.List;

import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.gdx.screen.MultiverseScreen;
import com.badlogic.gdx.math.Affine2;

public class GdxMultiverse extends Multiverse {
	private static final float UNIVERSE_SIZE = MultiverseScreen.SIZE_IN_PIXELS * 12 / 16;
	private static final float INTER_UNIVERSE_STRIDE = MultiverseScreen.SIZE_IN_PIXELS;

	protected GdxMultiverse(Rules rules, List<Universe> universes) {
		super(rules, universes);
	}

	public static GdxMultiverse createMultiverse(Rules rules, List<Universe> universes) {
		return new GdxMultiverse(rules, universes);
	}

	public void render() {
		Affine2 transform = new Affine2();

		var universes = universes();
		universes.sort(null);

		int index = 0;
		for (var universe : universes) {
			if (((GdxUniverse) universe).isActive()) {
				break;
			}

			index++;
		}

		assert index != universes.size();

		transform.scale(UNIVERSE_SIZE, UNIVERSE_SIZE);
		transform.translate(-0.5f, -0.5f);
		transform.preTranslate(-INTER_UNIVERSE_STRIDE * index, 0f);

		for (var universe : universes()) {
			((GdxUniverse) universe).render(transform);

			transform.preTranslate(INTER_UNIVERSE_STRIDE, 0f);
		}
	}
}
