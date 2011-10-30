package amaitjo;


import amaitjo.brains.bt.writing.BitMap;
import amaitjo.brains.bt.writing.Graffiti;
import java.util.Random;
import junit.framework.TestCase;


public class TestGraffiti extends TestCase
{
  int _secret = new Random().nextInt();

  private int getSecret()
  {
    return _secret;
  }

  int _region = 0x1;

  public void testTurn()
  {
    for (int i = 0; i < 100000; i++)
    {
      int _turn = i;

      Graffiti graffiti = new Graffiti(getSecret());
//      graffiti.HomePheromones = 456;

      Long writing = graffiti.encodeFoodPheromone(_turn, 789);
      Graffiti g1 = Graffiti.fromWriting(getSecret(), writing);
      assertEquals(g1.TurnWritten, _turn);
      assertEquals(g1.FoodPheromones, 789);
//      assertEquals(g1.HomePheromones, 456);
      assertTrue(g1.getDecayedFoodPheromone(_turn) <= 789);
//      assertTrue(g1.getDecayedHomePheromone(_turn) <= 456);

      graffiti = new Graffiti(getSecret());
      graffiti.FoodPheromones = 789;
      writing = graffiti.encodeHomePheromone(_turn, 456);
      g1 = Graffiti.fromWriting(getSecret(), writing);
      assertEquals(g1.TurnWritten, _turn);
      assertEquals(g1.FoodPheromones, 789);
//      assertEquals(g1.HomePheromones, 456);
      assertTrue(g1.getDecayedFoodPheromone(_turn) <= 789);
//      assertTrue(g1.getDecayedHomePheromone(_turn) <= 456);
    }
  }

  public void testRandomValues()
  {
    Long writing;

    Random rng = new Random();

    for (int i = 0; i < 1000; i++)
    {
      int magic = Math.abs(rng.nextInt());

      Graffiti master = new Graffiti(magic);
      master.TurnWritten = Math.abs(rng.nextInt()) % 100000;
      master.FoodPheromones = Math.abs(rng.nextInt()) % 1024;
//      master.HomePheromones = Math.abs(rng.nextInt()) % 1024;
      master.Magic = magic;

      System.out.println(master);

      {
        Graffiti gagaGraffiti = new Graffiti(magic);
        gagaGraffiti.TurnWritten = master.TurnWritten;
        gagaGraffiti.FoodPheromones = master.FoodPheromones;
//        gagaGraffiti.HomePheromones = master.HomePheromones;
        writing = gagaGraffiti.write();
      }

      {
        Graffiti gagaGraffiti = new Graffiti(magic);
        BitMap.read(gagaGraffiti._handlers, writing);

        System.out.println(gagaGraffiti);

        assert gagaGraffiti.TurnWritten == master.TurnWritten;
        assert gagaGraffiti.FoodPheromones == master.FoodPheromones;
//        assert gagaGraffiti.HomePheromones == master.HomePheromones;
        assert gagaGraffiti.Magic == master.Magic;
      }
    }
  }
}
