package DummyCore.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;

import DummyCore.Client.AdvancedModelLoader;
import DummyCore.Client.GuiMainMenuOld;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Client.SBRHAwareModel;
import DummyCore.Client.ISBRH.RenderAllFacesWithHorizontalOffset;
import DummyCore.Client.ISBRH.RenderAnvil;
import DummyCore.Client.ISBRH.RenderBothCrosses;
import DummyCore.Client.ISBRH.RenderConnectedToBlock;
import DummyCore.Client.ISBRH.RenderCrops;
import DummyCore.Client.ISBRH.RenderCrossedSquares;
import DummyCore.Client.ISBRH.RenderCube;
import DummyCore.Client.ISBRH.RenderCubeAndCrossedSquares;
import DummyCore.Client.ISBRH.RenderFacesWithOffset;
import DummyCore.Client.ISBRH.RenderHorizontalCross;
import DummyCore.Client.ISBRH.RenderNull;
import DummyCore.Client.obj.ObjModelLoader;
import DummyCore.Client.techne.TechneModelLoader;
import DummyCore.Core.CoreInitialiser;
import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;

//Internal
public class NetProxy_Client extends NetProxy_Server{
	
	public static final Hashtable<String, ShaderGroup> shaders = new Hashtable<String, ShaderGroup>();
	public static final Hashtable<Block,Integer[]> cachedMeta = new Hashtable<Block,Integer[]>();
	public static final Hashtable<Item,Integer[]> cachedMetaI = new Hashtable<Item,Integer[]>();
	
	//Why vanilla's(or is it forge?) thread checking?
	public void handlePacketS35(S35PacketUpdateTileEntity packetIn)
	{
        if (Minecraft.getMinecraft().theWorld.isBlockLoaded(packetIn.getPos()))
        {
            TileEntity tileentity = Minecraft.getMinecraft().theWorld.getTileEntity(packetIn.getPos());
            
            if(tileentity == null)
            	return;
            int i = packetIn.getTileEntityType();

            if (i == 1 && tileentity instanceof TileEntityMobSpawner || i == 2 && tileentity instanceof TileEntityCommandBlock || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner)
            {
                tileentity.readFromNBT(packetIn.getNbtCompound());
            }
            else
            {
            	NetworkManager nm = null;
            	try{
            		Field f = Minecraft.class.getDeclaredField(ASMManager.chooseByEnvironment("myNetworkManager", "field_71453_ak"));
            		f.setAccessible(true);
            		nm = NetworkManager.class.cast(f.get(Minecraft.getMinecraft().theWorld));
            	}catch(Exception e){
            	}
                tileentity.onDataPacket(nm, packetIn);
            }
        }
	}
	
