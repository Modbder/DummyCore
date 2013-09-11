package DummyCore.Utils;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import DummyCore.Core.Core;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.src.FMLRenderAccessLibrary;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class RendererColoredLight extends Render{

	private RenderBlocks blockRenderer = new RenderBlocks();
	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {	
		if(entity instanceof ColoredLightHandler)
		{
			entity = (ColoredLightHandler)entity;
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        GL11.glEnable(GL11.GL_CULL_FACE);
	        GL11.glDepthMask(false);
	        GL11.glPushMatrix();
	        blockRenderer.blockAccess = entity.worldObj; 
	        EnumLightColor lightColor = EnumLightColor.WHITE;
	        float lightSize = 1.0F;
	        Coord3D ePos = new Coord3D(entity.posX,entity.posY,entity.posZ);
			Block b = MiscUtils.getBlock(entity.worldObj, (int)entity.posX, (int)entity.posY-1, (int)entity.posZ);
			Block b1 = MiscUtils.getBlock(entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
			if(b != null && Core.lightBlocks.contains(b) && b instanceof BlockEmmitsColoredLight)
			{
				lightColor = ((BlockEmmitsColoredLight)b).getColor(entity.worldObj, (int)entity.posX, (int)entity.posY-1, (int)entity.posZ);
				lightSize = ((BlockEmmitsColoredLight)b).getLightBrightness(entity.worldObj, (int)entity.posX, (int)entity.posY-1, (int)entity.posZ);
			}
			if(b1 != null && Core.lightBlocks.contains(b1) && b1 instanceof BlockEmmitsColoredLight)
			{
				lightColor = ((BlockEmmitsColoredLight)b1).getColor(entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
				lightSize = ((BlockEmmitsColoredLight)b1).getLightBrightness(entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ);
			}
	        for(int x = -8; x < 8; ++x)
	        {
	            for(int y = -8; y < 8; ++y)
	            {
	                for(int z = -8; z < 8; ++z)
	                {
	                	Coord3D bPos = new Coord3D(entity.posX+x,entity.posY+y,entity.posZ+z);
	                	float posX = (bPos.x - ePos.x);
	                	float posY = (bPos.y - ePos.y);
	                	float posZ = (bPos.z - ePos.z);
	                	posX = (float) Math.sqrt(posX*posX);
	                	posY = (float) Math.sqrt(posY*posY);
	                	posZ = (float) Math.sqrt(posZ*posZ);
	                	float distance = (posX+posY+posZ)/3;
	                	distance = (float) Math.sqrt(distance*distance);
	                	float brightness = (lightSize/16) - (distance/4);
	                	if(brightness<0)brightness=0;
	                	//if(brightness > 0.6F) System.out.println(distance);
	                	Block b11 = MiscUtils.getBlock(entity.worldObj, (int)entity.posX+x, (int)entity.posY+y-1, (int)entity.posZ+z);
	                	if(b11 != null)
	                		renderCorrectLight(d0+x+0.5D,d1+y-0.5D,d2+z+0.5D,brightness,entity.worldObj,b11,lightColor);
	                }
	            }
	        }
	        GL11.glPopMatrix();
	        GL11.glDepthMask(true);
	        GL11.glDisable(GL11.GL_CULL_FACE);
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glShadeModel(GL11.GL_FLAT);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		// TODO Auto-generated method stub
		return new ResourceLocation("dummycore","textures/misc/lightmap.png");
	}
	
    protected void renderCorrectLight(double par2, double par4, double par6, float brightness,IBlockAccess w,Block block, EnumLightColor color)
    {
    	Tessellator var3 = Tessellator.instance;
        RenderHelper.disableStandardItemLighting();
        float var4 = 1;
        float var5 = 0.0F;
        float stability = 1;
        int mru = 20000;
        GL11.glPushMatrix();
        GL11.glTranslated(par2-0.5D, par4, par6-0.5D);
        GL11.glScalef(1.0015F, 1.0015F, 1.0015F);
        //blockRenderer.renderBlockAllFaces(Block.glass, (int)par2, (int)par4, (int)par6);
        blockRenderer.useInventoryTint = true;
        if(brightness > 0)
        	this.renderBlockAsItem(block, 0, brightness,color);
        GL11.glPopMatrix();
        RenderHelper.enableStandardItemLighting();
    }
    
    public void renderBlockAsItem(Block par1Block, int par2, float par3, EnumLightColor color)
    {
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;

        if (par1Block == Block.dispenser || par1Block == Block.dropper || par1Block == Block.furnaceIdle)
        {
            par2 = 3;
        }

        int j;
        float f1 = color.getColor().getRed();
        float f2 = color.getColor().getGreen();
        float f3 = color.getColor().getBlue();

        if (blockRenderer.useInventoryTint)
        {
            f1 = f1 / 255.0F;
            f2 = f2 / 255.0F;
            f3 = f3 / 255.0F;
            GL11.glColor4f(f1 * par3, f2 * par3, f3 * par3, par3);
        }

        j = par1Block.getRenderType();
        blockRenderer.setRenderBoundsFromBlock(par1Block);
        int k;

        if (j != 0 && j != 31 && j != 39 && j != 16 && j != 26)
        {
            if (j == 1)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                blockRenderer.drawCrossedSquares(par1Block, par2, -0.5D, -0.5D, -0.5D, 1.0F);
                tessellator.draw();
            }
            else if (j == 19)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                par1Block.setBlockBoundsForItemRender();
                blockRenderer.renderBlockStemSmall(par1Block, par2, blockRenderer.renderMaxY, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 23)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                par1Block.setBlockBoundsForItemRender();
                tessellator.draw();
            }
            else if (j == 13)
            {
                par1Block.setBlockBoundsForItemRender();
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                f1 = 0.0625F;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 0));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 1));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addTranslation(0.0F, 0.0F, f1);
                blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 2));
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addTranslation(0.0F, 0.0F, -f1);
                blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 3));
                tessellator.addTranslation(0.0F, 0.0F, f1);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 4));
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addTranslation(-f1, 0.0F, 0.0F);
                blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 5));
                tessellator.addTranslation(f1, 0.0F, 0.0F);
                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 22)
            {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                ChestItemRenderHelper.instance.renderChest(par1Block, par2, par3);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            }
            else if (j == 6)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                blockRenderer.renderBlockCropsImpl(par1Block, par2, -0.5D, -0.5D, -0.5D);
                tessellator.draw();
            }
            else if (j == 2)
            {
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                blockRenderer.renderTorchAtAngle(par1Block, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D, 0);
                tessellator.draw();
            }
            else if (j == 10)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
                    }

                    if (k == 1)
                    {
                        blockRenderer.setRenderBounds(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 27)
            {
                k = 0;
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                tessellator.startDrawingQuads();

                for (int l = 0; l < 8; ++l)
                {
                    byte b0 = 0;
                    byte b1 = 1;

                    if (l == 0)
                    {
                        b0 = 2;
                    }

                    if (l == 1)
                    {
                        b0 = 3;
                    }

                    if (l == 2)
                    {
                        b0 = 4;
                    }

                    if (l == 3)
                    {
                        b0 = 5;
                        b1 = 2;
                    }

                    if (l == 4)
                    {
                        b0 = 6;
                        b1 = 3;
                    }

                    if (l == 5)
                    {
                        b0 = 7;
                        b1 = 5;
                    }

                    if (l == 6)
                    {
                        b0 = 6;
                        b1 = 2;
                    }

                    if (l == 7)
                    {
                        b0 = 3;
                    }

                    float f4 = (float)b0 / 16.0F;
                    float f5 = 1.0F - (float)k / 16.0F;
                    float f6 = 1.0F - (float)(k + b1) / 16.0F;
                    k += b1;
                    blockRenderer.setRenderBounds((double)(0.5F - f4), (double)f6, (double)(0.5F - f4), (double)(0.5F + f4), (double)f5, (double)(0.5F + f4));
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 0));
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 1));
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 2));
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 3));
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 4));
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 5));
                }

                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 11)
            {
                for (k = 0; k < 4; ++k)
                {
                    f2 = 0.125F;

                    if (k == 0)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), 0.0D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), 0.0D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), (double)(1.0F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(1.0F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    if (k == 3)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), (double)(0.5F - f2 * 3.0F), (double)(-f2 * 2.0F), (double)(0.5F + f2), (double)(0.5F - f2), (double)(1.0F + f2 * 2.0F));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 21)
            {
                for (k = 0; k < 3; ++k)
                {
                    f2 = 0.0625F;

                    if (k == 0)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), 0.30000001192092896D, 0.0D, (double)(0.5F + f2), 1.0D, (double)(f2 * 2.0F));
                    }

                    if (k == 1)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), 0.30000001192092896D, (double)(1.0F - f2 * 2.0F), (double)(0.5F + f2), 1.0D, 1.0D);
                    }

                    f2 = 0.0625F;

                    if (k == 2)
                    {
                        blockRenderer.setRenderBounds((double)(0.5F - f2), 0.5D, 0.0D, (double)(0.5F + f2), (double)(1.0F - f2), 1.0D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 0));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 1));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 3));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 4));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSide(par1Block, 5));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            }
            else if (j == 32)
            {
                for (k = 0; k < 2; ++k)
                {
                    if (k == 0)
                    {
                        blockRenderer.setRenderBounds(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);
                    }

                    if (k == 1)
                    {
                        blockRenderer.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            }
            else if (j == 35)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                blockRenderer.renderBlockAnvilOrient((BlockAnvil)par1Block, 0, 0, 0, par2 << 2, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else if (j == 34)
            {
                for (k = 0; k < 3; ++k)
                {
                    if (k == 0)
                    {
                        blockRenderer.setRenderBounds(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
                        blockRenderer.setOverrideBlockTexture(blockRenderer.getBlockIcon(Block.obsidian));
                    }
                    else if (k == 1)
                    {
                        blockRenderer.setRenderBounds(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.875D, 0.8125D);
                        blockRenderer.setOverrideBlockTexture(blockRenderer.getBlockIcon(Block.beacon));
                    }
                    else if (k == 2)
                    {
                        blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                        blockRenderer.setOverrideBlockTexture(blockRenderer.getBlockIcon(Block.glass));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, -1.0F, 0.0F);
                    blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 1.0F, 0.0F);
                    blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
                    tessellator.draw();
                    tessellator.startDrawingQuads();
                    tessellator.setNormal(1.0F, 0.0F, 0.0F);
                    blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
                    tessellator.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                blockRenderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                blockRenderer.clearOverrideBlockTexture();
            }
            else if (j == 38)
            {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                blockRenderer.renderBlockHopperMetadata((BlockHopper)par1Block, 0, 0, 0, 0, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            else
            {
                FMLRenderAccessLibrary.renderInventoryBlock(blockRenderer, par1Block, par2, j);
            }
        }
        else
        {
            if (j == 16)
            {
                par2 = 1;
            }
            GL11.glColor4f(f1*par3, f2*par3, f3*par3,par3);
            par1Block.setBlockBoundsForItemRender();
            blockRenderer.setRenderBoundsFromBlock(par1Block);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            blockRenderer.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 0, par2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            blockRenderer.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 1, par2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            blockRenderer.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 2, par2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            blockRenderer.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 3, par2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            blockRenderer.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 4, par2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            blockRenderer.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, blockRenderer.getBlockIconFromSideAndMetadata(par1Block, 5, par2));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

}
