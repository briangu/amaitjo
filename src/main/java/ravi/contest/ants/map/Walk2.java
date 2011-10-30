package ravi.contest.ants.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Walk2 {
	private List<Point> _walk;
	private Set<Point> _vertices;

	public Walk2() {
		_walk = new ArrayList<Point>();
		_vertices = new HashSet<Point>();
	}

  public void clear()
  {
    _walk.clear();
    _vertices.clear();
  }

	public void visit(Point p) {
		// You cannot walk to the place you are at now.
    Point prevPoint = getPrevPoint(0);
		if ((getTotalSteps() == 0) || prevPoint == null || (!prevPoint.equals(p))) {
			_walk.add(p);
			_vertices.add(p);
		}
	}

	public boolean hasVisited(Point p) {
		return _vertices.contains(p);
	}

	public Point getPoint(int stepNumber) {
		if ((stepNumber >= 0) && (stepNumber < _walk.size())) {
			return _walk.get(stepNumber);
		}
		return null;
	}
	
	/**
	 * Answers "where was I a few steps ago?".
	 */
	public Point getPrevPoint(int numStepsBack) {
    int index =_walk.size() - 1 - numStepsBack;
		if (index >= 0 && index < _walk.size()) {
			return _walk.get(index);
		} else {
			return null;
		}
	}

	// When was the last time I was at point p?
//	public int getTime(Point p) {
//		return getTime(p, 0);
//	}

	/**
	 * Answers
	 * "when was the last time at or before 'beforeTime', that I was at point p?"
	 * . Returns -1 if such a time was not found.
	 */
//	public int getTime(Point p, int beforeTime) {
//		for (int i = beforeTime; i < _walk.size(); i++) {
//			if (_walk.get(_walk.size() - 1 - i).equals(p)) {
//				return i;
//			}
//		}
//		return -1;
//	}

	public int getEarliestVisit(Point p, int onOrAfterStepNumber) {
		for (int i = onOrAfterStepNumber; i < _walk.size(); i++) {
			if (_walk.get(i).equals(p)) {
				return i;
			}
		}
		return -1;
	}

	public int getEarliestVisit(Point p) {
		return getEarliestVisit(p, 0);
	}

	public int getLatestVisit(Point p, int onOrBeforeStepNumber) {
		for (int i = onOrBeforeStepNumber; i >= 0; i--) {
			if (_walk.get(i).equals(p)) {
				return i;
			}
		}
		return -1;
	}

	public int getLatestVisit(Point p) {
		return getLatestVisit(p, _walk.size() - 1);
	}
	
	// Short-circuits/removes cycles within the walk.
	// E.g. if A->B->C->B->D is the walk, then this
	// will compress the walk to A->B->D.
	public void compress() {
		for (int i=_walk.size()-1; i>=0; i--) {
			// need to make sure that i is within bounds on every iteration.
			if (i >= _walk.size()) {
				i = _walk.size() - 1;
			}
			// When was I earliest at this point?
			int earliestVisit = getEarliestVisit(getPoint(i));
			if (earliestVisit < i) {
				// This point was visited earlier in the walk too.
				// Remove the later instance of this point.
				_walk.remove(i);
				// Remove all steps between [earliestVisit, i], exclusive.
				for (int j=earliestVisit+1; j<i; j++) {
					// Delete point at (earliestVisit+1). We will
					// repeatedly do this till we remove the cycle.
					Point p = _walk.remove(earliestVisit+1);
					// If there is no "p" left in this walk,
					// remove it from the list of vertices.
					int earlierVisitForP = getLatestVisit(p, earliestVisit-1);
					if ((earlierVisitForP >= 0) && (earlierVisitForP < earliestVisit)) {
						// This point will remain in the walk,
						// as it was visited even before current point.
						// Do not remove it. Do nothing.
					} else {
						// This point will not remain in the walk.
						// Remove it from the list of vertices too.
						_vertices.remove(p);
					}
				}
			}
		}
	}

	public int getTotalSteps() {
		return _walk.size();
	}

	public String toString() {
		return _walk.toString();
	}
}
