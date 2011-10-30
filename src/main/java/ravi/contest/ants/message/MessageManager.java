package ravi.contest.ants.message;

public class MessageManager {
	public Message deserialize(long message) {
		MessageType messageType = MessageType.deserialize((int)(message & Message.FIELD_MASK_MESSAGE_TYPE));
		if (messageType != null) {
			switch(messageType) {
				case FOOD: return FoodMessage.deserialize(message);
				case NUMBERING: return NumberingMessage.deserialize(message);
				case NEST_LOCATION: return NestLocationMessage.deserialize(message);
			}
		}
		return null;
	}
	
	public long serialize(Message message) {
		return message.serialize();
	}
	
	public MySay deserialize(String message) {
		if (message != null) {
			if (message.startsWith(SayType.BOUNDARY_REPORT.name())) {
				return BoundaryReportSay.deserialize(message);
			} else if (message.startsWith(SayType.TOPOLOGY.name())) {
				// Topology for individual locations.
				return TopologySay.deserialize(message);
			} else if (message.startsWith(SayType.TOPDIR.name())) {
				// Topology with just directions relative to a starting point.
				return TopologySay2.deserialize(message);
			}
		}
		return null;
	}
	
	public String serialize(MySay say) {
		return say.serialize();
	}
}
