package ravi.contest.ants.movement;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.map.Direction2;
import ravi.contest.ants.map.Point;

public class MovementUtils {
	// Maximum ratio allowed for the length of a walk to average distance between two points.
	// If it takes us more than this to cover the distance, we can be reasonably sure that
	// there is no path between the points.
	public static final int MAX_WALK_LENGTH_TO_DISTANCE_RATIO = 3; // heuristic.

	public static Point move(Point p, Direction d) {
		Direction2 dir = Direction2.get(d);
		return new Point(p.x + dir.x, p.y + dir.y);
	}
	
	public static int getEstimatedWalkLength(int distance, double obstacleDensity) {
		// Estimate: e^{(2*density)^5} = e^{32*density^5}
		return (int) Math.ceil(distance * Math.pow(Math.E, Math.pow(2*obstacleDensity, 5)));
	}
	
	public static int getMaximumAllowedWalkLengthForSearch(int distance, double obstacleDensity) {
//		int maxAllowedWalkLength = Math.max(
//			MAX_WALK_LENGTH_TO_DISTANCE_RATIO * getEstimatedWalkLength(distance, obstacleDensity),
//			50
//		);
//		return maxAllowedWalkLength;
		return 2*distance;
	}
}
