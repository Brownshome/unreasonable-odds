package brownshome.unreasonableodds.generation;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTileGeneratorTest {
	private static final class FilledTile implements TileType {
		static final FilledTile INSTANCE = new FilledTile();

		@Override
		public TileType reflect() {
			return this;
		}

		@Override
		public TileType rotate() {
			return this;
		}

		@Override
		public String toString() {
			return "F";
		}
	}

	private static final class BlankTile implements TileType {
		static final BlankTile INSTANCE = new BlankTile();

		@Override
		public TileType reflect() {
			return this;
		}

		@Override
		public TileType rotate() {
			return this;
		}

		@Override
		public String toString() {
			return "B";
		}
	}

	@Test
	void generateCheckBoard() {
		TileType[][] archetype = new TileType[4][4];

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				boolean isFilled = (x + y) % 2 == 0;

				archetype[y][x] = isFilled ? FilledTile.INSTANCE : BlankTile.INSTANCE;
			}
		}

		var generator = new FloorTileGenerator(FilledTile.INSTANCE, 2, archetype);
		var generatedGrid = new TileType[5][5];

		generator.generateGrid(generatedGrid, new Random() { @Override protected int next(int bits) { return 0; } });

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				if (((x + y) % 2 == 0) == (generatedGrid[0][0] == FilledTile.INSTANCE)) {
					assertEquals(FilledTile.INSTANCE, generatedGrid[y][x], "Cell (%d, %d)".formatted(x, y));
				} else {
					assertEquals(BlankTile.INSTANCE, generatedGrid[y][x], "Cell (%d, %d)".formatted(x, y));
				}
			}
		}
	}
}