package DummyCore.Utils;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import DummyCore.Core.Core;
import DummyCore.Core.DCMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;


public class LoadingUtils {
	
	public static final ArrayList<String> knownBigASMModifiers = new ArrayList<String>();
	
	public static void makeACrash(String message,Class<?> thrower, Throwable t, boolean shutdown)
	{
		Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]",message);
		Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Loading errors were detected at state "+Loader.instance().getLoaderState()+"!");
		Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","The class that has thrown the error: "+thrower);
		DCMod[] ofsetters = tryDetermineOffendorsFromClasses(tryDetermineOffendorsFromTrace(t));
		for(DCMod mod : ofsetters)
			Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Likely involved mod registered via DummyCore: "+mod);
		for(ModContainer mCon : tryDetermineModOffendorsFromClasses(tryDetermineOffendorsFromTrace(t)))
			Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Likely involved mod: "+mCon.getName()+"[modid:"+mCon.getModId()+",modClasspath:"+(mCon.getMod() != null ? mCon.getMod().getClass() : "NULL{mod has no mod instance?}")+"]");
		
		if(shutdown)
			FMLCommonHandler.instance().getSidedDelegate().haltGame(message, t);
	}
	
	public static void makeACrash(String message,Throwable t, boolean shutdown)
	{
		Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Loading errors were detected at state "+Loader.instance().getLoaderState()+"!");
		DCMod[] ofsetters = tryDetermineOffendorsFromClasses(tryDetermineOffendorsFromTrace(t));
		for(DCMod mod : ofsetters)
			Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Likely involved mod registered via DummyCore: "+mod);
		for(ModContainer mCon : tryDetermineModOffendorsFromClasses(tryDetermineOffendorsFromTrace(t)))
			Notifier.notify(shutdown ? Level.FATAL : Level.ERROR, "", Notifier.error,"[DummyCoreLoader]","Likely involved mod: "+mCon.getName()+"[modid:"+mCon.getModId()+",modClasspath:"+(mCon.getMod() != null ? mCon.getMod().getClass() : "NULL{mod has no mod instance?}")+"]");
		
		if(shutdown)
			FMLCommonHandler.instance().getSidedDelegate().haltGame(message, t);
	}
	
	public static Class<?>[] tryDetermineOffendorsFromTrace(Throwable t)
	{
		StackTraceElement[] elements = t.getStackTrace();
		Class<?>[] offendors = new Class[elements.length];
		int i = -1;
		for(StackTraceElement ste : elements)
		{
			++i;
			String clsName = ste.getClassName();
			try
			{
				Class<?> offendor = Class.forName(clsName);
				if(offendor != null)
					offendors[i] = offendor;
			}
			catch(ClassNotFoundException cnfe)
			{
				continue;
			}
		}
		return offendors;
	}
	
	public static DCMod[] tryDetermineOffendorsFromClasses(Class<?>... detectedInTrace)
	{
		DCMod[] retMods = new DCMod[0];
		
		for(Class<?> clazz : detectedInTrace)
		{
			if(Core.isModRegistered(clazz))
			{
				DCMod[] newArray = new DCMod[retMods.length+1];
				System.arraycopy(retMods, 0, newArray, 0, retMods.length);
				newArray[newArray.length-1] = Core.getModFromClass(clazz);
				retMods = newArray;
			}
		}
		
		return retMods;
	}
	
	public static ModContainer[] tryDetermineModOffendorsFromClasses(Class<?>... detectedInTrace)
	{
		ModContainer[] retMods = new ModContainer[0];
		
		for(Class<?> clazz : detectedInTrace)
		{
			for(int i = 0; i < Loader.instance().getActiveModList().size(); ++i)
			{
				ModContainer mc = Loader.instance().getActiveModList().get(i);
				if(mc.getMod() != null && mc.getMod().getClass().equals(clazz))
				{
					ModContainer[] newArray = new ModContainer[retMods.length+1];
					System.arraycopy(retMods, 0, newArray, 0, retMods.length);
					newArray[newArray.length-1] = mc;
					retMods = newArray;
				}
			}
		}
		
		return retMods;
	}

}
