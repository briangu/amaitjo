package amaitjo.brains.bt;


import amaitjo.brains.bt.writing.Graffiti;
import amaitjo.common.Coord;
import amaitjo.common.DirectionHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import org.linkedin.contest.ants.api.Action;
import org.linkedin.contest.ants.api.Ant;
import org.linkedin.contest.ants.api.Direction;
import org.linkedin.contest.ants.api.DropFood;
import org.linkedin.contest.ants.api.Environment;
import org.linkedin.contest.ants.api.GetFood;
import org.linkedin.contest.ants.api.Move;
import org.linkedin.contest.ants.api.Pass;
import org.linkedin.contest.ants.api.Say;
import org.linkedin.contest.ants.api.Square;
import org.linkedin.contest.ants.api.WorldEvent;
import org.linkedin.contest.ants.api.Write;
import ravi.contest.ants.Knowledge;
import ravi.contest.ants.map.MapUtils;
import ravi.contest.ants.map.Point;
import ravi.contest.ants.map.Walk2;
import ravi.contest.ants.movement.NavigationAdvisor;


public class Ozzy implements Ant
{
  final String MSG_PREFIX = "An ant says ";
  final String MSG_ORACLE_PREFIX = "ORACLE";
  final String MSG_NEO_PREFIX = "NEO";

  final static long MOVE_SAMPLE_RATE = 250;

  int _turn = 0;

  int _groupId;
  int _id;

  protected Environment _environment = null;

  Stack<ActionGenerator> _generators = new Stack<ActionGenerator>();
  Stack<Action> _actionStack = new Stack<Action>();
  private Map<Direction, Graffiti> _graffiti;

  public final static int SECRET = 0x3141;

  int _maxPioneerDistance;
  int _maxPioneeringSteps;
  int _groupCount;

  Knowledge _globalKnowledge = new Knowledge();
  NavigationAdvisor _onStar = new NavigationAdvisor();

  private double K = 0.001;   // originally 0.001
  private double N = 10.0;    // original 10.0
  private double MINIMUM_ODDS_BEFORE_FLEEING = 20; // 20
  private double MINIMUM_APPROACH_ODDS = 20; // 30
  private double ENEMY_NEST_FIGHT_ODDS_FACTOR = 0.5;

  private Random _rng = new Random();
  private int _boardSize;
  Coord _absolutePos = Coord.ORIGIN.clone();
  List<Direction> _directions = DirectionHelper.createDirectionList();

  int _minX = -64;
  int _minY = -64;
  int _maxX = 64;
  int _maxY = 64;

  public Integer getMode()
  {
    if (_generators.peek() instanceof Settler) return 1;
    if (_generators.peek() instanceof ReverseSettler) return 2;
    if (_generators.peek() instanceof Pioneer) return 3;
    return 0;
  }

  public void log(String text)
  {
    /*if(_generators.size() > 0 && _generators.peek() instanceof NestContext) {
        ((NestContext)_generators.peek()).log(text);
      } else {
        System.out.println(_id + ": (turn:" + _turn + " gen[" + _generators.size() + "]:" + _generators.peek().getClass().getSimpleName() + ") " + text);
      }*/
  }
  
  public void info(String text)
  {
/*
    if(_generators.size() > 0 && _generators.peek() instanceof NestContext) {
        ((NestContext)_generators.peek()).log(text);
      } else {
        System.out.println(_id + ": (turn:" + _turn + " gen[" + _generators.size() + "]:" + _generators.peek().getClass().getSimpleName() + ") " + text);
      }
*/
  }

  protected int getSecret()
  {
    return SECRET;
  }

  @Override
  public void init()
  {
    _boardSize = 512;
    _maxPioneerDistance = _boardSize;
    _maxPioneeringSteps = _maxPioneerDistance * 2;
    _groupCount = 20;

    _generators.push(new Antnitializer());
  }

