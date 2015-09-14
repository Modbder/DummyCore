package DummyCore.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.4
 * @Description used to store any String for players/worlds in the world save folder and get it.
 */
public class DummyDataUtils {
	private static NBTTagCompound globalConfig;
	private static Hashtable<String,File> playerFiles = new Hashtable<String,File>();
	private static Hashtable<String,NBTTagCompound> playerConfigs = new Hashtable<String,NBTTagCompound>();
	private static String getPath;
	private static boolean isWorking;
	private static File directory;
	private static final Class<DummyDataUtils> clazz = DummyDataUtils.class;
	
	/**
	 * A new version of world loads, better server compathabilities.
	 */
	@SubscribeEvent
	public void serverWorldLoad(WorldEvent.Load event)
	{
		try
		{
			World w = event.world;
			if(w != null && !w.isRemote && w.provider != null && w.provider.dimensionId == 0)
			{
				File f = event.world.getSaveHandler().getWorldDirectory();
				if(f != null)
				{
					String fPath = f.getAbsolutePath();
					String dDataPath = fPath+"//DummyData//";
					directory = new File(dDataPath);
					directory.mkdirs();
					File globalDataDat = new File(directory+"//GlobalData.ddat");
					
					File f1 = new File(directory+"//PlayerData//");
					f1.mkdirs();
					getPath = dDataPath;
					globalConfig = createOrLoadTag(globalDataDat);
					isWorking = true;
				}
			}
		}catch(Exception e)
		{
			Notifier.notifyCustomMod("DummyCore", "Error loading DummyData!");e.printStackTrace();return;
		}
	}
	
	@SubscribeEvent
	public void serverWorldSave(WorldEvent.Save event)
	{
		try
		{
			World w = event.world;
			if(w != null && !w.isRemote && w.provider != null && w.provider.dimensionId == 0)
			{
				File f = event.world.getSaveHandler().getWorldDirectory();
				if(f != null)
				{
					String fPath = f.getAbsolutePath();
					String dDataPath = fPath+"//DummyData//";
					directory = new File(dDataPath);
					directory.mkdirs();
					File globalDataDat = new File(directory+"//GlobalData.ddat");
					saveGlobalTag(globalDataDat);
				}
			}
		}catch(Exception e)
		{
			Notifier.notifyCustomMod("DummyCore", "Error loading DummyData!");e.printStackTrace();return;
		}
	}
	
	@SubscribeEvent
	public void playerLoad(LoadFromFile event)
	{
		if(!event.entityPlayer.worldObj.isRemote)
		{
			EntityPlayer player = event.entityPlayer;
			boolean exists = true;
			File playerFile = getDataFileForPlayer(player.getCommandSenderName());
			if(playerFile.isDirectory())
			{
				restoreFileFromDir(playerFile);
				exists = false;
			}
			
			if(!playerFile.exists())
			{
				exists = false;
				createFile(playerFile);
			}
			
			if(exists)
			{
				NBTTagCompound tag = loadNBTFromFile(playerFile);
				playerConfigs.put(player.getCommandSenderName(), tag);
			}else
			{
				playerConfigs.put(player.getCommandSenderName(), new NBTTagCompound());
			}
		}
	}
	
	@SubscribeEvent
	public void playerSave(SaveToFile event)
	{
		if(!event.entityPlayer.worldObj.isRemote)
		{
			EntityPlayer player = event.entityPlayer;
			File playerFile = getDataFileForPlayer(player.getCommandSenderName());
			if(playerFile.isDirectory())
				restoreFileFromDir(playerFile);
			
			if(!playerFile.exists())
				createFile(playerFile);
			
			writeNBTToFile(globalConfig,playerFile);
		}
	}
	
	@SubscribeEvent
	public void playerLogOut(PlayerLoggedOutEvent event)
	{
		if(!event.player.worldObj.isRemote)
		{
			playerFiles.remove(event.player.getCommandSenderName());
			playerConfigs.remove(event.player.getCommandSenderName());
		}
	}
	
	private static void saveGlobalTag(File file)
	{
		if(file.isDirectory())
			restoreFileFromDir(file);
		
		if(!file.exists())
			createFile(file);
		writeNBTToFile(globalConfig,file);
	}
	
	
	private static void writeNBTToFile(NBTTagCompound tag, File file)
	{
		try
		{
			FileOutputStream oStream = new FileOutputStream(file);
			try
			{
				CompressedStreamTools.writeCompressed(tag, oStream);
			}
			catch(IOException ioe)
			{
				LoadingUtils.makeACrash(file+" appears to be damaged, either fix it or delete it!", clazz, ioe, false);
			}
			finally
			{
				oStream.close();
			}
		}
		catch(FileNotFoundException fnfe)
		{
			LoadingUtils.makeACrash(file+" does not exists. This is an impossible error and should be reported as soon as possible", clazz, fnfe, true);
		}
		catch(SecurityException se)
		{
			LoadingUtils.makeACrash(file+" Can't be accessed by Java, check your anti-virus and file privelleges!", clazz, se, true);
		}
		catch(IOException ioe)
		{
			LoadingUtils.makeACrash(file+" Can't be created, check your file system!", clazz, ioe, true);
		}
	}
	
