package ravi.contest.ants.test.map;


import junit.framework.TestCase;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.Walk;


public class TestWalk extends TestCase {
	public void testCreation() {
		Walk walk = new Walk();
		walk.goForth(Direction.east);
		walk.goForth(Direction.north);
		walk.goForth(Direction.north);
		walk.goForth(Direction.northeast);

		// Now walk back and assert at each step.
		assertEquals(Direction.northeast, walk.goBack());
		assertEquals(Direction.north, walk.goBack());
		assertEquals(Direction.north, walk.goBack());
		assertEquals(Direction.east, walk.goBack());
	}
}