  protected HashMap<Direction, Graffiti> createGraffitiMap()
  {
    List<Direction> directions = Arrays.asList(Direction.values());

    HashMap<Direction, Graffiti> map = new HashMap<Direction, Graffiti>(directions.size() + 1);

    for (Direction d : directions)
    {
      Long writing = _environment.getSquare(d).getWriting();
      Graffiti graffiti = Graffiti.fromWriting(getSecret(), writing);
      map.put(d, graffiti);
    }

    return map;
  }

  /**
   * Action loop:
   * <p/>
   * If action stack is empty, poll current generator for more actions.
   * <p/>
   * Else:  grab the action from the top of the stack and run it.
   */
  @Override
  public Action act(Environment environment, List<WorldEvent> events)
  {
    _turn++;
    _environment = environment;

    Action action;

    try
    {
      _globalKnowledge.incrementTime();
      _globalKnowledge.add(environment);

      _graffiti = createGraffitiMap();

      // give the ant a chance to react to the environment
      //if(this.getClass() != EvilOzzy.class) react(environment);

      if (_actionStack.size() == 0)
      {
        _generators.peek().act(environment, events);
      }

      if (_actionStack.size() > 0)
      {
        action = _actionStack.pop();

        if (action == null)
        {
          System.out.println("action is null!");
          action = new Pass();
        }

        if (_turn % MOVE_SAMPLE_RATE == 0)
        {
          log("action sample: " + action.getClass().getSimpleName());
        }

        if (action instanceof Move)
        {
          Direction d = ((Move) action).getDirection();
          if (d != null)
          {
            _globalKnowledge.updatePosition(d);
            _absolutePos.Update(d);
            _generators.peek().updatePos(environment, d, _globalKnowledge.getPosition()); // update relpos
          }
          else
          {
            System.out.println("direction == null!");
          }
        }
      }
      else
      {
        log("didn't make an action!");
        action = null;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      action = null;
    }

    return action;
  }

  private void react(Environment environment)
  {
    try {
      EnemySighting sighting = checkForEnemies(environment);
      // if there is a nearby risk, move away from it, then back the next turn, note that
      // this is recursive, the ant will keep moving away from the risk until it's gone, then
      // go back to exactly where it was
      if (sighting != null)
      {
        Direction evadeDirection = DirectionHelper.getOppositeDirection(sighting._direction);
        for(Direction dir : DirectionHelper.directionsByDir(evadeDirection)) {
          Square evadeSquare = environment.getSquare(dir);
          if (evadeSquare.isPassable() && !evadeSquare.hasAnts())
          {
            System.out.println("Run Away! odds: " + sighting._fightOdds);
            _actionStack.push(new Move(sighting._direction));
            _actionStack.push(new Move(DirectionHelper.getOppositeDirection(sighting._direction)));
          }
          else
          {
            System.out.println("Can't Run Away! Direction is either a barrier or has other ants. odds: " + sighting._fightOdds);
          }
        }
      }
    } catch (Throwable t)
    {
      System.out.println("error when reacting");
      t.printStackTrace();
    }
  }

  public EnemySighting checkForEnemies(Environment environment)
  {
    int foreignPheromonesInSurroundings = 0;
    int friendlyPheromonesInSurroundings = 0;
    int mostDangerious = 100;
    Direction directionOfFightRisk = null;

    for (Direction dir : _directions)
    {
      if (!(_graffiti.get(dir).isEmpty))
      {
        if (_graffiti.get(dir).isForeign)
        {
          foreignPheromonesInSurroundings++;
        }
        else
        {
          friendlyPheromonesInSurroundings++;
        }
      }
    }

    for (Direction dir : _directions)
    {
      int odds = fightOdds(environment.getSquare(Direction.here), environment.getSquare(dir));
      if (odds >= 0)
      {
        odds = odds + friendlyPheromonesInSurroundings * 20 - foreignPheromonesInSurroundings * 20;
        if (odds < 0) odds = 0;
        else if (odds > 100) odds = 100;

        if (odds < mostDangerious)
        {
          mostDangerious = odds;
          directionOfFightRisk = dir;
        }
      }
    }
    if (mostDangerious > 0 && mostDangerious < MINIMUM_ODDS_BEFORE_FLEEING)
    {
      return new EnemySighting(directionOfFightRisk, mostDangerious);
    }
    else
    {
      return null;
    }
  }

  private int fightOdds(Square currentSquare, Square potentialEnemySquare)
  {
    int potentialEnemyCount = potentialEnemySquare.getNumberOfAnts();
    int armyCount = currentSquare.getNumberOfAnts(); // adjacent friendly's count too, but we don't know if they are friendly or not
    if (potentialEnemyCount == 0) return -1; // not a fight

    int odds = 50 + armyCount - potentialEnemyCount;

    if (odds < 0) odds = 0;
    else if (odds > 100) odds = 100;

    int radialDistanceFromNest = 0;
    if (_generators.size() > 0)
    {
      Coord relPos = _generators.peek().getRelPos();
      radialDistanceFromNest = (int) relPos.DistanceFromNest();
    }
    
    Point currentPosition = _globalKnowledge.getPosition();
    Point approxEnemyNestPosition = _globalKnowledge.getApproxEnemyNest();
    if(currentPosition != null && approxEnemyNestPosition != null) {
      int xdelta =  approxEnemyNestPosition.x-currentPosition.x;
      int ydelta =  approxEnemyNestPosition.y-currentPosition.y;
      int approxDistanceFromEnemyNest = (int)Math.sqrt(xdelta*xdelta+ydelta*ydelta);
      if (approxDistanceFromEnemyNest < 100)
        odds -= (100 - approxDistanceFromEnemyNest) * ENEMY_NEST_FIGHT_ODDS_FACTOR;
    }

    if (radialDistanceFromNest < 100) odds += (100 - radialDistanceFromNest);

    if (odds < 0) odds = 0;
    else if (odds > 100) odds = 100;

    return odds;
  }

  @Override
  public Action onDeath(WorldEvent cause)
  {
    return null;
  }

  private interface ActionGenerator
  {
    public void act(Environment environment, List<WorldEvent> events);

    public void setPos(Coord pos);

    public void updatePos(Environment environment, Direction d, Point pt);

    public Coord getRelPos();
  }

  private abstract class NestContext implements ActionGenerator
  {
    protected Coord _relPos = new Coord(0, 0);

    Square _curSquare;
    Direction _orientation = DirectionHelper.getRandomDirection();
    List<Direction> _directions = DirectionHelper.createDirectionList();

    Point _absoluteNestLocation = _globalKnowledge.getPosition();

    Walk2 _walkHistory = new Walk2();

    protected boolean shutdown()
    {
      int remainingTurns = 100000 - _turn;
      int dist = MapUtils.getDistance(_globalKnowledge.getPosition(), _absoluteNestLocation);
      return (remainingTurns <= (_generators.size() * dist * 1.5));
    }

    @Override
    public Coord getRelPos()
    {
      return _relPos;
    }

    public void log(String text)
    {
      //System.out.println(_id + ": (turn:" + _turn + ", relPos:" + _relPos + ", gen[" + _generators.size() + "]:" + _generators.peek().getClass().getSimpleName() + ") " + text);
    }

    public void info(String text)
    {
//      System.out.println(_id + ": (turn:" + _turn + ", relPos:" + _relPos + ", gen[" + _generators.size() + "]:" + _generators.peek().getClass().getSimpleName() + ") " + text);
    }

    protected Direction getNeighborWithMaxFoodPheromones()
    {
      int max = Integer.MIN_VALUE;
      Direction bestDir = Direction.here;

      for (Direction d : _directions)
      {
        int level = _graffiti.get(d).getDecayedFoodPheromone(_turn);
        if (level > max)
        {
          max = level;
          bestDir = d;
        }
      }

      return bestDir;
    }

    protected boolean atRealNest()
    {
      return _curSquare.isNest() && _globalKnowledge.getPosition().x == 0 && _globalKnowledge.getPosition().y == 0;
    }

    protected boolean atOurRelativeNest()
    {
      if (atRealNest())
      {
        if (_generators.size() == 1)
        {
          if ((!_relPos.atOrigin() || !_absolutePos.atOrigin()))
          {
            System.out.println("out of sync");
            _relPos = Coord.ORIGIN.clone();
            _absolutePos = Coord.ORIGIN.clone();
            return false;
          }
          return true;
        }
      }

      return _relPos.atOrigin();
    }

    @Override
    public abstract void act(Environment environment, List<WorldEvent> events);

    @Override
    public void setPos(Coord pos)
    {
      _relPos = pos.clone();
    }

    @Override
    public void updatePos(Environment environment, Direction d, Point pt)
    {
      _relPos.Update(d);
      _walkHistory.visit(pt);
    }

    private boolean tooFarFromNest(Direction d)
    {
      double div = _maxPioneerDistance / Math.max(9, (11 * (_turn / 100000))); //3.141592;
      return _relPos.createFromAdd(d).DistanceFromNest() > div;
    }

    protected Direction getMaxLocationTowardsFood()
    {
      List<Direction> directions = DirectionHelper.directionsByDir(_orientation);
      final int[][] indices = new int[][]{{0, 3}, {3, directions.size()}};
      double[] testList = new double[directions.size()];
      Direction d = Direction.here;

      for (int r = 0; r < indices.length && d.equals(Direction.here); r++)
      {
        double sum = 0;

        for (int i = indices[r][0]; i < indices[r][1]; i++)
        {
          Square sq = _environment.getSquare(directions.get(i));
          if (!sq.isPassable()) continue;
          if (sq.isNest()) continue;
//          if (sq.getNumberOfAnts() > 10) continue;
          if (sq.hasAnts()) continue;
          if (tooFarFromNest(directions.get(i))) continue;
          if (_graffiti.get(directions.get(i)).isSettlerNest) continue;
          int level = sq.hasFood() ? Graffiti.MAX_PHEROMONE : _graffiti.get(directions.get(i)).getDecayedFoodPheromone(_turn);
          double selectionProb = Math.pow(K + level, N);
          testList[i] = selectionProb;
          sum += selectionProb;
        }

        double pick = _rng.nextDouble();
        double pctSum = 0;

        for (int i = indices[r][0]; i < indices[r][1]; i++)
        {
          pctSum += testList[i] / sum;
          if (pick < pctSum)
          {
            d = directions.get(i);
            break;
          }
        }
      }

      return d;
    }

    protected void dropFoodPheromones()
    {
      if (_curSquare.hasFood())
      {
        _actionStack.push(new Write(_graffiti.get(Direction.here).encodeFoodPheromone(_turn,
                                                                                      Graffiti.MAX_PHEROMONE)));
      }
      else
      {
        int max = _graffiti.get(getNeighborWithMaxFoodPheromones()).getDecayedFoodPheromone(_turn);
        int des = max - 8;
        int d = des - _graffiti.get(Direction.here).getDecayedFoodPheromone(_turn);
        if (d > 0)
        {
          _actionStack.push(new Write(_graffiti.get(Direction.here).encodeFoodPheromone(_turn, d)));
        }
      }
    }
  }

  private int getMaxPioneerDistance()
  {
    return _maxPioneerDistance;
//    if (_groupId == 0) return _maxPioneerDistance;
//    return (_turn < 5000) ? 50 : _maxPioneerDistance;
//    return (int) Math.max(_earlyDropDistance+3, _maxPioneerDistance * (1 - ((_turn / 8.0)  / 100000.0)));
//    return (int) Math.min(100, Math.abs(Math.sin(_turn) * _maxPioneerDistance));
//    return (int) Math.min(_maxPioneerDistance * (1 - _turn / 100000.0), _maxPioneerDistance / 2);
//    return _maxPioneerDistance;
  }

  private static class EnemySighting
  {
    Direction _direction;
    int _fightOdds;

    public EnemySighting(Direction direction, int fightOdds)
    {
      _direction = direction;
      _fightOdds = fightOdds;
    }
  }

  private class ReverseSettler extends NestContext
  {
    Stack<Direction> _toFood = new Stack<Direction>();
    Stack<Direction> _toNest = new Stack<Direction>();
    boolean _haveFood = false;
    boolean _done = false;

    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      _graffiti = createGraffitiMap();
      _curSquare = _environment.getSquare(Direction.here);

      if (_haveFood || _done)
      {
        if (atOurRelativeNest())
        {
          if (_done)
          {
            _generators.pop();
            return;
          }
          _haveFood = false;
          _actionStack.push(new DropFood(Direction.here));
          return;
        }

        Direction homeDir =
            (_toNest.size() > 0)
                ? _toNest.pop()
                : _onStar.advise(_globalKnowledge.getPosition(), _absoluteNestLocation, _globalKnowledge.getMap(), _walkHistory);

        _toFood.push(homeDir);
        _actionStack.push(new Move(homeDir));
      }
      else
      {
        if (_toFood.size() == 0)
        {
          _walkHistory.clear();

          if (_curSquare.hasFood() && !_curSquare.isNest())
          {
            _haveFood = true;
            _actionStack.push(new GetFood(Direction.here));
          }
          else
          {
            _done = true;
          }
        }
        else
        {
          Direction d = _toFood.pop();
          _toNest.push(d);
          Direction foodDir = DirectionHelper.getOppositeDirection(d);
          _actionStack.push(new Move(foodDir));
        }
      }
    }
  }

