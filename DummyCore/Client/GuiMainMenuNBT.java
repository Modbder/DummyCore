package DummyCore.Client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import DummyCore.Utils.Coord2D;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.GIFImage;
import DummyCore.Utils.IMainMenu;
import DummyCore.Utils.Notifier;
import DummyCore.Utils.Pair;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class GuiMainMenuNBT extends GuiMainMenu implements IMainMenu{
	
	public static final Hashtable<Integer,NBTTagCompound> idToTagMapping = new Hashtable<Integer,NBTTagCompound>();
	public static final Random rand = new Random();
	public ArrayList<Pair<Integer,ResourceLocation>> menuTextures = new ArrayList<Pair<Integer,ResourceLocation>>();
	public ArrayList<Integer> overlayTimeList = new ArrayList<Integer>();
	public ArrayList<ResourceLocation> music = new ArrayList<ResourceLocation>();
	//-1 - invalid
	//0 - default texture scaled to the size
	//1 - 6 images(panorama)
	//2 - 1 texture rendered across the screen
	//3 - n textures with frame times
	//4 - 2 swapping textures
	//5 - gif image(unfinished)
	public int menuType;
	public ResourceLocation menuTexture = null;
	public ResourceLocation customCursorTexture = null;
	public boolean gif;
	public ArrayList<AbstractGUIObject> objects = new ArrayList<AbstractGUIObject>();
	public int tickTime;
	public float overlayTickTime;
	public int panoramaTimer = 0;
	public DynamicTexture viewportTexture;
	public ScaledResolution mcRes;
	public boolean hasGradient = false;
	public int gradientColorStart,gradientColorEnd;
	public int textureRepeat = 10;
	public int textureIndex = 0;
	public int currentGifFrame;
	public int gifTick;
	public GIFImage gifImage;
	public URI clickedURI;
	
	public List<String> textLeft = new ArrayList<String>();
	public List<String> textRight = new ArrayList<String>();
	
    protected void actionPerformed(GuiButton button)
    {
    	super.actionPerformed(button);
    	try
    	{
	    	if(button instanceof CustomButton)
	    	{
	    		CustomButton cb = CustomButton.class.cast(button);
	    		if(cb.url != null)
	    		{
	                if (this.mc.gameSettings.chatLinksPrompt)
	                {
	                    this.clickedURI = new URI(cb.url.toString());
	                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickedURI.toString(), button.id, false));
	                }
	                else
	                {
	                    this.openURI(new URI(cb.url.toString()));
	                }
	    		}
	    	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void openURI(URI uri)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
            clickedURI = null;
        }
        catch (Throwable throwable)
        {
            Notifier.notifyError("Couldn\'t open link");
        }
    }
	
    @SuppressWarnings("unchecked")
	public void confirmClicked(boolean yes, int id)
    {
    	super.confirmClicked(yes, id);
    	for(GuiButton btn : (List<GuiButton>)this.buttonList)
    		if(btn.id == id && btn instanceof CustomButton && CustomButton.class.cast(btn).url != null)
    		{
    			if(yes)
    				this.openURI(clickedURI);
    			
    			this.mc.displayGuiScreen(this);
    			break;
    		}
    }
    
    public void updateScreen()
    {
    	for(int i = 0; i < objects.size(); ++i)
    		objects.get(i).guiTick();
    	
        ++this.panoramaTimer;
        if(menuType == 5)
        {
        	++gifTick;
        	if(gifTick >= overlayTimeList.get(currentGifFrame))
        	{
        		if(currentGifFrame+1 >= overlayTimeList.size())
        		{
        			gifTick = 0;
        			currentGifFrame = 0;
        		}else
        		{
        			++currentGifFrame;
        			gifTick = 0;
        		}
        	}
        }
        if(menuType == 3 || menuType == 4)
        {
	        ++tickTime;
	        if(!menuTextures.isEmpty())
	        {
	        	int maxTicks = menuTextures.get(textureIndex).getFirst() + (menuType == 3 ? 0 : overlayTimeList.get(textureIndex));
	        	if(tickTime >= maxTicks)
	        	{
	        		if(textureIndex+1 >= menuTextures.size())
	        		{
	        			textureIndex = 0;
	        			tickTime = 0;
	        			overlayTickTime = 0;
	        		}else
	        		{
	        			++textureIndex;
	        			tickTime = 0;
	        			overlayTickTime = 0;
	        		}
	        	}
	        	if(tickTime >= maxTicks-overlayTimeList.get(textureIndex))
	        	{
	        		++overlayTickTime;
	        	}
	        }
        }
    }
    
    @SuppressWarnings("unchecked")
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	GL11.glPushMatrix();
    	GL11.glEnable(GL11.GL_ALPHA_TEST);
    	if(menuType == 0)
    	{
	    	mc.renderEngine.bindTexture(menuTexture);
	    	Tessellator tec = Tessellator.instance;
	    	tec.startDrawingQuads();
	    	tec.addVertexWithUV(0, 0, zLevel, 0, 0);
	    	tec.addVertexWithUV(0, mcRes.getScaledHeight_double(), zLevel, 0, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), mcRes.getScaledHeight_double(), zLevel, 1, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), 0, zLevel, 1, 0);
	    	tec.draw();
    	}
    	if(menuType == 1)
    	{
    		GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            this.renderSkybox(mouseX, mouseY, partialTicks);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glColor4d(1, 1, 1, 1);
            GL11.glPopMatrix();
    	}
    	if(menuType == 2)
    		this.drawBackground(textureRepeat);
    	if(menuType == 3)
    	{
	    	mc.renderEngine.bindTexture(menuTextures.get(textureIndex).getSecond());
	    	Tessellator tec = Tessellator.instance;
	    	tec.startDrawingQuads();
	    	tec.addVertexWithUV(0, 0, zLevel, 0, 0);
	    	tec.addVertexWithUV(0, mcRes.getScaledHeight_double(), zLevel, 0, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), mcRes.getScaledHeight_double(), zLevel, 1, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), 0, zLevel, 1, 0);
	    	tec.draw();
    	}
    	if(menuType == 4)
    	{
    		int maxTicks = menuTextures.get(textureIndex).getFirst();
	    	mc.renderEngine.bindTexture(menuTextures.get(textureIndex).getSecond());
	    	Tessellator tec = Tessellator.instance;
	    	tec.startDrawingQuads();
	    	tec.addVertexWithUV(0, 0, zLevel, 0, 0);
	    	tec.addVertexWithUV(0, mcRes.getScaledHeight_double(), zLevel, 0, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), mcRes.getScaledHeight_double(), zLevel, 1, 1);
	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), 0, zLevel, 1, 0);
	    	tec.draw();
        	if(tickTime >= maxTicks)
        	{
        		GL11.glDisable(GL11.GL_ALPHA_TEST);
        		GL11.glEnable(GL11.GL_BLEND);
        		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        		
    	    	mc.renderEngine.bindTexture(menuTextures.get(textureIndex +1 >= menuTextures.size() ? 0 : textureIndex+1).getSecond());
    	    	float maxFadeIndex = overlayTimeList.get(textureIndex);
    	    	float currentFadeIndex = (overlayTickTime + partialTicks) / maxFadeIndex;
    	    	zLevel += 10;
    	    	tec.startDrawingQuads();
    	    	tec.setColorRGBA_F(1, 1, 1, currentFadeIndex);
    	    	tec.addVertexWithUV(0, 0, zLevel, 0, 0);
    	    	tec.addVertexWithUV(0, mcRes.getScaledHeight_double(), zLevel, 0, 1);
    	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), mcRes.getScaledHeight_double(), zLevel, 1, 1);
    	    	tec.addVertexWithUV(mcRes.getScaledWidth_double(), 0, zLevel, 1, 0);
    	    	tec.draw();
    	    	tec.setColorRGBA_F(1, 1, 1, 1);
    	    	GL11.glColor4d(1, 1, 1, 1);
    	    	zLevel -= 10;
        		GL11.glDisable(GL11.GL_BLEND);
        		GL11.glEnable(GL11.GL_ALPHA_TEST);
        	}
    	}
    	if(menuType == 5)
    	{
    		GL11.glPushMatrix();

    		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    		GL11.glDisable(GL11.GL_ALPHA_TEST);
    		GL11.glEnable(GL11.GL_BLEND);
    		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    		
    		gifImage.drawOnScreen(currentGifFrame, 0, 0, 1, 1, mcRes.getScaledWidth(), mcRes.getScaledHeight());
    		
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
    		GL11.glPopMatrix();
    	}
    	
    	if(this.hasGradient)
    	{
    		this.zLevel += 10;
    		this.drawGradientRect(0, 0, this.width, this.height, this.gradientColorStart, this.gradientColorEnd);
    		this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
    		this.zLevel -= 10;
    	}
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	
        for (int i = 0; i < this.textLeft.size(); i++)
        {
            String brd = textLeft.get(i);
            if (!Strings.isNullOrEmpty(brd))
                this.drawString(this.fontRendererObj, brd, 2, this.height - ( 10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        for (int i = 0; i < this.textRight.size(); i++)
        {
            String brd = textRight.get(i);
            if (!Strings.isNullOrEmpty(brd))
                this.drawString(this.fontRendererObj, brd, this.width - this.fontRendererObj.getStringWidth(brd) - 2, this.height - ( 10 + i * (this.fontRendererObj.FONT_HEIGHT + 1)), 16777215);
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        ForgeHooksClient.renderMainMenu(this, fontRendererObj, width, height);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
    	for(AbstractGUIObject obj : objects)
    		obj.drawOnScreen(mouseX, mouseY, partialTicks,zLevel+10);
    	
    	for(GuiButton button : (ArrayList<GuiButton>)this.buttonList)
    		if(!objects.contains(button))
    		{
    			GL11.glTranslated(0, 0, 20);
    			button.drawButton(mc, mouseX, mouseY);
    			GL11.glTranslated(0, 0, -20);
    		}
    	
    	for(GuiLabel label : (ArrayList<GuiLabel>)this.labelList)
    		if(!objects.contains(label))
    		{
    			GL11.glTranslated(0, 0, 20);
    			label.func_146159_a(mc, mouseX, mouseY);
    			GL11.glTranslated(0, 0, -20);
    		}
    	GL11.glPopMatrix();
    }
    
    public void drawBackground(int repeat)
    {
    	boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
    	boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(menuTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(0.0D, (double)this.height, 0.0D, 0.0D, (double)((float)this.height / f + (float)repeat));
        tessellator.addVertexWithUV((double)this.width, (double)this.height, 0.0D, (double)((float)this.width / f), (double)((float)this.height / f + (float)repeat));
        tessellator.addVertexWithUV((double)this.width, 0.0D, 0.0D, (double)((float)this.width / f), (double)repeat);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)repeat);
        tessellator.draw();
        if(lighting)
        	GL11.glEnable(GL11.GL_LIGHTING);
        if(fog)
        	GL11.glEnable(GL11.GL_FOG);
    }
    
    public void drawPanorama(int mouseX, int mouseY, float partialTicks)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        byte b0 = 8;

        for (int k = 0; k < b0 * b0; ++k)
        {
            GL11.glPushMatrix();
            float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
            float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
            float f3 = 0.0F;
            GL11.glTranslatef(f1, f2, f3);
            GL11.glRotatef(MathHelper.sin(((float)this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-((float)this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l)
            {
                GL11.glPushMatrix();

                if (l == 1)
                    GL11.glRotated(90, 0, 1, 0);

                if (l == 2)
                    GL11.glRotated(180, 0, 1, 0);

                if (l == 3)
                    GL11.glRotated(-90, 0, 1, 0);

                if (l == 4)
                    GL11.glRotated(90, 1, 0, 0);

                if (l == 5)
                    GL11.glRotated(-90, 1, 0, 0);

                this.mc.getTextureManager().bindTexture(menuTextures.get(l).getSecond());
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA_I(0xffffff, 255 / (k + 1));
                float f4 = 0.0F;
                tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double)(0.0F + f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, (double)(1.0F - f4), (double)(0.0F + f4));
                tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, (double)(1.0F - f4), (double)(1.0F - f4));
                tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double)(0.0F + f4), (double)(1.0F - f4));
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
	
    public void rotateAndBlurSkybox(float partialTicks)
    {
        this.mc.getTextureManager().bindTexture(menuTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        byte b0 = 3;

        for (int i = 0; i < b0; ++i)
        {
            tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(i + 1));
            int j = this.width;
            int k = this.height;
            float f1 = (float)(i - b0 / 2) / 256.0F;
            tessellator.addVertexWithUV((double)j, (double)k, (double)this.zLevel, (double)(0.0F + f1), 1.0D);
            tessellator.addVertexWithUV((double)j, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 1.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(1.0F + f1), 0.0D);
            tessellator.addVertexWithUV(0.0D, (double)k, (double)this.zLevel, (double)(0.0F + f1), 0.0D);
        }

        tessellator.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColorMask(true, true, true, true);
    }
    
    public void renderSkybox(int mouseX, int mouseY, float partialTicks)
    {
        this.mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(mouseX, mouseY, partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.rotateAndBlurSkybox(partialTicks);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        float f1 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
        float f2 = (float)this.height * f1 / 256.0F;
        float f3 = (float)this.width * f1 / 256.0F;
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.width;
        int l = this.height;
        tessellator.addVertexWithUV(0.0D, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F + f3));
        tessellator.addVertexWithUV((double)k, (double)l, (double)this.zLevel, (double)(0.5F - f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV((double)k, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F - f3));
        tessellator.addVertexWithUV(0.0D, 0.0D, (double)this.zLevel, (double)(0.5F + f2), (double)(0.5F + f3));
        tessellator.draw();
    }
    
	public GuiMainMenuNBT()
	{
		this.mc = Minecraft.getMinecraft();
		setupNBTInfo();
	}
	
	@Override
	public void initGui()
	{
		textureIndex = 0;
		currentGifFrame = 0;
		initNBTInfo();
		mcRes = new ScaledResolution(mc,mc.displayWidth,mc.displayHeight);
	}
	
	@SuppressWarnings("unchecked")
	public void initNBTInfo()
	{
		objects.clear();
		NBTTagCompound tag = idToTagMapping.get(DummyConfig.getMainMenu());
		if(tag.hasKey("Images", 9))
		{
			NBTTagList nbtLst = tag.getTagList("Images", 10);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
			{
				NBTTagCompound tg = nbtLst.getCompoundTagAt(i);
				CustomImage image = new CustomImage();
				image.texture = new ResourceLocation(tg.getString("Texture"));
				image.gif = tg.getString("Texture").endsWith(".gif");
				if(image.gif)
					image.gifImage = new GIFImage(image.texture);
				
				int defaultX = tg.getInteger("XAlignment") == 0 ? 0 : tg.getInteger("XAlignment") == 1 ? this.width / 2 : tg.getInteger("XAlignment") == 2 ? this.width : this.width/tg.getInteger("XAlignment");
				int defaultY = tg.getInteger("YAlignment") == 0 ? 0 : tg.getInteger("YAlignment") == 1 ? this.height / 2 : tg.getInteger("YAlignment") == 2 ? this.height : this.height/tg.getInteger("YAlignment");
				image.x = defaultX + tg.getInteger("X");
				image.y = defaultY + tg.getInteger("Y");
				image.minU = tg.hasKey("MinU") ? tg.getDouble("MinU") : (double)tg.getInteger("TextureMinX")/256D;
				image.maxU = tg.hasKey("MaxU") ? tg.getDouble("MaxU") : (double)tg.getInteger("TextureMaxX")/256D;
				image.minV = tg.hasKey("MinV") ? tg.getDouble("MinV") : (double)tg.getInteger("TextureMinY")/256D;
				image.maxV = tg.hasKey("MaxV") ? tg.getDouble("MaxV") : (double)tg.getInteger("TextureMaxY")/256D;
				image.sizeX = tg.getInteger("XSize");
				image.sizeY = tg.getInteger("YSize");
				if(tg.hasKey("Text", 9))
				{
					NBTTagList texts = tg.getTagList("Text", 10);
					for(int j = 0; j < texts.tagCount(); ++j)
					{
						NBTTagCompound t = texts.getCompoundTagAt(j);
						image.textAngle = t.getFloat("Angle");
						String s = t.hasKey("Splashes") ? createRandomSplash(new ResourceLocation(t.getString("Splashes"))) : t.getString("Text");
						image.additionalText.add(new Pair<Coord2D,String>(new Coord2D(t.getInteger("X"),t.getInteger("Y")),s));
					}
				}
				objects.add(image);
			}
		}
		if(tag.hasKey("Buttons", 9))
		{
			NBTTagList nbtLst = tag.getTagList("Buttons", 10);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
			{
				NBTTagCompound tg = nbtLst.getCompoundTagAt(i);
				int id = tg.getInteger("ButtonID");
				int defaultX = tg.getInteger("XAlignment") == 0 ? 0 : tg.getInteger("XAlignment") == 1 ? this.width / 2 : tg.getInteger("XAlignment") == 2 ? this.width : this.width/tg.getInteger("XAlignment");
				int defaultY = tg.getInteger("YAlignment") == 0 ? 0 : tg.getInteger("YAlignment") == 1 ? this.height / 2 : tg.getInteger("YAlignment") == 2 ? this.height : this.height/tg.getInteger("YAlignment");
				int x = defaultX + tg.getInteger("X");
				int y = defaultY + tg.getInteger("Y");
				int sizeX = tg.getInteger("XSize");
				int sizeY = tg.getInteger("YSize");
				String text = tg.getString("Text");
				CustomButton button = new CustomButton(id,x,y,sizeX,sizeY,StatCollector.canTranslate(text) ? StatCollector.translateToLocal(text) : text);
				button.texture = new ResourceLocation(tg.getString("Texture"));
				button.sound = new ResourceLocation(tg.getString("Sound"));
				button.minU = tg.hasKey("MinU") ? tg.getDouble("MinU") : (double)tg.getInteger("TextureMinX")/256D;
				button.maxU = tg.hasKey("MaxU") ? tg.getDouble("MaxU") : (double)tg.getInteger("TextureMaxX")/256D;
				button.minV = tg.hasKey("MinV") ? tg.getDouble("MinV") : (double)tg.getInteger("TextureMinY")/256D;
				button.maxV = tg.hasKey("MaxV") ? tg.getDouble("MaxV") : (double)tg.getInteger("TextureMaxY")/256D;
				button.buttonYOffset = tg.hasKey("ButtonYUVOffset") ? tg.getDouble("ButtonYUVOffset") : (double)tg.getInteger("ButtonYOffset")/256D;
				if(tg.hasKey("URL")){
					try{
						button.url = new URL(tg.getString("URL"));
					}
					catch(Exception e){
						
					}
				}
				objects.add(button);
				this.buttonList.add(button);
			}
		}
	}
	
	public String createRandomSplash(ResourceLocation splashesLoc)
	{
		String retStr = "missingno";
		
		BufferedReader bufferedreader = null;
		
		try
	    {
			ArrayList<String> arraylist = new ArrayList<String>();
	        bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashesLoc).getInputStream(), "UTF-8"));
	        String s;
	        while ((s = bufferedreader.readLine()) != null)
	        {
	        	s = s.trim();
	            if (!s.isEmpty())
	            	arraylist.add(s);
	        }
	        if (!arraylist.isEmpty())
	            retStr = (String)arraylist.get(rand.nextInt(arraylist.size()));
	        
	    }
	    catch (IOException e){
	    	e.printStackTrace();
	    }
	    finally
	    {
	        if (bufferedreader != null)
	        {
	            try
	            {
	                bufferedreader.close();
	            }
	            catch (IOException ioexception){}
	        }
	    }
		return "|Splash|"+retStr;
	}
	
	public void setupNBTInfo()
	{
		menuTextures.clear();
		music.clear();
		overlayTimeList.clear();
		textLeft.clear();
		textRight.clear();
		
		NBTTagCompound tag = idToTagMapping.get(DummyConfig.getMainMenu());
		menuType = tag.getInteger("MenuType");
		if(tag.hasKey("GradientStart") && tag.hasKey("GradientEnd"))
		{
			hasGradient = true;
			gradientColorStart = tag.getInteger("GradientStart");
			gradientColorEnd = tag.getInteger("GradientEnd");
		}
		if(tag.hasKey("TextureRepeats"))
			this.textureRepeat = tag.getInteger("TextureRepeats");
			
		if(tag.hasKey("Textures", 9))
		{
			NBTTagList nbtLst = tag.getTagList("Textures", 10);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
			{
				NBTTagCompound tg = nbtLst.getCompoundTagAt(i);
				menuTextures.add(new Pair<Integer,ResourceLocation>(tg.getInteger("FadeTime"),new ResourceLocation(tg.getString("Texture"))));
				overlayTimeList.add(tg.getInteger("OverlayTime"));
			}
		}else
		{
			if(tag.hasKey("Texture",8))
			{
				menuTexture = new ResourceLocation(tag.getString("Texture"));
				if(tag.getString("Texture").endsWith(".gif"))
				{
					gif = true;
					gifImage = new GIFImage(new ResourceLocation(tag.getString("Texture")));
					for(int i = 0; i < gifImage.frames; ++i)
						overlayTimeList.add(tag.getInteger("FrameDelay"));
					
					if(tag.hasKey("FramesDelay",10))
					{
						NBTTagCompound framesDelay = tag.getCompoundTag("FramesDelay");
						for(int i = 0; i < gifImage.frames; ++i)
							if(framesDelay.hasKey(i+"", 3))
								overlayTimeList.set(i, framesDelay.getInteger(i+""));
					}
				}
			}
			else
			{
				menuTexture = null;
				menuType = 1;
			}
		}
		if(tag.hasKey("Music", 9))
		{
			NBTTagList nbtLst = tag.getTagList("Music", 10);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
				music.add(new ResourceLocation(nbtLst.getCompoundTagAt(i).getString("Music")));
		}
		if(tag.hasKey("TextLeft",9))
		{
			NBTTagList nbtLst = tag.getTagList("TextLeft", 8);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
			{
				String txt = nbtLst.getStringTagAt(i);
				txt = parsePossibleLink(txt);
				if(txt.equals("|SidedInfo|") && FMLCommonHandler.instance().getSidedDelegate()!=null)
					textLeft.addAll(FMLCommonHandler.instance().getSidedDelegate().getAdditionalBrandingInformation());
				else
					if(!Strings.isNullOrEmpty(txt))
						textLeft.add(txt);
			}
			textLeft = Lists.reverse(textLeft);
		}
		
		if(tag.hasKey("TextRight",9))
		{
			NBTTagList nbtLst = tag.getTagList("TextRight", 8);
			for(int i = 0; i < nbtLst.tagCount(); ++i)
			{
				String txt = nbtLst.getStringTagAt(i);
				txt = parsePossibleLink(txt);
				if(txt.equals("|SidedInfo|") && FMLCommonHandler.instance().getSidedDelegate()!=null)
					textRight.addAll(FMLCommonHandler.instance().getSidedDelegate().getAdditionalBrandingInformation());
				else
					if(!Strings.isNullOrEmpty(txt))
						textRight.add(txt);
			}
			textRight = Lists.reverse(textRight);
		}
		if(menuType == 1)
		{
			viewportTexture = new DynamicTexture(256, 256);
			menuTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		}
	}
	
	public String parsePossibleLink(String s)
	{
		if(s.equals("|MC|"))
			s = Loader.instance().getMCVersionString();
		if(s.equals("|MCP|"))
			s = Loader.instance().getMCPVersionString();
		if(s.equals("|FML|"))
			s = "FML v"+Loader.instance().getFMLVersionString();
		if(s.equals("|Forge|"))
			s = MinecraftForge.getBrandingVersion();
		if(s.equals("|FMLBranding|"))
			s = Loader.instance().getFMLBrandingProperties().containsKey("fmlbranding") ? Loader.instance().getFMLBrandingProperties().get("fmlbranding") : "";
		if(s.equals("|Mods|"))
		{
            int tModCount = Loader.instance().getModList().size();
            int aModCount = Loader.instance().getActiveModList().size();
            s = String.format("%d mod%s loaded, %d mod%s active", tModCount, tModCount!=1 ? "s" :"", aModCount, aModCount!=1 ? "s" :"" );
		}
		if(s.equals("|Copyright|"))
			s = "Copyright Mojang AB. Do not distribute!";
		return s;
	}
	
	public static interface AbstractGUIObject
	{
		public void drawOnScreen(int mouseX, int mouseY, float partialTicks, double zLevel);
		public void guiTick();
	}
	
	public static class CustomImage extends GuiScreen implements AbstractGUIObject
	{
		public ResourceLocation texture = null;
		public float textAngle;
		public boolean gif;
		public ArrayList<Pair<Coord2D,String>> additionalText = new ArrayList<Pair<Coord2D,String>>();
		public int x,y;
		public int sizeX,sizeY;
		public double minU,minV,maxU,maxV;
		public GIFImage gifImage;
		public int frameTime;
		
		public void drawOnScreen(int mouseX, int mouseY, float partialTicks, double zLevel)
		{
			if(mc == null)
				mc = Minecraft.getMinecraft();
			
			if(!gif)
			{
				mc.renderEngine.bindTexture(texture);
				Tessellator tec = Tessellator.instance;
				tec.startDrawingQuads();
				
				tec.addVertexWithUV(x, y, zLevel, minU, minV);
				tec.addVertexWithUV(x, y+sizeY, zLevel, minU, maxV);
				tec.addVertexWithUV(x+sizeX, y+sizeY, zLevel, maxU, maxV);
				tec.addVertexWithUV(x+sizeX, y, zLevel, maxU, minV);
				
				tec.draw();
			}else{
				
	    		GL11.glPushMatrix();
	    		
	    		GL11.glDisable(GL11.GL_ALPHA_TEST);
	    		GL11.glEnable(GL11.GL_BLEND);
	    		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    		GL11.glTranslated(x, y, zLevel);
	    		
	    		gifImage.drawOnScreen(MathHelper.floor_double(frameTime+partialTicks) % gifImage.frames, minU, minV, maxU, maxV, sizeX, sizeY);
	            
	            GL11.glTranslated(-x, -y, -zLevel);
	    		
	            GL11.glDisable(GL11.GL_BLEND);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	    		GL11.glPopMatrix();
				
			}
			
			if(!additionalText.isEmpty())
			{
				for(Pair<Coord2D,String> p : additionalText)
				{
					String text = p.getSecond();
					boolean splash = text.contains("|Splash|");
					if(splash)
						text = text.substring(8);
					
					double dx = p.getFirst().x;
					double dy = p.getFirst().z;
					GL11.glPushMatrix();
					
					GL11.glTranslated(x+dx, y+dy, zLevel+10);
					
					GL11.glRotated(textAngle, 0, 0, 1);
					
					double scale = 1.8D;
					if(splash)
					{
						scale = 1.8D - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
						scale = scale * 100.0F / (float)(mc.fontRenderer.getStringWidth(text) + 32);
					}
					
					GL11.glScaled(scale, scale, 1);
					
					this.drawCenteredString(mc.fontRenderer, text, 0, -8, -256);
					
					GL11.glScaled(1/scale, 1/scale, 1);
					
					GL11.glRotated(-textAngle, 0, 0, 1);
					
					GL11.glTranslated(-(x+dx), -(y+dy), -(zLevel+10));
					
					GL11.glColor4d(1, 1, 1, 1);
					
					GL11.glPopMatrix();
				}
			}
		}

		@Override
		public void guiTick() {
			if(gif)
				++frameTime;
		}
	}
	
	public static class CustomButton extends GuiButton implements AbstractGUIObject
	{
		public ResourceLocation texture = null;
		public ResourceLocation sound = null;
		public double minU,minV,maxU,maxV;
		public double buttonYOffset;
		public URL url;
		
		public CustomButton(int id, int x, int y, int sizeX, int sizeY, String text)
		{
			super(id,x,y,sizeX,sizeY,text);
		}
		
	    public void func_146113_a(SoundHandler sh)
	    {
	    	sh.playSound(PositionedSoundRecord.func_147674_a(sound != null ? sound : new ResourceLocation("gui.button.press"), 1.0F));
	    }
		
	    public void drawOnScreen(int mouseX, int mouseY, float partialTicks, double zLevel)
	    {
			Minecraft mc = Minecraft.getMinecraft();
			
			mc.renderEngine.bindTexture(texture);
			Tessellator tec = Tessellator.instance;
			tec.startDrawingQuads();
			
			int l = 0xe0e0e0;
			double offsetY = buttonYOffset;
			if(!this.enabled)
			{
				offsetY = 0;
				l = 0xa0a0a0;
			}
			
			if(mouseX >= xPosition && mouseX <= xPosition+width)
			{
				if(mouseY >= yPosition && mouseY <= yPosition+height)
				{
					offsetY = buttonYOffset * 2;
					l = 0xffffff;
				}
			}
			
			tec.addVertexWithUV(xPosition, yPosition, zLevel, minU, minV+offsetY);
			tec.addVertexWithUV(xPosition, yPosition+height, zLevel, minU, maxV+offsetY);
			tec.addVertexWithUV(xPosition+width, yPosition+height, zLevel, maxU, maxV+offsetY);
			tec.addVertexWithUV(xPosition+width, yPosition, zLevel, maxU, minV+offsetY);
			
			tec.draw();
			
			GL11.glTranslated(0, 0, 100);
			this.drawCenteredString(mc.fontRenderer, displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			GL11.glTranslated(0, 0, -100);
			GL11.glColor4d(1, 1, 1, 1);
	    }

		@Override
		public void guiTick() {
			
		}
	}
}
