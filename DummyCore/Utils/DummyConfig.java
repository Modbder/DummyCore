package DummyCore.Utils;

import net.minecraftforge.common.config.Configuration;

public class DummyConfig implements IDummyConfig{

	public int MultiItemUID;
	public int MultiBlockUID;
	private static int mainMenuID;
	public boolean removeMissingTexturesErrors;
	public static boolean enableNotifierLogging;
	public static boolean shouldChangeImage;
	public static Configuration cfg;
	public void load(Configuration config)
	{
		cfg = config;
		removeMissingTexturesErrors = config.getBoolean("removeMissingTexturesErrors", "GLOBAL", true, "");
		enableNotifierLogging = config.getBoolean("enableNotifierLogging", "GLOBAL", true, "");
		shouldChangeImage = config.getBoolean("shouldChangeImageInCreativeTabs", "GLOBAL", true, "");
		mainMenuID = config.getInt("mainMenuID", "GLOBAL", 0,0,Integer.MAX_VALUE, "");
	}
	
	public static void setMainMenu(int i)
	{
		cfg.load();
		cfg.get("GLOBAL", "mainMenuID", 0, "").set(i);
		mainMenuID = i;
		cfg.save();
	}
	
	public static int getMainMenu()
	{
		return mainMenuID;
	}
}
