package ravi.contest.ants.test.message;

import junit.framework.TestCase;
import ravi.contest.ants.message.NestLocationMessage;

public class TestNestLocationMessage extends TestCase {
	public void testCreation() {
		NestLocationMessage message = new NestLocationMessage(-45, 100);
		long serialized = message.serialize();
		NestLocationMessage message2 = NestLocationMessage.deserialize(serialized);
		assertEquals(message, message2);
		assertTrue(message.equals(message2));
	}
	
	public void testCreation2() {
		NestLocationMessage message = new NestLocationMessage(-45, -100);
		long serialized = message.serialize();
		NestLocationMessage message2 = NestLocationMessage.deserialize(serialized);
		assertEquals(message, message2);
		assertTrue(message.equals(message2));
	}

	public void testCreation3() {
		NestLocationMessage message = new NestLocationMessage(45, 100);
		long serialized = message.serialize();
		NestLocationMessage message2 = NestLocationMessage.deserialize(serialized);
		assertEquals(message, message2);
		assertTrue(message.equals(message2));
	}

	public void testCreation4() {
		int X = 45;
		int Y = 100;
		NestLocationMessage message = new NestLocationMessage(X, Y);
		long serialized = message.serialize();
		NestLocationMessage message2 = NestLocationMessage.deserialize(serialized);
		
		for (int i=-512; i<512; i++) {
			for (int j=-512; j<512; j++) {
				if ((i != X) && (j != Y)) {
					assertFalse(message2.equals(new NestLocationMessage(i, j)));
				}
			}
		}
	}
}
