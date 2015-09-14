package DummyCore.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import DummyCore.Client.GuiMainMenuOld;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;

import cpw.mods.fml.client.FMLClientHandler;

public class NetProxy_Client extends NetProxy_Server{
	
	public static final Hashtable<String, ShaderGroup> shaders = new Hashtable<String, ShaderGroup>();

	@Override
	public EntityPlayer getPlayerOnSide(INetHandler handler)
	{
		if(handler instanceof NetHandlerPlayClient)
		{
			return Minecraft.getMinecraft().thePlayer;
		}
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}
	
	@Override
	public void registerInfo()
	{
		MainMenuRegistry.initMenuConfigs();
		MainMenuRegistry.registerNewGui(GuiMainMenuVanilla.class,"[DC] Vanilla","Just a simple vanilla MC gui.");
		MainMenuRegistry.registerNewGui(GuiMainMenuOld.class,"[DC] Old Vanilla","An old MC gui.");
		TimerHijack.initMCTimer();
	}
	
	@Override
	public void registerInit()
	{
		if(CoreInitialiser.cfg.removeMissingTexturesErrors)
		{
			try
			{
				Class<TextureMap> textureMap = TextureMap.class;
				Field logger = textureMap.getDeclaredFields()[0];
				boolean canAccess = logger.isAccessible();
				if(!canAccess)
					logger.setAccessible(true);
				Logger lg = Logger.class.cast(logger.get(null));
				lg.setLevel(Level.OFF);
						
				if(!canAccess)
					logger.setAccessible(false);
			}
			catch(Exception e)
			{
				Notifier.notifyError("DummyCore was sadly unable to remove missing texture errors :(");
			}
		}
		MainMenuRegistry.registerMenuConfigs();
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		try
		{
			Class<?> guiClass = Class.forName(GuiContainerLibrary.guis.get(ID));
			Constructor<?> constrctr_gui = guiClass.getConstructor(Container.class, TileEntity.class);
			Class<?> containerClass = Class.forName(GuiContainerLibrary.containers.get(ID));
			Constructor<?> constrctr = containerClass.getConstructor(InventoryPlayer.class, TileEntity.class);
			Object obj = constrctr.newInstance(player.inventory,world.getTileEntity(x, y, z));
			return constrctr_gui.newInstance(obj,world.getTileEntity(x, y, z));
		}catch(Exception e)
		{
			Notifier.notifySimple("Unable to open GUI for ID "+ID);
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeMissingTextureErrors()
	{
		if(CoreInitialiser.cfg.removeMissingTexturesErrors)
		{
			try
			{
				Class<FMLClientHandler> fmlClientHandler = FMLClientHandler.class;
				Field missingTextures = fmlClientHandler.getDeclaredField("missingTextures");
				Field badTextureDomains = fmlClientHandler.getDeclaredField("badTextureDomains");
				Field brokenTextures = fmlClientHandler.getDeclaredField("brokenTextures");
				boolean canAccess = missingTextures.isAccessible();
				if(!canAccess)
					missingTextures.setAccessible(true);
				
				SetMultimap<String,ResourceLocation> smmp = SetMultimap.class.cast(missingTextures.get(FMLClientHandler.instance()));
				smmp.clear();
				
				if(!canAccess)
					missingTextures.setAccessible(false);
				
				canAccess = badTextureDomains.isAccessible();
				if(!canAccess)
					badTextureDomains.setAccessible(true);
				
				Set<String> set = Set.class.cast(badTextureDomains.get(FMLClientHandler.instance()));
				set.clear();
				
				if(!canAccess)
					badTextureDomains.setAccessible(false);
				
				canAccess = brokenTextures.isAccessible();
				if(!canAccess)
					brokenTextures.setAccessible(true);
				
				Table<String, String, Set<ResourceLocation>> table = Table.class.cast(brokenTextures.get(FMLClientHandler.instance()));
				table.clear();
				
				if(!canAccess)
					brokenTextures.setAccessible(false);
				
				Notifier.notifyWarn("DummyCore has removed all possible texture errors the FML could output to the console!");
			}
			catch(Exception e)
			{
				Notifier.notifyError("DummyCore was sadly unable to remove missing texture errors :(");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void initShaders(ResourceLocation rLoc)
	{
    	Minecraft mc = Minecraft.getMinecraft();
    	EntityRenderer er = mc.entityRenderer;
    	try
    	{
    		if(rLoc == null)
    		{
    			er.deactivateShader();
    		}else
    		{
	    		er.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), rLoc);
	    		er.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
    		}
    	}catch(Exception e)
    	{
    		return;
    	}
	}
	
	@Override
	public void choseDisplayStack(CreativePageBlocks blocks)
	{
		World w = Minecraft.getMinecraft().theWorld;
    	if(w.isRemote && w.getWorldTime() % 60 == 0)
    	{
    		blocks.delayTime = 0;
    		blocks.blockList = blocks.initialiseBlocksList();
    		if(blocks.blockList != null && !blocks.blockList.isEmpty())
    		{
    			Random rand;
    			if(DummyConfig.shouldChangeImage)
    				rand = new Random(w.getWorldTime());
    			else
    				rand = new Random(0);
    			int random = rand.nextInt(blocks.blockList.size());
    			ItemStack itm = blocks.blockList.get(random);
    			if(itm != null && itm.getItem() != null)
    				blocks.displayStack = itm;
    		}
    	}
	}
	
	@Override
	public void choseDisplayStack(CreativePageItems items)
	{
		World w = Minecraft.getMinecraft().theWorld;
    	if(w.isRemote && w.getWorldTime() % 60 == 0)
    	{
    		items.delayTime = 0;
    		items.itemList = items.initialiseItemsList();
    		if(items.itemList != null && !items.itemList.isEmpty())
    		{
    			Random rand;
    			if(DummyConfig.shouldChangeImage)
    				rand = new Random(w.getWorldTime());
    			else
    				rand = new Random(0);
    			int random = rand.nextInt(items.itemList.size());
    			ItemStack itm = items.itemList.get(random);
    			if(itm != null && itm.getItem() != null)
    				items.displayStack = itm;
    		}
    	}
	}
}
