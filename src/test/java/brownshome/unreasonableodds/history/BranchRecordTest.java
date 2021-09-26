package brownshome.unreasonableodds.history;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchRecordTest {
	private final Instant a = Instant.ofEpochSecond(1);
	private final Instant b = Instant.ofEpochSecond(2);

	@Test
	void compareEquals() {
		assertEquals(0, BranchRecord.blankRecord(a).compareTo(BranchRecord.blankRecord(a)));
	}

	@Test
	void compareRootBranches() {
		assertTrue(BranchRecord.blankRecord(a).compareTo(BranchRecord.blankRecord(b)) > 0);
		assertTrue(BranchRecord.blankRecord(b).compareTo(BranchRecord.blankRecord(a)) < 0);
	}

	@Test
	void compareAncestors() {
		var child = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1));

		assertTrue(BranchRecord.blankRecord(a).compareTo(child) < 0);
		assertTrue(child.compareTo(BranchRecord.blankRecord(a)) > 0);
	}

	@Test
	void compareSiblings() {
		var childB = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1));
		var childC = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(2));

		assertTrue(childB.compareTo(childC) > 0);
		assertTrue(childC.compareTo(childB) < 0);
	}

	@Test
	void compareDifferentTrees() {
		var childA = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1));
		var childB = BranchRecord.blankRecord(b).newBranch(Duration.ofSeconds(1));

		assertTrue(childA.compareTo(childB) > 0);
		assertTrue(childB.compareTo(childA) < 0);
	}

	@Test
	void equals() {
		var first = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1)).newBranch(Duration.ofSeconds(1));
		var second = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1)).newBranch(Duration.ofSeconds(1));

		assertTrue(first.equals(second));
	}

	@Test
	void testHashCode() {
		var first = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1)).newBranch(Duration.ofSeconds(1));
		var second = BranchRecord.blankRecord(a).newBranch(Duration.ofSeconds(1)).newBranch(Duration.ofSeconds(1));

		assertTrue(first.hashCode() == second.hashCode());
	}
}