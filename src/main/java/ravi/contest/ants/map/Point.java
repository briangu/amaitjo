package ravi.contest.ants.map;

import org.linkedin.contest.ants.api.Direction;

public class Point {
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

	public int x;
	public int y;

  public final static Point ORIGIN = new Point(0,0);

	public Point() {};
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	private Point(int num) {
		// Get X co-ordinate and its sign.
		x = num & MASK_X;
		x = ((num & MASK_X_SIGN) > 0 ? -1 : 1) * x;
		// Get Y co-ordinate and its sign.
		y = (num & MASK_Y) >>  10;
		y = ((num & MASK_Y_SIGN) > 0 ? -1 : 1) * y;
	}
	
	public void move(Direction d) {
		Direction2 dir = Direction2.get(d);
		x += dir.x;
		y += dir.y;
	}
	
	// Gets the direction of p relative to myself.
	public Direction getDirectionTo(Point p) {
		return Direction2.get(p.x - x, p.y - y).direction;
	}
	
	@Override
	public boolean equals(Object p) {
		if (p instanceof Point) {
			Point pt = (Point) p;
			return (x == pt.x) && (y == pt.y);
		} else {
			return false;
		}
	}
	
	public int serialize() {
		int bits = 0;
		// Add Y coordinate sign.
		bits = (y > 0 ? 0 : 1);
		// Make space for Y coordinate and push it in.
		bits = bits << 9;
		bits = bits | Math.abs(y);
		// Make space for X sign and push it in.
		bits = bits << 1;
		bits = bits | (x > 0 ? 0 : 1);
		// Make space for X coordinate and push it in.
		bits = bits << 9;
		bits = bits | Math.abs(x);
		
		return bits;
	}
	
	public static Point deserialize(int bits) {
		return new Point(bits);
	}
	
	public int hashCode() {
		//return (x << 16) + y;
//		int hashCode = x*1000 + y;
//		return hashCode;
		return serialize();
	}
	
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
