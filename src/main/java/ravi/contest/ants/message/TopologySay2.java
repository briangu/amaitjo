package ravi.contest.ants.message;

import java.util.ArrayList;
import java.util.List;

import org.linkedin.contest.ants.api.Direction;

import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.Point;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Message format:
 * <ol>
 *   <li>"TOPDIR"
 *   <li>3-bytes-for-starting-point
 *   <li>2-bytes-for-number-of-directions-included
 *   <li>3-bits-for-direction
 *   <li>1-bit-for-square-type
 * <ol>
 * Direction and square type information is repeated multiple times (upto a limit).
 */
public class TopologySay2 implements MySay {
	/**
	 * Maximum number of directions that can be included in a serialized message
	 * with a limit of 255 characters.
	 */
	public static final int MAX_DIRECTIONS = 356; // by experimentation. See TestTopologySay2.java for a test method.

	private Point _start; // Starting point for the directions list.
	private List<Direction> _directionList = new ArrayList<Direction>(); // list of directions relative to the starting point.
	private List<LocalMap.SquareType> _squareTypeList = new ArrayList<LocalMap.SquareType>(); // type of square (aligned with direction)
	
	public TopologySay2(Point p) {
		_start = p;
	}
	
	public void add(Direction dir, LocalMap.SquareType type) {
		_directionList.add(dir);
		_squareTypeList.add(type);
	}
	
	public int size() {
		return _directionList.size();
	}
	
	public Direction getDirection(int i) {
		return _directionList.get(i);
	}
	
	public LocalMap.SquareType getSquareType(int i) {
		return _squareTypeList.get(i);
	}
	
	public String serialize() {
		// Allocate byte storage for our serialization.
		int numBits = _directionList.size() * 4; // 3 bits for direction + 1 for EMPTY v/s OBSTACLE = 4 bits per direction.
		int numBytes = (numBits + 4)/8 + 3 /*3 bytes for starting point*/+ 2 /*2 bytes for number of directions included*/;
		byte[] bytes = new byte[numBytes];
		
		// Serialize directions.
		for (int i=0; i<_directionList.size(); i++) {
			// Construct bit data.
			byte dirBits = serialize(_directionList.get(i));
			byte squareTypeBits = (byte) (_squareTypeList.get(i).equals(LocalMap.SquareType.OBSTACLE) ? 1 : 0);
			byte bits = (byte) ((dirBits << 1) | (squareTypeBits & 0x1) & 0xF);
			// Determine position where this data goes.
			int indexIntoByteArray = i/2;
			int numLeftShift = 4 * (i%2);
			// Insert data at determined position.
			if (numLeftShift == 0) {
				bytes[indexIntoByteArray] = bits;
			} else {
				bytes[indexIntoByteArray] |= (bits << numLeftShift);
			}
		}
		
		// Serialize starting point information.
		int serializedPoint = _start.serialize();

		// Insert serialized data into byte array.
		bytes[numBytes-3] = (byte) (serializedPoint & 0xFF);
		bytes[numBytes-2] = (byte) ((serializedPoint & 0xFF00) >> 8);
		bytes[numBytes-1] = (byte) ((serializedPoint & 0xFF0000) >> 16);

		// Insert the number of directions included in this message.
		bytes[numBytes-5] = (byte) (_directionList.size() & 0xFF);
		bytes[numBytes-4] = (byte) ((_directionList.size() & 0xFF00) >> 8);

		// Encode message.
		BASE64Encoder encoder = new BASE64Encoder();
		return SayType.TOPDIR.name() + encoder.encode(bytes);
	}
	
	public static TopologySay2 deserialize(String s) {
		BASE64Decoder decoder = new BASE64Decoder();
		TopologySay2 say = null;
		try {
			byte[] bytes = decoder.decodeBuffer(s.substring(SayType.TOPDIR.name().length()));
			
			int numBytes = bytes.length;

			// First 3 bytes form the starting point.
			int serializedPoint = (bytes[numBytes - 1] & 0xFF);
			serializedPoint = (serializedPoint << 8) | (bytes[numBytes - 2] & 0xFF);
			serializedPoint = (serializedPoint << 8) | (bytes[numBytes - 3] & 0xFF);
			Point startingPoint = Point.deserialize(serializedPoint);

			// Create our topology object.
			say = new TopologySay2(startingPoint);

			// Next 2 bytes form the number of directions included in this message.
			int numDirections = (bytes[numBytes - 4] & 0xFF);
			numDirections = (numDirections << 8) | (bytes[numBytes - 5] & 0xFF);

			// Iterate over our bytes and get direction/square-type information.
			for (int i=0; i<numDirections; i++) {
				int index = i/2;
				int numRightShift = 4 * (i%2);
				byte b = bytes[index];
				if (numRightShift > 0) {
					b >>= numRightShift;
				}
				SquareType squareType = (b & 0x1) == 1 ? SquareType.OBSTACLE : SquareType.EMPTY;
				int serializedDir = (b & 0xE) >> 1;
				Direction dir = deserializeDir(serializedDir);
				say.add(dir, squareType);
			}
		} catch (Exception e) {
			System.err.println("Could not decode " + SayType.TOPDIR.name() + " message. " + s);
		}

		// Return our deserialized object.
		return say;
	}
	
	private static byte serialize(Direction dir) {
		switch (dir) {
		case north: return 0x0;
		case northeast : return 0x1;
		case east : return 0x2;
		case southeast : return 0x3;
		case south : return 0x4;
		case southwest : return 0x5;
		case west : return 0x6;
		case northwest : return 0x7;
		default: return 0x0; // arbitrary.
		}
	}
	
	private static Direction deserializeDir(int b) {
		switch (b) {
		case 0: return Direction.north;
		case 1: return Direction.northeast;
		case 2: return Direction.east;
		case 3: return Direction.southeast;
		case 4: return Direction.south;
		case 5: return Direction.southwest;
		case 6: return Direction.west;
		case 7: return Direction.northwest;
		default: return Direction.north; // arbitrary
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TopologySay2[");
		sb.append("start=");
		sb.append(_start);
		sb.append(",topology={");
		for (int i=0; i<_directionList.size(); i++) {
			Direction dir = _directionList.get(i);
			LocalMap.SquareType type = _squareTypeList.get(i);
			sb.append("(");
			sb.append(dir.name());
			sb.append(",");
			sb.append(type.name());
			sb.append(")");
		}
		sb.append("}");
		sb.append("]");
		return sb.toString();
	}

	@Override
	public SayType getType() {
		return SayType.TOPDIR;
	}
}
