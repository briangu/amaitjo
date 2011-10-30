package ravi.contest.ants.map;

import java.util.ArrayList;
import java.util.List;

import org.linkedin.contest.ants.api.Direction;

public class Walk {
	// Walk is essentially a stack data structure
	// with directed segments as elements.
	private List<DirectedSegment> _walk;
	
	public Walk() {
		_walk = new ArrayList<DirectedSegment>();
	}
	
	// Pop from the stack.
	public Direction goBack() {
		if (_walk.size() > 0) {
			DirectedSegment segment = _walk.get(_walk.size() - 1);
			if (segment.getDistance() <= 1) {
				_walk.remove(_walk.size() - 1);
			} else {
				segment.setDistance(segment.getDistance() - 1);
			}
			return segment.getDirection();
		}
		return null;
	}
	
	// Push into the stack.
	public void goForth(Direction direction) {
		if (_walk.size() > 0) {
			DirectedSegment segment = _walk.get(_walk.size() - 1);
			if (segment.getDirection() == direction) {
				// Same direction as before.
				// Just increment the current segment's length.
				segment.setDistance(segment.getDistance() + 1);
			} else {
				// Create a new directed segment.
				segment = new DirectedSegment(direction, 1);
				_walk.add(segment);
			}
		} else {
			// This is the beginning of the walk.
			DirectedSegment segment = new DirectedSegment(direction, 1);
			_walk.add(segment);
		}
	}
}
