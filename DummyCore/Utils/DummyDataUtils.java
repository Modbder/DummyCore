package DummyCore.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.naming.spi.DirectoryManager;

import net.minecraftforge.common.Configuration;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.bouncycastle.asn1.x500.DirectoryString;

import cpw.mods.fml.common.discovery.DirectoryDiscoverer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

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
	
	public static void load(FMLServerStartingEvent event)
	{
		String name = event.getServer().getFile(event.getServer().getFolderName()).getAbsolutePath();
		System.out.println(name);
		int length = 0;
		for(int i = 0; i < name.length(); ++i)
		{
			if(name.substring(0, i).contains("\\.\\"))
			{
				--length;
				--length;
				break;
			}
			++length;
		}
		String print = name.substring(0,length);
		print += "saves\\";
		print += event.getServer().getFolderName();
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
		if(!canWorkWithData())return;
		globalConfig.load();
		globalConfig.get(modid, dataName, dataString).set(dataString);
		globalConfig.save();
	}
	
	
	/**
	 * Writes the given String to the GlobalData.ddat file with the key of modid
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @param dataString - data to store
	 */
	public static void writeGlobalDataForMod(String modid, String dataString)
	{
		if(!canWorkWithData())return;
		globalConfig.load();
		globalConfig.get(modid, modid, dataString).set(dataString);
		globalConfig.save();
	}
	
	
	/**
	 * Returns the String from the GlobalData.ddat file with the key of modid
	 * @version From DummyCore 1.4
	 * @param modid - the id of your mod
	 * @return a string with the data, written to the ddat file. Returns "no data" if none was found
	 */
	public static String getGlobalDataForMod(String modid)
	{
		if(!canWorkWithData())return null;
		globalConfig.load();
		String ret = globalConfig.get(modid, modid, "no data").getString();
		globalConfig.save();
		return ret;
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
		if(!canWorkWithData())return null;
		globalConfig.load();
		String ret = globalConfig.get(modid, dataName, "no data").getString();
		globalConfig.save();
		return ret;
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
		if(!canWorkWithData())return;
		Configuration config = getDataConfigForPlayer(playerName);
		config.load();
		config.get(modid, dataName, dataValue).set(dataValue);
		config.save();
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
		if(!canWorkWithData())return null;
		Configuration config = getDataConfigForPlayer(playerName);
		config.load();
		String ret = config.get(modid, dataName, "no data").getString();
		config.save();
		return ret;
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

}
