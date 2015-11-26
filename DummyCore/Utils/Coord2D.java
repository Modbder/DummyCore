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
	
	/**
	 * Creates a 2d coordinate for 2 given vars
	 * @param i - x
	 * @param j - z
	 */
	public Coord2D(float i, float j)
	{
		this.x = i;
		this.z = j;
	}
	
	/**
	 * Creates a 0,0 2d coordinate
	 */
	public Coord2D()
	{
		this(0,0);
	}
	
	/**
	 * Not only transforms the coord to a string,  but also does it in a way that it can be later parsed by DummyData
	 */
	public String toString()
	{
		return "||x:"+x+"||z:"+z;
	}
	
	/**
	 * Creates a Coord2D object from a valid DummyData string
	 * @param data - the valid DummyData string
	 * @return a newly created object
	 */
	public static Coord2D fromString(String data)
	{
		DummyData[] dt = DataStorage.parseData(data);
		float cX = Float.parseFloat(dt[0].fieldValue);
		float cZ = Float.parseFloat(dt[1].fieldValue);
		return new Coord2D(cX,cZ);
	}
	
	public boolean equals(Object obj)
	{
		return obj instanceof Coord2D ? x == ((Coord2D)obj).x && z == ((Coord2D)obj).z : super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Float.hashCode(x) + Float.hashCode(z)^3;
	}
}
