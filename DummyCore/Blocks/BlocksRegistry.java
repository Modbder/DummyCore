package DummyCore.Blocks;

import java.util.Hashtable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Items.MultiItem;
import DummyCore.Utils.IDummyMultiBlock;
import DummyCore.Utils.IDummyMultiItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description Use this class to register new blocks in your game.
 *  This will automatically add your blocks to the corresponding creative tab
 */
public class BlocksRegistry {
	
	/**
	 * Used to check the creative tab block belongs to
	 */
	public static Hashtable<Block,String> blocksList = new Hashtable();
	
	/**
	 * Use this to register new simple blocks.
	 * @version From DummyCore 1.0
	 * @param b - the block to be registered.
	 * @param name - in-game name of the block. Will be written to the corresponding .lang file
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 * @param blockClass - used, if you want to register a block, that has an ItemBlock. Can be null.
	 */
	public static void registerBlock(Block b, String name, Class modClass, Class blockClass)
	{
		String blockName = name;
		Configuration conf = Core.getLangFileForMod(modClass);
		conf.load();
		blockName = conf.get("Blocks Names", b.getUnlocalizedName(), name).getString();
		conf.save();
		if(blockClass == null)
		{
			GameRegistry.registerBlock(b, Core.getModName(Core.getIdForMod(modClass))+".block."+name);
		}else
		{
			GameRegistry.registerBlock(b, blockClass, Core.getModName(Core.getIdForMod(modClass))+".block."+name);
		}
		LanguageRegistry.addName(b, blockName);
		b.setCreativeTab(Core.getBlockTabForMod(modClass));
		blocksList.put(b, Core.getBlockTabForMod(modClass).getTabLabel());
	}
	
	/**
	 * Use this to add your block to the MultiBlock storage system. This will add your block to the DummyCore creative tab. This does not requires creating new Block variable, and allows you to register lots of blocks, while not using more ID's
	 * @param unlocalisedName - the name of your block in code. Always remember to check this! This is the way to get your blocks if you need to!
	 * @param inGameName - in-game name of the block. Will be written to the corresponding .lang file
	 * @param texturePath - path to the texture file, that will be used for this block. Should be modname:texturename
	 * @param multiBlockHandler - should be an initialized! object, that implements IDummyMultiBlock. Usually it is just an empty .class file. Used to handle different actions, happening with your multiblock. Always remember to check for your block's name! Can be null, then none of actions will be handled.
	 * @param modClass - class file of your mod. If registered from the mod itself, use getClass(), else just put in this field something like YourModClassName.class
	 * @version From DummyCore 1.1
	 */
	public static void registerMultiBlock(String unlocalisedName, String inGameName, String texturePath, IDummyMultiBlock multiBlockHandler, Class modClass)
	{
		String blockName = inGameName;
		Configuration conf = Core.getLangFileForMod(modClass);
		conf.load();
		blockName = conf.get("Blocks Names", unlocalisedName, inGameName).getString();
		conf.save();
		if(multiBlockHandler != null)
		{
			MultiBlock.registerMultiBlock(unlocalisedName, inGameName, texturePath, multiBlockHandler);
		}else
		{
			MultiBlock.registerMultiBlock(unlocalisedName, inGameName, texturePath);
		}
		
	}
	
	/**
	 * Used to handle the single id for all of the multiblocks. Do not change this!
	 * @return the id of all the multiblocks
	 * @version From DummyCore 1.1
	 */
	public static int getMultiBlockID()
	{
		return CoreInitialiser.mBlock.blockuid;
	}

}
