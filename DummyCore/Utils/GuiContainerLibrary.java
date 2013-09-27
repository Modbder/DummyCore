package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

public class GuiContainerLibrary {
	private static List<Gui> gui = new ArrayList();
	private static List<Container> container = new ArrayList();
	private static List<Integer> regInt = new ArrayList();
	
	public static void registerGuiContainer(int id, Container c, Gui g)
	{
		regInt.add(id);
		container.add(c);
		gui.add(g);
	}
	
	public static Container getContainerByID(int id)
	{
		int i = getListArg(id);
		if(i > 0)
		{
			return container.get(i);
		}
		return null;
	}
	
	public static Gui getGuiByID(int id)
	{
		int i = getListArg(id);
		if(i > 0)
		{
			return gui.get(i);
		}
		return null;
	}
	
	private static int getListArg(int id)
	{
		if(regInt.contains(id))
		{
			for(int i = 0; i < regInt.size(); ++i)
			{
				if(regInt.get(i) == id)
					return regInt.get(i);
			}
		}
		return -1;
	}
}
