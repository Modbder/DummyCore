package DummyCore.Client;

import DummyCore.Utils.IMainMenu;

public class MainMenuGUIRenderer {
	
	public static IMainMenu currentScreenBeingRendered;
	
	public static void setCurrentGUI(IMainMenu menu)
	{
		currentScreenBeingRendered = menu;
	}
	
	public static void initGui()
	{
	}

}
