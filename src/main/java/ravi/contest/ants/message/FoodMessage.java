package ravi.contest.ants.message;

import ravi.contest.ants.map.Location;

public class FoodMessage implements Message {
	// 3 bits for message type already allocated - bits 0-2.

	// 3 bits for food quantity - bits 3-5.
	public static final long FIELD_MASK_FOOD_QUANTITY = 0x38; // 0011.1000

	// 20 bits for location - bits 6-25.
	public static final long FIELD_MASK_FOOD_LOCATION = 0x03FFFFC0; // 0011.F.F.F.F.1100.0000

	// 17 bits for world time - bits 26-42.
	// However, eclipse doesn't allow for more than 32 bits for long.
	// The setting here is after down-shifting (left shifting) by 26 bits.
	public static final long FIELD_MASK_TIME = 0x1FFFF;
	
	public static enum FoodQuantity {
		ZERO(0),
		ONE(1),
		TWO(2),
		LEQ_FOUR(3),
		LEQ_EIGHT(4),
		LEQ_SIXTEEN(5),
		LEQ_THIRTY_TWO(6),
		GT_THIRTY_TWO(7);

		int _number;
		private FoodQuantity(int n) {
			_number = n;
		}
		
		public int serialize() {
			return _number;
		}

		public static FoodQuantity deserialize(int n) {
			switch(n) {
				case 0: return ZERO;
				case 1: return ONE;
				case 2: return TWO;
				case 3: return LEQ_FOUR;
				case 4: return LEQ_EIGHT;
				case 5: return LEQ_SIXTEEN;
				case 6: return LEQ_THIRTY_TWO;
				case 7: return GT_THIRTY_TWO;
				default: return ZERO;
			}
		}
		
		public static int normalize(int n) {
			if (n <= 0) {
				return 0;
			} else if (n == 1) {
				return 1;
			} else if (n == 2) {
				return 2;
			} else if (n <= 4) {
				return 3;
			} else if (n <= 8) {
				return 4;
			} else if (n <= 16) {
				return 5;
			} else if (n <= 32) {
				return 6;
			} else {
				return 7;
			}
		}
	}
	
	private Location _location;
	private FoodQuantity _quantity;
	private int _time;
	
	public static FoodMessage deserialize(long number) {
		FoodMessage fm = new FoodMessage();
		fm._quantity = FoodQuantity.deserialize((int) ((number & FIELD_MASK_FOOD_QUANTITY) >> 3));
		fm._location = Location.deserialize((int) (number & FIELD_MASK_FOOD_LOCATION) >> 6);
		fm._time = (int) ((number >> 26) & FIELD_MASK_TIME);
		return fm;
	}
	
	@Override
	public MessageType getType() {
		return MessageType.FOOD;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}
	
	public FoodQuantity getQuantity() {
		return _quantity;
	}
	
	public void setQuantity(int quantity) {
		_quantity = FoodQuantity.deserialize(FoodQuantity.normalize(quantity));
	}
	
	public int getTime() {
		return _time;
	}
	
	public void setTime(int time) {
		_time = time;
	}
	
	public long serialize() {
		long number = MessageType.FOOD.serialize();
		number = number | (_quantity.serialize() << 3);
		number = number | (_location.serialize() << 6);
		number = number | (((long) _time) << 26);
		return number;
	}
}
