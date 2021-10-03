package brownshome.unreasonableodds.generation;

import java.util.*;
import java.util.function.Function;

public final class FloorTileGenerator {
	private final TileType borderType;
	private final int localAreaSize;
	private final TileType[][] archetype;

	private static final class TileArea extends LocalArea {
		final int areaSize;
		final TileType[] tiles;

		TileArea(int index, double probability, int areaSize, TileType[] tiles) {
			super(index, probability);

			this.areaSize = areaSize;
			this.tiles = tiles;
		}

		TileType get(int x, int y) {
			assert x >= 0 && x < areaSize && y >= 0 && y < areaSize;
			return tiles[x + y * areaSize];
		}

		boolean isBorderedByOn(TileType borderType, Direction d) {
			for (int i = 0; i < areaSize; i++) {
				var tile = switch (d) {
					case UP -> get(i, 0);
					case LEFT -> get(0, i);
					case DOWN -> get(i, areaSize - 1);
					case RIGHT -> get(areaSize - 1, i);
				};

				if (!tile.equals(borderType)) {
					return false;
				}
			}

			return true;
		}

		public static void checkCompatibility(TileArea a, TileArea b) {
			directionLoop:
			for (var d : Direction.values()) {
				int xMin, xMax, yMin, yMax;

				xMin = Math.max(0, d.x);
				yMin = Math.max(0, d.y);
				xMax = Math.min(a.areaSize, d.x + b.areaSize);
				yMax = Math.min(a.areaSize, d.y + b.areaSize);

				for (int x = xMin; x < xMax; x++) for (int y = yMin; y < yMax; y++) {
					if (a.get(x, y) != b.get(x - d.x, y - d.y)) {
						continue directionLoop;
					}
				}

				makeCompatible(a, d, b);
			}
		}

		@Override
		public String toString() {
			StringBuilder output = new StringBuilder("Tile Area [\n");

			for (int y = 0; y < areaSize; y++) {
				for (int x = 0; x < areaSize; x++) {
					output.append(String.format("\t%4s", get(x, y)));
				}

				output.append('\n');
			}

			output.append(']');

			return output.toString();
		}
	}

	private final LocalArea[] localAreas;

	public FloorTileGenerator(TileType borderType, int localAreaSize, TileType[][] archetype) {
		this.borderType = borderType;
		this.localAreaSize = localAreaSize;
		this.archetype = archetype;

		int height = archetype.length;
		assert height != 0;
		int width = archetype[0].length;

		localAreas = generateLocalAreas(localAreaSize, archetype, width, height, borderType);
	}

	private static LocalArea[] generateLocalAreas(int localAreaSize, TileType[][] archetype, int width, int height, TileType borderType) {
		@FunctionalInterface
		interface TileGenerator {
			TileType get(int x, int y);
		}

		record Segment(TileType[] tiles, int size, int hash) {
			Segment(TileType[] tiles, int size) {
				this(tiles, size, Arrays.hashCode(tiles));
			}

			static Segment makeSegment(TileGenerator generator, int size) {
				TileType[] tiles = new TileType[size * size];
				for (int y = 0; y < size; y++) for (int x = 0; x < size; x++) {
					tiles[x + y * size] = generator.get(x, y);
				}

				return new Segment(tiles, size);
			}

			TileType get(int x, int y) {
				assert x >= 0 && x < size && y >= 0 && y < size;
				return tiles[x + y * size];
			}

			Segment rotate() {
				return Segment.makeSegment((x, y) -> get(y, size - 1 - x).rotate(), size);
			}

			Segment reflect() {
				return Segment.makeSegment((x, y) -> get(size - 1 - x, y).reflect(), size);
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof Segment a && Arrays.deepEquals(tiles, a.tiles);
			}

			@Override
			public int hashCode() {
				return hash;
			}
		}

		Map<Segment, Integer> counts = new HashMap<>();

		for (int y = 1 - localAreaSize; y < height; y++) for (int x = 1 - localAreaSize; x < width; x++) {
			int yOffset = y, xOffset = x;

			var segment = Segment.makeSegment((localX, localY) -> {
				localX += xOffset;
				localY += yOffset;

				if (localY < 0 || localY >= height
						|| localX < 0 || localX >= width) {
					return borderType;
				}

				return archetype[localY][localX];
			}, localAreaSize);

			// Don't include the border items in the calculation
			boolean isBorder = y < 0 || x < 0;

			for (int i = 0; i < 4; i++) {
				counts.merge(segment, isBorder ? 0 : 1, Integer::sum);
				counts.merge(segment.reflect(), isBorder ? 0 : 1, Integer::sum);
				segment = segment.rotate();
			}
		}

		int count = 0;
		for (int i : counts.values()) {
			count += i;
		}

		LocalArea[] areas = new LocalArea[counts.size()];
		{ int i = 0;
			for (var entry : counts.entrySet()) {
				var segment = entry.getKey();
				var frequency = entry.getValue();

				areas[i] = new TileArea(i,
						(double) frequency / count,
						segment.size,
						segment.tiles);

				i++;
			}
		}

		for (int a = 0; a < areas.length; a++) for (int b = a; b < areas.length; b++) {
			var areaA = (TileArea) areas[a];
			var areaB = (TileArea) areas[b];

			TileArea.checkCompatibility(areaA, areaB);
		}

		return areas;
	}

	public void generateGrid(TileType[][] grid, Random random) {
		int height = grid.length;

		if (height == 0) {
			return;
		}

		int width = grid[0].length;
		int padding = localAreaSize - 1;

		var generator = new WaveCollapseGridGenerator(width + padding, height + padding, localAreas);

		// Set the specified tiles in the grid
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			var cell = generator.get(x, y);
			int finalX = x, finalY = y;

			List<TileArea> bannedAreas = new ArrayList<>();
			cell.possibleTypes().forEach(a -> {
				var tileArea = (TileArea) a;

				for (int ly = 0; ly < tileArea.areaSize; ly++) for (int lx = 0; lx < tileArea.areaSize; lx++) {
					int gx = lx + finalX - padding;
					int gy = ly + finalY - padding;

					var tile = gx < 0 || gx >= width || gy < 0 || gy >= height
							? borderType : grid[gy][gx];

					if (tile != null && !tileArea.get(lx, ly).equals(tile)) {
						bannedAreas.add(tileArea);
						return;
					}
				}
			});

			// Delay the banning
			bannedAreas.forEach(cell::setNotPossible);
		}

		// Collapse the grid
		WaveCollapseGridGenerator.Cell nextCell;
		while ((nextCell = generator.selectNextCell(random)) != null) {
			nextCell.collapse(random);
		}

		// Write out the grid
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
			grid[y][x] = ((TileArea) generator.get(x + padding, y + padding).observed()).get(0, 0);
		}
	}
}
