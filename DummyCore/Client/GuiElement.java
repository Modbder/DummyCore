package DummyCore.Client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public abstract class GuiElement {
	int zLevel = 0;
	
	public abstract ResourceLocation getElementTexture();

	public abstract void draw(int posX, int posY);
	
	public abstract int getX();
	
	public abstract int getY();
	
    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(p_73729_1_ + 0, p_73729_2_ + p_73729_6_, this.zLevel, (p_73729_3_ + 0) * f, (p_73729_4_ + p_73729_6_) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(p_73729_1_ + p_73729_5_, p_73729_2_ + p_73729_6_, this.zLevel, (p_73729_3_ + p_73729_5_) * f, (p_73729_4_ + p_73729_6_) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(p_73729_1_ + p_73729_5_, p_73729_2_ + 0, this.zLevel, (p_73729_3_ + p_73729_5_) * f, (p_73729_4_ + 0) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(p_73729_1_ + 0, p_73729_2_ + 0, this.zLevel, (p_73729_3_ + 0) * f, (p_73729_4_ + 0) * f1);
        tessellator.draw();
    }

    public void drawTexturedModelRectFromIcon(int p_94065_1_, int p_94065_2_, Icon p_94065_3_, int p_94065_4_, int p_94065_5_)
    {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(p_94065_1_ + 0, p_94065_2_ + p_94065_5_, this.zLevel, p_94065_3_.getMinU(), p_94065_3_.getMaxV());
        tessellator.getWorldRenderer().addVertexWithUV(p_94065_1_ + p_94065_4_, p_94065_2_ + p_94065_5_, this.zLevel, p_94065_3_.getMaxU(), p_94065_3_.getMaxV());
        tessellator.getWorldRenderer().addVertexWithUV(p_94065_1_ + p_94065_4_, p_94065_2_ + 0, this.zLevel, p_94065_3_.getMaxU(), p_94065_3_.getMinV());
        tessellator.getWorldRenderer().addVertexWithUV(p_94065_1_ + 0, p_94065_2_ + 0, this.zLevel, p_94065_3_.getMinU(), p_94065_3_.getMinV());
        tessellator.draw();
    }

    public static void func_146110_a(int p_146110_0_, int p_146110_1_, float p_146110_2_, float p_146110_3_, int p_146110_4_, int p_146110_5_, float p_146110_6_, float p_146110_7_)
    {
        float f4 = 1.0F / p_146110_6_;
        float f5 = 1.0F / p_146110_7_;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(p_146110_0_, p_146110_1_ + p_146110_5_, 0.0D, p_146110_2_ * f4, (p_146110_3_ + p_146110_5_) * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_146110_0_ + p_146110_4_, p_146110_1_ + p_146110_5_, 0.0D, (p_146110_2_ + p_146110_4_) * f4, (p_146110_3_ + p_146110_5_) * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_146110_0_ + p_146110_4_, p_146110_1_, 0.0D, (p_146110_2_ + p_146110_4_) * f4, p_146110_3_ * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_146110_0_, p_146110_1_, 0.0D, p_146110_2_ * f4, p_146110_3_ * f5);
        tessellator.draw();
    }

    public static void func_152125_a(int p_152125_0_, int p_152125_1_, float p_152125_2_, float p_152125_3_, int p_152125_4_, int p_152125_5_, int p_152125_6_, int p_152125_7_, float p_152125_8_, float p_152125_9_)
    {
        float f4 = 1.0F / p_152125_8_;
        float f5 = 1.0F / p_152125_9_;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(p_152125_0_, p_152125_1_ + p_152125_7_, 0.0D, p_152125_2_ * f4, (p_152125_3_ + p_152125_5_) * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_152125_0_ + p_152125_6_, p_152125_1_ + p_152125_7_, 0.0D, (p_152125_2_ + p_152125_4_) * f4, (p_152125_3_ + p_152125_5_) * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_152125_0_ + p_152125_6_, p_152125_1_, 0.0D, (p_152125_2_ + p_152125_4_) * f4, p_152125_3_ * f5);
        tessellator.getWorldRenderer().addVertexWithUV(p_152125_0_, p_152125_1_, 0.0D, p_152125_2_ * f4, p_152125_3_ * f5);
        tessellator.draw();
    }

}
