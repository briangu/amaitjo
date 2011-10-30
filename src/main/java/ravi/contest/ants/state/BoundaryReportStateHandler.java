package ravi.contest.ants.state;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Move;
import org.linkedin.contest.ants.api.Say;
import org.linkedin.contest.ants.api.WorldEvent;

import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.Direction2;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.message.BoundaryReportSay;
import ravi.contest.ants.message.TopologySay;

public class BoundaryReportStateHandler implements StateHandler {

	// True if the boundary report was delivered at the nest, false otherwise.
	private boolean _boundaryReportDelivered = false;

	// True if topology information was delivered at the nest, false otherwise.
	private boolean _topologyReportDelivered = false;
	
	// Knowledge of our surroundings.
	private Knowledge _knowledge;
	
	// Iterator over the topology report.
	// Used in case there is more data than can fit in one message.
	private Iterator<Point> _topologyReportIterator;
	
	public void setKnowledge(Knowledge knowledge) {
		_knowledge = knowledge;
	}
	
	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		Point position = _knowledge.getPosition();
		// Are we at our nest yet?
		if ((position.x == 0) && (position.y == 0)) { 
			// We have reached our nest.
			if (!_boundaryReportDelivered) {
				// Communicate boundary information.
 				BoundaryReportSay say = createBoundaryReportSay();
				System.out.println("Ant[" + _knowledge.getId() + "] @time=" + + _knowledge.getTime() + ", Delivering boundary report " + say);
				_boundaryReportDelivered = true;
				return new Say(say.serialize(), Direction.here);
			} else if (!_topologyReportDelivered) {
				// Construct topology report.
				if (_topologyReportIterator == null) {
					TreeSet<Point> topologyReportToDeliver = _knowledge.getMap().getSquares();
					_topologyReportIterator = topologyReportToDeliver.iterator();
				}
				TopologySay say = new TopologySay();
				for (int i=0; (i<TopologySay.MAX_POINTS_IN_SERIALIZED_LENGTH_OF_255) && _topologyReportIterator.hasNext(); i++) {
					Point p = _topologyReportIterator.next();
					say.add(p, _knowledge.getMap().getSquareType(p.x, p.y));
				}
				System.out.println("Ant[" + _knowledge.getId() + "] @time=" + + _knowledge.getTime() + ", Delivering topology report " + say);
				_topologyReportDelivered = !_topologyReportIterator.hasNext();
				return new Say(say.serialize(), Direction.here);
			} else {
				// We are done with this phase.
				return null;
			}
		} else {
			// We are not yet at the nest.
			// TODO: determine the fastest way home as an init step.
			
			// Determine the position just previous to the current one,
			// when we visited the current position for the first time.
			// This short circuits the walk back to the nest.
			Walk2 walkToNest = _knowledge.getWalkBackToNest();
			int stepNumber = walkToNest.getEarliestVisit(position);
			// stepNumber will be greater than 1, since we are not yet
			// at the nest and nest location is included in the walk.
			Point prevPoint = walkToNest.getPoint(stepNumber - 1);
			// Go to this point.
			Direction2 dir = Direction2.get(prevPoint.x - position.x, prevPoint.y - position.y);
			// TODO: assess risk.
			return new Move(dir.direction);
		}
	}

	@Override
	public State getNextState() {
		if (_topologyReportDelivered) {
			reset();
			return State.WAIT_FOR_FULL_BOUNDARY_REPORT;
		} else {
			return State.BOUNDARY_REPORT;
		}
	}
	
	private BoundaryReportSay createBoundaryReportSay() {
		BoundaryReportSay say = new BoundaryReportSay();
		say.id = _knowledge.getId();
		// Determine the direction best aligned with where we wanted to go.
		// We theorize that if you aligned with:
		// NORTH => you are best qualified to talk about TOP boundary.
		// EAST => RIGHT boundary
		// NORTHEAST => TOP and RIGHT
		// SOUTHWEST => BOTTOM and LEFT
		// .. and so on.
		Point territoryCenter = _knowledge.getTerritoryCenter();
		LocalMap localMap = _knowledge.getMap();
		Direction2 bestDirection = MapUtils.getBestDirection(0, 0, territoryCenter.x, territoryCenter.y);
		switch (bestDirection) {
		case north:
			// TODO: make sure that the location we are returning is an OBSTACLE.
			say.y = localMap.getTopMargin();
			break;
		case south:
			say.y = localMap.getBottomMargin();
			break;
		case east:
			say.x = localMap.getRightMargin();
			break;
		case west:
			say.x = localMap.getLeftMargin();
			break;
		case northeast:
			say.x = localMap.getRightMargin();
			say.y = localMap.getTopMargin();
			break;
		case northwest:
			say.x = localMap.getLeftMargin();
			say.y = localMap.getTopMargin();
			break;
		case southeast:
			say.x = localMap.getRightMargin();
			say.y = localMap.getBottomMargin();
			break;
		case southwest:
			say.x = localMap.getLeftMargin();
			say.y = localMap.getBottomMargin();
			break;
		}
		return say;
	}
	
	private void reset() {
		_boundaryReportDelivered = false;
		_topologyReportDelivered = false;
	}
}
