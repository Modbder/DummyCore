package DummyCore.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import DummyCore.Client.GuiButton_ChangeGUI;
import DummyCore.Client.GuiMainMenuVanilla;
import DummyCore.Client.MainMenuGUIRenderer;
import DummyCore.Client.MainMenuRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Events.DummyEvent_OnClientGUIButtonPress;
import DummyCore.Events.DummyEvent_OnKeyboardKeyPressed_Server;
import DummyCore.Events.DummyEvent_OnPacketRecieved;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

/**
 * @author Modbder
 * @version From DummyCore 1.7
 * @Description Used for internal DummyCore features. Do NOT change!
 */
public class DummyEventHandler {
	
	public static boolean[] isKeyPressed;
	
	public void onBlockBeeingBroken(PlayerEvent.BreakSpeed event)
	{
		if(MiscUtils.isBlockUnbreakable(event.entityPlayer.worldObj, event.x, event.y, event.z))
		{
			event.setCanceled(true);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMainMenuGUISetup(InitGuiEvent.Pre event)
	{
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(RenderTickEvent event)
	{
		try
		{
			if(isKeyPressed == null) isKeyPressed = new boolean[Keyboard.getKeyCount()];
		if(FMLClientHandler.instance().getClientPlayerEntity() != null)
			for(int i = 0; i < isKeyPressed.length; ++i)
			{
				if(Keyboard.isKeyDown(Keyboard.getKeyIndex(Keyboard.getKeyName(i))) && !isKeyPressed[i])
				{
					isKeyPressed[i] = true;
					int id = i;
					String keyName = Keyboard.getKeyName(i);
					String username = FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName();
					DummyData aaa = new DummyData("id",id);
					DummyData aab = new DummyData("name",keyName);
					DummyData aac = new DummyData("username",username);
					DummyData aad = new DummyData("pressed",true);
					DataStorage.addDataToString(aaa);
					DataStorage.addDataToString(aab);
					DataStorage.addDataToString(aac);
					DataStorage.addDataToString(aad);
					String dataString = DataStorage.getDataString();
					DummyPacketIMSG packet = new DummyPacketIMSG("||mod:DummyCore.ButtonPress"+dataString);
					CoreInitialiser.packetHandler.sendToServer(packet);
				}
				if(!Keyboard.isKeyDown(Keyboard.getKeyIndex(Keyboard.getKeyName(i))) && isKeyPressed[i])
				{
					isKeyPressed[i] = false;
					int id = i;
					String keyName = Keyboard.getKeyName(i);
					String username = FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName();
					DummyData aaa = new DummyData("id",id);
					DummyData aab = new DummyData("name",keyName);
					DummyData aac = new DummyData("username",username);
					DummyData aad = new DummyData("pressed",false);
					DataStorage.addDataToString(aaa);
					DataStorage.addDataToString(aab);
					DataStorage.addDataToString(aac);
					DataStorage.addDataToString(aad);
					String dataString = DataStorage.getDataString();
					DummyPacketIMSG packet = new DummyPacketIMSG("||mod:DummyCore.ButtonPress"+dataString);
					CoreInitialiser.packetHandler.sendToServer(packet);
				}
			}
		}catch(Exception e)
		{
			DataStorage.getDataString();
			return;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMainMenuGUISetup(InitGuiEvent.Post event)
	{
		if(event.gui instanceof IMainMenu)
		{
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
					TileEntity tile = event.recievedEntity.worldObj.getTileEntity(x, y, z);
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
					event.recievedEntity.worldObj.spawnParticle(type, x, y, z, r, g, b);
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
					Chunk chunk = world.getChunkFromBlockCoords(x,z);
					byte[] biome = chunk.getBiomeArray();
					int cbiome = biome[(z & 0xf) << 4 | x & 0xf];
					cbiome = id & 0xff;
					biome[(z & 0xf) << 4 | x & 0xf] = (byte) cbiome;
					chunk.setBiomeArray(biome);
					world.markBlocksDirtyVertical(x, z, 16, 16);
				}
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
						EntityPlayer player = manager.func_152612_a(username);
						MinecraftForge.EVENT_BUS.post(new DummyEvent_OnKeyboardKeyPressed_Server(id, name, player,pressed));
					}
				}
				if(modData.fieldName.equalsIgnoreCase("mod") && modData.fieldValue.equalsIgnoreCase("dummyCore.guiButton"))
				{
					int id = Integer.parseInt(packetData[1].fieldValue);
					String pClName = packetData[2].fieldValue;
					String bClName = packetData[3].fieldValue;
					String username = packetData[4].fieldValue;
					int x = Integer.parseInt(packetData[5].fieldValue);
					int y = Integer.parseInt(packetData[6].fieldValue);
					int z = Integer.parseInt(packetData[7].fieldValue);
					Side side = FMLCommonHandler.instance().getEffectiveSide();
					if(side == Side.SERVER)
					{
						MinecraftServer server = MinecraftServer.getServer();
						ServerConfigurationManager manager = server.getConfigurationManager();
						EntityPlayer player = manager.func_152612_a(username);
						MinecraftForge.EVENT_BUS.post(new DummyEvent_OnClientGUIButtonPress(id, pClName, bClName, player,x,y,z));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onDescrAdded(ItemTooltipEvent event)
	{
		ItemStack stack = event.itemStack;
		String unlocName = stack.getUnlocalizedName();
		if(MiscUtils.descriptionTable.containsKey(unlocName))
		{
			event.toolTip.add(MiscUtils.descriptionCTable.get(unlocName)+MiscUtils.descriptionTable.get(unlocName));
		}else
		{
			List list = Arrays.asList(stack.getItem().itemRegistry.getNameForObject(stack.getItem()),stack.getItemDamage());
			if(MiscUtils.descriptionNTable.containsKey(list))
			{
				event.toolTip.add(MiscUtils.descriptionNCTable.get(list)+MiscUtils.descriptionNTable.get(list));
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerTickEnd(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		IInventory inv = player.inventory;
		for(int k = 0; k < inv.getSizeInventory(); ++k)
		{
			ItemStack s = inv.getStackInSlot(k);
			if(s != null)
			{
				if(s.getItem() instanceof IAttributeModifier)
				{
					String itype = ((IAttributeModifier)s.getItem()).getType(s, player);
					if(itype.equals("inventory"))
					{
						String last5OfUUID = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player);
						int operation = ((IAttributeModifier)s.getItem()).getOperation(s, player);
						double ivalue = ((IAttributeModifier)s.getItem()).getValue(s, player);
						IAttribute iattrib = ((IAttributeModifier)s.getItem()).getAttribute(s, player);
						MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
					}else
					{
						if(itype.equals("hold") && player.getCurrentEquippedItem() == s)
						{
							String last5OfUUID = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player);
							int operation = ((IAttributeModifier)s.getItem()).getOperation(s, player);
							double ivalue = ((IAttributeModifier)s.getItem()).getValue(s, player);
							IAttribute iattrib = ((IAttributeModifier)s.getItem()).getAttribute(s, player);
							MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
						}else
						{
							if(itype.equals("armor") && (player.inventory.armorInventory[0] == s ||player.inventory.armorInventory[1] == s ||player.inventory.armorInventory[2] == s ||player.inventory.armorInventory[3] == s))
							{
								String last5OfUUID = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player);
								int operation = ((IAttributeModifier)s.getItem()).getOperation(s, player);
								double ivalue = ((IAttributeModifier)s.getItem()).getValue(s, player);
								IAttribute iattrib = ((IAttributeModifier)s.getItem()).getAttribute(s, player);
								MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
							}
						}
					}
				}else
				{
					List l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(s.getItemDamage()));
					if(MiscUtils.shouldIgnoreDamage.contains(s.getItem()))
						l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(-1));
					if(MiscUtils.modifierType.containsKey(l))
					{
						String itype = MiscUtils.modifierType.get(l);
						
						if(itype.equals("inventory"))
						{
							String last5OfUUID = MiscUtils.modifierUUID.get(l);
							int operation = MiscUtils.modifierOperation.get(l);
							double ivalue = MiscUtils.modifierValue.get(l);
							IAttribute iattrib = MiscUtils.modifier.get(l);
							MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
						}else
						{
							if(itype.equals("hold") && player.getCurrentEquippedItem() == s)
							{
								String last5OfUUID = MiscUtils.modifierUUID.get(l);
								int operation = MiscUtils.modifierOperation.get(l);
								double ivalue = MiscUtils.modifierValue.get(l);
								IAttribute iattrib = MiscUtils.modifier.get(l);
								MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
							}else
							{
								if(itype.equals("armor") && (player.inventory.armorInventory[0] == s ||player.inventory.armorInventory[1] == s ||player.inventory.armorInventory[2] == s ||player.inventory.armorInventory[3] == s))
								{
									String last5OfUUID = MiscUtils.modifierUUID.get(l);
									int operation = MiscUtils.modifierOperation.get(l);
									double ivalue = MiscUtils.modifierValue.get(l);
									IAttribute iattrib = MiscUtils.modifier.get(l);
									MiscUtils.applyPlayerModifier(player, iattrib, last5OfUUID, ivalue, false, operation, itype);
								}
							}
						}
					}
				}
			}
		}
		for(int i1 = 0; i1 < player.getAttributeMap().getAllAttributes().size(); ++i1)
		{
			IAttributeInstance ainst = (IAttributeInstance) player.getAttributeMap().getAllAttributes().toArray()[i1];
			Collection coll = ainst.func_111122_c();
			for(int j = 0; j < coll.size(); ++j)
			{
				if(coll.toArray()[j] instanceof AttributeModifier)
				{
					AttributeModifier mod = (AttributeModifier) coll.toArray()[j];
					String name = mod.getName();
					boolean remove = true;
					if(name.contains("dam."))
					{
						String sub = mod.getID().toString().substring(mod.getID().toString().length()-5, mod.getID().toString().length());
						if(name.contains("hold"))
						{
							ItemStack s = player.getCurrentEquippedItem();
							if(s != null)
							{
								if(s.getItem() instanceof IAttributeModifier)
								{
									String uuid = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player).toLowerCase();
									if(uuid.equals(sub))
										remove = false;
								}
								List l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(s.getItemDamage()));
								if(MiscUtils.shouldIgnoreDamage.contains(s.getItem()))
									l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(-1));
								if(MiscUtils.modifierUUID.containsKey(l))
								{
									String uuid = MiscUtils.modifierUUID.get(l).toLowerCase();
									if(uuid.equals(sub))
										remove = false;
								}
							}
						}
						if(name.contains("inventory"))
						{
							IInventory inven = player.inventory;
							for(int k = 0; k < inven.getSizeInventory(); ++k)
							{
								ItemStack s = inven.getStackInSlot(k);
								if(s != null)
								{
									if(s.getItem() instanceof IAttributeModifier)
									{
										String uuid = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player).toLowerCase();
										if(uuid.equals(sub))
											remove = false;
									}
									List l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(s.getItemDamage()));
									if(MiscUtils.shouldIgnoreDamage.contains(s.getItem()))
										l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(-1));
									if(MiscUtils.modifierUUID.containsKey(l))
									{
										
										String uuid = MiscUtils.modifierUUID.get(l).toLowerCase();
										if(uuid.equals(sub))
											remove = false;
									}
								}
							}
						}
						if(name.contains("armor"))
						{
							for(int k = 0; k < player.inventory.armorInventory.length; ++k)
							{
								ItemStack s = player.inventory.armorInventory[k];
								
								if(s != null)
								{
									if(s.getItem() instanceof IAttributeModifier)
									{
										String uuid = ((IAttributeModifier)s.getItem()).last5OfUUID(s, player).toLowerCase();
										if(uuid.equals(sub))
											remove = false;
									}
									List l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(s.getItemDamage()));
									if(MiscUtils.shouldIgnoreDamage.contains(s.getItem()))
										l = Arrays.asList(s.getItem().getUnlocalizedName(),Integer.toString(-1));
									if(MiscUtils.modifierUUID.containsKey(l))
									{
										String uuid = MiscUtils.modifierUUID.get(l).toLowerCase();
										if(uuid.equals(sub))
											remove = false;
									}
								}
							}
						}
						if(remove)
							MiscUtils.applyPlayerModifier(player, ainst.getAttribute(), sub, 1, true, 0, "remove");
					}
				}
			}
		}
	}

}
