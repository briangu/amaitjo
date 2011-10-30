package ravi.contest.ants.test.message;


import junit.framework.TestCase;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.Location;
import ravi.contest.ants.message.FoodMessage;
import ravi.contest.ants.message.FoodMessage.FoodQuantity;
import ravi.contest.ants.message.MessageType;


public class TestFoodMessage extends TestCase {
	public void testCreation() {
		// Set location.
		Location loc = new Location();
		loc.add(Direction.north, 5);
		loc.add(Direction.east, 15);
		
		// Set world time.
		int time = 52981;
		
		// Set food quantity.
		int quantity = 15;
		
		// Construct serialized food message.
		long message = MessageType.FOOD.serialize();
		message = message | (FoodQuantity.normalize(quantity) << 3);
		message = message | (loc.serialize() << 6);
		message = message | (((long) time) << 26);
		
		FoodMessage fm = FoodMessage.deserialize(message);
		assertEquals(MessageType.FOOD, fm.getType());
		assertEquals(FoodQuantity.LEQ_SIXTEEN, fm.getQuantity());
		assertEquals(loc, fm.getLocation());
		assertEquals(time, fm.getTime());
		assertEquals(message, fm.serialize());
	}

	public void testUpdate() {
		// Set location.
		Location loc = new Location();
		loc.add(Direction.north, 5);
		loc.add(Direction.east, 15);
		
		// Set world time.
		int time = 52981;
		
		// Set food quantity.
		int quantity = 15;
		
		// Construct serialized food message.
		long message = MessageType.FOOD.serialize();
		message = message | (FoodQuantity.normalize(quantity) << 3);
		message = message | (loc.serialize() << 6);
		message = message | (((long) time) << 26);
		
		FoodMessage fm = FoodMessage.deserialize(message);
		assertEquals(time, fm.getTime());

		// Update location.
		Location loc2 = fm.getLocation();
		loc2.add(Direction.north, 1);
		fm.setLocation(loc2);
		
		// Update quantity.
		fm.setQuantity(8);
		
		// Update time.
		fm.setTime(time + 1);
		
		long message2 = fm.serialize();
		
		FoodMessage fm2 = FoodMessage.deserialize(message2);
		assertEquals(MessageType.FOOD, fm2.getType());
		assertEquals(FoodQuantity.LEQ_EIGHT, fm2.getQuantity());
		assertEquals(loc2, fm2.getLocation());
		assertEquals(time + 1, fm2.getTime());
	}
}
