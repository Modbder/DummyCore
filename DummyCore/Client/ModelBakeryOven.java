package DummyCore.Client;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;

public class ModelBakeryOven {

	public int[] data;
	public int quad = 0;
	public int shadeColor = -1;
	public int uselessInt = 0;
	public EnumFacing workedWith;
	
	public static final ModelBakeryOven instance = new ModelBakeryOven();
	
	public void start(EnumFacing face)
	{
		this.start(face,-1);
	}
	
	public void start(EnumFacing face, int shadeColor)
	{
		workedWith = face;
		quad = 0;
		this.shadeColor = shadeColor;
		data = new int[28];
	}
	
    public float getFaceBrightness(EnumFacing face)
    {
        switch (face.ordinal())
        {
            case 0:
                return 0.5F;
            case 1:
                return 1.0F;
            case 3:
            case 2:
                return 0.8F;
            case 4:
            case 5:
                return 0.6F;
            default:
                return 0.5F;
        }
    }
	
    public int getFaceShadeColor(EnumFacing face)
    {
        float f = getFaceBrightness(face);
        int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }
	
	public void addVertexWithUV(double x, double y, double z, double u, double v)
	{
		int l = quad*7;
		data[l] = Float.floatToRawIntBits((float) x);
		data[l+1] = Float.floatToRawIntBits((float) y);
		data[l+2] = Float.floatToRawIntBits((float) z);
		data[l+3] = shadeColor == -1 ? getFaceShadeColor(workedWith) : shadeColor;
		data[l+4] = Float.floatToRawIntBits((float) u);
		data[l+5] = Float.floatToRawIntBits((float) v);
		data[l+6] = uselessInt;
		if(quad < 3)
			++quad;
	}
	
	public int[] done()
	{
		int[] vertexData = data.clone();
		ForgeHooksClient.fillNormal(vertexData, workedWith);
		return vertexData;
	}
}
