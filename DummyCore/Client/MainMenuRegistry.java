package DummyCore.Client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyData;
import DummyCore.Utils.IMainMenu;
import DummyCore.Utils.Notifier;

public class MainMenuRegistry {
	
	public static List<Class<? extends GuiScreen>> menuList = new ArrayList<Class<? extends GuiScreen>>();
	
	public static List<DummyData> menuInfoLst = new ArrayList<DummyData>();
	
	public static boolean isGuiDisplayed;
	
	public static GuiScreen currentScreen;
	
	public static void registerNewGui(Class<? extends GuiScreen> menu)
	{
		if(IMainMenu.class.isAssignableFrom(menu))
		{
			menuList.add(menu);
			menuInfoLst.add(new DummyData(menu.getName(),"No description provided by author ;("));
		}else
		{
			Notifier.notifyCustomMod("DummyCore", "Attempting to register "+menu+" as a main menu, but the registered object does not implements IMainMenu!");
		}
	}
	
	public static void registerNewGui(Class<? extends GuiScreen> menu, String name, String description)
	{
		if(IMainMenu.class.isAssignableFrom(menu))
		{
			menuList.add(menu);
			menuInfoLst.add(new DummyData(name,description));
		}else
		{
			Notifier.notifyCustomMod("DummyCore", "Attempting to register "+menu+" as a main menu, but the registered object does not implements IMainMenu!");
		}
	}
	
	public static void newMainMenu(int index)
	{
		try
		{
			if(menuList.size() < index)
			{
				index = menuList.size()-1;
			}
			DummyConfig.setMainMenu(index);
			currentScreen = menuList.get(DummyConfig.getMainMenu()).newInstance();
			Minecraft.getMinecraft().displayGuiScreen(currentScreen);
		}catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	public static GuiScreen getGuiDisplayed()
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen != null && mc.currentScreen.getClass() != GuiMainMenu.class && mc.currentScreen instanceof IMainMenu)
			return mc.currentScreen;
		if(currentScreen != null)
			return currentScreen;
		try 
		{
			return menuList.get(DummyConfig.getMainMenu()).newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
