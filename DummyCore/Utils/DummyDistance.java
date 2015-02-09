package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.2
 * @Description Used to store the distance betwenn 2 points.
 */
public class DummyDistance {
	private final float distance;
	
	public DummyDistance()
	{
		distance = 0;
	}
	
	public DummyDistance(Coord3D first, Coord3D second)
	{
		float diffX = MathUtils.getDifference(first.x, second.x);
		float diffY = MathUtils.getDifference(first.y, second.y);
		float diffZ = MathUtils.getDifference(first.z, second.z);
		distance = (float) Math.sqrt(diffX*diffX+diffY*diffY+diffZ*diffZ);
	}
	
	public DummyDistance(Coord2D first, Coord2D second)
	{
		float diffX = MathUtils.getDifference(first.x, second.x);
		float diffZ = MathUtils.getDifference(first.z, second.z);
		distance = (float) Math.sqrt(diffX*diffX+diffZ*diffZ);
	}
	
	public DummyDistance(float first, float second)
	{
		distance = MathUtils.getDifference(first, second);
	}
	
	public float getDistance()
	{
		return this.distance;
	}
}
