package ravi.contest.ants.test.map;

import junit.framework.TestCase;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.map.Point;

public class TestPoint extends TestCase {
	public void testSerialization() {
		Point p1 = new Point(13, 90);
		Point p2 = new Point(-13, 90);
		Point p3 = new Point(13, -90);
		Point p4 = new Point(-13, -90);

		int serializedPoint1 = p1.serialize();
		Point deserializedPoint1 = Point.deserialize(serializedPoint1);
		assertEquals(p1, deserializedPoint1);
		assertFalse(p2.equals(deserializedPoint1));
		assertFalse(p3.equals(deserializedPoint1));
		assertFalse(p4.equals(deserializedPoint1));

		int serializedPoint2 = p2.serialize();
		Point deserializedPoint2 = Point.deserialize(serializedPoint2);
		assertEquals(p2, deserializedPoint2);
		assertFalse(p1.equals(deserializedPoint2));
		assertFalse(p3.equals(deserializedPoint2));
		assertFalse(p4.equals(deserializedPoint2));

		int serializedPoint3 = p3.serialize();
		Point deserializedPoint3 = Point.deserialize(serializedPoint3);
		assertEquals(p3, deserializedPoint3);
		assertFalse(p1.equals(deserializedPoint3));
		assertFalse(p2.equals(deserializedPoint3));
		assertFalse(p4.equals(deserializedPoint3));

		int serializedPoint4 = p4.serialize();
		Point deserializedPoint4 = Point.deserialize(serializedPoint4);
		assertEquals(p4, deserializedPoint4);
		assertFalse(p1.equals(deserializedPoint4));
		assertFalse(p2.equals(deserializedPoint4));
		assertFalse(p3.equals(deserializedPoint4));
	}
	
	public void testEquals() {
		Point p = new Point(-12, 75);
		Point p2 = new Point(-12, 75);
		assertTrue(p.equals(p2));
		assertEquals(p.hashCode(), p2.hashCode());
	}
	
	public void testDirectionTo() {
		Point p1 = new Point(-12, 75);
		Point p2 = new Point(-12, 76);
		assertEquals(Direction.north, p1.getDirectionTo(p2));
		assertEquals(Direction.south, p2.getDirectionTo(p1));

		p1 = new Point(-12, 75);
		p2 = new Point(-13, 75);
		assertEquals(Direction.west, p1.getDirectionTo(p2));
		assertEquals(Direction.east, p2.getDirectionTo(p1));

		p1 = new Point(-12, 75);
		p2 = new Point(-13, 76);
		assertEquals(Direction.northwest, p1.getDirectionTo(p2));
		assertEquals(Direction.southeast, p2.getDirectionTo(p1));

		p1 = new Point(-12, 75);
		p2 = new Point(-13, 74);
		assertEquals(Direction.southwest, p1.getDirectionTo(p2));
		assertEquals(Direction.northeast, p2.getDirectionTo(p1));
	}
}
