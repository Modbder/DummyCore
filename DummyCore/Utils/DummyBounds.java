package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.2
 * @Description Used to check, if the float is inbetween 2 others.
 */
public class DummyBounds {
	private final float min;
	private final float max;
	
	public DummyBounds(float m1, float m2)
	{
		min = m1;
		max = m2;
	}
	
	public boolean isInRange(float f)
	{
		return f > min && f < max;
	}

}
