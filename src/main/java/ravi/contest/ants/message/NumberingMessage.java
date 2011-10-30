package ravi.contest.ants.message;

public class NumberingMessage implements Message {

	private int _number;
	
	@Override
	public MessageType getType() {
		return MessageType.NUMBERING;
	}
	
	private NumberingMessage() {}
	
	public NumberingMessage(int number) {
		setNumber(number);
	}

	public static NumberingMessage deserialize(long bits) {
		NumberingMessage message = new NumberingMessage();
		message.setNumber((int) (bits >> 3)); // Remove 3 bits of message type.
		return message;
	}
	
	@Override
	public long serialize() {
		return MessageType.NUMBERING.serialize() | (_number << 3);
	}

	public void setNumber(int n) {
		_number = n;
	}
	
	public int getNumber() {
		return _number;
	}
}
