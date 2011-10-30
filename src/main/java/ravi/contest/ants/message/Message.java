package ravi.contest.ants.message;

public interface Message {
	public static final long FIELD_MASK_MESSAGE_TYPE = 0x7; // first 3 bits.
	public MessageType getType();
	public long serialize();
}
