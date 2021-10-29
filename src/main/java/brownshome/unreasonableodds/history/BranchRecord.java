package brownshome.unreasonableodds.history;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import brownshome.netcode.annotation.converter.Networkable;
import brownshome.unreasonableodds.packets.converters.InstantConverter;

public final class BranchRecord implements Comparable<BranchRecord>, Networkable {
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

	public BranchRecord(ByteBuffer buffer) {
		this(Short.toUnsignedInt(buffer.getShort()), buffer);
	}

	/**
	 * Reads from a network buffer
	 * @param n the number of instants to read from the buffer
	 * @param buffer the buffer
	 */
	private BranchRecord(int n, ByteBuffer buffer) {
		this(n == 1 ? null : new BranchRecord(n - 1, buffer), InstantConverter.INSTANCE.read(buffer));
	}

	@Override
	public void write(ByteBuffer buffer) {
		int index = buffer.position();

		// Skip a short
		buffer.getShort();

		int n = writeImpl(buffer);
		assert n < (1 << Short.SIZE);

		// Fill in the length byte
		buffer.putShort(index, (short) n);
	}

	private int writeImpl(ByteBuffer buffer) {
		int i = 1;

		if (parentBranch != null) {
			i += parentBranch.writeImpl(buffer);
		}

		InstantConverter.INSTANCE.write(buffer, branchTime);

		return i;
	}

	@Override
	public int size() {
		return parentBranch != null
				? parentBranch.size() + InstantConverter.INSTANCE.size(branchTime)
				: Short.BYTES + InstantConverter.INSTANCE.size(branchTime);
	}

	@Override
	public boolean isSizeExact() {
		return true;
	}

	@Override
	public boolean isSizeConstant() {
		return false;
	}
}
