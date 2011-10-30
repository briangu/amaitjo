package ravi.contest.ants.map;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ravi.contest.ants.Constants;


public class LocalMap {
	public enum SquareType {
		UNKNOWN,
		OBSTACLE,
		HAS_FOOD,
		EMPTY,
		;
	}
	
	private Map<Long, SquareType> _squareTypeMap = new HashMap<Long, LocalMap.SquareType>();
	
	private int _marginLeft = Constants.BOARD_SIZE;
	private int _marginRight = -Constants.BOARD_SIZE;
	private int _marginTop = -Constants.BOARD_SIZE;
	private int _marginBottom = Constants.BOARD_SIZE;
	
	private static final ShortestPathCalculator _shortestPathCalculator = new ShortestPathCalculator();
	
	// Type of square represented by the given x and y co-ordinates.
	public SquareType getSquareType(int x, int y) {
		SquareType squareType = _squareTypeMap.get(constructKey(x, y));
		return squareType != null ? squareType : SquareType.UNKNOWN;
	}
	
	// x and y are small values, in the range of [-512, 512].
	public void setSquareType(int x, int y, SquareType type) {
		_squareTypeMap.put(constructKey(x, y), type);
		if (x < _marginLeft) {
			_marginLeft = x;
		}
		if (_marginRight < x) {
			_marginRight = x;
		}
		if (y < _marginBottom) {
			_marginBottom = y;
		}
		if (_marginTop < y) {
			_marginTop = y;
		}
	}
	
	// x uses top 16 bits, y uses bottom 16 bits.
	private static Long constructKey(int x, int y) {
		//long key = (x << 16) + y;
		long key = x * 10000 + y;
//		System.out.println("["+x+","+y+"] -> " + key);
		return key;
	}
	
	private static Point deconstructKey(Long key) {
		Point p = new Point();
		p.x = (int) Math.round(key/10000.0);
		p.y = (int) (key - 10000*p.x);
		return p;
	}

  public SquareType getSquareType(Point pt)
  {
    return _squareTypeMap.get(constructKey(pt.x, pt.y));
  }

	// Number of points known by this map.
	public int size() {
		return _squareTypeMap.size();
	}

	public int getLeftMargin() {
		return _marginLeft;
	}
	
	public int getRightMargin() {
		return _marginRight;
	}
	
	public int getTopMargin() {
		return _marginTop;
	}
	
	public int getBottomMargin() {
		return _marginBottom;
	}
	
	private static Comparator<Point> _comparator = new Comparator<Point>() {
		@Override
		public int compare(Point p, Point q) {
			if (p.y < q.y) {
				return -1;
			} else if (p.y > q.y) {
				return 1;
			} else {
				if (p.x < q.x) {
					return -1;
				} else if (p.x > q.x){
					return 1;
				} else {
					return 0;
				}
			}
		}
	};
	
	public TreeSet<Point> getSquares() {
		TreeSet<Point> squares = new TreeSet<Point>(_comparator);
		Set<Long> keySet = _squareTypeMap.keySet();
		for (Long key : keySet) {
//			Point p = new Point();
//			p.x = (int) Math.round(key/10000.0);
//			p.y = (int) (key - 10000*p.x);
			Point p = deconstructKey(key);
			squares.add(p);
		}
		return squares;
	}
	
	// Gets an approximate shortest path between the source and the destination
	// based on topology information this LocalMap knows.
	public Walk2 getShortestPath(Point source, Point destination) {
		return _shortestPathCalculator.getApproxShortestPath(this, source, destination);
	}
}
