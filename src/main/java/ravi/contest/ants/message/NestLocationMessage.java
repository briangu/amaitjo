package ravi.contest.ants.message;

import ravi.contest.ants.map.Location;

public class NestLocationMessage implements Message {
	// 3 bits for message type already allocated - bits 0-2.

	// 20 bits for location - bits 3-22.
	public static final long FIELD_MASK_NEST_LOCATION = 0x7FFFF8; // 7.F:F.F:F.1000

	private Location _location;

	public NestLocationMessage(int x, int y) {
		_location = new Location(x, y);
	}
	
	private NestLocationMessage(Location location) {
		_location = location;
	}
	
	@Override
	public MessageType getType() {
		return MessageType.NEST_LOCATION;
	}

	@Override
	public long serialize() {
		long number = MessageType.FOOD.serialize();
		number = number | (_location.serialize() << 3);
		return number;
	}

	public static NestLocationMessage deserialize(long number) {
		return new NestLocationMessage(Location.deserialize((int) ((number & FIELD_MASK_NEST_LOCATION) >> 3)));
	}
	
	public boolean equals(Object o) {
		if (o instanceof NestLocationMessage) {
			return _location.equals(((NestLocationMessage) o)._location);
		}
		return false;
	}
}
