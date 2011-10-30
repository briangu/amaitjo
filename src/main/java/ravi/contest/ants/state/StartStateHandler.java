package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Move;
import org.linkedin.contest.ants.api.Pass;
import org.linkedin.contest.ants.api.WorldEvent;

import ravi.contest.ants.Constants;
import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.Direction2;
import ravi.contest.ants.map.MapUtils;

public class StartStateHandler implements StateHandler {
	private Knowledge _knowledge;

	public StartStateHandler(Knowledge knowledge) {
		_knowledge = knowledge;
	}

	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		// (1) Initialize id.
		_knowledge.setId(Constants.NUM_ANTS - environment.getSquare(Direction.here).getNumberOfAnts());

		// (2) Move out of this square, otherwise other ants' ids WILL get messed up!
		if (StateUtils.isOracle(_knowledge.getId())) {
			// I am the Oracle. I am stationed at the nest.
			return new Pass();
		} else {
			// Determine the center of my territory and head that way.
			int x = (_knowledge.getTerritoryBottomLeftPoint().x + _knowledge.getTerritoryTopRightPoint().x)/2;
			int y = (_knowledge.getTerritoryBottomLeftPoint().y + _knowledge.getTerritoryTopRightPoint().y)/2;
			Direction2 bestDirection = MapUtils.getBestDirection(0, 0, x, y);
			if (bestDirection == Direction2.here) {
				// Cannot remain at the nest. It will mess up the numbering.
				bestDirection = Direction2.north;
			}
			return new Move(bestDirection.direction);
		}
	}

	@Override
	public State getNextState() {
		if (_knowledge.getId() == Constants.NUM_ANTS - 1) {
			return State.ORACLE_AWAIT_BOUNDARY_REPORTS;
		} else {
			return State.TERRITORY_SEARCH;
		}
	}
}
