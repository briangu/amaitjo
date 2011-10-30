package ravi.contest.ants.map;

import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;

import ravi.contest.ants.Constants;

public class MapUtils {
  public static int getDistance(Point a, Point b) {
    return getDistance(a.x, a.y, b.x, b.y);
  }

	public static int getDistance(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
//		return Math.abs(x1-x2) + Math.abs(y1-y2);
	}
	
	public static double getCosineDistance(int x1, int y1, int x2, int y2) {
		return (x1*x2 + y1*y2) / (Math.sqrt(x1*x1 + y1*y1) * Math.sqrt(x2*x2 + y2*y2));
	}
	
	// Input points should be in absolute co-ordinates, not relative to nest.
	public static Point pivotAroundNest(Point absoluteNest, Point absoluteP) {
		Point pivot = new Point();
		pivot.x = Constants.BOARD_SIZE/2 + absoluteNest.x - absoluteP.x;
		pivot.y = Constants.BOARD_SIZE/2 + absoluteNest.y - absoluteP.y;
		return pivot;
	}
	
	// Determines if the given square is free of obstacles.
	public static boolean isOpenSquare(Environment envt) {
		return envt.getSquare(Direction.here).isPassable()
				&& envt.getSquare(Direction.north).isPassable()
				&& envt.getSquare(Direction.northeast).isPassable()
				&& envt.getSquare(Direction.northwest).isPassable()
				&& envt.getSquare(Direction.south).isPassable()
				&& envt.getSquare(Direction.southeast).isPassable()
				&& envt.getSquare(Direction.southwest).isPassable()
				&& envt.getSquare(Direction.east).isPassable()
				&& envt.getSquare(Direction.west).isPassable();
	}
	
	// Gets the direction best aligned with the vector from source to destination.
	public static Direction2 getBestDirection(int sourceX, int sourceY, int destX, int destY) {
		int destVectorX = destX - sourceX;
		int destVectorY = destY - sourceY;

		Direction2 bestDirection = null;
		double bestVectorOverlap = -Constants.BOARD_SIZE;
		// Check every possible direction.
		for (Direction2 dir : Direction2.getDirections()) {
			if (dir != Direction2.here) {
				double currentVectorOverlap = MapUtils.getCosineDistance(dir.x, dir.y, destVectorX, destVectorY); // dot product is commutative.
				// This is a better direction if the degree of overlap is more than seen so far.
				if ((bestDirection == null) || (currentVectorOverlap > bestVectorOverlap)) {
					bestVectorOverlap = currentVectorOverlap;
					bestDirection = dir;
				}
			}
		}
		
		return bestDirection;
	}

	// Gets the number of territories along the side of the board.
	// Each territory is allocated to one ant.
	public static int getNumTerritoriesAlongASide() {
		// Number of territories must be at most equal to the number of ants.
		// In other words, each territory must be assigned to a different ant.
		// This necessitates that the number of territories along one side of
		// the world must be less than or equal to the square root of the number
		// of ants.
		return (int) Math.floor(Math.sqrt(Constants.NUM_ANTS));
	}
	
	// Gets the length of the side of the square representing the territory
	// allocated to ant with a given id.
	public static int getTerritorySideLength() {
		// Each square must be present in some territory. This necessitates
		// a ceiling function instead of a floor.
		return (int) Math.ceil(Constants.BOARD_SIZE * 1.0 / getNumTerritoriesAlongASide());
	}
	
	// Gets the bottom left point of the territory allocated to ant with given id.
	public static Point getTerritoryBottomLeftPoint(int id) {
		// Get the number of areas along each side of the square.
		// This is the square root of the number of ants available.
		int numAreasAlongSide = getNumTerritoriesAlongASide();
		int squareWidth = getTerritorySideLength();
		
		Point territoryBottomLeftPoint = new Point();
		territoryBottomLeftPoint.x = (id % numAreasAlongSide) * squareWidth - (Constants.BOARD_SIZE / 2);
		territoryBottomLeftPoint.y = (id / numAreasAlongSide) * squareWidth - (Constants.BOARD_SIZE / 2);
		
		return territoryBottomLeftPoint;
	}
	
	// Gets the top right point of the territory allocated to ant with given id.
	public static Point getTerritoryTopRightPoint(int id) {
		// Get the number of areas along each side of the square.
		// This is the square root of the number of ants available.
		int squareWidth = getTerritorySideLength();
		
		Point territoryTopRightPoint = getTerritoryBottomLeftPoint(id);
		territoryTopRightPoint.x += (squareWidth - 1);
		if (territoryTopRightPoint.x > Constants.BOARD_SIZE/2) {
			territoryTopRightPoint.x = Constants.BOARD_SIZE/2;
		}
		territoryTopRightPoint.y += (squareWidth - 1);
		if (territoryTopRightPoint.y > Constants.BOARD_SIZE/2) {
			territoryTopRightPoint.y = Constants.BOARD_SIZE/2;
		}
		
		return territoryTopRightPoint;
	}
}
