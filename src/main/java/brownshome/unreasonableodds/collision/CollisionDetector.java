package brownshome.unreasonableodds.collision;

import java.util.BitSet;
import java.util.List;
import java.util.function.Consumer;

import brownshome.unreasonableodds.components.CollisionShape;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

/**
 * Detects collisions between the queried shape and the shapes that make up this detector
 */
public final class CollisionDetector {
	private final int gridSize;
	private final BitSet[][] grid;
	private final List<CollisionShape> shapes;

	private CollisionDetector(int gridSize, BitSet[][] grid, List<CollisionShape> shapes) {
		this.gridSize = gridSize;
		this.grid = grid;
		this.shapes = shapes;
	}

	public static CollisionDetector createCollisionDetector(int gridSize, List<CollisionShape> shapes) {
		BitSet[][] grid = new BitSet[gridSize][gridSize];

		MVec2 min = Vec2.of(0, 0);
		MVec2 max = Vec2.of(0, 0);

		for (int i = 0; i < shapes.size(); i++) {
			CollisionShape shape = shapes.get(i);

			min.set(shape.lesserExtent());
			max.set(shape.greaterExtent());

			min.scale(gridSize);
			max.scale(gridSize);

			int minX = Math.max((int) min.x(), 0);
			int minY = Math.max((int) min.y(), 0);

			int maxX = Math.min((int) Math.ceil(max.x()), gridSize - 1);
			int maxY = Math.min((int) Math.ceil(max.y()), gridSize - 1);

			for (int x = minX; x < maxX; x++) {
				for (int y = minY; y < maxY; y++) {
					if (grid[x][y] == null) {
						grid[x][y] = new BitSet();
					}

					grid[x][y].set(i);
				}
			}
		}

		return new CollisionDetector(gridSize, grid, shapes);
	}

	public void forCollidingShapes(CollisionShape shape, Consumer<CollisionShape> shapeConsumer) {
		MVec2 min = shape.lesserExtent().copy();
		MVec2 max = shape.greaterExtent().copy();

		min.scale(gridSize);
		max.scale(gridSize);

		int minX = Math.max((int) min.x(), 0);
		int minY = Math.max((int) min.y(), 0);

		int maxX = Math.min((int) Math.ceil(max.x()), gridSize - 1);
		int maxY = Math.min((int) Math.ceil(max.y()), gridSize - 1);

		BitSet combinedSet = new BitSet();
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				var set = grid[x][y];

				if (set == null) {
					continue;
				}

				combinedSet.or(set);
			}
		}

		for (int index = combinedSet.nextSetBit(0); index != -1; index = combinedSet.nextSetBit(index)) {
			if (shapes.get(index).doesCollideWith(shape)) {
				shapeConsumer.accept(shapes.get(index));
			}
		}
	}
}
