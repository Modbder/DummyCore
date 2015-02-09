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
		return "||x:"+x+"||z:"+z;
	}
	
	public static Coord2D fromString(String data)
	{
		DummyData[] dt = DataStorage.parseData(data);
		float cX = Float.parseFloat(dt[0].fieldValue);
		float cZ = Float.parseFloat(dt[1].fieldValue);
		return new Coord2D(cX,cZ);
	}

}