  List<Point> _pioneerTargets = new ArrayList<Point>();

  public void generatePioneerTargets()
  {
    _pioneerTargets.clear();
    for (int i = 0; i < 1024; i += 32)
    {
      for (int j = 0; j < 1024; j += 32)
      {
        _pioneerTargets.add(new Point(i - 512,j - 512));
      }
    }
    Collections.shuffle(_pioneerTargets);
  }

  private Point getNewTarget()
  {
    if (_pioneerTargets.size() == 0) generatePioneerTargets();

    if (_turn < 5000) return _pioneerTargets.remove(0);

    while(_pioneerTargets.size() > 0)
    {
      Point pt = _pioneerTargets.remove(0);
      if (pt.x < (_minX-_rng.nextInt(32))) continue;
      if (pt.x > (_maxX+_rng.nextInt(32))) continue;
      if (pt.y > (_maxY+_rng.nextInt(32))) continue;
      if (pt.y < (_minY-_rng.nextInt(32))) continue;
      return pt;
    }

    generatePioneerTargets();

    return _pioneerTargets.remove(0);
  }

  private class Pioneer extends NestContext
  {
    int _pioneeringSteps = 0;
    int _middleSettlers = 0;
    Point _target;
    Point _startPos = _globalKnowledge.getPosition();

