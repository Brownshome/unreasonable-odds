package brownshome.unreasonableodds.generation;

import java.util.*;
import java.util.stream.Collectors;

public final class FloorTileGenerator {
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

	public FloorTileGenerator(int localAreaSize, TileType[][] archetype) {
		this.localAreaSize = localAreaSize;
		this.archetype = archetype;

		int height = archetype.length;
		assert height != 0;
		int width = archetype[0].length;

		localAreas = generateLocalAreas(localAreaSize, archetype, width, height);
	}

	private static LocalArea[] generateLocalAreas(int localAreaSize, TileType[][] archetype, int width, int height) {
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
		Map<Direction, Set<Segment>> borderSegments = new EnumMap<>(Direction.class);
		for (var d : Direction.values()) {
			borderSegments.put(d, new HashSet<>());
		}

		for (int y = 0; y <= height - localAreaSize; y++) for (int x = 0; x <= width - localAreaSize; x++) {
			int yOffset = y, xOffset = x;

			var segment = Segment.makeSegment((localX, localY) -> archetype[localY + yOffset][localX + xOffset], localAreaSize);

			Set<Direction> borders = EnumSet.noneOf(Direction.class);
			if (x == 0) {
				borders.add(Direction.LEFT);
			} else if (x == width - localAreaSize) {
				borders.add(Direction.RIGHT);
			}

			if (y == 0) {
				borders.add(Direction.DOWN);
			} else if (y == height - localAreaSize) {
				borders.add(Direction.UP);
			}

			for (int i = 0; i < 4; i++) {
				counts.merge(segment, 1, Integer::sum);
				for (var border : borders) {
					borderSegments.get(border).add(segment);
				}

				var reflected = segment.reflect();
				counts.merge(reflected, 1, Integer::sum);
				for (var border : borders) {
					borderSegments.get(border).add(reflected);
				}

				segment = segment.rotate();
				borders = borders.stream().map(Direction::rotate).collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class)));
			}
		}

		int count = 0;
		for (int i : counts.values()) {
			count += i;
		}

		// The first tile is a corner tile, the next 4 areas are border tiles
		LocalArea[] areas = new LocalArea[counts.size() + Direction.values().length + 1];

		int i = 0;
		areas[i] = new LocalArea(i, 0.0) {
			@Override
			public String toString() {
				return "CORNER";
			}
		};

		i++;

		for (var d : Direction.values()) {
			areas[i] = new LocalArea(i, 0.0) {
				@Override
				public String toString() {
					return d.toString();
				}
			};

			var side = d.rotate();
			// Compatible with self along edge
			LocalArea.makeCompatible(areas[i], side, areas[i]);

			// Corner connections
			LocalArea.makeCompatible(areas[0], side, areas[i]);
			LocalArea.makeCompatible(areas[i], side, areas[0]);

			i++;
		}

		for (var entry : counts.entrySet()) {
			var segment = entry.getKey();
			var frequency = entry.getValue();

			areas[i] = new TileArea(i,
					(double) frequency / count,
					segment.size,
					segment.tiles);

			// Set the border segments compatible to any tile found on the border
			for (var d : Direction.values()) {
				if (borderSegments.get(d).contains(segment)) {
					LocalArea.makeCompatible(areas[i], d, areas[d.ordinal() + 1]);
				}
			}

			i++;
		}

		for (int a = Direction.values().length + 1; a < areas.length; a++) for (int b = a; b < areas.length; b++) {
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

		int genHeight = height + 2 - (localAreaSize - 1), genWidth = width + 2 - (localAreaSize - 1);

		// One tile pad for the border, and subtract areaSize - 1 as we write NxN blocks
		var generator = new WaveCollapseGridGenerator(genWidth, genHeight, localAreas);

		// Set the corner tiles. This should be enough to collapse all the edges
		var cornerTile = localAreas[0];
		generator.get(0, 0).collapse(cornerTile);
		generator.get(genWidth - 1, genHeight - 1).collapse(cornerTile);

		// Set the specified tiles in the grid
		for (int y = 0; y < height - (localAreaSize - 1); y++) for (int x = 0; x < width - (localAreaSize - 1); x++) {
			var cell = generator.get(x + 1, y + 1);
			int tileX = x, tileY = y;

			List<TileArea> bannedAreas = new ArrayList<>();
			cell.possibleTypes().forEach(a -> {
				var tileArea = (TileArea) a;

				for (int ly = 0; ly < tileArea.areaSize; ly++) for (int lx = 0; lx < tileArea.areaSize; lx++) {
					int gx = lx + tileX;
					int gy = ly + tileY;

					var tile = grid[gy][gx];

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
			int tx = Math.min(x, width - localAreaSize);
			int ty = Math.min(y, height - localAreaSize);

			grid[y][x] = ((TileArea) generator.get(tx + 1, ty + 1).observed()).get(x - tx, y - ty);
		}
	}
}
