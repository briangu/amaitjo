package ravi.contest.ants.test.map;

import junit.framework.TestCase;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.ShortestPathCalculator;
import ravi.contest.ants.map.Walk2;

public class TestShortestPathCalculator extends TestCase {
	/**
	 * 6: .#.$.#
	 * 5: #.###.
	 * 4: .##.#.
	 * 3: .#@.#.
	 * 2: .#.#..
	 * 1: #.####
	 */
	public void test1() {
		ShortestPathCalculator calc = new ShortestPathCalculator();
		LocalMap map = new LocalMap();
		map.setSquareType(1, 1, SquareType.OBSTACLE);
		map.setSquareType(1, 2, SquareType.EMPTY);
		map.setSquareType(1, 3, SquareType.EMPTY);
		map.setSquareType(1, 4, SquareType.EMPTY);
		map.setSquareType(1, 5, SquareType.OBSTACLE);
		map.setSquareType(1, 6, SquareType.EMPTY);
		
		map.setSquareType(2, 1, SquareType.EMPTY);
		map.setSquareType(2, 2, SquareType.OBSTACLE);
		map.setSquareType(2, 3, SquareType.OBSTACLE);
		map.setSquareType(2, 4, SquareType.OBSTACLE);
		map.setSquareType(2, 5, SquareType.EMPTY);
		map.setSquareType(2, 6, SquareType.OBSTACLE);

		map.setSquareType(3, 1, SquareType.OBSTACLE);
		map.setSquareType(3, 2, SquareType.EMPTY);
		map.setSquareType(3, 3, SquareType.EMPTY);
		map.setSquareType(3, 4, SquareType.OBSTACLE);
		map.setSquareType(3, 5, SquareType.OBSTACLE);
		map.setSquareType(3, 6, SquareType.EMPTY);

		map.setSquareType(4, 1, SquareType.OBSTACLE);
		map.setSquareType(4, 2, SquareType.OBSTACLE);
		map.setSquareType(4, 3, SquareType.EMPTY);
		map.setSquareType(4, 4, SquareType.EMPTY);
		map.setSquareType(4, 5, SquareType.OBSTACLE);
		map.setSquareType(4, 6, SquareType.EMPTY);

		map.setSquareType(5, 1, SquareType.OBSTACLE);
		map.setSquareType(5, 2, SquareType.EMPTY);
		map.setSquareType(5, 3, SquareType.OBSTACLE);
		map.setSquareType(5, 4, SquareType.OBSTACLE);
		map.setSquareType(5, 5, SquareType.OBSTACLE);
		map.setSquareType(5, 6, SquareType.EMPTY);

		map.setSquareType(6, 1, SquareType.OBSTACLE);
		map.setSquareType(6, 2, SquareType.EMPTY);
		map.setSquareType(6, 3, SquareType.EMPTY);
		map.setSquareType(6, 4, SquareType.EMPTY);
		map.setSquareType(6, 5, SquareType.EMPTY);
		map.setSquareType(6, 6, SquareType.OBSTACLE);
		
		Point source = new Point(3, 3);
		Point destination = new Point(4, 6);
		
		Walk2 walk = map.getShortestPath(source, destination);
//		Walk2 walk = calc.getApproxShortestPath(map, source, destination);
		assertEquals(source, walk.getPoint(0));
		assertEquals(new Point(4, 3), walk.getPoint(1));
		assertEquals(new Point(5, 2), walk.getPoint(2));
		assertEquals(new Point(6, 3), walk.getPoint(3));
		assertEquals(new Point(6, 4), walk.getPoint(4));
		assertEquals(new Point(6, 5), walk.getPoint(5));
		assertEquals(new Point(5, 6), walk.getPoint(6));
		assertEquals(destination, walk.getPoint(7));
	}
	
	/**
	 * 4: #@.......
	 * 3: #####...#
	 * 2: #.....###
	 * 1: #$......#
	 *    123456789
	 */
	public void test2() {
		ShortestPathCalculator calc = new ShortestPathCalculator();
		LocalMap map = new LocalMap();

		map.setSquareType(1, 1, SquareType.OBSTACLE);
		map.setSquareType(2, 1, SquareType.EMPTY);
		map.setSquareType(3, 1, SquareType.EMPTY);
		map.setSquareType(4, 1, SquareType.EMPTY);
		map.setSquareType(5, 1, SquareType.EMPTY);
		map.setSquareType(6, 1, SquareType.EMPTY);
		map.setSquareType(7, 1, SquareType.EMPTY);
		map.setSquareType(8, 1, SquareType.EMPTY);
		map.setSquareType(9, 1, SquareType.OBSTACLE);

		map.setSquareType(1, 2, SquareType.OBSTACLE);
		map.setSquareType(2, 2, SquareType.EMPTY);
		map.setSquareType(3, 2, SquareType.EMPTY);
		map.setSquareType(4, 2, SquareType.EMPTY);
		map.setSquareType(5, 2, SquareType.EMPTY);
		map.setSquareType(6, 2, SquareType.EMPTY);
		map.setSquareType(7, 2, SquareType.OBSTACLE);
		map.setSquareType(8, 2, SquareType.OBSTACLE);
		map.setSquareType(9, 2, SquareType.OBSTACLE);

		map.setSquareType(1, 3, SquareType.OBSTACLE);
		map.setSquareType(2, 3, SquareType.OBSTACLE);
		map.setSquareType(3, 3, SquareType.OBSTACLE);
		map.setSquareType(4, 3, SquareType.OBSTACLE);
		map.setSquareType(5, 3, SquareType.OBSTACLE);
		map.setSquareType(6, 3, SquareType.EMPTY);
		map.setSquareType(7, 3, SquareType.EMPTY);
		map.setSquareType(8, 3, SquareType.EMPTY);
		map.setSquareType(9, 3, SquareType.OBSTACLE);

		map.setSquareType(1, 4, SquareType.OBSTACLE);
		map.setSquareType(2, 4, SquareType.EMPTY);
		map.setSquareType(3, 4, SquareType.EMPTY);
		map.setSquareType(4, 4, SquareType.EMPTY);
		map.setSquareType(5, 4, SquareType.EMPTY);
		map.setSquareType(6, 4, SquareType.EMPTY);
		map.setSquareType(7, 4, SquareType.EMPTY);
		map.setSquareType(8, 4, SquareType.EMPTY);
		map.setSquareType(9, 4, SquareType.EMPTY);

		Point source = new Point(2, 4);
		Point destination = new Point(2, 1);
		
		Walk2 walk = map.getShortestPath(source, destination);
//		System.out.println(walk);
		assertEquals(source, walk.getPoint(0));
		assertEquals(new Point(3, 4), walk.getPoint(1));
		assertEquals(new Point(4, 4), walk.getPoint(2));
		assertEquals(new Point(5, 4), walk.getPoint(3));
		assertEquals(new Point(6, 3), walk.getPoint(4));
		assertEquals(new Point(6, 2), walk.getPoint(5));
		assertEquals(new Point(6, 1), walk.getPoint(6));
		assertEquals(new Point(5, 1), walk.getPoint(7));
		assertEquals(new Point(4, 1), walk.getPoint(8));
		assertEquals(new Point(3, 1), walk.getPoint(9));
		assertEquals(destination, walk.getPoint(10));
	}
}
