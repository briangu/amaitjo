package amaitjo.brains.bt;


public class EvilOzzy extends Ozzy
{
	public static int SECRET = 0x6666; // ByteBuffer.wrap(EvilAntyGaga.class.getClass().toString().getBytes()).asIntBuffer().get();
	
	@Override
  protected int getSecret()
  {
  	return SECRET;
  }
}
