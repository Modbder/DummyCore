package DummyCore.Utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TessellatorWrapper {
	
	public static final TessellatorWrapper instance = new TessellatorWrapper();
	
	public void startDrawing(int mode)
	{
		Tessellator.getInstance().getWorldRenderer().begin(mode, DefaultVertexFormats.POSITION_TEX);
	}
	
	public void startDrawingQuads()
	{
		startDrawing(7);
	}
	
	public void addVertexWithUV(double x, double y, double z, double u, double v)
	{
		Tessellator.getInstance().getWorldRenderer().pos(x, y, z).tex(u, v).endVertex();
	}
	
	public void addVertex(double x, double y, double z)
	{
		Tessellator.getInstance().getWorldRenderer().pos(x, y, z).endVertex();
	}
	
	public void draw()
	{
		Tessellator.getInstance().draw();
	}
	
	public static TessellatorWrapper getInstance()
	{
		return instance;
	}
	
	public void setColorRGBA_F(float r, float g, float b, float a)
	{
		Tessellator.getInstance().getWorldRenderer().color(r,g,b,a);
	}
	
	public void setColorOpaque_I(int color)
	{
	    float cR = (float)((color & 0xFF0000) >> 16) / 0xff;
	    float cG = (float)((color & 0xFF00) >> 8) / 0xff;
	    float cB = (float)((color & 0xFF)) / 0xff;
		
		Tessellator.getInstance().getWorldRenderer().color(cR,cG,cB,1);
	}
	
	public void setColorRGBA_I(int color, int alpha)
	{
	    float cR = (float)((color & 0xFF0000) >> 16) / 0xff;
	    float cG = (float)((color & 0xFF00) >> 8) / 0xff;
	    float cB = (float)((color & 0xFF)) / 0xff;
		
		Tessellator.getInstance().getWorldRenderer().color(cR,cG,cB,(float)alpha/255);
	}
	
	public void setTranslation(double x, double y, double z)
	{
		Tessellator.getInstance().getWorldRenderer().putPosition(x, y, z);
	}
}
