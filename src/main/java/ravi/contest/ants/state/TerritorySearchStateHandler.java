package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Move;
import org.linkedin.contest.ants.api.WorldEvent;

import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.movement.MovementUtils;
import ravi.contest.ants.movement.NavigationAdvisor;

public class TerritorySearchStateHandler implements StateHandler {
	private Knowledge _knowledge;
	private NavigationAdvisor _navigationAdvisor = new NavigationAdvisor();

	private Point _territoryDestination;

	// True if we reached our territory destination.
	private boolean _reachedDestination;
	private int _territorySearchStartTime;

	// True if the handler thinks that it has reached the boundary.
	private boolean _reachedBoundary;
	
	private Walk2 _walkToTerritoryDestination;
	
	public TerritorySearchStateHandler() {
		reset();
	}
	
	public void setKnowledge(Knowledge knowledge) {
		_knowledge = knowledge;
	}
	
	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		// Determine the location we want to go to, if not already.
		if (_territoryDestination == null) {
			// This is the first time we are going towards our territory.
			// Initialize the destination we are headed towards - the center of our territory.
			_territoryDestination = _knowledge.getTerritoryCenter();
			// Initialize our start time.
			_territorySearchStartTime = _knowledge.getTime();
			// Mark that we haven't reached out destination yet.
			_reachedDestination = false;
			// Initialize our walk to the territory destination.
			_walkToTerritoryDestination = new Walk2();
		}
		
		// Where are we, relative to the nest?
		Point position = _knowledge.getPosition();

		// Mark that we have visited this position in our walk.
		_walkToTerritoryDestination.visit(position);

		// Are we at our territory destination?
		if (position.equals(_territoryDestination)) {
			// We are at our destination.
			// This finishes the phase of territory search.
			_reachedDestination = true;
			// We now transition to the next state - FORAGE.
			// Hence return null here to indicate transition to the next state.
			return null;
		} else {
			// We have not yet reached our destination.
			// Dynamically, get the estimated time to reach our destination.
			// Calculate the maximum time allowed to reach our territory from this estimate.
			int distance = MapUtils.getDistance(0 /*nest.x*/, 0/*nest.y*/, _territoryDestination.x, _territoryDestination.y);
			int maxAllowedWalkLength = MovementUtils.getMaximumAllowedWalkLengthForSearch(distance, _knowledge.getObstacleDensity());
			if (maxAllowedWalkLength < distance) {
				maxAllowedWalkLength = distance;
			}
			// How long have we been searching for our territory?
			// If it isn't too long, we will continue our search.
			if (_knowledge.getTime() - _territorySearchStartTime < maxAllowedWalkLength) {
				// It is within allowable limits. Continue searching.
				Direction dir = _navigationAdvisor.advise(_knowledge.getPosition(), _territoryDestination, _knowledge.getMap(), _walkToTerritoryDestination);
				// TODO: assess risk.
				return new Move(dir);
			} else {
				System.out.println("Ant[" + _knowledge.getId() + "] Done with territory search after " + maxAllowedWalkLength + " steps.");
				// We have reached out allowable limit.
				// Check if we are within our territory.
				Point bottomLeft = _knowledge.getTerritoryBottomLeftPoint();
				Point topRight = _knowledge.getTerritoryTopRightPoint();
				if ((bottomLeft.x <= position.x) && (position.x <= topRight.x) && (bottomLeft.y <= position.y) && (position.y <= topRight.y)) {
					// We are in our territory, just that we haven't been able to
					// find our destination within the territory. Go to the next state.
					_reachedDestination = true;
					return null;
				} else {
					// We are not in our territory.
					// We may have reached a boundary. Report back to nest
					// and let the Oracle deal with it.
					_reachedBoundary = true;
					// TODO: perhaps if we have indeed hit a boundary or not.
					// If we are discovering new squares, we haven't!
					return null;
				}
			}
		}
	}

	@Override
	public State getNextState() {
		if (_reachedDestination) {
			reset();
			return State.FORAGE;
		} else if (_reachedBoundary) {
			reset();
			return State.BOUNDARY_REPORT;
		} else {
			// Not reached our destination and we are to keep searching for our territory.
			return State.TERRITORY_SEARCH;
		}
	}

	public void reset() {
		_reachedDestination = false;
		_territoryDestination = null;
		_territorySearchStartTime = -1;
		_walkToTerritoryDestination = null;
		
		_reachedBoundary = false;
	}
}
