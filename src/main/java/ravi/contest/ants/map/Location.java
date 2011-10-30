package ravi.contest.ants.map;

import org.linkedin.contest.ants.api.Direction;

public class Location {
	// Number of bits occupied by this object.
	public static final int MASK_NUM_BITS = 20;
	
	// 9 bits for relative X co-ordinate.
	public static final int MASK_X = 0x01FF; // Mask = 0001.1111.1111
	
	// 1 bit for X's sign, 0 is positive, 1 is negative.
	public static final int MASK_X_SIGN = 0x0200; // Mask = 0010.0000.0000

	// 9 bits for relative Y co-ordinate.
	public static final int MASK_Y = 0x07FC00; // Mask = 0111.1111.1100.0000.0000

	// 1 bit for Y's sign, 0 is positive, 1 is negative.
	public static final int MASK_Y_SIGN = 0x080000; // Mask = 1000.0000.0000.0000.0000

	private int _x, _y;
	
	public Location() {
		_x = 0;
		_y = 0;
	}
	
	public Location(int x, int y) {
		_x = x;
		_y = y;
	}
	
	private Location(int num) {
		// Get X co-ordinate and its sign.
		_x = num & MASK_X;
		_x = ((num & MASK_X_SIGN) > 0 ? -1 : 1) * _x;
		// Get Y co-ordinate and its sign.
		_y = (num & MASK_Y) >>  10;
		_y = ((num & MASK_Y_SIGN) > 0 ? -1 : 1) * _y;
	}
	
	public void add(Direction dir, int dist) {
		switch(dir) {
			case north:
				_y += dist;
				break;
			case south:
				_y -= dist;
				break;
			case east:
				_x += dist;
				break;
			case west:
				_x -= dist;
				break;
			case northeast:
				_x += dist;
				_y += dist;
				break;
			case northwest:
				_x -= dist;
				_y += dist;
				break;
			case southeast:
				_x += dist;
				_y -= dist;
				break;
			case southwest:
				_x -= dist;
				_y -= dist;
				break;
			default:
				break;
		}
	}
	
	public int getX() {
		return _x;
 	}
	
	public int getY() {
		return _y;
	}
	
	public int serialize() {
		int bits = 0;
		// Add Y coordinate sign.
		bits = (_y > 0 ? 0 : 1);
		// Make space for Y coordinate and push it in.
		bits = bits << 9;
		bits = bits | Math.abs(_y);
		// Make space for X sign and push it in.
		bits = bits << 1;
		bits = bits | (_x > 0 ? 0 : 1);
		// Make space for X coordinate and push it in.
		bits = bits << 9;
		bits = bits | Math.abs(_x);
		
		return bits;
	}
	
	public static Location deserialize(int bits) {
		return new Location(bits);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Location) {
			Location other = (Location) o;
			return (_x == other._x) && (_y == other._y);
		}
		return false;
	}
	
	public int hashCode() {
		// toBits() returns a unique int
		// as long as numbers are between [-511, 511].
		return serialize();
	}
	
	public String toString() {
		return "Location[x=" + _x + ",y=" + _y + "]";
	}
}
