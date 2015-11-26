package DummyCore.Utils;

import java.util.Arrays;

import DummyCore.Client.GuiButton_ChangeGUI;
import DummyCore.Client.IconRegister;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Events.DummyEvent_OnClientGUIButtonPress;
import DummyCore.Events.DummyEvent_OnPacketRecieved;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Modbder
 * @version From DummyCore 1.7
 * @Description Used for internal DummyCore features. Do NOT change!
 */
public class DummyEventHandler {
	
	public static int syncTime;
	public static boolean[] isKeyPressed;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void textureInit(TextureStitchEvent.Pre event)
	{
		IconRegister.currentMap = event.map;
		for(Pair<String,Block> p : OldTextureHandler.oldBlocksToRender)
			IOldCubicBlock.class.cast(p.getSecond()).registerBlockIcons(IconRegister.instance);
		for(Pair<String,Item> p : OldTextureHandler.oldItemsToRender)
			IOldItem.class.cast(p.getSecond()).registerIcons(IconRegister.instance);
	}
	
	@SubscribeEvent
	public void onBlockBeeingBroken(PlayerEvent.BreakSpeed event)
	{
		if(MiscUtils.isBlockUnbreakable(event.entityPlayer.worldObj, event.pos.getX(), event.pos.getY(), event.pos.getZ()))
		{
			event.setCanceled(true);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMainMenuGUISetup(InitGuiEvent.Pre event)
	{
		if(!CoreInitialiser.cfg.allowCustomMainMenu)
			return;
		if(event.gui.getClass() == GuiMainMenu.class)
		{
			event.setCanceled(true);
			MainMenuRegistry.newMainMenu(DummyConfig.getMainMenu());
		}
		if(event.gui instanceof IMainMenu)
		{
			if(MainMenuRegistry.menuList.get(DummyConfig.getMainMenu()) != event.gui.getClass())
			{
				event.setCanceled(true);
				MainMenuRegistry.newMainMenu(DummyConfig.getMainMenu());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMainMenuGUISetup(InitGuiEvent.Post event)
	{
		if(!CoreInitialiser.cfg.allowCustomMainMenu)
			return;
		
		if(event.gui instanceof IMainMenu)
		{
			boolean add = true;
			for(int i = 0; i < event.buttonList.size(); ++i)
			{
				Object obj = event.buttonList.get(i);
				if(obj instanceof GuiButton && GuiButton.class.cast(obj).id == 65536)
				{
					add = false;
					break;
				}
			}
			if(add)
				event.buttonList.add(new GuiButton_ChangeGUI(65535, event.gui.width/2 + 104, event.gui.height/4 + 24 + 72, 100, 20, "Change Main Menu"));
		}
	}
	
	@SubscribeEvent
	public void onPacketRecieved(DummyEvent_OnPacketRecieved event)
	{
		DummyData[] packetData = DataStorage.parseData(event.recievedData);
		if(packetData != null && packetData.length > 0)
		{
			try {
				DummyData modData = packetData[0];
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.tilesync"))
				{
					int x = Integer.parseInt(packetData[1].fieldValue);
					int y = Integer.parseInt(packetData[2].fieldValue);
					int z = Integer.parseInt(packetData[3].fieldValue);
					TileEntity tile = event.recievedEntity.worldObj.getTileEntity(new BlockPos(x, y, z));
					if(tile != null && tile instanceof ITEHasGameData)
					{
						DummyData[] tileShouldRecieve = Arrays.copyOfRange(packetData, 4, packetData.length);
						((ITEHasGameData)tile).setData(tileShouldRecieve);
					}
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.particle"))
				{
					String type = packetData[1].fieldValue;
					float x = Float.parseFloat(packetData[2].fieldValue);
					float y = Float.parseFloat(packetData[3].fieldValue);
					float z = Float.parseFloat(packetData[4].fieldValue);
					double r = Double.parseDouble(packetData[5].fieldValue);
					double g = Double.parseDouble(packetData[6].fieldValue);
					double b = Double.parseDouble(packetData[7].fieldValue);
					event.recievedEntity.worldObj.spawnParticle(EnumParticleTypes.valueOf(type.toUpperCase()), x, y, z, r, g, b);
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.sound"))
				{
					double x = Double.parseDouble(packetData[1].fieldValue);
					double y = Double.parseDouble(packetData[2].fieldValue);
					double z = Double.parseDouble(packetData[3].fieldValue);
					float vol = Float.parseFloat(packetData[4].fieldValue);
					float pitch = Float.parseFloat(packetData[5].fieldValue);
					String snd = packetData[6].fieldValue;
					event.recievedEntity.worldObj.playSound(x, y, z, snd, vol, pitch, false);
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.infosync"))
				{
					String modName = packetData[1].fieldName;
					String dataName = packetData[1].fieldValue;
					String dataItself = event.recievedData.substring(event.recievedData.indexOf("||ddata:")+8);
					MiscUtils.registeredClientWorldData.put(modName+"|"+dataName, dataItself);
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.playerinfosync"))
				{
					String playerName = packetData[1].fieldValue;
					String modName = packetData[2].fieldName;
					String dataName = packetData[2].fieldValue;
					String dataItself = event.recievedData.substring(event.recievedData.indexOf("||ddata:")+8);
					MiscUtils.registeredClientData.put(playerName+"_"+modName+"|"+dataName, dataItself);
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummycore.biomechange"))
				{
					int x = Integer.parseInt(packetData[1].fieldValue);
					int z = Integer.parseInt(packetData[2].fieldValue);
					int id = Integer.parseInt(packetData[3].fieldValue);
					World world = event.recievedEntity.worldObj;
					Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x,world.getActualHeight(),z));
					byte[] biome = chunk.getBiomeArray();
					int cbiome = biome[(z & 0xf) << 4 | x & 0xf];
					cbiome = id & 0xff;
					biome[(z & 0xf) << 4 | x & 0xf] = (byte) cbiome;
					chunk.setBiomeArray(biome);
					world.markBlocksDirtyVertical(x, z, 16, 16);
				}
				/*
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummyCore.buttonpress"))
				{
					int id = Integer.parseInt(packetData[1].fieldValue);
					String name = packetData[2].fieldValue;
					String username = packetData[3].fieldValue;
					boolean pressed = Boolean.parseBoolean(packetData[4].fieldValue);
					Side side = FMLCommonHandler.instance().getEffectiveSide();
					if(side == Side.SERVER)
					{
						MinecraftServer server = MinecraftServer.getServer();
						ServerConfigurationManager manager = server.getConfigurationManager();
						EntityPlayer player = manager.getPlayerByUsername(username);
						MinecraftForge.EVENT_BUS.post(new DummyEvent_OnKeyboardKeyPressed_Server(id, name, player,pressed));
					}
				}
				*/
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummyCore.guiButton"))
				{
					int id = Integer.parseInt(packetData[1].fieldValue);
					String pClName = packetData[2].fieldValue;
					String bClName = packetData[3].fieldValue;
					String username = packetData[4].fieldValue;
					int x = Integer.parseInt(packetData[5].fieldValue);
					int y = Integer.parseInt(packetData[6].fieldValue);
					int z = Integer.parseInt(packetData[7].fieldValue);
					DummyData[] data = new DummyData[packetData.length-8];
					for(int i = 8; i < packetData.length; ++i)
						data[i-8] = packetData[i];
					Side side = FMLCommonHandler.instance().getEffectiveSide();
					if(side == Side.SERVER)
					{
						MinecraftServer server = MinecraftServer.getServer();
						ServerConfigurationManager manager = server.getConfigurationManager();
						EntityPlayer player = manager.getPlayerByUsername(username);
						MinecraftForge.EVENT_BUS.post(new DummyEvent_OnClientGUIButtonPress(id, pClName, bClName, player,x,y,z,data));
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			++syncTime;
			if(syncTime >= DummyConfig.dummyCoreSyncTimer)
			{
				syncTime = 0;
				SyncUtils.makeSync_LotsSmallPackets();
			}
			actionsTick();
		}
	}
	
	private void actionsTick()
	{
		if(!MiscUtils.actions.isEmpty())
			for(int i = 0; i < MiscUtils.actions.size(); ++i)
			{
				ScheduledServerAction ssa = MiscUtils.actions.get(i);
				--ssa.actionTime;
				if(ssa.actionTime <= 0)
				{
					ssa.execute();
					MiscUtils.actions.remove(i);
				}
			}
	}
	
	@SubscribeEvent
	public void clientWorldLoad(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityPlayer && event.world.isRemote)
			ModVersionChecker.dispatchModChecks();
	}

}
