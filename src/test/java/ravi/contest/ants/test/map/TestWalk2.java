package ravi.contest.ants.test.map;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;


public class TestWalk2 extends TestCase {
	public void testOne() {
		Walk2 walk = new Walk2();
		Point p1 = new  Point(1, 2);
		walk.visit(p1);
		Point p2 = new  Point(1, 2);
		assertTrue(p1.hashCode() == p2.hashCode());
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
		assertTrue(walk.hasVisited(p2));
		
		Set<Point> set = new HashSet<Point>();
		set.add(p1);
		assertTrue(set.contains(p1));
		assertTrue(set.contains(p2));
		
		Map<Point, Boolean> map = new HashMap<Point, Boolean>();
		map.put(p1, Boolean.TRUE);
		assertTrue(map.containsKey(p2));
	}
	
	// A->B->C->B->D compresses to
	// A->B->D
	public void testCompress() {
		Walk2 walk = new Walk2();
		Point p1 = new Point(1, 2); // Point A
		Point p2 = new Point(2, 3); // Point B
		Point p3 = new Point(3, 4); // Point C
		Point p4 = new Point(2, 3); // Point B
		Point p5 = new Point(2, 4); // Point D
		
		walk.visit(p1);
		walk.visit(p2);
		walk.visit(p3);
		walk.visit(p4);
		walk.visit(p5);
		
		walk.compress();
		assertEquals(3, walk.getTotalSteps());
		assertEquals(0, walk.getEarliestVisit(p1));
		assertEquals(1, walk.getEarliestVisit(p2));
		assertEquals(2, walk.getEarliestVisit(p5));
		assertEquals(-1, walk.getEarliestVisit(p3));
	}
	
	// A->B->C->B->D->E->D compresses to
	// A->B->D
	public void testCompress2() {
		Walk2 walk = new Walk2();
		Point p1 = new Point(1, 2); // Point A
		Point p2 = new Point(2, 3); // Point B
		Point p3 = new Point(3, 4); // Point C
		Point p4 = new Point(2, 3); // Point B
		Point p5 = new Point(2, 4); // Point D
		Point p6 = new Point(2, 5); // Point E
		Point p7 = new Point(2, 4); // Point D
		
		walk.visit(p1);
		walk.visit(p2);
		walk.visit(p3);
		walk.visit(p4);
		walk.visit(p5);
		walk.visit(p6);
		walk.visit(p7);

		walk.compress();
		
		assertEquals(3, walk.getTotalSteps());
		assertEquals(0, walk.getEarliestVisit(p1));
		assertEquals(1, walk.getEarliestVisit(p2));
		assertEquals(2, walk.getEarliestVisit(p5));
		assertEquals(-1, walk.getEarliestVisit(p3));
		assertEquals(-1, walk.getEarliestVisit(p6));
	}
	
	// A->B->C->B->D->E->D->B compresses to
	// A->B
	public void testCompress3() {
		Walk2 walk = new Walk2();
		Point p1 = new Point(1, 2); // Point A
		Point p2 = new Point(2, 3); // Point B
		Point p3 = new Point(3, 4); // Point C
		Point p4 = new Point(2, 3); // Point B
		Point p5 = new Point(2, 4); // Point D
		Point p6 = new Point(2, 5); // Point E
		Point p7 = new Point(2, 4); // Point D
		Point p8 = new Point(2, 3); // Point B
		
		walk.visit(p1);
		walk.visit(p2);
		walk.visit(p3);
		walk.visit(p4);
		walk.visit(p5);
		walk.visit(p6);
		walk.visit(p7);
		walk.visit(p8);

		walk.compress();
		
		assertEquals(2, walk.getTotalSteps());
		assertEquals(0, walk.getEarliestVisit(p1));
		assertEquals(1, walk.getEarliestVisit(p2));
		assertEquals(-1, walk.getEarliestVisit(p3));
		assertEquals(-1, walk.getEarliestVisit(p5));
		assertEquals(-1, walk.getEarliestVisit(p6));
	}
	
	// A->B->C->D->B->C->E compresses to
	// A->B->C->E
	public void testCompress4() {
		Walk2 walk = new Walk2();
		Point p1 = new Point(1, 2); // Point A
		Point p2 = new Point(2, 3); // Point B
		Point p3 = new Point(3, 4); // Point C
		Point p4 = new Point(2, 4); // Point D
		Point p5 = new Point(2, 3); // Point B
		Point p6 = new Point(3, 4); // Point C
		Point p7 = new Point(2, 5); // Point E
		
		walk.visit(p1);
		walk.visit(p2);
		walk.visit(p3);
		walk.visit(p4);
		walk.visit(p5);
		walk.visit(p6);
		walk.visit(p7);

		walk.compress();
		
		assertEquals(4, walk.getTotalSteps());
		assertEquals(0, walk.getEarliestVisit(p1));
		assertEquals(1, walk.getEarliestVisit(p2));
		assertEquals(2, walk.getEarliestVisit(p3));
		assertEquals(3, walk.getEarliestVisit(p7));
		assertEquals(-1, walk.getEarliestVisit(p4));
		assertTrue(walk.hasVisited(p1));
		assertTrue(walk.hasVisited(p2));
		assertTrue(walk.hasVisited(p3));
		assertTrue(walk.hasVisited(p7));
		assertFalse(walk.hasVisited(p4));
	}
}
