package DummyCore.Utils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import DummyCore.Client.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class DrawUtils {
	
	public static Hashtable<String,ResourceLocation> locTable = new Hashtable<String,ResourceLocation>();
	public static final Random itemRand = new Random();
	
	/**
	 * Used to bind texture from the mod. First string is the mod id, and the second is the texture path.
	 * @version From DummyCore 2.0
	 * @param mod - the in-code modname. always use small letters!
	 * @param texture - path to your thexture.
	 */
	@SideOnly(Side.CLIENT)
	public static void bindTexture(String mod, String texture)
	{
		if(locTable.containsKey(mod+":"+texture))
			Minecraft.getMinecraft().getTextureManager().bindTexture(locTable.get(mod+":"+texture));
		else
		{
			ResourceLocation loc = new ResourceLocation(mod,texture);
			locTable.put(mod+":"+texture, loc);
			Minecraft.getMinecraft().getTextureManager().bindTexture(loc);	
		}
	}
	
    @Deprecated
    public static boolean drawScaledTexturedRect_Items(int x, int y, Icon icon, int width, int height, float zLevel)
    {
    	return drawScaledTexturedRect(x,y,icon,width,height,zLevel);
    }
	
	/**
	 * Used to draw a textured rectangle using the given IIcon
	 * @version From DummyCore 2.0
	 * @param x - the X coordinate on the screen. Should be bound to the ScaledResolution
	 * @param y - the Y coordinate on the screen. Should be bound to the ScaledResolution
	 * @param icon - the icon itself
	 * @param width - the width of your Icon
	 * @param height - the height of your Icon
	 * @param zLevel - the z rendering level on the GUI(depth)
	 * @return
	 */
    @SideOnly(Side.CLIENT)
    public static boolean drawScaledTexturedRect(int x, int y, Icon icon, int width, int height, float zLevel)
    {
        if(icon == null)
            return false;
        
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getWorldRenderer().startDrawingQuads();
		tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + height, zLevel, minU, minV + ((maxV - minV) * height) / 16D);
		tessellator.getWorldRenderer().addVertexWithUV(x + width, y + height, zLevel, minU + ((maxU - minU) * width) / 16D, minV + ((maxV - minV) * height) / 16D);
		tessellator.getWorldRenderer().addVertexWithUV(x + width, y + 0, zLevel, minU + ((maxU - minU) * width) / 16D, minV);
		tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + 0, zLevel, minU, minV);
		tessellator.draw();
		return true;
    }
    
	/**
	 * Used to draw a textured rectangle using the given IIcon
	 * @version From DummyCore 2.0
	 * @param x - the X coordinate on the screen. Should be bound to the ScaledResolution
	 * @param y - the Y coordinate on the screen. Should be bound to the ScaledResolution
	 * @param icon - the icon itself
	 * @param width - the width of your rectangle
	 * @param height - the height of your rectangle
	 * @param zLevel - the z rendering level on the GUI(depth)
	 * @return
	 */
    @SideOnly(Side.CLIENT)
    public static void drawTexture(int x, int y, Icon icon, int width, int height, float zLevel)
    {
        for(int i = 0; i < width; i += 16)
            for(int j = 0; j < height; j += 16)
                drawScaledTexturedRect(x + i, y + j, icon, Math.min(width - i, 16), Math.min(height - j, 16),zLevel);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Deprecated
    public static void drawTexture_Items(int x, int y, Icon icon, int width, int height, float zLevel)
    {
    	drawTexture(x,y,icon,width,height,zLevel);
    }
    
	/**
	 * Actually draws a textured rectangle
	 * @version From DummyCore 2.0
	 * @param x - first vertex U
	 * @param y - first vertex V
	 * @param textureX - second vertex U
	 * @param textureY - second vertex V
	 * @param sizeX - third vertex U
	 * @param sizeY - third vertex V
	 * @param zLevel - the zlevel on the GUI
	 */
    @SideOnly(Side.CLIENT)
    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int sizeX, int sizeY, int zLevel)
    {
        float f = 0.00390625F; //1/256
        float f1 = 0.00390625F; //1/256
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + sizeY, zLevel, (textureX + 0) * f, (textureY + sizeY) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(x + sizeX, y + sizeY, zLevel, (textureX + sizeX) * f, (textureY + sizeY) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(x + sizeX, y + 0, zLevel, (textureX + sizeX) * f, (textureY + 0) * f1);
        tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + 0, zLevel, (textureX + 0) * f, (textureY + 0) * f1);
        tessellator.draw();
    }
    
    /**
     * Renders the given ItemStack in the world. Call ONLY from render methods!
     * @version From DummyCore 2.0
     * @param stk - ItemStack you wish to render
     * @param posX - xCoord in the world
     * @param posY - yCoord in the world
     * @param posZ - zCoord in the world
     * @param screenPosX - x position on the screen(given by render)
     * @param screenPosY - y position on the screen(given by render)
     * @param screenPosZ - z position on the screen(given by render)
     * @param rotation - the X axis rotation
     * @param rotationZ - the Z axis rotation
     * @param colorRed - red color index(0.0F is 0% and 1.0F is 100%)
     * @param colorGreen - green color index(0.0F is 0% and 1.0F is 100%)
     * @param colorBlue - blue color index(0.0F is 0% and 1.0F is 100%)
     * @param offsetX - offset by X
     * @param offsetY - offset by Y
     * @param offsetZ - offset by Z
     * @param force3DRender - should be item rendered in 3d even if the fancy graphics are off?
     */
    @SideOnly(Side.CLIENT)
    public static void renderItemStack_Full(ItemStack stk,double posX, double posY, double posZ, double screenPosX, double screenPosY, double screenPosZ, float rotation, float rotationZ, float colorRed, float colorGreen, float colorBlue, float offsetX, float offsetY, float offsetZ, boolean force3DRender)
    {
    	if(stk == null)
    		return;
    	
        ItemStack itemstack = stk.copy();
        itemstack.stackSize = 0;
        itemRand.setSeed(187L);
        boolean flag = false;
        
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        flag = true;
            

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemstack);
        int i = getISRenderPasses(itemstack, screenPosX, screenPosY, screenPosZ, TimerHijack.mcTimer.renderPartialTicks, ibakedmodel,force3DRender,rotation,rotationZ);

        for (int j = 0; j < i; ++j)
        {
            if (ibakedmodel.isGui3d())
            {
                GlStateManager.pushMatrix();

                if (j > 0)
                {
                    float f2 = (itemRand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f3 = (itemRand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f4 = (itemRand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f2, f3, f4);
                }

                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            }
            else
            {
                // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
                if (j > 0)
                {
                    float f2 = (itemRand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f3 = (itemRand.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f2, f3, 0);
                }
                Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                GlStateManager.translate(0.0F, 0.0F, 0.046875F);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        if (flag)
        	Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
    }
    
    public static int getISRenderPasses(ItemStack itemstack, double screenX, double screenY, double screenZ, float partialTicks, IBakedModel model, boolean fancy, float rotationX, float rotationY)
    {
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        
		boolean flag = model.isGui3d();
		int i = 1;
		GlStateManager.translate((float)screenX, (float)screenY, (float)screenZ);
		float f3;

		if (flag || fancy)
		{
		    GlStateManager.rotate(rotationX, 0.0F, 1.0F, 0.0F);
		    GlStateManager.rotate(rotationY, 1.0F, 0.0F, 0.0F);
		}

		if (!flag)
		{
		    f3 = -0.0F * (i - 1) * 0.5F;
		    float f4 = -0.0F * (i - 1) * 0.5F;
		    float f5 = -0.046875F * (i - 1) * 0.5F;
		    GlStateManager.translate(f3, f4, f5);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		return i;
    }
    

    @Deprecated
    public static void renderItemStack(ItemStack stk,double posX, double posY, double posZ, double screenPosX, double screenPosY, double screenPosZ, float rotation, float colorRed, float colorGreen, float colorBlue, int renderPass, int itemsAmount, boolean force3DRender)
    {
    	renderItemStack_Full(stk,posX,posY,posZ,screenPosX,screenPosY,screenPosZ,rotation,0,colorRed,colorGreen,colorBlue,0,0,0,force3DRender);
    }
    
    @SuppressWarnings("unchecked")
	public static void renderItemStackGUIInformation(ItemStack stk, int x, int y, FontRenderer font, double zLevel, int colorStart, int colorEnd, int colorBorder)
    {
    	List<String> lst = stk.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        if (!lst.isEmpty())
        {
            int k = 0;
            Iterator<String> iterator = lst.iterator();

            while (iterator.hasNext())
            {
                String s = iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (lst.size() > 1)
            {
                i1 += 2 + (lst.size() - 1) * 10;
            }

            if (j2 + k > 256)
            {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > 256)
            {
                k2 = 256 - i1 - 6;
            }

            GL11.glTranslated(0, 0, zLevel);
            int j1 = colorBorder;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1,zLevel);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1,zLevel);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1,zLevel);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1,zLevel);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1,zLevel);
            int k1 = colorStart;
            int l1 = colorEnd;
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1,zLevel);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1,zLevel);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1,zLevel);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1,zLevel);

            for (int i2 = 0; i2 < lst.size(); ++i2)
            {
                String s1 = lst.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                {
                    k2 += 2;
                }

                k2 += 10;
            }

            GL11.glTranslated(0, 0, -zLevel);
        }
        GL11.glColor3f(1, 1, 1);
    }
    
    public static void drawGradientRect(int x, int y, int ex, int ey, int colorStart, int colorEnd, double zLevel)
    {
        float f = (colorStart >> 24 & 255) / 255.0F;
        float f1 = (colorStart >> 16 & 255) / 255.0F;
        float f2 = (colorStart >> 8 & 255) / 255.0F;
        float f3 = (colorStart & 255) / 255.0F;
        float f4 = (colorEnd >> 24 & 255) / 255.0F;
        float f5 = (colorEnd >> 16 & 255) / 255.0F;
        float f6 = (colorEnd >> 8 & 255) / 255.0F;
        float f7 = (colorEnd & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().setColorRGBA_F(f1, f2, f3, f);
        tessellator.getWorldRenderer().addVertex(ex, y, zLevel);
        tessellator.getWorldRenderer().addVertex(x, y, zLevel);
        tessellator.getWorldRenderer().setColorRGBA_F(f5, f6, f7, f4);
        tessellator.getWorldRenderer().addVertex(x, ey, zLevel);
        tessellator.getWorldRenderer().addVertex(ex, ey, zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public static byte getButtonHoverState(GuiButton btn, boolean mouseOver)
    {
        byte b0 = 1;

        if (!btn.enabled)
        {
            b0 = 0;
        }
        else if (mouseOver)
        {
            b0 = 2;
        }

        return b0;
    }
}
