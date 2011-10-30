package amaitjo.brains.bt.writing;


import java.math.BigInteger;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Random;
import sun.security.util.BigInt;


public class BitMap
{
  public interface BitMapHandler
  {
    public void read(BitSet bits);
    public BitSet writer();
  }

  public static void read(LinkedHashMap<Integer, BitMapHandler> handlers, Long graffiti)
  {
    int startIndex = 0;

    BitSet data = valueOf(graffiti);

    for (Integer stopIndex : handlers.keySet())
    {
      BitSet rangeData = data.get(startIndex, stopIndex);
      handlers.get(stopIndex).read(rangeData);
      startIndex = stopIndex;
    }
  }

  public static BitSet write(LinkedHashMap<Integer, BitMapHandler> handlers)
  {
    int startIndex = 0;

    BitSet data = new BitSet(Long.SIZE);

    for (Integer stopIndex : handlers.keySet())
    {
      BitSet rangeData = handlers.get(stopIndex).writer();
      for (int i = startIndex, j = 0; i < stopIndex; i++, j++)
      {
        data.set(i, rangeData.get(j));
      }
      startIndex = stopIndex;
    }

    return data;
  }

  public static BitSet _valueOf(Long value)
  {
    BitSet bits = new BitSet(Long.SIZE);

    if (value == null) return bits;

    int index = 0;
    while (value != 0L) {
      if ((value & 1) != 0) {
        bits.set(index);
      }
      ++index;
//      value = value >>> 1;
      value = Long.rotateRight(value, 1);
    }
    return bits;
  }

  public static BitSet valueOf(Long value)
  {
    BitSet bits = new BitSet(Long.SIZE);

    if (value == null) return bits;

    BigInteger bigInteger = BigInteger.valueOf(value);

    for (int i = 0; i < 64; i++)
    {
      if (bigInteger.testBit(i)) bits.set(i);
    }

    return bits;
  }

  public static BitSet valueOf(Integer value)
  {
    BitSet bits = new BitSet(Integer.SIZE);

    if (value == null) return bits;

    BigInteger bigInteger = BigInteger.valueOf(value);

    for (int i = 0; i < 32; i++)
    {
      if (bigInteger.testBit(i)) bits.set(i);
    }

    return bits;
  }

  public static BitSet valueOf(Byte value)
  {
    BitSet bits = new BitSet(Byte.SIZE);

    if (value == null) return bits;

    BigInteger bigInteger = BigInteger.valueOf(value);

    for (int i = 0; i < 8; i++)
    {
      if (bigInteger.testBit(i)) bits.set(i);
    }

    return bits;
  }

  public static int toInteger(BitSet bitSet)
  {
    BigInteger bigInteger = BigInteger.valueOf(0L);

    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1))
    {
      bigInteger = bigInteger.setBit(i);
    }

    return bigInteger.intValue();
  }

  public static long toLong(BitSet bitSet)
  {
    BigInteger bigInteger = BigInteger.valueOf(0L);

    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1))
    {
      bigInteger = bigInteger.setBit(i);
    }

    return bigInteger.longValue();
  }

  public static byte toByte(BitSet bitSet)
  {
    BigInteger bigInteger = BigInteger.valueOf(0L);

    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1))
    {
      bigInteger = bigInteger.setBit(i);
    }

    return bigInteger.byteValue();
  }

  public static void UnitTest()
  {
    Random rng = new Random();

    for (int i = 0; i < 1000; i++)
    {
      Long lval = rng.nextLong();
      BitSet set = valueOf(lval);
      assert toLong(set) == lval;

      Integer ival = rng.nextInt();
      BitSet iset = valueOf(ival);
      assert toInteger(iset) == ival;
    }

//    LinkedHashMap<Integer, BitMapHandler> _handlers
  }

/*
  private void runUnitTests()
  {
    Long writing = encodeWriting(10, 0x12345678);
    assert (decodeHomePheromone(writing) == 0x1234);
    assert (decodeFoodPheromone(writing) == 0x5678);
    assert (decodeTurnWritten(writing) == 10);
    assert (getDecayedHomePheromone(writing) <= 0x1234);
    assert (getDecayedFoodPheromone(writing) <= 0x5678);
    writing = encodeFoodPheromone(10, 0xBEEF, writing);
    assert (decodeFoodPheromone(writing) == 0xBEEF);
    writing = encodeHomePheromone(10, 0xDEAD, writing);
    assert (decodeHomePheromone(writing) == 0xDEAD);
    assert (decodePheromones(writing) == ((decodeHomePheromone(writing) << 16) | decodeFoodPheromone(writing)));
    assert (decodeTurnWritten(writing) == 10);
  }
*/
}
