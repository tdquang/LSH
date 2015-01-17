import java.math.BigInteger;
import java.util.Random;


public class HashFunction 
{
	private int a;
	private int b;
	private int c;
	
	public HashFunction(Random random, int c)
	{
		a=relativelyPrime(c, random);
		b=random.nextInt(c);
		this.c=c;
	}
	
	public int applyHashFunction(int x)
	{
		return (a*x+b)%c;
	}
	
	private static int gcd(int a, int b) 
	{
	    BigInteger b1 = new BigInteger(""+a); 
	    BigInteger b2 = new BigInteger(""+b);
	    BigInteger gcd = b1.gcd(b2);
	    return gcd.intValue();
	}
	private int relativelyPrime(int c, Random random)
	{
		int canidate=random.nextInt(c);
		if(gcd(canidate, c)!=1)
		{
			return canidate;
		}
		else
		{
			return relativelyPrime(c, random);
		}
	}

}
