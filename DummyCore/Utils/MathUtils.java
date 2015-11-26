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
	 * Gets a percentage between current and max and returns an int representing that percentage of the textureSize param
	 * @param current - current value
	 * @param max - max value
	 * @param textureSize - the var to bind the percentage to
	 * @return An int representing the percentage of the textureSize param
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
	 * Simply gets a percentage value
	 * @param current - current value
	 * @param max - max value
	 * @return The percentage of current from max
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
	
	/**
	 * Checks if the given array contains a given object
	 * @param array - the array to search through
	 * @param searched - the object we are searching for
	 * @return true if the array already contains the given object, false otherwise
	 */
	public static boolean arrayContains(Object[] array, Object searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i].equals(searched))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given array contains a given int
	 * @param array - the array to search through
	 * @param searched - the int we are searching for
	 * @return true if the array already contains the given int, false otherwise
	 */
	public static boolean arrayContains(int[] array, int searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == searched)
				return true;
		}
		return false;
	}
	
	/**
	 * Loops through the given array to find the position of the searched param in the array
	 * @param array - the array to search through
	 * @param searched - the int we are searching for
	 * @return Index of the searched param, -1 if the array has no searched param
	 */
	public static int getIntInArray(int[] array, int searched)
	{
		for(int i = 0; i < array.length; ++i)
		{
			if(array[i] == searched)
				return i;
		}
		return -1;
	}
	
	/**
	 * Compares the boolean array if all booleans within it are the same
	 * @param array the array
	 * @return true if all booleans within the array are the same, false otherwise
	 */
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