	private static void restoreFileFromDir(File file)
	{
		Notifier.notifyError(file+" Is a directory, and should not be. Trying to resolve the issue...");
		try
		{
			file.delete();
			file.createNewFile();
		}
		catch(IOException ioe)
		{
			LoadingUtils.makeACrash(file+" Can't be created, check your file system!", clazz, ioe, true);
		}
		catch(SecurityException se)
		{
			LoadingUtils.makeACrash(file+" Can't be accessed by Java, check your anti-virus and file privelleges!", clazz, se, true);
		}
	}
	
	private static void createFile(File file)
	{
		try
		{
			file.createNewFile();
		}
		catch(IOException ioe)
		{
			LoadingUtils.makeACrash(file+" Can't be created, check your file system!", clazz, ioe, true);
		}
		catch(SecurityException se)
		{
			LoadingUtils.makeACrash(file+" Can't be accessed by Java, check your anti-virus and file privelleges!", clazz, se, true);
		}
	}
	
	private static NBTTagCompound createOrLoadTag(File file)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		boolean exists = true;
		if(file.isDirectory())
		{
			restoreFileFromDir(file);
			exists = false;
		}
		
		if(!file.exists())
		{
			exists = false;
			createFile(file);
		}
		
		if(exists)
		{
			tag = loadNBTFromFile(file);
		}
		
