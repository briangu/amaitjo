package ravi.contest.ants.message;

import java.util.HashMap;
import java.util.Map;

import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class TopologySay implements MySay {
	// We can accommodate 60 points in a message of length 255 when serialized.
	public static final int MAX_POINTS_IN_SERIALIZED_LENGTH_OF_255 = 60;
	
	private Map<Point, SquareType> _topology;

	public TopologySay() {
		_topology = new HashMap<Point, LocalMap.SquareType>();
	}
	
	public void add(Point p, SquareType type) {
		_topology.put(p, type);
	}
	
	@Override
	public SayType getType() {
		return SayType.TOPOLOGY;
	}
	
	public Map<Point, SquareType> getTopology() {
		return _topology;
	}

	@Override
	public String serialize() {
		// 3 bytes for each {location, square type}.
		byte[] bytes = new byte[_topology.size() * 3];
		int byteIndex = 0;
		for (Map.Entry<Point, SquareType> entry : _topology.entrySet()) {
			int serialized = serialize(entry);
			// Chop into 3 bytes, little endian style.
			bytes[byteIndex++] = (byte) (serialized & 0xFF);
			bytes[byteIndex++] = (byte) ((serialized & 0xFF00) >> 8);
			bytes[byteIndex++] = (byte) ((serialized & 0xFF0000) >> 16);
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return "TOPOLOGY" + encoder.encode(bytes);
	}
	
	public static TopologySay deserialize(String str) {
		byte[] bytes = null;

		// Parse serialized representation.
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			bytes = decoder.decodeBuffer(str.substring(SayType.TOPOLOGY.name().length()));
		} catch (Exception e) {
			System.err.println("Bad " + SayType.TOPOLOGY.name() + " message: " + str);
		}
		
		// Deserialize and compose object.
		TopologySay say = null;
		if (bytes != null) {
			say = new TopologySay();
			for (int i=0; i<bytes.length; i+=3) {
				int serialized = (bytes[i] & 0xFF)
						| ((bytes[i + 1] & 0xFF) << 8)
						| ((bytes[i + 2] & 0xFF) << 16);
				int serializedSquareType = serialized & 0x3;
				int serializedPoint = serialized >> 2;
				Point p = Point.deserialize(serializedPoint);
				SquareType type = deserializeSquareType(serializedSquareType);
				say.add(p, type);
			}
		}

		// Return composed object.
		return say;
	}
	
	private static int serialize(Map.Entry<Point, SquareType> entry) {
		int serializedPoint = entry.getKey().serialize(); // 20 bits.
		int serializedType = serialize(entry.getValue()); // 2 bits.
		// We use 3 bytes (24 bits), with 2 bits to spare!
		return ((serializedPoint << 2) | serializedType);
	}
	
	// Returns 2 bits of serialized square type.
	private static byte serialize(SquareType type) {
		switch(type) {
			case EMPTY: return 0x0;
			case OBSTACLE: return 0x1;
			case HAS_FOOD: return 0x2;
			case UNKNOWN: return 0x3;
			default: return 0x3;
		}
	}
	
	private static SquareType deserializeSquareType(int bits) {
		switch(bits) {
			case 0: return SquareType.EMPTY;
			case 1: return SquareType.OBSTACLE;
			case 2: return SquareType.HAS_FOOD;
			case 3: return SquareType.UNKNOWN;
			default: return SquareType.UNKNOWN;
		}
	}
	
	public int size() {
		return _topology.size();
	}
	
	public String toString() {
		return "TopologySay[size=" + size() + "]";
	}
}
