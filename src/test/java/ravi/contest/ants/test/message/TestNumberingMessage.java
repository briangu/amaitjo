package ravi.contest.ants.test.message;

import junit.framework.TestCase;
import ravi.contest.ants.message.MessageType;
import ravi.contest.ants.message.NumberingMessage;


public class TestNumberingMessage extends TestCase {
	public void testCreation() {
		int number = 25;
		long bits = MessageType.NUMBERING.serialize() | (number << 3);
		NumberingMessage message = NumberingMessage.deserialize(bits);
		assertEquals(number, message.getNumber());
	}

	public void testUpdate() {
		int number = 25;
		long bits = MessageType.NUMBERING.serialize() | (number << 3);
		NumberingMessage message = NumberingMessage.deserialize(bits);
		message.setNumber(number + 1);
		
		long bits2 = message.serialize();
		NumberingMessage message2 = NumberingMessage.deserialize(bits2);
		assertEquals(number + 1, message2.getNumber());
	}
	
	public void testIteration() {
		NumberingMessage message = new NumberingMessage(0);
		for (int i=0; i<50; i++) {
			long bits = message.serialize();
			NumberingMessage message2 = NumberingMessage.deserialize(bits);
			assertEquals(i, message2.getNumber());
			message.setNumber(i + 1);
		}
	}
}
