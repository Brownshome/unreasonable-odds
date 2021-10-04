package brownshome.unreasonableodds.collision;

import java.util.*;
import java.util.function.Consumer;

import brownshome.unreasonableodds.components.Collidable;
import brownshome.unreasonableodds.components.CollisionShape;
import brownshome.vecmath.MVec2;
import brownshome.vecmath.Vec2;

/**
 * Detects collisions between the queried shape and the shapes that make up this detector
 */
public final class CollisionDetector {
	private static final int DEFAULT_GRID_SIZE = 64;

	private final int gridSize;
	private final BitSet[][] grid;
	private final List<Collidable> shapes;

	private CollisionDetector(int gridSize, BitSet[][] grid, List<Collidable> shapes) {
		this.gridSize = gridSize;
		this.grid = grid;
		this.shapes = shapes;
	}

	public static CollisionDetector createDetector() {
		return new CollisionDetector(0, new BitSet[0][0], Collections.emptyList());
	}

	public Collection<Collidable> collidables() {
		return shapes;
	}

	@FunctionalInterface
	private interface CoordinateConsumer {
		void apply(int x, int y);
	}

	private static void forAllCoords(Vec2 min, Vec2 max, int gridSize, CoordinateConsumer consumer) {
		MVec2 scaledMin = min.copy();
		MVec2 scaledMax = max.copy();

		scaledMin.scale(gridSize);
		scaledMax.scale(gridSize);

		int minX = (int) scaledMin.x();
		int minY = (int) scaledMin.y();

		int maxX = (int) Math.ceil(scaledMax.x());
		int maxY = (int) Math.ceil(scaledMax.y());

		minX = Math.min(Math.max(0, minX), gridSize - 1);
		minY = Math.min(Math.max(0, minY), gridSize - 1);

		maxX = Math.min(Math.max(1, maxX), gridSize);
		maxY = Math.min(Math.max(1, maxY), gridSize);

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				consumer.apply(x, y);
			}
		}
	}

	public static CollisionDetector createDetector(List<Collidable> shapes) {
		int gridSize = DEFAULT_GRID_SIZE;

		BitSet[][] grid = new BitSet[gridSize][gridSize];
		for (int i = 0; i < shapes.size(); i++) {
			CollisionShape shape = shapes.get(i).collisionShape();
			int finalI = i;

			forAllCoords(shape.lesserExtent(), shape.greaterExtent(), gridSize, (x, y) -> {
				if (grid[x][y] == null) {
					grid[x][y] = new BitSet();
				}

				grid[x][y].set(finalI);
			});
		}

		return new CollisionDetector(gridSize, grid, shapes);
	}

	private void forEachSquareInRange(Vec2 min, Vec2 max, Consumer<Collidable> consumer) {
		BitSet combinedSet = new BitSet();

		forAllCoords(min, max, gridSize, (x, y) -> {
			var set = grid[x][y];

			if (set == null) {
				return;
			}

			combinedSet.or(set);
		});

		for (int index = combinedSet.nextSetBit(0); index != -1; index = combinedSet.nextSetBit(index + 1)) {
			consumer.accept(shapes.get(index));
		}
	}

	public void forEachCollidingShape(CollisionShape shape, Consumer<Collidable> shapeConsumer) {
		forEachSquareInRange(shape.lesserExtent(), shape.greaterExtent(), c -> {
			if (c.collisionShape().doesCollideWith(shape)) {
				shapeConsumer.accept(c);
			}
		});
	}

	@FunctionalInterface
	public interface SweptCollisionCallback {
		void call(CollisionShape.SweptCollision sweptCollision, Collidable collidable);
	}

	public void forEachCollidingShapeSwept(CollisionShape shape, Vec2 sweep, SweptCollisionCallback callback) {
		MVec2 min = shape.lesserExtent().copy();
		MVec2 max = shape.greaterExtent().copy();

		if (sweep.x() > 0.0) {
			max.x(max.x() + sweep.x());
		} else {
			min.x(min.x() + sweep.x());
		}

		if (sweep.y() > 0.0) {
			max.y(max.y() + sweep.y());
		} else {
			min.y(min.y() + sweep.y());
		}

		forEachSquareInRange(min, max, c -> {
			if (!c.collisionShape().doesCollideWith(shape)) {
				var point = c.collisionShape().sweptCollision(shape, sweep);

				if (point != null) {
					callback.call(point, c);
				}
			}
		});
	}
}
