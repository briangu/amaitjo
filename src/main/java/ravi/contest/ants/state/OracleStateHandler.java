package ravi.contest.ants.state;

import java.util.List;

import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Pass;
import org.linkedin.contest.ants.api.WorldEvent;

import ravi.contest.ants.Knowledge;
import ravi.contest.ants.message.MessageManager;
import ravi.contest.ants.message.MySay;
import ravi.contest.ants.message.TopologySay;
import ravi.contest.ants.oracle.OracleUtils;

public class OracleStateHandler implements StateHandler {

	private Knowledge _knowledge;
	private MessageManager _messageManager;
	
	public void setKnowledge(Knowledge  knowledge) {
		_knowledge = knowledge;
	}
	
	public void setMessageManager(MessageManager messageManager) {
		_messageManager = messageManager;
	}
	
	@Override
	public Action act(Environment environment, List<WorldEvent> events) {
		// An Oracle can only be contacted via SAYs.
		if (events != null) {
			// Someone wishes to contact me.
			System.out.println("OracleStateHandler.act()");
			for (WorldEvent event : events) {
				MySay say = _messageManager.deserialize(event.getEvent());
				if (say != null) {
					// I understand this message.
					switch(say.getType()) {
					case TOPOLOGY:
						TopologySay topologySay = (TopologySay) say;
						OracleUtils.updateTopology(_knowledge, topologySay);
						break;
					}
				}
			}
		}

		// Oracle needs to do nothing.
		return new Pass();
	}
	
	@Override
	public State getNextState() {
		// Once an oracle, always an oracle.
		return State.ORACLE;
	}

}
