package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description Used to work with colors. Requires RGB colors, lesser than 256 to work. Can also convert them to the corresponding Hex value.
 */
public class DummyColor {
	
	private int[] color = new int[3];
	
	public DummyColor(int hex)
	{
		int r = (int) ((double)((hex & 0xFF0000) >> 16) / 0xff * 255);
		int g = (int) ((double)((hex & 0xFF00) >> 8) / 0xff * 255);
		int b = (int) ((double)((hex & 0xFF)) / 0xff * 255);
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
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
	
	public float getRedF()
	{
		return (float)color[0]/255F;
	}
	
	public float getGreenF()
	{
		return (float)color[1]/255F;
	}
	
	public float getBlueF()
	{
		return (float)color[2]/255F;
	}
	
	public double getRedD()
	{
		return getRedF();
	}
	
	public double getGreenD()
	{
		return getGreenF();
	}
	
	public double getBlueD()
	{
		return getBlueF();
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
