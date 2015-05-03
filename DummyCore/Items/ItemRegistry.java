package DummyCore.Items;

import java.util.Hashtable;

import net.minecraft.item.Item;
import DummyCore.Core.Core;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

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
		}
		GameRegistry.registerItem(i, name);
	}
	
	/**
	 * Use this to register new simple items.
	 * @version From DummyCore 1.0
	 * @param i - the item to be registered.
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 */
	@Deprecated
	public static void registerItem(Item i, Class<?> modClass)
	{
		//Notifier.notifyCustomMod("DummyCore", "[Warning] Mod "+Core.getModName(Core.getIdForMod(modClass))+" tries to register items in an outdated way, may cause errors!");
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			i.setCreativeTab(Core.getItemTabForMod(modClass));
			itemsList.put(i, Core.getItemTabForMod(modClass).getTabLabel());
		}
	}
	
	
}
