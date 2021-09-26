package brownshome.unreasonableodds.history;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class BranchRecord implements Comparable<BranchRecord> {
	private final BranchRecord parentBranch;
	private final Instant branchTime;

	private BranchRecord(BranchRecord parentBranch, Instant branchTime) {
		assert branchTime != null;

		this.parentBranch = parentBranch;
		this.branchTime = branchTime;
	}

	public static BranchRecord blankRecord(Instant epoch) {
		return new BranchRecord(null, epoch);
	}

	/**
	 * Creates a new branch
	 * @param ageOfUniverse the age of the new universe
	 * @return a new branch record
	 */
	public BranchRecord newBranch(Duration ageOfUniverse) {
		assert ageOfUniverse.compareTo(Duration.ZERO) > 0;

		return new BranchRecord(this, branchTime.plus(ageOfUniverse));
	}

	@Override
	public int compareTo(BranchRecord o) {
		// The same parent, compare times
		if (Objects.equals(parentBranch, o.parentBranch)) {
			return o.branchTime.compareTo(branchTime);
		}

		if (parentBranch == null) {
			// this < o
			return -1;
		}

		if (o.parentBranch == null) {
			// this > o
			return 1;
		}

		// The also handles cases where one branch is the other's parent
		var timeCompare = branchTime.compareTo(o.branchTime);

		if (timeCompare == 0) {
			return parentBranch.compareTo(o.parentBranch);
		}

		if (timeCompare < 0) {
			return equals(o.parentBranch)
					? -1
					: compareTo(o.parentBranch);
		} else {
			return parentBranch.equals(o)
					? 1
					: parentBranch.compareTo(o);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(parentBranch, branchTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		return obj instanceof BranchRecord branchRecord
				&& branchTime.equals(branchRecord.branchTime)
				&& Objects.equals(branchRecord.parentBranch, parentBranch);
	}
}
