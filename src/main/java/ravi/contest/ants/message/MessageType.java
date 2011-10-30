package ravi.contest.ants.message;

public enum MessageType {
	FOOD(1),
	TOPOLOGY(2),
	NUMBERING(3),
	NEST_LOCATION(4),
	// Can only have 3 bits of message type
	// So, a max of 0-7 message types.
	;
	MessageType(int num) {
		_number = num;
	}
	private int _number;
	public int serialize() {
		return _number;
	}
	
	public static MessageType deserialize(int number) {
		switch (number) {
			case 1: return FOOD;
			case 2: return TOPOLOGY;
			case 3: return NUMBERING;
			case 4: return NEST_LOCATION;
			default: return null;
		}
	}
}
