package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Pass;
import org.linkedin.contest.ants.api.WorldEvent;
import org.linkedin.contest.ants.api.Write;

import ravi.contest.ants.Constants;
import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.message.BoundaryReportSay;
import ravi.contest.ants.message.MessageManager;
import ravi.contest.ants.message.MySay;
import ravi.contest.ants.message.NestLocationMessage;
import ravi.contest.ants.message.TopologySay;
import ravi.contest.ants.movement.MovementUtils;
import ravi.contest.ants.oracle.OracleUtils;

public class OracleAwaitBoundaryReportStateHandler implements StateHandler {
	private Knowledge _knowledge;
	private MessageManager _messageManager;
	
	// Maximum amount of time the Oracle waits, before deciding on where the nest is located.
	private int _maxWaitTimeForNestLocationDecision = -1;
	
	private int _boundaryCandidateTop = -Constants.BOARD_SIZE;
	private int _boundaryCandidateBottom = Constants.BOARD_SIZE;
	private int _boundaryCandidateLeft = Constants.BOARD_SIZE;
	private int _boundaryCandidateRight = -Constants.BOARD_SIZE;
	
	private int _numVotesForBoundaryTop = 0;
	private int _numVotesForBoundaryBottom = 0;
	private int _numVotesForBoundaryLeft = 0;
	private int _numVotesForBoundaryRight = 0;
	
	// Have I published my nest's absolute location?
	// Once I have published it, I am done with this phase.
	private boolean _nestLocationPublished = false;
	
	public void setKnowledge(Knowledge  knowledge) {
		_knowledge = knowledge;
	}
	
	public void setMessageManager(MessageManager messageManager) {
		_messageManager = messageManager;
	}
	
	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		// Am I initialized?
		if (_maxWaitTimeForNestLocationDecision < 0) {
			// I am not initialized. Initialize.
			// Make reasonable estimates about obstacle density,
			// since I, the Oracle, cannot determine that itself.
			// Approx. 680 time units with the below parameters.
			_maxWaitTimeForNestLocationDecision = 2 * MovementUtils.getMaximumAllowedWalkLengthForSearch(Constants.BOARD_SIZE/3, 0.4);
			System.out.println("Ant[" + _knowledge.getId() + "] Max wait time for nest location: " + _maxWaitTimeForNestLocationDecision);
		}
		
		// Can we make a boundary decision at this time?
		boolean haveEnoughDataToDecide = false;
		// If the votes are skewed in one side's favor, for both horizontal and
		// vertical boundaries, then we can make a decision now.
		if ((((_numVotesForBoundaryLeft >= 5) && (_numVotesForBoundaryRight == 0)) || ((_numVotesForBoundaryRight >= 5) && (_numVotesForBoundaryLeft == 0)))
				&& (((_numVotesForBoundaryTop >= 5) && (_numVotesForBoundaryBottom == 0)) || ((_numVotesForBoundaryBottom >= 5) && (_numVotesForBoundaryTop == 0)))) {
			haveEnoughDataToDecide = true;
		}
		
		// Do we have to make a boundary decision at this time?
		if (haveEnoughDataToDecide || (_knowledge.getTime() > _maxWaitTimeForNestLocationDecision)) {
			// It is time to decide...
			// We choose by the most number of votes for the boundaries.
			int boundaryX = (_numVotesForBoundaryLeft > _numVotesForBoundaryRight) ? _boundaryCandidateLeft : _boundaryCandidateRight;
			int boundaryY = (_numVotesForBoundaryTop > _numVotesForBoundaryBottom) ? _boundaryCandidateTop : _boundaryCandidateBottom;

			System.out.println("Ant[" + _knowledge.getId() + "] Local map has " + _knowledge.getMap().size() + " points.");

			// Calculate nest location from boundary information.
			LocalMap map = _knowledge.getMap();
			int nestLocationX = 0;
			int nestLocationY = 0;
			if (boundaryX > 0) {
				nestLocationX = Constants.BOARD_SIZE/2 - map.getRightMargin();
			} else {
				nestLocationX = -Constants.BOARD_SIZE/2 + map.getLeftMargin();
			}
			if (boundaryY > 0) {
				nestLocationY = Constants.BOARD_SIZE/2 - map.getTopMargin();
			} else {
				nestLocationY = -Constants.BOARD_SIZE/2 + map.getBottomMargin();
			}
			NestLocationMessage message = new NestLocationMessage(nestLocationX, nestLocationY);

			// Update knowledge with nest location.
			_knowledge.setNestAbsolute(new Point(nestLocationX, nestLocationY));
			System.out.println("Ant[" + _knowledge.getId() + "] Setting nest location as " + _knowledge.getNestAbsolute());

			// Publish nest location.
			_nestLocationPublished = true;
			return new Write(message.serialize());
		} else {
			// Continue polling for BOUNDARY_REPORT SAYs.
			// An Oracle can only be contacted via SAYs.
			if (events != null) {
				// Someone wishes to contact me.
				for (WorldEvent event : events) {
					System.out.println("Ant[" + _knowledge.getId() + "] Got WorldEvent.");
					MySay say = _messageManager.deserialize(event.getEvent());
					if (say != null) {
						// I understand this message.
						switch(say.getType()) {
						case BOUNDARY_REPORT:
							BoundaryReportSay boundaryReportSay = (BoundaryReportSay) say;
							handleBoundaryReportSay(boundaryReportSay);
							break;
						case TOPOLOGY:
							TopologySay topologySay = (TopologySay) say;
							OracleUtils.updateTopology(_knowledge, topologySay);
							break;
						}
					}
				}
			}
			
			// Oracle needs to keep waiting for boundary reports.
			return new Pass();
		}
	}

	@Override
	public State getNextState() {
		if (_nestLocationPublished) {
			// We are done with publishing nest location.
			// Onto Oracle state now.
			return State.ORACLE;
		} else {
			// Nest location not yet published.
			// Continue awaiting boundary reports.
			return State.ORACLE_AWAIT_BOUNDARY_REPORTS;
		}
	}

	private void handleBoundaryReportSay(BoundaryReportSay say) {
		int x = say.x;
		int y = say.y;

		if (x > 0) {
			// Count this as a vote for this boundary.
			_numVotesForBoundaryRight++;
			if (x > _boundaryCandidateRight) {
				// Better than our current estimate of right boundary.
				_boundaryCandidateRight = x;
			}
		} else if (x < 0) {
			// Count this as a vote for this boundary.
			_numVotesForBoundaryLeft++;
			if (x < _boundaryCandidateLeft) {
				// Better than our current estimate of left boundary.
				_boundaryCandidateLeft = x;
			}
		}
		
		if (y > 0) {
			// Count this as a vote for this boundary.
			_numVotesForBoundaryTop++;
			if (y > _boundaryCandidateTop) {
				// Better than our current estimate of top boundary.
				_boundaryCandidateTop = y;
			}
		} else if (y < 0) {
			// Count this as a vote for this boundary.
			_numVotesForBoundaryBottom++;
			if (y < _boundaryCandidateBottom) {
				// Better than our current estimate of bottom boundary.
				_boundaryCandidateBottom = y;
			}
		}
	}
}
