package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;

public class GuiContainerLibrary {
	public static List<String> guis = new ArrayList<String>();
	public static List<String> containers = new ArrayList<String>();
	
	/**
	 * Can be used for easier GUI opening, if you do not want to make a Proxy for that. This method should only be used to open simple GUIs(tile entity ones)
	 * @param guiClassPath - the path to GUI class. Gui class should extend GuiCommon!
	 * @param containerClassPath - the path to your Container class.
	 * @return the ID with which you should use to open your GUI on block right-click.
	 */
	public static int registerGuiContainer(String guiClassPath, String containerClassPath)
	{
		int lstSize = guis.size();
		try
		{
			guis.add(guiClassPath);
			containers.add(containerClassPath);
		}catch(Exception e)
		{
			Notifier.notifySimple("Unable to register GUI-Container with ID "+lstSize);
			e.printStackTrace();
			return -1;
		}
		return lstSize;
	}
}
