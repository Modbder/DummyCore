package DummyCore.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PrimitiveUtils {
	
    public static Double[] of(double...object)
    {
    	Double[] ret = new Double[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static double[] back(Double...object)
    {
    	double[] ret = new double[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Float[] of(float...object)
    {
    	Float[] ret = new Float[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static float[] back(Float...object)
    {
    	float[] ret = new float[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Byte[] of(byte...object)
    {
    	Byte[] ret = new Byte[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static byte[] back(Byte...object)
    {
    	byte[] ret = new byte[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Integer[] of(int...object)
    {
    	Integer[] ret = new Integer[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static int[] back(Integer...object)
    {
    	int[] ret = new int[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Boolean[] of(boolean...object)
    {
    	Boolean[] ret = new Boolean[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static boolean[] back(Boolean...object)
    {
    	boolean[] ret = new boolean[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Short[] of(short...object)
    {
    	Short[] ret = new Short[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static short[] back(Short...object)
    {
    	short[] ret = new short[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Long[] of(long...object)
    {
    	Long[] ret = new Long[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static long[] back(Long...object)
    {
    	long[] ret = new long[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static Character[] of(char...object)
    {
    	Character[] ret = new Character[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static char[] back(Character...object)
    {
    	char[] ret = new char[object.length];
    	for(int i = 0; i < ret.length; ++i)
    		ret[i] = object[i];
    	return ret;
    }
    
    public static <T>ArrayList<T> listOf(T[] array)
    {
    	ArrayList<T> ret = new ArrayList<T>(array.length);
    	for(T obj : array)
    		ret.add(obj);
    	return ret;
    }
    
    public static <T>boolean checkArray(T[] array, T object)
    {
    	for(T t : array)
    		if(t != null && t.equals(object))
    			return true;
    	
    	return false;
    }
    
	/**
	 * Checks if 2 strings are equal and null || empty
	 * @param par1 - the first string
	 * @param par2 - the second string
	 * @return True if both strings are empty or null, false otherwise
	 */
	public static boolean checkSameAndNullStrings(String par1, String par2)
	{
		if(par1 == par2)
		{
			if(par1 == null && par2 == null)
				return true;
			else
				if(par1 != null && par2 != null)
					if(par1.isEmpty() && par2.isEmpty())
						return true;
		}
		
		return false;
	}
	
    /**
     * Checks if the class with the given name exists
     * @param className - the name to check for
     * @return true if the class exists, false otherwise
     */
    public static boolean classExists(String className)
    {
    	try
    	{
    		return Class.forName(className) != null;
    	}
    	catch(ClassNotFoundException cnfe)
    	{
    		return false;
    	}
    }
    
    /**
     * Used to check if the given class actually has the named method. Used when working with APIs of different mods(actually not)
     * @param c - the class
     * @param mName - the name of the method
     * @param classes - actual parameters of the method
     * @return true if the given method exist, false if not
     */
    public static boolean classHasMethod(Class<?> c, String mName, Class<?>... classes)
    {
    	try {
			Method m = c.getMethod(mName, classes);
			return m != null;
		} catch (Exception e) {
			return false;
		}
    }
}
