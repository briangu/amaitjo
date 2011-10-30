package ravi.contest.ants.test.message;

import junit.framework.TestCase;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.message.TopologySay2;

public class TestTopologySay2 extends TestCase {
	public void testCreation() {
		TopologySay2 say = new TopologySay2(new Point(-10, 36));
		say.add(Direction.east, LocalMap.SquareType.EMPTY);
		say.add(Direction.east, LocalMap.SquareType.OBSTACLE);
		say.add(Direction.south, LocalMap.SquareType.OBSTACLE);
		say.add(Direction.west, LocalMap.SquareType.EMPTY);
		say.add(Direction.northwest, LocalMap.SquareType.OBSTACLE);
		say.add(Direction.northeast, LocalMap.SquareType.EMPTY);
		say.add(Direction.southwest, LocalMap.SquareType.OBSTACLE);
		say.add(Direction.southeast, LocalMap.SquareType.EMPTY);
		say.add(Direction.north, LocalMap.SquareType.EMPTY);

		String serialized = say.serialize();
		TopologySay2 say2 = TopologySay2.deserialize(serialized);
		assertEquals(9, say.size());
		assertEquals(Direction.east, say.getDirection(0));
		assertEquals(LocalMap.SquareType.EMPTY, say.getSquareType(0));
		assertEquals(Direction.east, say.getDirection(1));
		assertEquals(LocalMap.SquareType.OBSTACLE, say.getSquareType(1));
		assertEquals(Direction.south, say.getDirection(2));
		assertEquals(LocalMap.SquareType.OBSTACLE, say.getSquareType(2));
		assertEquals(Direction.west, say.getDirection(3));
		assertEquals(LocalMap.SquareType.EMPTY, say.getSquareType(3));
		assertEquals(Direction.northwest, say.getDirection(4));
		assertEquals(LocalMap.SquareType.OBSTACLE, say.getSquareType(4));
		assertEquals(Direction.northeast, say.getDirection(5));
		assertEquals(LocalMap.SquareType.EMPTY, say.getSquareType(5));
		assertEquals(Direction.southwest, say.getDirection(6));
		assertEquals(LocalMap.SquareType.OBSTACLE, say.getSquareType(6));
		assertEquals(Direction.southeast, say.getDirection(7));
		assertEquals(LocalMap.SquareType.EMPTY, say.getSquareType(7));
		assertEquals(Direction.north, say.getDirection(8));
		assertEquals(LocalMap.SquareType.EMPTY, say.getSquareType(8));
	}
	
	public void testMaxDirs() {
		for (int i=0; i<Integer.MAX_VALUE; i++) {
			if (!isUnder255Chars(i)) {
				System.out.println("Most number of directions under 255 characters is " + (i-1));
				break;
			}
		}
	}

	private static boolean isUnder255Chars(int numDirs) {
		TopologySay2 say = new TopologySay2(new Point(0, 0));
		for (int i=0; i<numDirs; i++) {
			say.add(Direction.north, LocalMap.SquareType.EMPTY);
		}
		return say.serialize().length() < 255;
	}
}