    public Pioneer()
    {
      _target = getNewTarget();
      _maxPioneeringSteps = (MapUtils.getDistance(_globalKnowledge.getPosition(), _target) * 3);
    }

    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      _curSquare = _environment.getSquare(Direction.here);

      float dist = MapUtils.getDistance(_startPos, _globalKnowledge.getPosition());

      double waypointDist = Math.max(Math.min(30, _globalKnowledge.getObstacleDensity() * 100), 16);
      if (dist > (_middleSettlers + 1) * waypointDist)  // 30 also works well, but is sparser
      {
        _middleSettlers++;

        ActionGenerator context = _generators.pop();

        ReverseSettler settler = (ReverseSettler) _generators.peek();
        settler.setPos(_relPos);

        _relPos = Coord.ORIGIN.clone();
        _startPos = _globalKnowledge.getPosition();

        _generators.push(new Settler());
        _generators.push(new ReverseSettler());
        _generators.push(context);
      }

      if (MapUtils.getDistance(_globalKnowledge.getPosition(), _target) < 4
          || (_pioneeringSteps > _maxPioneeringSteps)
          || shutdown())
      {
        ActionGenerator pop = _generators.pop();
        log("removed " + pop.getClass().getSimpleName() + " now using: " + _generators.peek().getClass().getSimpleName());

        // we need this to tell the underlying nest context where it is when we pop the later contexts
        ReverseSettler settler = (ReverseSettler) _generators.peek();
        settler.setPos(_relPos);

        _graffiti = createGraffitiMap();
        Graffiti nestGraffiti = _graffiti.get(Direction.here);
        nestGraffiti.isSettlerNest = true;
        _actionStack.push(new Write(nestGraffiti.write()));
        info("Wrote nest location to square");

        if (!shutdown())
        {
          _generators.push(new Settler());

          log("switching to settler");
        }

        return;
      }

