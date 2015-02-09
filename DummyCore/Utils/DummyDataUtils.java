package DummyCore.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.naming.spi.DirectoryManager;

import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import DummyCore.Core.CoreInitialiser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.discovery.DirectoryDiscoverer;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.4
 * @Description used to store any String for players/worlds in the world save folder and get it.
 */
public class DummyDataUtils {
	private static File mainWorldFile;
	private static Configuration globalConfig;
	private static Hashtable<String,File> playerFiles = new Hashtable<String,File>();
	private static Hashtable<String,Configuration> playerConfigs = new Hashtable<String,Configuration>();
	private static String getPath;
	private static boolean isWorking;
	private static File directory;
	
	/**
	 * A new version of world loads, better server compathabilities.
	 */
	@SubscribeEvent
	public void serverWorldLoad(WorldEvent.Load event)
	{
		try
		{
			World w = event.world;
			if(w != null && !w.isRemote && w.provider != null && w.provider.dimensionId == 0 && w.provider instanceof WorldProviderSurface)
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
					mainWorldFile = globalDataDat;
					globalConfig = new Configuration(globalDataDat);
					isWorking = true;
				}
			}
		}catch(Exception e)
		{
			Notifier.notifyCustomMod("DummyCore", "Error loading DummyData!");e.printStackTrace();return;
		}
	}
	
	@Deprecated
	public static void load(FMLServerAboutToStartEvent event)
	{
		String name = event.getServer().getFile(event.getServer().getFolderName()).getAbsolutePath();
		int length = 0;
		boolean shouldAddSaves = true;
		for(int i = 0; i < name.length(); ++i)
		{
			if(name.substring(0, i).contains("\\.\\"))
			{ 
				shouldAddSaves = true;
				--length;
				--length;
				break;
			}
			++length;
		}
		String print = name.substring(0,length);
		if(shouldAddSaves)
		{
			print += "saves\\";
			print += event.getServer().getFolderName();
		}
		print += "\\DummyData\\";
		File f = new File(print);
		f.mkdirs();
		File file = new File(print+"GlobalData.ddat");
		try {
			if(!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		File f1 = new File(print+"/PlayerData/");
		f1.mkdirs();
		getPath = print;
		mainWorldFile = file;
		globalConfig = new Configuration(file);
		isWorking = true;
	}
	
	public static void stop()
	{
		mainWorldFile = null;
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
					// TODO Auto-generated catch block
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
	
	private static Configuration getDataConfigForPlayer(String playerName)
	{
		if(!canWorkWithData())return null;
		if(!playerConfigs.containsKey(playerName))
		{
			playerConfigs.put(playerName, new Configuration(getDataFileForPlayer(playerName)));
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
			globalConfig.load();
			globalConfig.get(modid, dataName, dataString).set(dataString);
			MiscUtils.registeredServerWorldData.put(modid+"|"+dataName, dataString);
			globalConfig.save();
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
			if(!canWorkWithData())return;
			globalConfig.load();
			MiscUtils.registeredServerWorldData.put(modid+"|"+dataName, globalConfig.get(modid, dataName, "no data").getString());
			globalConfig.save();
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
			if(!canWorkWithData())return;
			globalConfig.load();
			MiscUtils.registeredServerWorldData.put(modid+"|"+modid, globalConfig.get(modid, modid, "no data").getString());
			globalConfig.save();
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
			globalConfig.load();
			globalConfig.get(modid, modid, dataString).set(dataString);
			MiscUtils.registeredServerWorldData.put(modid+"|"+modid, dataString);
			globalConfig.save();
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
		//@Deprecated
		//if(!canWorkWithData())return null;
		//globalConfig.load();
		//String ret = globalConfig.get(modid, modid, "no data").getString();
		//globalConfig.save();
		//return ret;
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
		//@Deprecated
		//if(!canWorkWithData())return null;
		//globalConfig.load();
		//String ret = globalConfig.get(modid, dataName, "no data").getString();
		//globalConfig.save();
		//return ret;
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
			if(!canWorkWithData())return;
			Configuration config = getDataConfigForPlayer(playerName);
			config.load();
			config.get(modid, dataName, dataValue).set(dataValue);
			MiscUtils.registeredServerData.put(playerName+"_"+modid+"|"+dataName, dataValue);
			config.save();
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
			Configuration config = getDataConfigForPlayer(playerName);
			config.load();
			MiscUtils.registeredServerData.put(playerName+"_"+modid+"|"+dataName, config.get(modid, dataName, "no data").getString());
			config.save();
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
		//@Deprecated
		//if(!canWorkWithData())return null;
		//Configuration config = getDataConfigForPlayer(playerName);
		//config.load();
		//String ret = config.get(modid, dataName, "no data").getString();
		//config.save();
		//return ret;
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
		if(MiscUtils.registeredServerWorldData.containsKey(modid+"|"+dataName))
		{
			String dataString = "||mod:DummyCore.InfoSync"+"||"+modid+":"+dataName+"||ddata:"+MiscUtils.registeredServerWorldData.get(modid+"|"+dataName);
			DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
			CoreInitialiser.packetHandler.sendToAll(simplePacket);
		}else
		{
			Notifier.notifyCustomMod(modid, "The sync packet for data "+modid+"|"+dataName+" could not be generated - the requested server data does not exist!");
		}
	}
	
	public static void syncGlobalDataToClient(String modid)
	{
		if(MiscUtils.registeredServerWorldData.containsKey(modid+"|"+modid))
		{
			String dataString = "||mod:DummyCore.InfoSync"+"||"+modid+":"+modid+"||ddata:"+MiscUtils.registeredServerWorldData.get(modid+"|"+modid);
			DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
			CoreInitialiser.packetHandler.sendToAll(simplePacket);
		}else
		{
			Notifier.notifyCustomMod(modid, "The sync packet for data "+modid+ "could not be generated - the requested server data does not exist!");
		}
	}
	
	public static void syncPlayerDataToClient(String playerName, String modid, String dataName)
	{
		if(MiscUtils.registeredServerData.containsKey(playerName+"_"+modid+"|"+dataName))
		{
			String dataString = "||mod:DummyCore.PlayerInfoSync"+"||"+"playerName:"+playerName+"||"+modid+ ":" + dataName+"||ddata:"+MiscUtils.registeredServerData.get(playerName+"_"+modid+"|"+dataName);
			DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
			CoreInitialiser.packetHandler.sendToAll(simplePacket);
		}else
		{
			Notifier.notifyCustomMod(modid, "The sync packet for data "+playerName+"_"+modid+ "could not be generated - the requested server data does not exist!");
		}
	}

}
