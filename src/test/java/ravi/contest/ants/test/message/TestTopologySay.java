package ravi.contest.ants.test.message;

import java.util.Map;

import junit.framework.TestCase;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.message.TopologySay;

public class TestTopologySay extends TestCase {
	public void testCreation() {
		TopologySay say = new TopologySay();
		Point p1 = new Point(13, 90);
		Point p2 = new Point(-13, 90);
		Point p3 = new Point(13, -90);
		Point p4 = new Point(-13, -90);
		
		say.add(p1, SquareType.EMPTY);
		say.add(p2, SquareType.HAS_FOOD);
		say.add(p3, SquareType.OBSTACLE);
		say.add(p4, SquareType.UNKNOWN);
		
		String serialized = say.serialize();
		assertEquals("TOPOLOGYNaAlN6glNKAFNqgF", serialized);
		
		TopologySay deserializedSay = TopologySay.deserialize(serialized);
		Map<Point, SquareType> topology = deserializedSay.getTopology();
		assertEquals(4, topology.size());
		assertTrue(topology.containsKey(p1));
		assertTrue(topology.containsKey(p2));
		assertTrue(topology.containsKey(p3));
		assertTrue(topology.containsKey(p4));
	}
	
	public void testLength255() {
		for (int i=30; i<80; i++) {
			testSerializedLength(i);
		}
	}
	
	private void testSerializedLength(int numPoints) {
		TopologySay say = new TopologySay();
		for (int i=0; i<numPoints; i++) {
			Point p = new Point(10*i, 10*i);
			say.add(p, SquareType.EMPTY);
		}
		String serialized = say.serialize();
		System.out.println(numPoints + " points -> size " + serialized.length());
	}
}
