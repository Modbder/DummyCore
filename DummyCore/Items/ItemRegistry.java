package DummyCore.Items;

import java.util.Hashtable;

import DummyCore.Core.Core;
import DummyCore.Utils.IOldItem;
import DummyCore.Utils.OldTextureHandler;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description Use this class to register new items in your game.
 *  This will automatically add your items to the corresponding creative tab
 */
public class ItemRegistry {
	
	/**
	 * Used to check the creative tab item belongs to
	 */
	public static Hashtable<Item,String> itemsList = new Hashtable<Item, String>();
	
	/**
	 * Use this to register new simple items.
	 * @version From DummyCore 1.0
	 * @param i - the item to be registered.
	 * @param name - name of the item in the itemregistry
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 */
	public static void registerItem(Item i, String name, Class<?> modClass)
	{
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			i.setCreativeTab(Core.getItemTabForMod(modClass));
			itemsList.put(i, Core.getItemTabForMod(modClass).getTabLabel());
			if(i instanceof IOldItem)
				OldTextureHandler.addOldItem(Core.getModFromClass(modClass).modid+":"+name, i);
		}
		GameRegistry.registerItem(i, name);
	}	
}
