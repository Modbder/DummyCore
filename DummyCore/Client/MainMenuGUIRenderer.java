package DummyCore.Client;

import DummyCore.Utils.IMainMenu;

/**
 * Internal. Undocumented.
 * @author modbder
 *
 */
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
