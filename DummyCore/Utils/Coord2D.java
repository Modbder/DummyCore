package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description used to work with 2d coord system. Of course, should be a vec2D, but we are working with minecraft =)
 */
public class Coord2D {
	
	public float x;
	public float z;
	
	public Coord2D(float i, float j)
	{
		this.x = i;
		this.z = j;
	}
	
	public Coord2D()
	{
		this(0,0);
	}
	
	public String toString()
	{
		return x+","+z;
	}

}
