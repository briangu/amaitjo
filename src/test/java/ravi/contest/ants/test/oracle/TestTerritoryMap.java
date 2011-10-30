package ravi.contest.ants.test.oracle;

import ravi.contest.ants.map.Point;
import ravi.contest.ants.oracle.TerritoryMap;
import junit.framework.TestCase;

public class TestTerritoryMap extends TestCase {
	public void testGetTerritory() {
		Point p = TerritoryMap.getTerritory(0, 0);
		// Above point should be at the center of the board.
	}
}
