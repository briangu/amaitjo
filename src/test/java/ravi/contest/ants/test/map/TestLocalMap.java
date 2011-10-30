package ravi.contest.ants.test.map;

import java.util.TreeSet;

import junit.framework.TestCase;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;


public class TestLocalMap extends TestCase {
	public void test1() {
		LocalMap localMap = new LocalMap();
		assertEquals(SquareType.UNKNOWN, localMap.getSquareType(1, 2));

		localMap.setSquareType(1, 2, SquareType.OBSTACLE);
		assertEquals(SquareType.OBSTACLE, localMap.getSquareType(1, 2));

		localMap.setSquareType(-1, 2, SquareType.EMPTY);
		assertEquals(SquareType.EMPTY, localMap.getSquareType(-1, 2));
	}

	public void test2() {
		LocalMap localMap = new LocalMap();
		localMap.setSquareType(-1, 2, SquareType.EMPTY);
		assertEquals(SquareType.EMPTY, localMap.getSquareType(-1, 2));

		for (int i=-512; i<513; i++) {
			for (int j=-512; j<513; j++) {
				if ((i == -1) && (j == 2)) {
					assertEquals(SquareType.EMPTY, localMap.getSquareType(i, j));
				} else {
					assertEquals(SquareType.UNKNOWN, localMap.getSquareType(i, j));
				}
			}
		}
	}
	
	public void test3() {
		LocalMap localMap = new LocalMap();
		localMap.setSquareType(10, 20, SquareType.EMPTY);
		localMap.setSquareType(9, 21, SquareType.EMPTY);
		localMap.setSquareType(15, -25, SquareType.EMPTY);
		localMap.setSquareType(15, -30, SquareType.EMPTY);
		localMap.setSquareType(-5, 6, SquareType.EMPTY);
		localMap.setSquareType(-10, 7, SquareType.EMPTY);
		assertEquals(15, localMap.getRightMargin());
		assertEquals(-10, localMap.getLeftMargin());
		assertEquals(21, localMap.getTopMargin());
		assertEquals(-30, localMap.getBottomMargin());
	}

	public void testGetSquares() {
		LocalMap localMap = new LocalMap();
		localMap.setSquareType(10, 20, SquareType.EMPTY);
		localMap.setSquareType(9, 21, SquareType.EMPTY);
		localMap.setSquareType(15, -25, SquareType.EMPTY);
		localMap.setSquareType(15, -30, SquareType.EMPTY);
		localMap.setSquareType(-5, 6, SquareType.EMPTY);
		localMap.setSquareType(-10, 7, SquareType.EMPTY);
		localMap.setSquareType(-500, 7, SquareType.EMPTY);

		TreeSet<Point> points = localMap.getSquares();
		assertTrue(points.contains(new Point(10, 20)));
		assertTrue(points.contains(new Point(9, 21)));
		assertTrue(points.contains(new Point(15, -25)));
		assertTrue(points.contains(new Point(15, -30)));
		assertTrue(points.contains(new Point(-5, 6)));
		assertTrue(points.contains(new Point(-10, 7)));
		assertTrue(points.contains(new Point(-500, 7)));
//		for (Point p : points) {
//			System.out.println(p);
//		}
	}
	
	public void testGetSquares2() {
		LocalMap localMap = new LocalMap();
		for (int i=-512; i<0; i++) {
			for (int j=-512; j<0; j++) {
				localMap.setSquareType(i, j, SquareType.EMPTY);
			}
		}
		TreeSet<Point> points = localMap.getSquares();
		for (int i=-512; i<0; i++) {
			for (int j=-512; j<0; j++) {
				assertTrue(points.contains(new Point(i, j)));
			}
		}
	}

	public void testGetSquares3() {
		LocalMap localMap = new LocalMap();
		for (int i=0; i<512; i++) {
			for (int j=0; j<512; j++) {
				localMap.setSquareType(i, j, SquareType.EMPTY);
			}
		}
		TreeSet<Point> points = localMap.getSquares();
		for (int i=0; i<512; i++) {
			for (int j=0; j<512; j++) {
				assertTrue(points.contains(new Point(i, j)));
			}
		}
	}

	public void testGetSquares4() {
		LocalMap localMap = new LocalMap();
		for (int i=-256; i<256; i++) {
			for (int j=-256; j<256; j++) {
				localMap.setSquareType(i, j, SquareType.EMPTY);
			}
		}
		TreeSet<Point> points = localMap.getSquares();
		for (int i=-256; i<256; i++) {
			for (int j=-256; j<256; j++) {
				assertTrue(points.contains(new Point(i, j)));
			}
		}
	}
}
