package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description Used to work with colors. Requires RGB colors, lesser than 256 to work. Can also convert them to the corresponding Hex value.
 */
public class DummyColor {
	
	private byte[] color = new byte[3];
	
	public DummyColor(byte red, byte green, byte blue)
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
		double aR = color[0]/255D;
		double aG = color[1]/255D;
		double aB = color[2]/255D;
		int aColor = ((int)(aR * 0xff) << 16) + ((int)(aG * 0xff) << 8) + ((int)(aB * 0xff));
		return aColor;
	}

}
