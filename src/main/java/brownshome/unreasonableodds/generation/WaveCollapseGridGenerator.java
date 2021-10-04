package brownshome.unreasonableodds.generation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates any grid-based pattern with discrete numbers of values in each cell
 * based on the local neighbourhood of each cell.
 *
 * Inspired by https://github.com/mxgmn/WaveFunctionCollapse
 */
public final class WaveCollapseGridGenerator {
	private final int width, height;

	private final LocalArea[] areas;

	public final class Cell {
		private final int x, y;

		/**
		 * Each array holds at index i the number of possible types in the cell in that direction that would allow this cell
		 * to be type i
		 */
		private final EnumMap<Direction, int[]> typeAgreement;

		private final BitSet possibleTypes;
		private int numberOfPossibleTypes;
		private double sumProbability, sumEntropy;

		private LocalArea observed = null;

		private Cell(int x, int y) {
			this.x = x;
			this.y = y;

			typeAgreement = new EnumMap<>(Direction.class);

			possibleTypes = new BitSet(areas.length);
			possibleTypes.set(0, areas.length);

			numberOfPossibleTypes = areas.length;
			sumProbability = WaveCollapseGridGenerator.this.sumProbability;
			sumEntropy = WaveCollapseGridGenerator.this.sumEntropy;
		}

		private int[] typeAgreement(Direction d) {
			return typeAgreement.computeIfAbsent(d, (direction) -> {
				int nx = x + direction.x, ny = y + direction.y;
				var cell = get(nx, ny);

				int[] agreement = new int[areas.length];
				for (int i = cell.possibleTypes.nextSetBit(0); i >= 0; i = cell.possibleTypes.nextSetBit(i + 1)) {
					var compatibility = areas[i].compatibility(direction.opposite());
					for (int j = compatibility.nextSetBit(0); j >= 0; j = compatibility.nextSetBit(j + 1)) {
						agreement[j]++;
					}
				}

				// Flag any not possible types
				for (int i = 0; i < agreement.length; i++) {
					if (agreement[i] == 0) {
						stack.add(new StackItem(this, areas[i]));
					}
				}

				return agreement;
			});
		}

		public double entropy() {
			return numberOfPossibleTypes == 1 ? 0 : Math.log(sumProbability) + sumEntropy / sumProbability;
		}

		public void collapse(Random random) {
			collapse(observeType(random));
		}

		public void collapse(LocalArea area) {
			assert possibleTypes.get(area.index());
			assert observed == null;

			observed = area;

			for (int i = possibleTypes.nextSetBit(0); i >= 0; i = possibleTypes.nextSetBit(i + 1)) {
				if (areas[i] != area) {
					stack.add(new StackItem(this, areas[i]));
				}
			}

			processStack();
		}

		public void setNotPossible(LocalArea area) {
			stack.add(new StackItem(this, area));

			processStack();
		}

		private LocalArea observeType(Random random) {
			if (possibleTypes.isEmpty()) {
				throw new IllegalStateException("Cannot observe an invalid state");
			}

			if (numberOfPossibleTypes == 1) {
				return areas[possibleTypes.length() - 1];
			}

			double r = random.nextDouble(sumProbability);

			for (int i = possibleTypes.nextSetBit(0); i >= 0; i = possibleTypes.nextSetBit(i + 1)) {
				if (r < areas[i].probability()) {
					return areas[i];
				}

				r -= areas[i].probability();
			}

			return areas[possibleTypes.length() - 1];
		}

		public LocalArea observed() {
			return observed;
		}

		public int numberOfPossibleTypes() {
			return numberOfPossibleTypes;
		}

		public int x() {
			return x;
		}

		public int y() {
			return y;
		}

		public Stream<LocalArea> possibleTypes() {
			return possibleTypes.stream().mapToObj(i -> areas[i]);
		}

		@Override
		public String toString() {
			if (observed != null) {
				return "(%d, %d) Observed: %s".formatted(x, y, observed.toString());
			} else {
				return "(%d, %d) Unknown: [%s]".formatted(x, y, possibleTypes.stream().mapToObj(i -> areas[i]).map(LocalArea::toString).collect(Collectors.joining(", ")));
			}
		}
	}

	private final double sumProbability, sumEntropy;
	private final Cell[][] cells;

	record StackItem(Cell cell, LocalArea area) { }

	// Use our own stack here, for large images we will stack-overflow without it
	Deque<StackItem> stack = new ArrayDeque<>();

	private void processStack() {
		StackItem item;
		while ((item = stack.poll()) != null) {
			if (item.cell.possibleTypes.get(item.area.index())) {
				for (var d : Direction.values()) {
					int nx = item.cell.x + d.x, ny = item.cell.y + d.y;
					if (nx < 0 || nx >= height || ny < 0 || ny >= width) {
						// No need to propagate out of the border of the world
						continue;
					}

					var neighbour = get(item.cell.x + d.x, item.cell.y + d.y);
					var compatibleRegions = item.area.compatibility(d);

					for (int i = compatibleRegions.nextSetBit(0); i >= 0; i = compatibleRegions.nextSetBit(i + 1)) {
						if (--neighbour.typeAgreement(d.opposite())[i] == 0) {
							stack.add(new StackItem(neighbour, areas[i]));
						}
					}
				}

				item.cell.possibleTypes.clear(item.area.index());

				item.cell.numberOfPossibleTypes--;
				item.cell.sumProbability -= item.area.probability();
				item.cell.sumEntropy -= item.area.individualEntropy();

				if (item.cell.numberOfPossibleTypes == 0) {
					throw new GenerationContradictionException(item.cell);
				}
			}
		}
	}

	public WaveCollapseGridGenerator(int width, int height, LocalArea[] areas) {
		this.width = width;
		this.height = height;

		this.areas = areas;

		double localSumProbability = 0.0, localSumEntropy = 0.0;
		for (LocalArea area : areas) {
			localSumEntropy += area.individualEntropy();
			localSumProbability += area.probability();
		}

		sumProbability = localSumProbability;
		sumEntropy = localSumEntropy;

		this.cells = new Cell[height][width];
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			cells[y][x] = new Cell(x, y);
		}
	}

	public Cell get(int x, int y) {
		return cells[y][x];
	}

	public Cell selectNextCell(Random random) {
		final double RANDOMNESS = 0.1;

		double entropy = Double.POSITIVE_INFINITY;
		Cell result = null;
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			var cell = get(x, y);

			if (cell.observed != null) {
				continue;
			}

			if (cell.numberOfPossibleTypes == 1) {
				return cell;
			}

			double e = cell.entropy() * random.nextDouble(1.0 - RANDOMNESS, 1.0 + RANDOMNESS);

			if (e < entropy) {
				entropy = e;
				result = cell;
			}
		}

		return result;
	}
}
