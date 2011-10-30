package ravi.contest.ants.oracle;

import java.util.HashMap;
import java.util.Map;

import ravi.contest.ants.Constants;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;

public class TerritoryMap {
	private TerritoryType[][] _map;
	
	private Point _center;
	
	private Map<Point, TerritoryType> _map2;
	private int _sideSize;
	
	public TerritoryMap() {
		_sideSize = (int) Math.floor(Math.sqrt(Constants.NUM_ANTS));
		_map = new TerritoryType[_sideSize][_sideSize];

		_center = new Point(_sideSize/2, _sideSize/2);

		_map2 = new HashMap<Point, TerritoryType>();
		for (int i=0; i<_sideSize; i++) {
			for (int j=0; j<_sideSize; j++) {
				//_map2.put(key, TerritoryType.UNKNOWN);
			}
		}
		// Mark the center as reachable.
		_map2.put(_center, TerritoryType.REACHABLE);
	}
	
	public void setBoundary(int antId, Point p) {
		
	}
	
	// Gets the territory in the territory map for the given point in the real world map.
	// NOTE: this method is public to allow for unit tests.
	public static Point getTerritory(int x, int y) {
		int squareSide = MapUtils.getTerritorySideLength();
		int territoryX = (int) Math.ceil((x + Constants.BOARD_SIZE/2) * 1.0 / squareSide);
		int territoryY = (int) Math.ceil((y + Constants.BOARD_SIZE/2) * 1.0 / squareSide);
		return new Point(territoryX, territoryY);
	}
}
