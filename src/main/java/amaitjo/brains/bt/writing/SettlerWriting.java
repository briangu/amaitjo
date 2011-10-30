package amaitjo.brains.bt.writing;


import amaitjo.common.DirectionHelper;
import org.linkedin.contest.ants.api.Direction;


public class SettlerWriting
{
  public int turnWritten;
  public Direction dir;

  private SettlerWriting()
  {
  }

  public SettlerWriting(int turn, Direction d)
  {
    turnWritten = turn;
    dir = d;
  }

  public Long encode()
  {
    Long val = Long.valueOf(turnWritten);
    val = Long.rotateLeft(val, 32);
    val += dir.ordinal();
    return val;
  }

  public static SettlerWriting decode(Long val)
  {
    SettlerWriting writing = new SettlerWriting();
    writing.dir = DirectionHelper.readWritingAsDirection(val);
    if (writing.dir == null) return null;
    val = Long.rotateRight(val, 32);
    writing.turnWritten = val.intValue();

    return writing;
  }
}
