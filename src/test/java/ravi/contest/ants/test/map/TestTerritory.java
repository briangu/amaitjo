package ravi.contest.ants.test.map;

import junit.framework.TestCase;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;

public class TestTerritory extends TestCase {
	public void testNumTerritoriesAlongASide() {
		// Assuming board size is 512x512 and num ants is 50.
		assertEquals(7, MapUtils.getNumTerritoriesAlongASide());
	}

	public void testTerritorySide() {
		// Assuming board size is 512x512 and num ants is 50.
		assertEquals(74, MapUtils.getTerritorySideLength());
	}
	
	public void testTerritoryPoints0() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(0);
		Point expectedBottomLeft = new Point(-256, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(0);
		Point expectedTopRight = new Point(-183, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints1() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(1);
		Point expectedBottomLeft = new Point(-182, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(1);
		Point expectedTopRight = new Point(-109, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints2() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(2);
		Point expectedBottomLeft = new Point(-108, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(2);
		Point expectedTopRight = new Point(-35, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints3() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(3);
		Point expectedBottomLeft = new Point(-34, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(3);
		Point expectedTopRight = new Point(39, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints4() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(4);
		Point expectedBottomLeft = new Point(40, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(4);
		Point expectedTopRight = new Point(113, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints5() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(5);
		Point expectedBottomLeft = new Point(114, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(5);
		Point expectedTopRight = new Point(187, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints6() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(6);
		Point expectedBottomLeft = new Point(188, -256);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(6);
		Point expectedTopRight = new Point(256, -183);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints7() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(7);
		Point expectedBottomLeft = new Point(-256, -182);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(7);
		Point expectedTopRight = new Point(-183, -109);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints13() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(13);
		Point expectedBottomLeft = new Point(188, -182);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(13);
		Point expectedTopRight = new Point(256, -109);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints42() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(42);
		Point expectedBottomLeft = new Point(-256, 188);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(42);
		Point expectedTopRight = new Point(-183, 256);
		assertEquals(expectedTopRight, topRight);
	}

	public void testTerritoryPoints48() {
		Point bottomLeft = MapUtils.getTerritoryBottomLeftPoint(48);
		Point expectedBottomLeft = new Point(188, 188);
		assertEquals(expectedBottomLeft, bottomLeft);
		
		Point topRight = MapUtils.getTerritoryTopRightPoint(48);
		Point expectedTopRight = new Point(256, 256);
		assertEquals(expectedTopRight, topRight);
	}
}
