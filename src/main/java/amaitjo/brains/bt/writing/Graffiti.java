package amaitjo.brains.bt.writing;


import java.math.BigInteger;
import java.util.BitSet;
import java.util.LinkedHashMap;


public class Graffiti
{
  public double FOOD_PHEROMONE_DECAY_PER_TURN = MAX_PHEROMONE / 16000.0f;   // % of pheromone lost per turn
  public static int MAX_PHEROMONE = 4095;
  public static int MAX_TURNS = 100000;

  public int Magic;

  public int TurnWritten;
  public int FoodPheromones;

//  public int HomePheromones;
  
  public Boolean isEmpty = false;
  public Boolean isForeign = false;
  public Boolean isSettlerNest = false;

  public Boolean isValid()
  {
    return !isEmpty && !isForeign;
  }

  public enum PheromoneType
  {
    Food,
    Home
  }

  public Graffiti(int magic)
  {
    Magic = magic;
  }

  private double computeFoodPheromoneDecay(int currentTurn)
  {
//    return FOOD_PHEROMONE_DECAY_PER_TURN + ((0.1 - 0.001) * (1 - (Math.sqrt(currentTurn) / Math.sqrt(100000))));
//    return FOOD_PHEROMONE_DECAY_PER_TURN + ((0.1 - 0.001) * ((Math.sqrt(currentTurn) / Math.sqrt(100000))));
//    return 0.001 * ((Math.pow(currentTurn - 50000, 2) / Math.pow(50000, 2)) + 0.0001);
    return FOOD_PHEROMONE_DECAY_PER_TURN;
  }

  private double decodeDecayFactor(int currentTurn)
  {
    double decay = computeFoodPheromoneDecay(currentTurn);
    int age = Math.abs(currentTurn - TurnWritten);
    double scale = Math.max(0, MAX_PHEROMONE - (age * decay)) / MAX_PHEROMONE;
    double factor = scale;
    return factor;
  }

  private double _decodeDecayFactor(int currentTurn)
  {
    double decay = computeFoodPheromoneDecay(currentTurn);
    int age = Math.abs(currentTurn - TurnWritten);
    double scale = MAX_PHEROMONE / (decay * MAX_PHEROMONE);
    double factor = 1.0 - (Math.min(age, scale) / scale);
    return factor;
  }

  public int applyDecay(int value, int currentTurn)
  {
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }

    return (int) Math.max(decodeDecayFactor(currentTurn) * value, 0);
  }

  public Long encodeFoodPheromone(int currentTurn, int pheromone)
  {
    if (pheromone > MAX_PHEROMONE)
    {
      throw new IllegalArgumentException("pheromone must be < 2^10");
    }
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }
    TurnWritten = currentTurn;
    FoodPheromones = pheromone;
//    HomePheromones = getDecayedHomePheromone(currentTurn);
    return write();
  }

  public Long encodeHomePheromone(int currentTurn, int pheromone)
  {
    if (pheromone > MAX_PHEROMONE)
    {
      throw new IllegalArgumentException("pheromone must be < 2^10");
    }
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }
    TurnWritten = currentTurn;
    FoodPheromones = getDecayedFoodPheromone(currentTurn);
//    HomePheromones = pheromone;
    return write();
  }

/*
  public int getcayedPheromone(int currentTurn, PheromoneType type)
  {
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }
    return
        type.equals(PheromoneType.Food)
          ? getDecayedFoodPheromone(currentTurn)
          : getDecayedHomePheromone(currentTurn);
  }
*/

  public int getDecayedFoodPheromone(int currentTurn)
  {
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }
    return applyDecay(FoodPheromones, currentTurn);
  }

/*
  public int getDecayedHomePheromone(int currentTurn)
  {
    if (currentTurn > MAX_TURNS)
    {
      throw new IllegalArgumentException("currentTurn must be < " + MAX_TURNS);
    }
//    return applyDecay(HomePheromones, currentTurn, PheromoneType.Home);
    // USING DECAY SEEMS to really screw up homing back to the nest.
    return HomePheromones;
  }
*/

  public final LinkedHashMap<Integer, BitMap.BitMapHandler> _handlers =
    new LinkedHashMap<Integer, BitMap.BitMapHandler>() {{
      put(17, new BitMap.BitMapHandler()
      {
        @Override
        public void read(BitSet bits)
        {
          TurnWritten = BitMap.toInteger(bits);
        }

        @Override
        public BitSet writer()
        {
          return BitMap.valueOf(TurnWritten);
        }
      });

      put(17+12, new BitMap.BitMapHandler()
      {
        @Override
        public void read(BitSet bits)
        {
          FoodPheromones = BitMap.toInteger(bits);
        }

        @Override
        public BitSet writer()
        {
          return BitMap.valueOf(FoodPheromones);
        }
      });

      put(17+12+8, new BitMap.BitMapHandler()
      {

        @Override
        public void read(BitSet bits)
        {
          byte hmac = BitMap.toByte(bits);
          Byte expectedHmac = calculateHMAC();

          if (!expectedHmac.equals(hmac))
          {
            isForeign = true;

            TurnWritten = 0;
            FoodPheromones = 0;
//            HomePheromones = 0;
            isSettlerNest = false;
          }
        }

        @Override
        public BitSet writer()
        {
          byte calculatedHmac = calculateHMAC();
          return BitMap.valueOf(calculatedHmac);
        }
      });
      
      put(17+12+8+1, new BitMap.BitMapHandler()
      {
        @Override
        public void read(BitSet bits)
        {
          isSettlerNest = BitMap.toInteger(bits) > 0;
        }

        @Override
        public BitSet writer()
        {
          return BitMap.valueOf(isSettlerNest ? 1 : 0);
        }
      });
    }};

  public byte calculateHMAC()
  {
    return calculateHMAC(Magic, TurnWritten);
  }

  public static byte calculateHMAC(int magic, int valA)
  {
    BigInteger bi = BigInteger.valueOf(magic);
    BigInteger bi2 = BigInteger.valueOf(valA);
    BigInteger xor = bi.xor(bi2);

    return xor.byteValue();
  }

  public Long write()
  {
    return BitMap.toLong(BitMap.write(_handlers));
  }

  public static Graffiti fromWriting(int magic, Long writing)
  {
    Graffiti gagaGraffiti = new Graffiti(magic);
    if (writing == null)
    {
      gagaGraffiti.isEmpty = true;
      return gagaGraffiti;
    }
    BitMap.read(gagaGraffiti._handlers, writing);
    return gagaGraffiti;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("TurnWritten: " + TurnWritten + "\n");
    sb.append("FoodPheromones: " + FoodPheromones + "\n");
//    sb.append("HomePheromones: " + HomePheromones + "\n");
    sb.append("isSettlerNest: " + isSettlerNest + "\n");
    sb.append("Magic: " + Magic + "\n");

    return sb.toString();
  }
}
