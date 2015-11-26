package DummyCore.Core;

import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import DummyCore.Utils.IDummyConfig;
import DummyCore.Utils.Notifier;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;

/**
 * A simple DCMod object. Holds some information about a mod. Not intended to use outside of DummyCore, but can be used.
 * @author modbder
 *
 */
public class DCMod{

	public Object modInstance;
	public Class<?> modClass;
	public ModContainer modContainer;
	public String modid;
	public String version;
	public String ufName;
	public IDummyConfig cfg;
	public Configuration fmlCfg;
	public CreativePageBlocks blocks;
	public CreativePageItems items;
	
	public DCMod(Class<?> mod, String uName)
	{
		ufName = uName;
		modClass = mod;
		modContainer = Loader.instance().activeModContainer();
		modInstance = modContainer.getMod();
		modid = modContainer.getModId();
		version = modContainer.getVersion();
		
		if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)){
			Notifier.notifyWarn(String.format("Mod %s is being registered in %s state! This can cause all kinds of unexpected behaviour!",ufName+"[modid:"+modid+",classpath:"+mod+"]",Loader.instance().getLoaderState().toString()));
		}
	}
	
	public DCMod injectConfig(IDummyConfig cfg)
	{
		this.cfg = cfg;
		return this;
	}
	
	public DCMod injectFMLConfig(Configuration cfg)
	{
		this.fmlCfg = cfg;
		return this;
	}
	
	public String toString()
	{
		return ufName+"[modid:"+modid+",classPath:"+modClass+"]";
	}
	
}
