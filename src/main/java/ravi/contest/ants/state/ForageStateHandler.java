package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Pass;
import org.linkedin.contest.ants.api.WorldEvent;

public class ForageStateHandler implements StateHandler {

	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		return new Pass();
	}

	@Override
	public State getNextState() {
		return State.FORAGE;
	}

}
