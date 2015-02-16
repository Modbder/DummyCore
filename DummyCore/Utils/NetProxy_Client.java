package DummyCore.Utils;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import DummyCore.Client.GuiMainMenuOld;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
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
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NetProxy_Client extends NetProxy_Server{
	
	public static final Hashtable<String, ShaderGroup> shaders = new Hashtable();

	@Override
	public EntityPlayer getPlayerOnSide(INetHandler handler)
	{
		if(handler instanceof NetHandlerPlayClient)
		{
			return Minecraft.getMinecraft().thePlayer;
		}
		return null;
	}
	
	@Override
	public void registerInfo()
	{
		MainMenuRegistry.registerNewGui(GuiMainMenuVanilla.class,"[DC] Vanilla","Just a simple vanilla MC gui.");
		MainMenuRegistry.registerNewGui(GuiMainMenuOld.class,"[DC] Old Vanilla","An old MC gui.");
	}
	
	@Override
	public void registerInit()
	{
		if(CoreInitialiser.cfg.removeMissingTexturesErrors)
		{
			Logger logger = LogManager.getLogger(TextureMap.class);
			org.apache.logging.log4j.core.Logger log = (org.apache.logging.log4j.core.Logger) logger;
			log.setLevel(Level.OFF);
		}
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		try
		{
			Class guiClass = Class.forName(GuiContainerLibrary.guis.get(ID));
			Constructor constrctr_gui = guiClass.getConstructor(Container.class, TileEntity.class);
			Class containerClass = Class.forName(GuiContainerLibrary.containers.get(ID));
			Constructor constrctr = containerClass.getConstructor(InventoryPlayer.class, TileEntity.class);
			Object obj = constrctr.newInstance(player.inventory,world.getTileEntity(x, y, z));
			return constrctr_gui.newInstance(obj,world.getTileEntity(x, y, z));
		}catch(Exception e)
		{
			Notifier.notifySimple("Unable to open GUI for ID "+ID);
			e.printStackTrace();
			return null;
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
