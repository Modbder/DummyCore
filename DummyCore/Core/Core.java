package DummyCore.Core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import DummyCore.Utils.IDummyConfig;
import DummyCore.Utils.Notifier;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Core {
	private static HashMap modList = new HashMap<Class,Integer>();
	private static HashMap langFilesList = new HashMap<Integer,Configuration>();
	private static HashMap configurationList = new HashMap<Integer,Configuration>();
	private static List modNameList = new ArrayList<String>();
	private static CreativeTabs[] blocksTabs = new CreativeTabs[512];
	private static CreativeTabs[] itemsTabs = new CreativeTabs[512];
	private static Configuration[] config = new Configuration[512];
	private static IDummyConfig[] configurationHandlers = new IDummyConfig[512];
	private static int modID;
	private static boolean[] isConfigLoaded = new boolean[512];
	
	private static void registerMod(Class c, String name) throws RuntimeException
	{
		if(!modList.containsKey(c))
		{
			int modId = getNextModId();
			if(modId < 0 || modId >= 512)
			{
				throw new RuntimeException("Mod "+name+" is trying to be registered with wrong id "+modId);
			}
			modList.put(c, modId);
			modNameList.add(modId, name);
			blocksTabs[modId] = new CreativePageBlocks(name);
			itemsTabs[modId] = new CreativePageItems(name);
			Notifier.notifySimple("Mod with name "+name+" and classpath "+c.getName()+".class using ID "+modId+" has been succesfully registered!");
		}else
		{
			throw new RuntimeException("Mod "+name+" is already registered!");
		}
	}
	
	private static void registerLangFileForMod(Class c, String path) throws IOException
	{
		File f = new File(path,getModName(getIdForMod(c))+".lang");
		if(!f.exists())
			f.createNewFile();
		Configuration config = new Configuration(f);
		langFilesList.put(getIdForMod(c),config);
		Notifier.notifySimple("Language File for mod "+getModName(getIdForMod(c))+" was successfully created with path "+path+getModName(getIdForMod(c))+".lang");
	}
	
	private static void registerConfigurationFileForMod(Class c, String path) throws IOException
	{
		File file = new File(path,getModName(getIdForMod(c))+".cfg");
		if(!file.exists())
			file.createNewFile();
		config[getIdForMod(c)] = new Configuration(file);
		config[getIdForMod(c)].save();
		configurationList.put(getIdForMod(c), config);
		Notifier.notifySimple("Configuration File for mod "+getModName(getIdForMod(c))+" was successfully created with path "+path+getModName(getIdForMod(c))+".cfg");
	}
	
	/**
	 * Use this in you Pre-Initiasisation functions. This will register your mod in the DummyCore system and automatically create all config and .lang files.
	 * From DummyCore v1.1 you no longer need to use IMCEvent to register all blocks and items.
	 * @param c - class file of your mod. MUST be registered from the mod itself. Use getClass().
	 * @param modname - this name will be used to name CreativeTabs, config and .lang files.
	 * @param configPath - the path to your configuration file. You can use FMLPreInitialisationEvent.getModConfigurationDirectory().getAbsolutePath() to get your path.
	 * @param config - the initialised! object, that implements IDummyConfig.
	 * @throws IOException - If something has gone wrong the game will give the corresponding error report.
	 * @version From DummyCore 1.0. 
	 * @Warning From DummyCore 1.1 no longer the function registerConfigurationHandler must be called.
	 * 
	 */
	public static void registerModAbsolute(Class c, String modname, String configPath, IDummyConfig config) throws IOException
	{
		registerMod(c,modname);
		registerLangFileForMod(c,configPath);
		registerConfigurationFileForMod(c,configPath);
		registerConfigurationHandler(config,c);
		loadConfigForMod(getIdForMod(c));
	}
	
	private static int getNextModId()
	{
		int i = 0;
		while(i < 512)
		{
			if(!modList.containsValue(i))
			{
				break;
			}
			++i;
		}
		return i;
	}
	
	/**
	 * Used to get the ID of the mod using .class file.
	 * @param m - the class file of the mod.
	 * @return The corresponding ID of the mod.
	 * @version From DummyCore 1.0
	 */
	public static int getIdForMod(Class m)
	{
		if(modList.containsKey(m))
		{
			return (Integer)modList.get(m);
		}
		return 0;
	}
	
	/**
	 * Used to get the name of the mod using .class file.
	 * @param i - the id of the mod.
	 * @return The corresponding name of the mod.
	 * @version From DummyCore 1.0
	 */
	public static String getModName(int i)
	{
		return (String)modNameList.get(i);
	}
	
	/**
	 * Used to get the config file of the mod using .class file.
	 * @param c - the class file of the mod.
	 * @return The corresponding config file of the mod.
	 * @version From DummyCore 1.0
	 */
	public static Configuration getConfigFileForMod(Class c)
	{
		return config[getIdForMod(c)];
	}
	
	/**
	 * Used to get the lang file of the mod using .class file.
	 * @param c - the class file of the mod.
	 * @return The corresponding lang file of the mod.
	 * @version From DummyCore 1.0
	 */
	public static Configuration getLangFileForMod(Class c)
	{
		return (Configuration) langFilesList.get(getIdForMod(c));
	}
	
	/**
	 * Used to register new Configuration Handlers of the mod. 
	 * @param config - the initialised! object, that implements IDummyConfig.
	 * @param c - the class file of the mod. Use getClass(). Should only be called in the mod itself.
	 * @throws RuntimeException
	 * @version From DummyCore 1.0
	 */
	private static void registerConfigurationHandler(IDummyConfig config, Class c) throws RuntimeException
	{
		int modId = getIdForMod(c);
		if(configurationHandlers[modId] == null)
		{
			configurationHandlers[modId] = config;
		}else
		{
			throw new RuntimeException("Configuration handler for mod "+getModName(modId)+" is already registered!");
		}
	}
	
	private static void loadConfigForMod(int t) throws RuntimeException
	{
		if(!isConfigLoaded[t])
		{
				if(configurationHandlers[t] != null && config[t] != null)
				{
					config[t].load();
					configurationHandlers[t].load(config[t]);
					config[t].save();
				}
				else
					if((configurationHandlers[t] == null && config[t] != null) || (configurationHandlers[t] != null && config[t] == null))
					{
						//throw new RuntimeException("Either the configuration handler for config was not registered, or the IConfig was not registered. The ID for both elements is "+t);
					}
			isConfigLoaded[t] = true;
		}else
		{
			throw new RuntimeException("Configuration handler was already initialised!");
		}
	}
	
	/**
	 * Use this to get the creativetab of all mod items.
	 * @param c - the .class file of the mod.
	 * @return The corresponding Creative Tab
	 * @version From DummyCore 1.0
	 */
	public static CreativeTabs getItemTabForMod(Class c)
	{
		return itemsTabs[getIdForMod(c)];
	}
	
	/**
	 * Use this to get the creativetab of all mod blocks.
	 * @param c - the .class file of the mod.
	 * @return The corresponding Creative Tab
	 * @version From DummyCore 1.0
	 */
	public static CreativeTabs getBlockTabForMod(Class c)
	{
		return blocksTabs[getIdForMod(c)];
	}

}
