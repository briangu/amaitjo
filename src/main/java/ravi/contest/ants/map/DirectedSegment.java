package ravi.contest.ants.map;

import org.linkedin.contest.ants.api.Direction;

public class DirectedSegment {
	private Direction _direction;
	private int _distance;
	
	public DirectedSegment(Direction dir, int dist) {
		_direction = dir;
		_distance = dist;
	}
	
	public Direction getDirection() {
		return _direction;
	}
	
	public int getDistance() {
		return _distance;
	}
	
	/* package private */void setDistance(int dist) {
		_distance = dist;
	}
}
