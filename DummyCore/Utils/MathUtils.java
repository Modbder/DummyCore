package DummyCore.Utils;

import java.util.Random;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description can be used to save you some time writing mathematical functions.
 *
 */
public class MathUtils {
	/**
	 * Used to get the size of a texture in pixels using current values. Very useful in any kind of progress bars in GUI
	 * @version From DummyCore 1.0
	 * @param current - the current value
	 * @param max - the maximum value
	 * @param textureSize - the maximum texture size
	 * @return Percentage-based int. For example, if the textureSize is 50, current is 100 and max is 200 this will return 25 - that is 50% of your texture size.
	 */
	public static int pixelatedTextureSize(int current, int max, int textureSize)
	{
		if(current > max)
			current = max;
    	float m = (float)current/max*100;
    	float n = m/100*textureSize;
    	return (int)n;
	}
	
	/**
	 * Used to get the persentage between 2 numbers
	 * @version From DummyCore 1.0
	 * @param current - the current value
	 * @param max - the maximum value
	 * @return Percentage-based int. For example, if current is 100 and max is 200 this will return 50.
	 */
	public static int getPercentage(int current, int max)
	{
    	float m = (float)current/max*100;
    	return (int)m;
	}
	
	/**
	 * Used to get the polar-based offset between 2 points in vec2D coord system.
	 * @version From DummyCore 1.0
	 * @param position - the current vec2D
	 * @param angle - the angle of offset
	 * @param distance - the distance the point will be offset
	 * @return new Coord2D with offset coords.
	 */
	public static Coord2D polarOffset(Coord2D position, float angle, float distance)
	{
		float d0 = (float) (position.x + Math.cos(angle * Math.PI / 180.0D) * distance);
		float d1 = (float) (position.z + Math.sin(angle * Math.PI / 180.0D) * distance);
		return new Coord2D(d0,d1);
	}
	
	/**
	 * Used to get a completely(-1.0D - 1.0D) random double.
	 * @version From DummyCore 1.0
	 * @param rand - the Random, that will randomise this.
	 * @return a random Double
	 */
	public static double randomDouble(Random rand)
	{
		return rand.nextDouble()-rand.nextDouble();
	}
	
	/**
	 * Used to get a completely(-1.0F - 1.0F) random float.
	 * @version From DummyCore 1.0
	 * @param rand - the Random, that will randomise this.
	 * @return a random Float
	 */
	public static float randomFloat(Random rand)
	{
		return rand.nextFloat()-rand.nextFloat();
	}
	
	/**
	 * Used to calculate difference between 2 float.
	 * @version From DummyCore 1.0
	 * @param pos1 - float #1
	 * @param pos2 - float #2
	 * @return always positive value of difference.
	 */
	public static float getDifference(float pos1, float pos2)
	{
		float diff = pos1-pos2;
		return (float)module(diff);
	}
	
	/**
	 * Swaps 2 integers between eachother.
	 * @version From DummyCore 1.1
	 * @param a - first int to swap
	 * @param b - second int to swap
	 * @return int[2] with this 2 values swapped.
	 */
	@Deprecated
	public static int[] swap(int a, int b)
	{
		return new int[]{b,a};
	}
	
	/**
	 * Used to convert deciminal number to the Hexadeciminal.
	 * @version From DummyCore 1.1
	 * @param a - the int to be converted
	 * @return this integer, converted into hexadeciminal
	 */
	public static int convertToHex(int a)
	{
		return Integer.parseInt(Integer.toString(a),16);
	}
	
	/**
	 * Used to get the always positive value of a double.
	 * @version From DummyCore 1.2
	 * @param a - the double to be converted
	 * @return this double, but positive(>0)
	 */
	public static double module(double a)
	{
		if(a < 0) a = -a;
		return a;
	}
	
	public static boolean arrayContains(Object[] array, Object searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i].equals(searched))
				return true;
		}
		return false;
	}
	
	public static boolean arrayContains(int[] array, int searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == searched)
				return true;
		}
		return false;
	}
	
	public static int getIntInArray(int[] array, int searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == searched)
				return i;
		}
		return -1;
	}
	
	public static boolean isArrayTheSame(boolean[] array)
	{
		boolean previous = array[0];
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == previous)
			{
				previous = array[i];
			}else
			{
				return false;
			}
		}
		return true;
	}
}
