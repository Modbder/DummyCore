package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description used to work with 3d coord system. Of course, should be a vec3D, but we are working with minecraft =)
 */
public class Coord3D {
	
	public float x;
	public float y;
	public float z;
	
	public Coord3D(double posX, double posY, double posZ)
	{
		this.x = (float) posX;
		this.y = (float) posY;
		this.z = (float) posZ;
	}
	
	public Coord3D(float i, float k, float j)
	{
		this.x = i;
		this.y = k;
		this.z = j;
	}
	
	public Coord3D()
	{
		this(0,0,0);
	}
	
	public String toString()
	{
		return "||x:"+x+"||y:"+y+"||z:"+z;
	}
	
	public static Coord3D fromString(String data)
	{
		DummyData[] dt = DataStorage.parseData(data);
		float cX = Float.parseFloat(dt[0].fieldValue);
		float cY = Float.parseFloat(dt[1].fieldValue);
		float cZ = Float.parseFloat(dt[2].fieldValue);
		return new Coord3D(cX,cY,cZ);
	}

}
