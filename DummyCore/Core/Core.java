package DummyCore.Core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import DummyCore.Utils.IDummyConfig;
import DummyCore.Utils.LoadingUtils;
import DummyCore.Utils.Notifier;

/**
 * The main class for all registration in DC
 * @author modbder
 *
 */
public class Core {
	
	public static final ArrayList<DCMod> registeredMods = new ArrayList<DCMod>();
	public static File mcDir;
	
	/**
	 * Checks if the class is a valid DCMod
	 * @param mod - the class to check
	 * @return true if the class is registered as a DCMod, false otherwise
	 */
	public static boolean isModRegistered(Class<?> mod)
	{
		for(DCMod dcm : registeredMods)
			if(dcm.modClass.equals(mod))
				return true;
		
		return false;
	}
	
	/**
	 * Gets the DCMod object for class
	 * @param c - the class to get the mod from
	 * @return null if no mod for the given class is present, instanceof DCMod otherwise
	 */
	public static DCMod getModFromClass(Class<?> c)
	{
		for(DCMod dcm : registeredMods)
			if(dcm.modClass.equals(c))
				return dcm;
		
		return null;
	}
	
	//Internal
	private static void registerMod(Class<?> c, String name, boolean addCreativeTabs)
	{
		if(!isModRegistered(c))
		{
			DCMod mod = new DCMod(c,name);
			if(addCreativeTabs)
			{
				mod.blocks = new CreativePageBlocks(name);
				mod.items = new CreativePageItems(name);
			}
			registeredMods.add(mod);
		}else
		{
			Notifier.notifyError(name+"[classPath:"+c+"]Already has a DummyCore mod associated with it(is already registered), ignoring");
		}
	}

	//Internal
	private static void registerConfigurationFileForMod(Class<?> c, String path)
	{
		try
		{
			if(!isModRegistered(c))
			{
				LoadingUtils.makeACrash("[DummyCore]Catched an attempt to register configuration file for not registered mod, this should not be possible and many things will go wrong as a result!", new IllegalStateException(c+" Is not a valid DCMod!"), false);
			}else
			{
				DCMod mod = getModFromClass(c);
				File file = new File(path,mod.ufName+".cfg");
				if(file.isDirectory())
					file.delete();
				
				if(!file.exists())
					file.createNewFile();
				
				Configuration cfg = new Configuration(file);
				cfg.save();
				mod.injectFMLConfig(cfg);
				Notifier.notifySimple("Configuration File for mod "+mod+" was successfully created with path "+path+mod.ufName+".cfg");
			}
		}
		catch(IOException e)
		{
			DCMod mod = getModFromClass(c);
			LoadingUtils.makeACrash("[DummyCore]Could not create a config file for mod "+mod+" - check your file system!", e, true);
		}
	}
	
	/**
	 * Use this in you Pre-Initialization functions. This will register your mod in the DummyCore system and automatically create all config files.
	 * From DummyCore v1.1 you no longer need to use IMCEvent to register all blocks and items.
	 * @param c - class file of your mod. Use getClass().
	 * @param modname - this name will be used to name CreativeTabs and config files.
	 * @param configPath - the path to your configuration file. You can use FMLPreInitializationEvent.getModConfigurationDirectory().getAbsolutePath() to get your path.
	 * @param config - the initialized! object, that implements IDummyConfig.
	 * @version From DummyCore 1.0. 
	 * @Warning From DummyCore 1.1 no longer the function registerConfigurationHandler must be called.
	 * 
	 */
	public static void registerModAbsolute(Class<?> c, String modname, String configPath, IDummyConfig config)
	{
		registerModAbsolute(c,modname,configPath,config,true);
	}
	
	/**
	 * Use this in you Pre-Initialization functions. This will register your mod in the DummyCore system and automatically create all config files.
	 * From DummyCore v1.1 you no longer need to use IMCEvent to register all blocks and items.
	 * @param c - class file of your mod. Use getClass().
	 * @param modname - this name will be used to name CreativeTabs and config files.
	 * @param configPath - the path to your configuration file. You can use FMLPreInitializationEvent.getModConfigurationDirectory().getAbsolutePath() to get your path.
	 * @param config - the initialized! object, that implements IDummyConfig.
	 * @param addCreativeTabs if the custom creative tabs for your mod should be created.
	 * @version From DummyCore 2.0. 
	 * 
	 */
	public static void registerModAbsolute(Class<?> c, String modname, String configPath, IDummyConfig config, boolean addCreativeTabs)
	{
		registerMod(c,modname,addCreativeTabs);
		registerConfigurationFileForMod(c,configPath);
		registerConfigurationHandler(config,c);
		loadConfigForMod(c);
	}
	
	/**
	 * Used to get the config file of the mod using .class file.
	 * @param c - the class file of the mod.
	 * @return The corresponding config file of the mod.
	 * @version From DummyCore 2.0
	 */
	public static Configuration getConfigFileForMod(Class<?> c)
	{
		if(!isModRegistered(c))
		{
			Notifier.notifyError("Catched an attempt to get configuration for a not registered mod, things are about go wery wrong! Offendor: "+c);
			return null;
		}
		return getModFromClass(c).fmlCfg;
	}
	
	/**
	 * Used to register new Configuration Handlers of the mod. 
	 * @param config - the initialised! object, that implements IDummyConfig.
	 * @param c - the class file of the mod. Use getClass(). Should only be called in the mod itself.
	 * @throws RuntimeException
	 * @version From DummyCore 2.0
	 */
	private static void registerConfigurationHandler(IDummyConfig config, Class<?> c)
	{
		if(!isModRegistered(c))
		{
			LoadingUtils.makeACrash("[DummyCore]Catched an attempt to register IDummyConfig for not registered mod, this should not be possible and many things will go wrong as a result!", new IllegalStateException(c+" Is not a valid DCMod!"), false);
		}else
		{
			DCMod mod = getModFromClass(c);
			mod.injectConfig(config);
			Notifier.notifySimple("IDummyConfing for mod "+mod+" was successfully created");
		}
	}
	
	//Internal
	public static void loadConfigForMod(Class<?> c)
	{
		if(!isModRegistered(c))
		{
			LoadingUtils.makeACrash("[DummyCore]Catched an attempt to load IDummyConfig for not registered mod, this should not be possible and many things will go wrong as a result!", new IllegalStateException(c+" Is not a valid DCMod!"), false);
		}else
		{
			DCMod mod = getModFromClass(c);
			mod.fmlCfg.load();
			mod.cfg.load(mod.fmlCfg);
			mod.fmlCfg.save();
		}
	}
	
	/**
	 * Use this to get the creativetab of all mod items.
	 * @param c - the .class file of the mod.
	 * @return The corresponding Creative Tab
	 * @version From DummyCore 2.0
	 */
	public static CreativeTabs getItemTabForMod(Class<?> c)
	{
		if(!isModRegistered(c))
		{
			Notifier.notifyError("Catched an attempt to get CreativeTabs for a not registered mod, things are about go wery wrong! Offendor: "+c);
			return null;
		}
		return getModFromClass(c).items;
	}
	
	/**
	 * Use this to get the creativetab of all mod blocks.
	 * @param c - the .class file of the mod.
	 * @return The corresponding Creative Tab
	 * @version From DummyCore 2.0
	 */
	public static CreativeTabs getBlockTabForMod(Class<?> c)
	{
		if(!isModRegistered(c))
		{
			Notifier.notifyError("Catched an attempt to get CreativeTabs for a not registered mod, things are about go wery wrong! Offendor: "+c);
			return null;
		}
		return getModFromClass(c).blocks;
	}

}
