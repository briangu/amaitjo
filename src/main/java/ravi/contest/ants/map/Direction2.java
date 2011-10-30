package ravi.contest.ants.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.linkedin.contest.ants.api.Direction;

// Note: "Direction"'s vectors are messed up!
// North is -1 and South is +1 in that model.
// Inconsistent with my model.
// Use Direction.deltaX and .deltaY with caution.
public enum Direction2 {
	north(0, 1, Direction.north),
	northeast(1, 1, Direction.northeast),
	east(1, 0, Direction.east),
	southeast(1, -1, Direction.southeast),
	south(0, -1, Direction.south),
	southwest(-1, -1, Direction.southwest),
	west(-1, 0, Direction.west),
	northwest(-1, 1, Direction.northwest),
	here(0, 0, Direction.here),
	;
	
	public final int x;
	public final int y;
	public final Direction direction;
	
	private Direction2(int deltaX, int deltaY, Direction dir) {
		x = deltaX;
		y = deltaY;
		direction = dir;
	}
	
	private static final List<Direction2> _directions = new ArrayList<Direction2>(Arrays.asList(Direction2.values()));

	public static List<Direction2> getDirections() {
		return _directions;
	}
	
	public static Direction2 get(Direction dir) {
		switch(dir) {
		case north: return north;
		case northeast: return northeast;
		case east: return east;
		case southeast: return southeast;
		case south: return south;
		case southwest: return southwest;
		case west: return west;
		case northwest: return northwest;
		default: return here;
		}
	}
	
	public static Direction2 get(int deltaX, int deltaY) {
		if (deltaX > 0) {
			if (deltaY > 0) {
				return northeast;
			} else if (deltaY < 0) {
				return southeast;
			} else {
				return east;
			}
		} else if (deltaX < 0) {
			if (deltaY > 0) {
				return northwest;
			} else if (deltaY < 0) {
				return southwest;
			} else {
				return west;
			}
		} else {
			if (deltaY > 0) {
				return north;
			} else if (deltaY < 0) {
				return south;
			} else {
				return here;
			}
		}
	}
}
