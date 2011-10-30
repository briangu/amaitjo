package ravi.contest.ants.movement;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.Constants;
import ravi.contest.ants.map.Direction2;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;

public class NavigationAdvisor {
	/**
	 * Advises on the best move to go from location to destination.
	 * @param location where you are at, right now
	 * @param destination where you want to go to
	 * @param map Information about the environment.
	 * @param walk Walk leading up to the location.
	 * @return best move leading to the destination.
	 */
	public Direction advise(Point location, Point destination, LocalMap map, Walk2 walk) {
		if (location.equals(destination)) {
			// (0) We are at our destination!
			return Direction.here;
		} else {
			// We are not at our destination yet. Determine the direction vector
			// best aligned with the location-direction vector. Degree of
			// alignment is given by the dot product of the two vectors.
			int destVectorX = destination.x - location.x;
			int destVectorY = destination.y - location.y;

			Direction2 bestDirection = null;
			double bestVectorOverlap = -Constants.BOARD_SIZE;
			// Check every possible direction, centered around the location.
			for (Direction2 dir : Direction2.getDirections()) {
				if (dir != Direction2.here) {
					Point p = new Point(location.x + dir.x, location.y + dir.y);
					// This direction is a candidate if doesn't lead to an obstacle and wasn't visited earlier.
					LocalMap.SquareType squareType = map.getSquareType(p.x, p.y);
					if ((squareType != LocalMap.SquareType.OBSTACLE) && (squareType != LocalMap.SquareType.UNKNOWN) && (!walk.hasVisited(p))) {
						double currentVectorOverlap = MapUtils.getCosineDistance(dir.x, dir.y, destVectorX, destVectorY); // dot product is commutative.
						// This is a better direction if the degree of overlap is more than seen so far.
						if ((bestDirection == null) || (currentVectorOverlap > bestVectorOverlap)) {
							bestVectorOverlap = currentVectorOverlap;
							bestDirection = dir;
						}
					}
				}
			}
			
			if (bestDirection == null) {
				// Reached a dead end, meaning no unvisited points are left.
				// We backtrack from here to the point just prior to visiting
				// this location (i.e. prior to the first time we got here).
				int stepNumber = walk.getEarliestVisit(location); // when was I first at this location?
				Point prevPoint = walk.getPoint(stepNumber - 1); // Get the point prior to this.
				if (prevPoint != null) {
					// We do have a prior point (i.e. we are not at the beginning of the walk).
					bestDirection = Direction2.get(prevPoint.x - location.x, prevPoint.y - location.y);
				}
			}
			
			return bestDirection != null ? bestDirection.direction : null;
		}
	}
}