      _pioneeringSteps++;

      Direction bestDir = _onStar.advise(_globalKnowledge.getPosition(), _target, _globalKnowledge.getMap(), _walkHistory);
      if (bestDir != null)
      {
        _actionStack.push(new Move(bestDir));
      }
      else
      {
        System.out.println("bestdir is null!");
      }
    }
  }

  private class Antnitializer extends NestContext
  {
    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      if (_turn == 1)
      {
        _curSquare = _environment.getSquare(Direction.here);
        _id = _curSquare.getNumberOfAnts();
        _groupId = _id % _groupCount;
        _actionStack.push(new Move(Direction.west));
      }
      else if (_turn == 2)
      {
        // NOTE: it's simpler to return to the nest before we push new settlers,
        //       we have to reset our relPos to 0,0
        _actionStack.push(new Move(Direction.east));
      }
      else
      {
        _generators.pop();

        if (_id > 1)
        {
          _generators.push(new Settler());
          _generators.push(new ReverseSettler());
          _generators.push(new Pioneer());
          _generators.push(new Neo());
        }
        else
        {
          _generators.push(new Oracle());
        }

        // TODO: Say where we are going
        _actionStack.push(new Pass());
      }
    }
  }

  private class Oracle extends NestContext
  {
    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      for (WorldEvent event : events)
      {
        String msg = event.getEvent();
        if (msg.startsWith(MSG_PREFIX))
        {
          msg = msg.substring(MSG_PREFIX.length());

          if (msg.startsWith(MSG_NEO_PREFIX))
          {
            msg = msg.substring(MSG_NEO_PREFIX.length());

            String[] parts = msg.split(",");
            int left = Integer.parseInt(parts[0]);
            int top = Integer.parseInt(parts[1]);
            int right = Integer.parseInt(parts[2]);
            int bottom = Integer.parseInt(parts[3]);

            if (left < _minX) _minX = left;
            if (top > _maxY) _maxY = top;
            if (right > _maxX) _maxX = right;
            if (bottom < _minY) _minY = bottom;
          }
        }
      }

      String msg =
          String.format(
              "%s%s,%s,%s,%s",
              MSG_ORACLE_PREFIX,
              _minX,
              _minY,
              _maxX,
              _maxY);
      _actionStack.push(new Say(msg, Direction.here));
    }
  }

  // neo listens to the oracle for the next pioneer target location
  private class Neo extends NestContext
  {
    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      boolean enlightened = false;

      for (WorldEvent event : events)
      {
        String msg = event.getEvent();
        if (msg.startsWith(MSG_PREFIX))
        {
          msg = msg.substring(MSG_PREFIX.length());

          if (msg.startsWith(MSG_ORACLE_PREFIX))
          {
            msg = msg.substring(MSG_ORACLE_PREFIX.length());

            String[] parts = msg.split(",");
            _minX = Integer.parseInt(parts[0]);
            _minY = Integer.parseInt(parts[1]);
            _maxX = Integer.parseInt(parts[2]);
            _maxY = Integer.parseInt(parts[3]);

            enlightened = true;
            break;
          }
        }
      }

      String msg =
          String.format(
              "%s%s,%s,%s,%s",
              MSG_NEO_PREFIX,
              _globalKnowledge.getMap().getLeftMargin(),
              _globalKnowledge.getMap().getTopMargin(),
              _globalKnowledge.getMap().getRightMargin(),
              _globalKnowledge.getMap().getBottomMargin()
              );
      _actionStack.push(new Say(msg, Direction.here));

      if (enlightened)
      {
        _generators.pop();
      }
    }
  }

  private class Settler extends NestContext
  {
    int _dropCount = 0;
    boolean _haveFood = false;
    List<Integer> _dropHistory = new ArrayList<Integer>();
    double MIN_DF = 0.50;
    int dfWindow = 70;
    boolean _returning = false;

    private void ReturnToNest()
    {
      if (atOurRelativeNest())
      {
        _haveFood = false;
        _orientation = getNeighborWithMaxFoodPheromones();
        _actionStack.push(new DropFood(Direction.here));
        
        _dropCount++;
        if (_curSquare.isNest())
        {
          _globalKnowledge.getWalkBackToNest().clear();
        }
        _walkHistory.clear();
        return;
      }

      Direction homeDir = _onStar.advise(_globalKnowledge.getPosition(), _absoluteNestLocation, _globalKnowledge.getMap(), _walkHistory);

      _actionStack.push(new Move(homeDir));

      dropFoodPheromones();
      _orientation = homeDir;
    }

    private void FindFoodSource()
    {
      _graffiti = createGraffitiMap();
      Graffiti nestGraffiti = _graffiti.get(Direction.here);

      if (_curSquare.hasFood()
          && !atOurRelativeNest()
          && !_curSquare.isNest()
          && !nestGraffiti.isSettlerNest)
      {
        _haveFood = true;
        if (!atOurRelativeNest()) dropFoodPheromones();
        _actionStack.push(new GetFood(Direction.here));
        _walkHistory.clear();
        return;
      }

      Direction foodDir = getMaxLocationTowardsFood();

      _actionStack.push(new Move(foodDir));

      if (!foodDir.equals(Direction.here))
      {
        _orientation = foodDir;
      }
    }

    // some computation based on drop history
    double getDf()
    {
      int window = dfWindow * _generators.size();
      if (_dropHistory.size() < window) return 1.0;
      double sum = 0;
      int startIndex = _dropHistory.size() - window;
      int q = _dropHistory.get(_dropHistory.size() - window);
      for (int i = _dropHistory.size() - window; i < _dropHistory.size(); i++)
      {
        // reward more recent finds of food
        sum += (_dropHistory.get(i) - q); // * (i - startIndex);
      }
      //System.out.println(window + " " + Math.abs(sum / window));
      return Math.abs(sum / window);
    }

    @Override
    public void act(Environment environment, List<WorldEvent> events)
    {
      try
      {
        _graffiti = createGraffitiMap();
        _curSquare = _environment.getSquare(Direction.here);

        if (_returning || getDf() < MIN_DF || shutdown())
        {
          if (!_returning)
          {
           // resetNestPath();
            _walkHistory.clear();
          }

          _returning = true;

          // we need to return to the origin so that the subsequent nest context starts where our nest is
          if (!_relPos.atOrigin())
          {
            _orientation = DirectionHelper.getOppositeDirection(DirectionHelper.getCoordDirection(Coord.ORIGIN));

//            log("returning home before reverting: " + _relPos.DistanceFromNest() + " addr: " + _relPos + " rhc: " + _returnHomeCount);
            ReturnToNest();
            //log("moving " + ((Move)_actionStack.peek()).getDirection().name());
            return;
          }

          _returning = false;

          log("generator count: " + _generators.size());
          if (_generators.size() == 1)
          {
            _dropHistory.clear();

            if (!shutdown())
            {
              _generators.push(new ReverseSettler());
              _generators.push(new Pioneer());
              _generators.push(new Neo());
            }
          }
          else
          {
            _generators.pop();
          }

          return;
        }

        if (_haveFood)
        {
          ReturnToNest();
        }
        else
        {
          FindFoodSource();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        _dropHistory.add(_dropCount);
      }
    }
  }
}
