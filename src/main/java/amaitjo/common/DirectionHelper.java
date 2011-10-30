package amaitjo.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;
import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.Environment;


public class DirectionHelper {

	private static Map<Direction,Direction> oppositeDirections = null;

  private static Map<Coord, Direction> unitDirMap = null;

	static
	{
		oppositeDirections = new HashMap<Direction,Direction>();
		oppositeDirections.put(Direction.here, Direction.here);
		setOppositeDirections(Direction.east, Direction.west,oppositeDirections);
		setOppositeDirections(Direction.north, Direction.south,oppositeDirections);
		setOppositeDirections(Direction.northeast, Direction.southwest,oppositeDirections);
		setOppositeDirections(Direction.southeast, Direction.northwest,oppositeDirections);

    unitDirMap = new HashMap<Coord, Direction>();
    for (Direction d : createRandomDirectionList())
    {
      Coord coord = new Coord(d.deltaX, d.deltaY);
      unitDirMap.put(coord, d);
    }
    unitDirMap.put(new Coord(0,0), Direction.here);
	}

  public static Direction getFirstPassableMove(Environment environment)
  {
    List<Direction> directions = createDirectionList();
    for (Direction d : directions)
    {
      if (environment.getSquare(d).isPassable())
      {
        return d;
      }
    }
    return Direction.here;
  }

	private static void setOppositeDirections(Direction d1, Direction d2, Map<Direction,Direction> map)
	{
		map.put(d1,d2);
		map.put(d2,d1);
	}
	
	public static Direction getOppositeDirection(Direction d)
	{
		return oppositeDirections.get(d);
	}

  public static Direction getRandomDirection()
  {
    return toDirection(new Random().nextInt(8));
  }

  public static Direction toDirection(Long l)
      throws IllegalArgumentException
  {
    if (l > Integer.MAX_VALUE) throw new IllegalArgumentException("unknown direction");
    return toDirection(l.intValue());
  }

  public static Direction toDirection(int i)
      throws IllegalArgumentException
  {
    switch (i)
    {
      case 0: return Direction.north;
      case 1: return Direction.northeast;
      case 2: return Direction.east;
      case 3: return Direction.southeast;
      case 4: return Direction.south;
      case 5: return Direction.southwest;
      case 6: return Direction.west;
      case 7: return Direction.northwest;
      case 8: return Direction.here;
    }

    throw new IllegalArgumentException("unknown direction");
  }

  // get the direction it would take to go towards coord from the nest
  public static Direction getCoordDirection(Coord coord)
  {
    Coord dirCoord = new Coord(
        coord.X == 0 ? 0 : coord.X > 0 ? 1 : -1,
        coord.Y == 0 ? 0 : coord.Y > 0 ? 1 : -1
    );
    return unitDirMap.get(dirCoord);
  }

  public static Direction getRelativeDir(Coord start, Coord dest)
  {
    Coord delta = new Coord(dest.X - start.X, dest.Y - start.Y);
    return getCoordDirection(delta);
  }

  public static List<Direction> directionsAwayByVector(Coord pos)
  {
    if (pos.atOrigin())
    {
      List<Direction> directions = createRandomDirectionList();
      Collections.shuffle(directions);
      return directions;
    }

    final Direction d = getCoordDirection(pos);

    return directionsByDir(d);
  }

  public static List<Direction> directionsHomeByVector(Coord pos)
  {
    if (pos.atOrigin())
    {
      List<Direction> directions = createRandomDirectionList();
      Collections.shuffle(directions);
      return directions;
    }

    final Direction d = getOppositeDirection(getCoordDirection(pos));

    return directionsByDir(d);
  }

  public static List<Direction> directionsByDir(final Direction d)
  {
    List<Direction> directions = createDirectionList();

    try
    {
      Collections.sort(directions, new Comparator<Direction>()
      {
        @Override
        public int compare(Direction o1, Direction o2)
        {
          Double dist1 = distance(d.deltaX, d.deltaY, o1.deltaX, o1.deltaY);
          Double dist2 = distance(d.deltaX, d.deltaY, o2.deltaX, o2.deltaY);
          return dist1.compareTo(dist2);
        }
      });
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }

    return directions;
  }

  public static double distance(Coord a, Coord b)
  {
    return distance(a.X, a.Y, b.X, b.Y);
  }

  public static double distance(int x1, int y1, int x2, int y2)
  {
    return Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2-y1));
  }

  public static List<Direction> createRandomDirectionList()
  {
    List<Direction> directions = createDirectionList();
    Collections.shuffle(directions);
    return directions;
  }

  public static List<Direction> createDirectionList()
  {
    List<Direction> directions = new ArrayList<Direction>()
    {{
        add(Direction.east);
        add(Direction.west);
        add(Direction.north);
        add(Direction.south);
        add(Direction.northeast);
        add(Direction.northwest);
        add(Direction.southeast);
        add(Direction.southwest);
    }};

    return directions;
  }

  public static Direction readWritingAsDirection(Long writing)
  {
    Direction d = null;

    try
    {
      d = DirectionHelper.toDirection(writing.intValue());
    }
    catch (Exception e)
    {
      // not a direction
    }

    return d;
  }
}
