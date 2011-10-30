package ravi.contest.ants;

import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.Square;

import ravi.contest.ants.map.LocalMap;
import ravi.contest.ants.map.LocalMap.SquareType;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.state.StateUtils;

public class Knowledge {
	// Id of the ant. Set at the first step.
	private int _id = -1;
	
	// Time in the world. Incremented upon each call to act().
	private int _time;
	
	private int _numSquaresSeen;
	private int _numSquaresWithObstacle;
	private int _numSquaresWithFood; // historical information, not updated if food quantity changes.
	private int _numFoodSeen; // historical information, not updated if food quantity changes.
	
	// Wild is defined as beyond the clearing of the nest.
	// This variable starts out as true and stays false, once changed.
	private boolean _neverBeenInTheWild = true;
	
	// Absolute co-ordinates of my nest.
	// Not known until I go back to my nest.
	private Point _myNestAbsolute;
	
	// My position relative to my nest.
	private Point _myPositionRelative;

	// Position of enemy nest relative to my nest.
	// I won't have this information until I run into a clearing or go back to my nest.
	private Point _enemyNestRelative;
	
	// Approximate radius of clearing around the nest.
	// This is the area around the nest devoid of obstacles.
	// We assume that this is approx. similar for both nests.
	private int _clearingRadiusAroundNest;
	
	// Limits of my territory.
	private Point _territoryBottomLeftPoint, _territoryTopRightPoint, _territoryCenter; 
	
	// Understanding of the environment relative to my nest.
	private LocalMap _map;
	
	// Walk back to the nest.
	private Walk2 _walkBackToNest;
	
	// TODO: maintain knowledge of [square, time] when ants are observed.
	// Communicate this to the oracle at an appropriate time.
	// TODO: perhaps have a list that holds messages to tell the Oracle. 
	
	public Knowledge() {
		_neverBeenInTheWild = true;
		_myPositionRelative = new Point(0, 0);
		_map = new LocalMap();
		// Initialize walk with nest location.
		_walkBackToNest = new Walk2();
		_walkBackToNest.visit(getPosition());
	}
	
	public double getObstacleDensity() {
		// NOTE: this method has a simplified model for discounting clearing around the nest
		// for calculating obstacle probability. This simplification is akin to assuming that
		// the ant traveled radially outward in the clearing around the nest (mostly true).
		int numSquaresSeenOutsideOfClearingRadius = _numSquaresSeen - _clearingRadiusAroundNest;
		return numSquaresSeenOutsideOfClearingRadius > 0 ? _numSquaresWithObstacle * 1.0D / numSquaresSeenOutsideOfClearingRadius : 0;
	}
	
	public double getFoodDensity() {
		return _numSquaresSeen > 0 ? _numFoodSeen * 1.0D / _numSquaresSeen : 0;
	}
	
	public void updatePosition(Direction dir) {
		if (dir != Direction.here) {
			_myPositionRelative.move(dir);
			_walkBackToNest.visit(getPosition());
		}
	}
	
	public Point getPosition() {
		return new Point(_myPositionRelative.x, _myPositionRelative.y);
	}

	// x and y are relative to my nest.
	// So they can be both positive and negative.
	// Envt is the environment around it.
	public void add(Environment envt) {
		int x = _myPositionRelative.x;
		int y = _myPositionRelative.y;
		add(x, y, envt.getSquare(Direction.here));
		add(x, y+1, envt.getSquare(Direction.north));
		add(x+1, y+1, envt.getSquare(Direction.northeast));
		add(x-1, y+1, envt.getSquare(Direction.northwest));
		add(x+1, y, envt.getSquare(Direction.east));
		add(x-1, y, envt.getSquare(Direction.west));
		add(x, y-1, envt.getSquare(Direction.south));
		add(x+1, y-1, envt.getSquare(Direction.southeast));
		add(x-1, y-1, envt.getSquare(Direction.southwest));
	}
	
	private void add(int x, int y, Square square) {
		SquareType prevSquareKnowledge = _map.getSquareType(x, y);
		if (prevSquareKnowledge == SquareType.UNKNOWN) {
			// We are visiting this square for the first time.
			_numSquaresSeen++;
			if (square.hasFood()) {
				_map.setSquareType(x, y, SquareType.HAS_FOOD);
				_numSquaresWithFood++;
				_numFoodSeen += square.getAmountOfFood();
			} else if (!square.isPassable()) {
				_map.setSquareType(x, y, SquareType.OBSTACLE);
				_numSquaresWithObstacle++;
			} else {
				_map.setSquareType(x, y, SquareType.EMPTY);
			}
		} else {
			// We have visited this square before.
			// Update our knowledge with the latest information.
			if ((prevSquareKnowledge == SquareType.HAS_FOOD) && !square.hasFood()) {
				_map.setSquareType(x, y, SquareType.EMPTY);
				// We do not change _numSquaresWithFood or _numFoodSeen.
			}
		}
		
		// If this square is an obstacle, adjust _neverBeenInTheWild, if needed.
		if (_neverBeenInTheWild && !square.isPassable()) {
			// We are at the edge of the wild now, baby!
			_neverBeenInTheWild = false;
			// Determine the clearing radius around the nest now.
			_clearingRadiusAroundNest = MapUtils.getDistance(0, 0, x, y);
		}
	}
	
	public void setId(int id) {
		_id = id;

		if (StateUtils.isOracle(_id)) {
			// I am the oracle. My territory is the nest.
			_territoryBottomLeftPoint = new Point(0, 0);
			_territoryTopRightPoint = _territoryBottomLeftPoint;
		} else {
			// Initialize my understanding of my territory.
			_territoryBottomLeftPoint = MapUtils.getTerritoryBottomLeftPoint(_id);
			_territoryTopRightPoint = MapUtils.getTerritoryTopRightPoint(_id);
		}
		
		_territoryCenter = new Point((_territoryBottomLeftPoint.x + _territoryTopRightPoint.x)/2, (_territoryBottomLeftPoint.y + _territoryTopRightPoint.y)/2);
		System.out.println("Ant[" + _id + "] territory: bottom-left:" + _territoryBottomLeftPoint + ", top-right:" + _territoryTopRightPoint + ", center:" + _territoryCenter);
	}
	
	public int getId() {
		return _id;
	}
	
	public void incrementTime() {
		_time++;
	}
	
	public int getTime() {
		return _time;
	}
	
	public LocalMap getMap() {
		return _map;
	}
	
	public void setNestAbsolute(Point nest) {
		_myNestAbsolute = nest;
		// TODO: recalculate territory if needed.
	}
	
	public Point getNestAbsolute() {
		return _myNestAbsolute;
	}
	
	public void setApproxEnemyNestX(Point p) {
		_enemyNestRelative = p;
	}
	
	public Point getApproxEnemyNest() {
		return _enemyNestRelative;
	}
	
	public Point getTerritoryTopRightPoint() {
		return _territoryTopRightPoint;
	}
	
	public Point getTerritoryBottomLeftPoint() {
		return _territoryBottomLeftPoint;
	}
	
	public Point getTerritoryCenter() {
		return _territoryCenter;
	}
	
	public Walk2 getWalkBackToNest() {
		return _walkBackToNest;
	}
}
