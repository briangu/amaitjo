package amaitjo.common;


import java.util.Random;
import org.linkedin.contest.ants.api.Direction;
import ravi.contest.ants.map.Point;


public class Coord
  {
    public int X;
    public int Y;

    public final static Coord ORIGIN = new Coord(0,0);

    public Coord(int x, int y)
    {
      X = x;
      Y = y;
    }

    public Coord(Coord coord)
    {
      this(coord.X, coord.Y);
    }

    public Coord(Point pt)
    {
      this(pt.x, pt.y);
    }

    public boolean equals(Object o)
    {
      return (o instanceof Coord) && (((Coord)o).X == X && ((Coord)o).Y == Y);
    }

    public String toString()
    {
      return String.format("%d,%d", X, Y);
    }

    @Override
    public int hashCode()
    {
      int result = X;
      result = 31 * result + Y;
      return result;
    }

    public Coord clone()
    {
      return new Coord(this);
    }

    public boolean atOrigin()
    {
      return X == 0 && Y == 0;
    }

    public float DistanceFromNest()
    {
      return (float)Math.sqrt(X * X + Y * Y);
    }

    public Coord createFromAdd(Direction d)
    {
      return new Coord(X + d.deltaX, Y + d.deltaY);
    }

    public Coord createFromAdd(Coord c)
    {
      return new Coord(X + c.X, Y + c.Y);
    }

    public void Update(Direction d)
    {
      X += d.deltaX;
      Y += d.deltaY;
    }

    public void Update(Coord c)
    {
      X += c.X;
      Y += c.Y;
    }

    public static Coord nextRandom(Random rng, int minX, int maxX, int minY, int maxY)
    {
      int rndX = (rng.nextInt(maxX*2)-maxX) * (rng.nextBoolean() ? -1 : 1);
      int rndY = (rng.nextInt(maxY*2)-maxY) * (rng.nextBoolean() ? -1 : 1);

      rndX = rndX != 0 ? Math.abs(rndX) < minX ? (rndX/Math.abs(rndX)) * minX : rndX : 0;
      rndY = rndY != 0 ? Math.abs(rndY) < minY ? (rndY/Math.abs(rndY)) * minY : rndY : 0;

      return new Coord(rndX, rndY);
    }

    public Point toPoint()
    {
      return new Point(X, Y);
    }
  }
