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
	
	/**
	 * Creates a 3d coordinate from 3 given vars
	 * @param posX - x
	 * @param posY - y
	 * @param posZ - z
	 */
	public Coord3D(double posX, double posY, double posZ)
	{
		this.x = (float) posX;
		this.y = (float) posY;
		this.z = (float) posZ;
	}
	
	/**
	 * Creates a 3d coordinate from 3 given vars
	 * @param i - x
	 * @param j - y
	 * @param k - z
	 */
	public Coord3D(float i, float k, float j)
	{
		this.x = i;
		this.y = k;
		this.z = j;
	}
	
	/**
	 * Creates a 0,0,0 3d point
	 */
	public Coord3D()
	{
		this(0,0,0);
	}
	
	/**
	 * Not only transforms the coord to a string,  but also does it in a way that it can be later parsed by DummyData
	 */
	public String toString()
	{
		return "||x:"+x+"||y:"+y+"||z:"+z;
	}
	
	/**
	 * Creates a Coord3D object from a valid DummyData string
	 * @param data - the valid DummyData string
	 * @return a newly created object
	 */
	public static Coord3D fromString(String data)
	{
		DummyData[] dt = DataStorage.parseData(data);
		float cX = Float.parseFloat(dt[0].fieldValue);
		float cY = Float.parseFloat(dt[1].fieldValue);
		float cZ = Float.parseFloat(dt[2].fieldValue);
		return new Coord3D(cX,cY,cZ);
	}

	public boolean equals(Object obj)
	{
		return obj instanceof Coord3D ? x == ((Coord3D)obj).x && y == ((Coord3D)obj).y && z == ((Coord3D)obj).z : super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Float.hashCode(x) + Float.hashCode(y)^3 + Float.hashCode(z)^9;
	}
}