	public static int getIndex(Item item, int meta)
	{
		return Item.getIdFromItem(item) << 16 | meta;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void injectOldItemBlockModel(Block b)
	{
		try
		{
			Class<ItemModelMesher> imm = ItemModelMesher.class;
			Field simpleShapesCacheField = imm.getDeclaredField(ASMManager.chooseByEnvironment("simpleShapesCache", "field_178091_b"));
			simpleShapesCacheField.setAccessible(true);
			Map m = Map.class.cast(simpleShapesCacheField.get(Minecraft.getMinecraft().getRenderItem().getItemModelMesher()));
			List<IBlockState> lst = IOldCubicBlock.class.cast(b).listPossibleStates(b);
			if(lst == null || lst.isEmpty())
				m.put(Integer.valueOf(getIndex(Item.getItemFromBlock(b),b.getMetaFromState(b.getDefaultState()))), new SBRHAwareModel(b,b.getDefaultState()));
			else
				for(IBlockState ibs : lst)
					m.put(Integer.valueOf(getIndex(Item.getItemFromBlock(b),b.getMetaFromState(ibs))), new SBRHAwareModel(b,ibs));
			
			simpleShapesCacheField.set(Minecraft.getMinecraft().getRenderItem().getItemModelMesher(), m);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
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
	
	public World getClientWorld()
	{
		return Minecraft.getMinecraft().theWorld;
	}
	
	public Integer[] createPossibleMetadataCacheFromBlock(Block b)
	{
		if(cachedMeta.containsKey(b))
			return cachedMeta.get(b);
		
		Item i = Item.getItemFromBlock(b);
		ArrayList<ItemStack> dummyTabsTrick = new ArrayList<ItemStack>();
		i.getSubItems(i, b.getCreativeTabToDisplayOn(), dummyTabsTrick);
		Integer[] retInt = new Integer[dummyTabsTrick.size()];
		int count = 0;
		for(ItemStack is : dummyTabsTrick)
		{
			if(is != null && is.getItem() == i)
			{
				retInt[count] = is.getItemDamage();
				++count;
			}
		}
		
		cachedMeta.put(b, retInt);
		return retInt;
	}
	
	public Integer[] createPossibleMetadataCacheFromItem(Item i)
	{
		if(cachedMetaI.containsKey(i))
			return cachedMetaI.get(i);
		
		ArrayList<ItemStack> dummyTabsTrick = new ArrayList<ItemStack>();
		i.getSubItems(i, i.getCreativeTab(), dummyTabsTrick);
		Integer[] retInt = new Integer[dummyTabsTrick.size()];
		int count = 0;
		for(ItemStack is : dummyTabsTrick)
		{
			if(is != null && is.getItem() == i)
			{
				retInt[count] = is.getItemDamage();
				++count;
			}
		}
		
		cachedMetaI.put(i, retInt);
		return retInt;
	}
	
	@Override
	public void registerInfo()
	{
		AdvancedModelLoader.registerModelHandler(new ObjModelLoader());
		AdvancedModelLoader.registerModelHandler(new TechneModelLoader());
		MainMenuRegistry.initMenuConfigs();
		MainMenuRegistry.registerNewGui(GuiMainMenuVanilla.class,"[DC] Vanilla","Just a simple vanilla MC gui.");
		MainMenuRegistry.registerNewGui(GuiMainMenuOld.class,"[DC] Old Vanilla","An old MC gui.");
		TimerHijack.initMCTimer();
		
		RenderAccessLibrary.registerRenderingHandler(new RenderNull());
		RenderAccessLibrary.registerRenderingHandler(new RenderCube());
		RenderAccessLibrary.registerRenderingHandler(new RenderCrossedSquares());
		RenderAccessLibrary.registerRenderingHandler(new RenderCubeAndCrossedSquares());
		RenderAccessLibrary.registerRenderingHandler(new RenderHorizontalCross());
		RenderAccessLibrary.registerRenderingHandler(new RenderBothCrosses());
		RenderAccessLibrary.registerRenderingHandler(new RenderFacesWithOffset());
		RenderAccessLibrary.registerRenderingHandler(new RenderCrops());
		RenderAccessLibrary.registerRenderingHandler(new RenderAllFacesWithHorizontalOffset());
		RenderAccessLibrary.registerRenderingHandler(new RenderConnectedToBlock());
		RenderAccessLibrary.registerRenderingHandler(new RenderAnvil());
		
		MinecraftForge.EVENT_BUS.register(new DCParticleEngine());
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
			Object obj = constrctr.newInstance(player.inventory,world.getTileEntity(new BlockPos(x, y, z)));
			return constrctr_gui.newInstance(obj,world.getTileEntity(new BlockPos(x, y, z)));
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
    			if(er.isShaderActive())
    				er.switchUseShader();
    		}else
    		{
    			Class<? extends EntityRenderer> erclazz = er.getClass();
    			Method loadShader = null;
    			for(Method m : erclazz.getDeclaredMethods())
    				if(m.getParameterCount() == 1 && m.getParameters()[0].getType() == ResourceLocation.class)
    				{
    					loadShader = m;
    					break;
    				}
    			if(loadShader != null)
    				loadShader.invoke(er, rLoc);
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
    	if(Minecraft.getMinecraft().thePlayer != null && w.isRemote && Minecraft.getMinecraft().thePlayer.ticksExisted % 60 == 0)
    	{
    		blocks.delayTime = 0;
    		blocks.blockList = blocks.initialiseBlocksList();
    		if(blocks.blockList != null && !blocks.blockList.isEmpty())
    		{
    			Random rand;
    			if(DummyConfig.shouldChangeImage)
    				rand = new Random(Minecraft.getMinecraft().thePlayer.ticksExisted);
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
    	if(Minecraft.getMinecraft().thePlayer != null && w.isRemote && Minecraft.getMinecraft().thePlayer.ticksExisted % 60 == 0)
    	{
    		items.delayTime = 0;
    		items.itemList = items.initialiseItemsList();
    		if(items.itemList != null && !items.itemList.isEmpty())
    		{
    			Random rand;
    			if(DummyConfig.shouldChangeImage)
    				rand = new Random(Minecraft.getMinecraft().thePlayer.ticksExisted);
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
