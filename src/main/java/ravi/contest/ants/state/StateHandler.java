package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.WorldEvent;

public interface StateHandler {
	public Action act(Environment environment, List<WorldEvent> events);
	public State getNextState();
}
