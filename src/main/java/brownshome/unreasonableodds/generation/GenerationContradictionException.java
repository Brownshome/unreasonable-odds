package brownshome.unreasonableodds.generation;

public class GenerationContradictionException extends IllegalStateException {
	private final int x, y;

	public GenerationContradictionException(int x, int y) {
		super("Contradictory cell (%d, %d)".formatted(x, y));
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}
}
