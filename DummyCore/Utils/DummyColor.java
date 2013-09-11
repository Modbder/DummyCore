package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description Used to work with colors. Requires RGB colors, lesser than 256 to work. Can also convert them to the corresponding Hex value.
 */
public class DummyColor {
	
	private int[] color = new int[3];
	
	public DummyColor(int red, int green, int blue)
	{
		color[0] = red;
		color[1] = green;
		color[2] = blue;
	}
	
	public int getRed()
	{
		return color[0];
	}
	
	public int getGreen()
	{
		return color[1];
	}
	
	public int getBlue()
	{
		return color[2];
	}
	
	public int getColorInHex()
	{
		int ret = 0x000000;
		int rHex = MathUtils.convertToHex(getRed());
		int gHex = MathUtils.convertToHex(getGreen());
		int bHex = MathUtils.convertToHex(getBlue());
		rHex *= 10000;
		gHex *= 100;
		ret = rHex+gHex+bHex;
		return ret;
	}

}