		return tag;
	}
	
	private static NBTTagCompound loadNBTFromFile(File file)
	{
		try
		{
			FileInputStream iStream = new FileInputStream(file);
			try
			{
				NBTTagCompound tag = CompressedStreamTools.readCompressed(iStream);
				return tag;
			}
			catch(IOException ioe)
			{
				LoadingUtils.makeACrash(file+" appears to be damaged, either fix it or delete it!", clazz, ioe, false);
			}
			
			finally
			{
				iStream.close();
			}
		}
		catch(FileNotFoundException fnfe)
		{
			LoadingUtils.makeACrash(file+" does not exists. This is an impossible error and should be reported as soon as possible", clazz, fnfe, true);
		}
		catch(SecurityException se)
		{
			LoadingUtils.makeACrash(file+" Can't be accessed by Java, check your anti-virus and file privelleges!", clazz, se, true);
		}
		catch(IOException ioe)
		{
			LoadingUtils.makeACrash(file+" Can't be created, check your file system!", clazz, ioe, true);
		}
		
		return new NBTTagCompound();
	}
	
	public static void stop()
	{
		globalConfig = null;
		playerFiles.clear();
		playerConfigs.clear();
		getPath = "no path";
		isWorking = false;
		directory = null;
		MiscUtils.registeredClientData.clear();
		MiscUtils.registeredClientWorldData.clear();
		MiscUtils.registeredServerData.clear();
		MiscUtils.registeredServerWorldData.clear();
	}
	
	private static File getDataFileForPlayer(String playerName)
	{
		if(!playerFiles.containsKey(playerName))
		{
			File ret = new File(getPath+"/PlayerData/"+playerName+".ddat");
			if(!ret.exists())
				try {
					ret.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			playerFiles.put(playerName, ret);
			return ret;
		}else
		{
			return playerFiles.get(playerName);
		}
		
	}
	
	private static NBTTagCompound getDataConfigForPlayer(String playerName)
	{
		if(!canWorkWithData())
			return null;
		
		if(!playerConfigs.containsKey(playerName))
		{
			playerConfigs.put(playerName, createOrLoadTag(playerFiles.get(playerName)));
		}
		return playerConfigs.get(playerName);
	}
	
	/**
	 * Writes the given String to the GlobalData.ddat file with the key of dataName
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @param dataName - key of the data
	 * @param dataString - data to store
	 */
	public static void writeCustomDataForMod(String modid, String dataName, String dataString)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			MiscUtils.registeredClientWorldData.put(modid+"|"+dataName, dataString);
		}else
		{
			if(!canWorkWithData())return;
			globalConfig.setString(modid+"|"+dataName,dataString);
			MiscUtils.registeredServerWorldData.put(modid+"|"+dataName, dataString);
			syncGlobalDataToClient(modid, dataName);
		}
	}
	
	public static void loadCustomDataForMod(String modid, String dataName)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return;
		}else
		{
			if(!canWorkWithData())
				return;
			
			MiscUtils.registeredServerWorldData.put(modid+"|"+dataName, globalConfig.getString(modid+"|"+dataName));
			syncGlobalDataToClient(modid, dataName);
		}
	}
	
	public static void loadGlobalDataForMod(String modid)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return;
		}else
		{
			if(!canWorkWithData())
				return;
			
			MiscUtils.registeredServerWorldData.put(modid+"|"+modid, globalConfig.getString(modid+"|"+modid));
			syncGlobalDataToClient(modid, modid);
		}
	}
	
	/**
	 * Writes the given String to the GlobalData.ddat file with the key of modid
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @param dataString - data to store
	 */
	public static void writeGlobalDataForMod(String modid, String dataString)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			MiscUtils.registeredClientWorldData.put(modid+"|"+modid, dataString);
		}else
		{
			if(!canWorkWithData())return;
			globalConfig.setString(modid+"|"+modid, dataString);
			MiscUtils.registeredServerWorldData.put(modid+"|"+modid, dataString);
			syncGlobalDataToClient(modid);
		}
	}
	
	
	/**
	 * Returns the String from the GlobalData.ddat file with the key of modid
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @return a string with the data, written to the ddat file. Returns "no data" if none was found
	 */
	public static String getGlobalDataForMod(String modid)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return MiscUtils.registeredClientWorldData.get(modid+"|"+modid);
		}else
		{
			if(!MiscUtils.registeredServerWorldData.containsKey(modid+"|"+modid))
				loadGlobalDataForMod(modid);
			return MiscUtils.registeredServerWorldData.get(modid+"|"+modid);
		}
	}
	
	/**
	 * Returns the String from the GlobalData.ddat file with the custom key
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @param dataName - key of the data
	 * @return a string with the data, written to the ddat file. Returns "no data" if none was found
	 */
	public static String getCustomDataForMod(String modid, String dataName)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return MiscUtils.registeredClientWorldData.get(modid+"|"+dataName);
		}else
		{
			if(!MiscUtils.registeredServerWorldData.containsKey(modid+"|"+dataName))
				loadCustomDataForMod(modid,dataName);
			return MiscUtils.registeredServerWorldData.get(modid+"|"+dataName);
		}
	}
	
	/**
	 * Sets the given data to the ddat file of the player with given name.
	 * @version From DummyCore 1.4
	 * @param playerName - name of the player
	 * @param modid - id of the mod
	 * @param dataName - key of the data
	 * @param dataValue - the data to store
	 */
	public static void setDataForPlayer(String playerName, String modid, String dataName, String dataValue)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			MiscUtils.registeredClientData.put(playerName+"_"+modid+"|"+dataName, dataValue);
		}else
		{
			if(!canWorkWithData())
				return;
			NBTTagCompound tag = getDataConfigForPlayer(playerName);
			tag.setString(modid+"|"+dataName, dataValue);
			MiscUtils.registeredServerData.put(playerName+"_"+modid+"|"+dataName, dataValue);
			syncPlayerDataToClient(playerName, modid, dataName);
		}
	}
	
	public static void loadPlayerDataForMod(String playerName, String modid, String dataName)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return;
		}else
		{
			if(!canWorkWithData())return;
			NBTTagCompound tag = getDataConfigForPlayer(playerName);
			MiscUtils.registeredServerData.put(playerName+"_"+modid+"|"+dataName, tag.getString(modid+"|"+dataName));
			syncPlayerDataToClient(playerName, modid, dataName);
		}
	}
	
	
	/**
	 * Returns the String from the ddat file of the given player with the custom key
	 * @version From DummyCore 1.4
	 * @param playerName - name of the player
	 * @param modid - id of the mod
	 * @param dataName - key of the data
	 * @return a string with the data, written to the ddat file. Returns "no data" if none was found
	 */
	public static String getDataForPlayer(String playerName, String modid, String dataName)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			return MiscUtils.registeredClientData.get(playerName+"_"+modid+"|"+dataName);
		}else
		{
			if(!MiscUtils.registeredServerData.containsKey(playerName+"_"+modid+"|"+dataName))
				loadPlayerDataForMod(playerName, modid, dataName);
			return MiscUtils.registeredServerData.get(playerName+"_"+modid+"|"+dataName);
		}
	}
	
	/**
	 * Used to check, if current world exists(for example, if the player is in the main menu returns false)
	 * @version From DummyCore 1.4
	 * @return True, if there is a running world, false if not.
	 */
	public static boolean canWorkWithData()
	{
		return isWorking;
	}
	
	public static void syncGlobalDataToClient(String modid, String dataName)
	{
		SyncUtils.addRequiresSync(modid, dataName);
	}
	
	public static void syncGlobalDataToClient(String modid)
	{
		SyncUtils.addRequiresSync(modid, modid);
	}
	
	public static void syncPlayerDataToClient(String playerName, String modid, String dataName)
	{
		SyncUtils.addRequiresSync(playerName, modid, dataName);
	}

}
