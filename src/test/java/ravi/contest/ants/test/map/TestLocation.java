package ravi.contest.ants.test.map;


import junit.framework.TestCase;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.Location;


public class TestLocation extends TestCase {
	public void testCreation() {
		Location loc = new Location();
		loc.add(Direction.north, 1);
		assertTrue(loc.getY() == 1);

		loc.add(Direction.east, 2);
		assertTrue(loc.getY() == 1);
		assertTrue(loc.getX() == 2);

		loc.add(Direction.west, 2);
		assertTrue(loc.getY() == 1);
		assertTrue(loc.getX() == 0);
		
		loc.add(Direction.south, 2);
		loc.add(Direction.west, 2);
		assertTrue(loc.getY() == -1);
		assertTrue(loc.getX() == -2);

		loc.add(Direction.southwest, 2);
		assertTrue(loc.getY() == -3);
		assertTrue(loc.getX() == -4);
		
		loc.add(Direction.northwest, 2);
		assertTrue(loc.getY() == -1);
		assertTrue(loc.getX() == -6);
		
		loc.add(Direction.southeast, 5);
		assertTrue(loc.getY() == -6);
		assertTrue(loc.getX() == -1);
		
		loc.add(Direction.northeast, 10);
		assertTrue(loc.getY() == 4);
		assertTrue(loc.getX() == 9);
	}
	
	public void testSerialize() {
		Location loc = new Location();
		loc.add(Direction.northeast, 1);
		int bits = loc.serialize();
		int expectedBits = (1 << 10) | (1);
		assertTrue(bits == expectedBits);
		
		loc.add(Direction.southeast, 5);
		int bits2 = loc.serialize();
		expectedBits = (1 << 19) | (4 << 10) | (6);
		assertTrue(bits2 == expectedBits);
		
		loc.add(Direction.southwest, 10);
		int bits3 = loc.serialize();
		expectedBits = (1 << 19) | (14 << 10) | (1 << 9) | (4);
		assertTrue(bits3 == expectedBits);
		
		loc.add(Direction.east, 10);
		int bits4 = loc.serialize();
		expectedBits = (1 << 19) | (14 << 10) | (6);
		assertTrue(bits4 == expectedBits);

		loc.add(Direction.north, 20);
		int bits5 = loc.serialize();
		expectedBits = (6 << 10) | (6);
		assertTrue(bits5 == expectedBits);
	}
	
	public void testDeserialize() {
		// south 14, west 4.
		int bits = (1 << 19) | (14 << 10) | (1 << 9) | (4);
		Location loc = Location.deserialize(bits);
		assertEquals(-4, loc.getX());
		assertEquals(-14, loc.getY());
	}
	
	public void testEquals() {
		Location loc = new Location();
		loc.add(Direction.north, 5);
		loc.add(Direction.east, 15);

		Location loc2 = new Location();
		loc2.add(Direction.north, 5);
		loc2.add(Direction.east, 15);
		
		assertEquals(loc, loc2);
	}
}

