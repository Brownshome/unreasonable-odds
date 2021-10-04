package brownshome.unreasonableodds.generation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.IntBinaryOperator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WaveCollapseGridGeneratorTest {
	private static final Random CONSTANT_RANDOM = new Random() {
		@Override
		protected int next(int bits) {
			return 0;
		}
	};

	private static final double ENTROPY_ACCURACY = 1e-6;

	@Test
	void generateSquareGrid() {
		// Generate four corners, all in a square
		LocalArea[] areas = new LocalArea[4];
		for (int i = 0; i < areas.length; i++) {
			areas[i] = new LocalArea(i, 1.0 / areas.length);
		}

		LocalArea.makeCompatible(areas[0], Direction.RIGHT, areas[1]);
		LocalArea.makeCompatible(areas[1], Direction.DOWN, areas[3]);
		LocalArea.makeCompatible(areas[3], Direction.LEFT, areas[2]);
		LocalArea.makeCompatible(areas[2], Direction.UP, areas[0]);

		LocalArea.makeCompatible(areas[0], Direction.UP, areas[2]);
		LocalArea.makeCompatible(areas[1], Direction.RIGHT, areas[0]);
		LocalArea.makeCompatible(areas[3], Direction.DOWN, areas[1]);
		LocalArea.makeCompatible(areas[2], Direction.LEFT, areas[3]);

		var generator = new WaveCollapseGridGenerator(16, 16, areas);

		for (int r = 0; r < 16; r++) for (int c = 0; c < 16; c++) {
			var cell = generator.get(r, c);
			assertEquals(4, cell.numberOfPossibleTypes());
			assertEquals(Math.log(4.0), cell.entropy(), ENTROPY_ACCURACY);
		}

		generator.get(0, 0).collapse(CONSTANT_RANDOM);

		for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
			var cell = generator.get(x, y);
			assertEquals(1, cell.numberOfPossibleTypes());
			assertEquals(0.0, cell.entropy(), ENTROPY_ACCURACY);

			if (y != 0 || x != 0) {
				cell.collapse(CONSTANT_RANDOM);
			}

			assertEquals(areas[2 * (y % 2) + x % 2], cell.observed());
		}
	}
}