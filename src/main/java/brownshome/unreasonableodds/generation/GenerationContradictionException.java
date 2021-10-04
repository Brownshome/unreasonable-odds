package brownshome.unreasonableodds.generation;

public class GenerationContradictionException extends IllegalStateException {
	private final WaveCollapseGridGenerator.Cell cell;

	public GenerationContradictionException(WaveCollapseGridGenerator.Cell cell) {
		this("Contradictory cell: %s".formatted(cell), cell);
	}

	public GenerationContradictionException(String s, WaveCollapseGridGenerator.Cell cell) {
		super(s);
		this.cell = cell;
	}

	public GenerationContradictionException(String message, Throwable cause, WaveCollapseGridGenerator.Cell cell) {
		super(message, cause);
		this.cell = cell;
	}

	public final WaveCollapseGridGenerator.Cell cell() {
		return cell;
	}
}
