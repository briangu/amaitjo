package ravi.contest.ants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Ant;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Move;
import org.linkedin.contest.ants.api.WorldEvent;

import ravi.contest.ants.message.MessageManager;
import ravi.contest.ants.state.BoundaryReportStateHandler;
import ravi.contest.ants.state.ForageStateHandler;
import ravi.contest.ants.state.OracleAwaitBoundaryReportStateHandler;
import ravi.contest.ants.state.OracleStateHandler;
import ravi.contest.ants.state.StartStateHandler;
import ravi.contest.ants.state.State;
import ravi.contest.ants.state.StateHandler;
import ravi.contest.ants.state.TerritorySearchStateHandler;
import ravi.contest.ants.state.WaitForBoundaryReportStateHandler;

public class RaviAnt implements Ant {
	// Knowledge of the world accumulated by the ant so far.
	private Knowledge _knowledge;
	
	// Current state of the state machine in the brain.
	private State _state;
	
	// Registered handlers, to handle state of the state machine.
	private Map<State, StateHandler> _stateHandlerMap;
	
	// Messages are handled by the message manager.
	private MessageManager _messageManager;
	
	@Override
	public void init() {
		_messageManager = new MessageManager();

		_knowledge = new Knowledge();
		
		// Initialize state.
		_state = State.START;
		
		// Initialize state handlers.
		// (1) Initialize START state handler.
		_stateHandlerMap = new HashMap<State, StateHandler>();
		StartStateHandler startStateHandler = new StartStateHandler(_knowledge);
		_stateHandlerMap.put(State.START, startStateHandler);
		
		// (2) Initialize TERRITORY_SEARCH state handler.
		TerritorySearchStateHandler territorySearchStateHandler = new TerritorySearchStateHandler();
		territorySearchStateHandler.setKnowledge(_knowledge);
		_stateHandlerMap.put(State.TERRITORY_SEARCH, territorySearchStateHandler);
		
		// (3) Initialize BOUNDARY_REPORT state handler.
		BoundaryReportStateHandler boundaryReportStateHandler = new BoundaryReportStateHandler();
		boundaryReportStateHandler.setKnowledge(_knowledge);
		_stateHandlerMap.put(State.BOUNDARY_REPORT, boundaryReportStateHandler);
		
		// (4) Initialize WAIT_FOR_BOUNDARY_REPORT state handler.
		WaitForBoundaryReportStateHandler wfbrStateHandler = new WaitForBoundaryReportStateHandler();
		_stateHandlerMap.put(State.WAIT_FOR_FULL_BOUNDARY_REPORT, wfbrStateHandler);
		
		// (5) Initialize FORAGE state handler.
		ForageStateHandler forageStateHandler = new ForageStateHandler();
		_stateHandlerMap.put(State.FORAGE, forageStateHandler);
		
		// (6) Initialize ORACLE_AWAIT_BOUNDARY_REPORTS state handler.
		OracleAwaitBoundaryReportStateHandler oabrStateHandler = new OracleAwaitBoundaryReportStateHandler();
		oabrStateHandler.setKnowledge(_knowledge);
		oabrStateHandler.setMessageManager(_messageManager);
		_stateHandlerMap.put(State.ORACLE_AWAIT_BOUNDARY_REPORTS, oabrStateHandler);
		
		// (7) Initialize ORACLE state handler.
		OracleStateHandler oracleStateHandler = new OracleStateHandler();
		oracleStateHandler.setKnowledge(_knowledge);
		oracleStateHandler.setMessageManager(_messageManager);
		_stateHandlerMap.put(State.ORACLE, oracleStateHandler);
	}

	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		// (1) Increment time.
		_knowledge.incrementTime();
		
		// (2) Learn my environment at the current location.
		_knowledge.add(environment);
		
		// (3) Now decide on the plan of action.
		StateHandler stateHandler = _stateHandlerMap.get(_state);
		Action action = stateHandler.act(environment, events);
		_state = stateHandler.getNextState();
		System.out.println("Ant[" + _knowledge.getId() + "] @time=" + _knowledge.getTime() + ", @pos=" + _knowledge.getPosition() + ", state=" +  _state);

		// (4) If the current state handler decided to not handle the event, go to the next state.
		if (action == null) {
			// Go to the next state.
			stateHandler = _stateHandlerMap.get(_state);
			action = stateHandler.act(environment, events);
			_state = stateHandler.getNextState();
		}

		// (5) Update my understanding of my position relative to my nest.
		if (action instanceof Move) {
			Move move = (Move) action;
			_knowledge.updatePosition(move.getDirection());
		}
		
		// (6) We are done for this turn!
		return action;
	}

	@Override
	public Action onDeath(WorldEvent cause) {
		// TODO Auto-generated method stub
		return null;
	}
}
