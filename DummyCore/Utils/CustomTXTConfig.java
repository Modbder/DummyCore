package DummyCore.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Hashtable;

import DummyCore.Core.Core;
import cpw.mods.fml.common.FMLCommonHandler;

public class CustomTXTConfig {
	
	public static final Hashtable<String,String> mappings = new Hashtable<String,String>();
	public static boolean init = false;
	
	public static void createCFG()
	{
		try
		{
			File cfgDir = new File(Core.mcDir,"config");
			if(!cfgDir.exists())
				cfgDir.mkdirs();
			
			File actualCfg = new File(cfgDir,"DummyCoreASMSettings.cfg");
			if(!actualCfg.exists())
				createDefaultCFG(actualCfg);
			
			readCfg(actualCfg);
			init = true;
		}
		catch(Exception e)
		{
			FMLCommonHandler.instance().raiseException(e, "[DummyCore]Something went wrong while trying to create ASM configuration!", true);
		}
	}
	
	public static void createDefaultCFG(File cfgFile)
	{
		try
		{
			if(!cfgFile.exists())
				cfgFile.createNewFile();
			PrintWriter pw = new PrintWriter(cfgFile,"UTF-8");
			pw.println("#Fix vanilla's mob packet issue?");
			pw.println("#This is not really a fix though...");
			pw.println("#If this breaks your game - turn this option to false");
			pw.println("fixS0FSpawnMobPacketCrash=true");
			pw.println("#Insert a simple call in FML's texture loader?");
			pw.println("#This enables the disabling texture errors feature of DummyCore");
			pw.println("#You can set this to false if ASM crashes you");
			pw.println("insertDCCallInTextureLoader=true");
			pw.flush();
			pw.close();
		}
		catch(Exception e)
		{
			FMLCommonHandler.instance().raiseException(e, "[DummyCore]Something went wrong while trying to create ASM configuration!", true);
		}
	}
	
	public static void readCfg(File cfgFile)
	{
		mappings.clear();
		try
		{
			FileReader fr = new FileReader(cfgFile);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null)
			{
				if(line.startsWith("#"))
					continue;
				if(line.indexOf('=') == -1)
					continue;
				mappings.put(line.substring(0, line.indexOf('=')), line.substring(line.indexOf('=')+1));
			}
			br.close();
			fr.close();
		}
		catch(Exception e)
		{
			LoadingUtils.makeACrash("[DummyCore]Failed to read ASM settings!", CustomTXTConfig.class, e, false);
		}
	}

}
