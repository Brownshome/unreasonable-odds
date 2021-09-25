package brownshome.unreasonableodds.history;

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

	public BranchRecord newBranch(Instant branchTime) {
		return new BranchRecord(this, branchTime);
	}

	@Override
	public int compareTo(BranchRecord o) {
		// The same parent, compare times
		if (Objects.equals(parentBranch, o.parentBranch)) {
			return branchTime.compareTo(o.branchTime);
		}

		if (parentBranch == null) {
			// this > o
			return 1;
		}

		if (o.parentBranch == null) {
			// this < o
			return -1;
		}

		// The also handles cases where one branch is the other's parent
		return parentBranch.compareTo(o.parentBranch);
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
