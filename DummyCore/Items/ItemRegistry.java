package DummyCore.Items;

import java.util.Hashtable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Utils.IDummyMultiItem;
import cpw.mods.fml.common.registry.LanguageRegistry;

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
	public static Hashtable<Item,String> itemsList = new Hashtable();
	
	/**
	 * Use this to register new simple items.
	 * @version From DummyCore 1.0
	 * @param i - the item to be registered.
	 * @param name - in-game name of the item. Will be written to the corresponding .lang file
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 */
	public static void registerItem(Item i, String name, Class modClass)
	{
		String itemName = name;
		Configuration conf = Core.getLangFileForMod(modClass);
		conf.load();
		itemName = conf.get("Items Names", i.getUnlocalizedName(), name).getString();
		conf.save();
		LanguageRegistry.addName(i, itemName);
		i.setCreativeTab(Core.getItemTabForMod(modClass));
		itemsList.put(i, Core.getItemTabForMod(modClass).getTabLabel());
	}
	
	/**
	 * Use this to add your item to the MultiItem storage system. This will add your item to the DummyCore creative tab. This does not requires creating new Item variable, and allows you to register lots of items, while not using more ID's
	 * @param unlocalisedName - the name of your item in code. Always remember to check this! This is the way to get your items if you need to!
	 * @param inGameName - in-game name of the item. Will be written to the corresponding .lang file
	 * @param texturePath - path to the texture file, that will be used for this item. Should be modname:texturename
	 * @param multiItemHandler - should be an initialized! object, that implements IDummyMultiItem. Usually it is just an empty .class file. Used to handle different actions, happening with your multiitem. Always remember to check for your item's name! Can be null, then none of actions will be handled.
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 * @version From DummyCore 1.1
	 */
	public static void registerMultiItem(String unlocalisedName, String inGameName, String texturePath, IDummyMultiItem multiItemHandler, Class modClass)
	{
		String itemName = inGameName;
		Configuration conf = Core.getLangFileForMod(modClass);
		conf.load();
		itemName = conf.get("Items Names", unlocalisedName, inGameName).getString();
		conf.save();
		if(multiItemHandler != null)
		{
			MultiItem.registerMultiItem(unlocalisedName, inGameName, texturePath, multiItemHandler);
		}else
		{
			MultiItem.registerMultiItem(unlocalisedName, inGameName, texturePath);
		}
		
	}
	
	/**
	 * Used to handle the single id for all of the multiitems. Do not change this!
	 * @return the id of all the multiitems
	 * @version From DummyCore 1.1
	 */
	public static int getMultiItemID()
	{
		return CoreInitialiser.mItem.itemuid;
	}

}
