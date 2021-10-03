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
	private final EnumMap<Direction, int[]> startingTypeAgreement;

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
			return typeAgreement.computeIfAbsent(d, (direction) -> startingTypeAgreement.get(direction).clone());
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
					setNotPossible(areas[i]);
				}
			}
		}

		public void setNotPossible(LocalArea area) {
			record StackItem(Cell cell, LocalArea localArea) { }

			// Use our own stack here, for large images we will overflow anyway
			Deque<StackItem> stack = new ArrayDeque<>();

			StackItem next = new StackItem(this, area);
			do {
				area = next.localArea;
				var cell = next.cell;

				if (cell.possibleTypes.get(area.index())) {
					cell.possibleTypes.clear(area.index());

					cell.numberOfPossibleTypes--;
					cell.sumProbability -= area.probability();
					cell.sumEntropy -= area.individualEntropy();

					if (cell.numberOfPossibleTypes == 0) {
						throw new GenerationContradictionException(cell.x, cell.y);
					}

					for (var d : Direction.values()) {
						int nx = cell.x + d.x, ny = cell.y + d.y;
						if (nx < 0 || nx >= height || ny < 0 || ny >= width) {
							// No need to propagate out of the border of the world
							continue;
						}

						var neighbour = get(cell.x + d.x, cell.y + d.y);
						var compatibleRegions = area.compatibility(d);

						for (int i = compatibleRegions.nextSetBit(0); i >= 0; i = compatibleRegions.nextSetBit(i + 1)) {
							if (--neighbour.typeAgreement(d.opposite())[i] == 0) {
								stack.add(new StackItem(neighbour, areas[i]));
							}
						}
					}
				}

				next = stack.poll();
			} while(next != null);
		}

		private LocalArea observeType(Random random) {
			if (possibleTypes.isEmpty()) {
				throw new IllegalStateException("Cannot observe an invalid state");
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
				return "Observed: %s".formatted(observed.toString());
			} else {
				return "Unknown: [%s]".formatted(possibleTypes.stream().mapToObj(i -> areas[i]).map(LocalArea::toString).collect(Collectors.joining(", ")));
			}
		}
	}

	private final double sumProbability, sumEntropy;
	private final Cell[][] cells;

	public WaveCollapseGridGenerator(int width, int height, LocalArea[] areas) {
		this.width = width;
		this.height = height;

		this.areas = areas;

		startingTypeAgreement = new EnumMap<>(Direction.class);
		for (var d : Direction.values()) {
			var agreement = new int[areas.length];

			for (int i = 0; i < areas.length; i++) {
				agreement[i] = areas[i].compatibility(d).cardinality();
			}

			startingTypeAgreement.put(d, agreement);
		}

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
