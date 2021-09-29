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

		for (int r = 0; r < 16; r++) for (int c = 0; c < 16; c++) {
			var cell = generator.get(r, c);
			assertEquals(1, cell.numberOfPossibleTypes());
			assertEquals(0.0, cell.entropy(), ENTROPY_ACCURACY);

			if (r != 0 || c != 0) {
				cell.collapse(CONSTANT_RANDOM);
			}

			assertEquals(areas[2 * (r % 2) + c % 2], cell.observed());
		}
	}

	@Test
	void generateImage() throws IOException {
		int N = 3;
		int width = 128, height = 128;

		BufferedImage image = ImageIO.read(WaveCollapseGridGeneratorTest.class.getResourceAsStream("test-image.png"));

		class Area extends LocalArea {
			final int[] imageData;
			final int n;

			Area(int index, double probability, int[] imageData, int n) {
				super(index, probability);
				this.imageData = imageData;
				this.n = n;
			}

			int get(int r, int c) {
				return imageData[r * n + c];
			}

			static void checkCompatibility(Area a, Area b) {
				directionLoop:
				for (var d : Direction.values()) {
					int dr = d.y;
					int dc = d.x;
					int rMin, rMax, cMin, cMax;

					rMin = Math.max(0, dr);
					cMin = Math.max(0, dc);
					rMax = Math.min(a.n, dr + b.n);
					cMax = Math.min(a.n, dc + b.n);

					for (int r = rMin; r < rMax; r++) for (int c = cMin; c < cMax; c++) {
						if (a.get(r, c) != b.get(r - dr, c - dc)) {
							continue directionLoop;
						}
					}

					makeCompatible(a, d, b);
				}
			}
		}

		record ImageSegment(int[] array, int hash, int n) {
			static ImageSegment createSegment(IntBinaryOperator pixelGetter, int n) {
				int[] data = new int[n * n];
				for (int r = 0; r < n; r++) for (int c = 0; c < n; c++) {
					data[r * n + c] = pixelGetter.applyAsInt(r, c);
				}

				return new ImageSegment(data, Arrays.hashCode(data), n);
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof ImageSegment a && Arrays.equals(array, a.array);
			}

			@Override
			public int hashCode() {
				return hash;
			}

			int get(int r, int c) {
				return array[r * n + c];
			}

			ImageSegment rotate() {
				return createSegment((r, c) -> get(c, n - 1 - r), n);
			}

			ImageSegment reflect() {
				return createSegment((r, c) -> get(n - 1 - r, n - 1 - c), n);
			}
		}

		Map<ImageSegment, Double> counts = new HashMap<>();

		double weight = 1.0;
		for (int r = 0; r < image.getHeight(); r++)	for (int c = 0; c < image.getWidth(); c++) {
			int rOff = r, cOff = c;
			var segment = ImageSegment.createSegment((rr, cc) -> image.getRGB((cc + cOff) % image.getWidth(), (rr + rOff) % image.getHeight()), N);
			counts.merge(segment, weight, Double::sum);
			counts.merge(segment.reflect(), weight, Double::sum);

			counts.merge(segment = segment.rotate(), weight, Double::sum);
			counts.merge(segment.reflect(), weight, Double::sum);

			counts.merge(segment = segment.rotate(), weight, Double::sum);
			counts.merge(segment.reflect(), weight, Double::sum);

			counts.merge(segment = segment.rotate(), weight, Double::sum);
			counts.merge(segment.reflect(), weight, Double::sum);
		}

		double total = counts.values().stream().mapToDouble(Double::doubleValue).sum();

		Area[] areas = new Area[counts.size()];

		{
			int i = 0;
			for (var e : counts.entrySet()) {
				areas[i] = new Area(i, e.getValue() / total, e.getKey().array, e.getKey().n());
				i++;
			}
		}

		for (int i = 0; i < areas.length; i++) for (int j = i; j < areas.length; j++) {
			Area.checkCompatibility(areas[i], areas[j]);
		}

		var generator = new WaveCollapseGridGenerator(width, height, areas);

		Random random = new Random(1);
		WaveCollapseGridGenerator.Cell nextCell;
		while ((nextCell = generator.selectNextCell(random)) != null) {
			nextCell.collapse(random);
		}
	}
}