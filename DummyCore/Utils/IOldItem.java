package DummyCore.Utils;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author modbder
 * @Description
 * Allows the rendering of items without the need to create .json files
 * <br>This makes the render system dynamically generate a model for your item
 * <br>In order for this to work for your items you must either register them using DC's {@link DummyCore.Items.ItemRegistry} or call {@link DummyCore.Utils.OldTextureHandler#addOldItem(String, net.minecraft.item.Item)} method somewhere at the preinit phase.
 */
public interface IOldItem {

	/**
	 * Gets the icon of the item based on it's metadata. Unused, since all calls are ItemStack sensitive, but this exists for convenience nonetheless
	 * @param meta - the metadata of the IS
	 * @return The icon for the given metadata
	 */
	public Icon getIconFromDamage(int meta);
	
	/**
	 * Gets the icon from the given ItemStack. Unused, since all calls are renderPass sensitive, but this exists for convenience nonetheless
	 * @param stk - the IS
	 * @return The icon for the given ItemStack
	 */
	public Icon getIconFromItemStack(ItemStack stk);
	
	/**
	 * This is being automatically called for your items each time the registration of textures is necessary. Init all your Icon objects here.
	 * @param ir the registry.
	 */
	public void registerIcons(IconRegister reg);
	
	/**
	 * Gets the render passes for the given ItemStack. This can return any value, but all values < 1 will get rounded to 1 automatically.
	 * @param stk - the IS
	 * @return The amount of render passes required for the ItemStack. Can return 0 and negatives - will get rounded to 1 automatically
	 */
	public int getRenderPasses(ItemStack stk);
	
	/**
	 * Gets the icon for both the given ItemStack and render pass
	 * @param stk - the IS
	 * @param pass - the current render pass, starting from 0 and up
	 * @return The icon for the given pass
	 */
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass);
	
	/**
	 * By default DC will only create the model for the given ItemStack once and store that into the Hashtable for increased performance.
	 * <br>However, if your ItemStack's rendering is complex(for example the Icon is based on the ItemStack's NBT, which can be changed) you might need rerendering of the IS
	 * <br>Returning true here will make DC to recreate the model for the ItemStack each frame which is a heavy performance hit. It will, however, allow you to dynamically change the Icon based on the changeable attributes
	 * @param stk - the IS
	 * @return true to enable model recreation, false otherwise
	 * @see {@link DummyCore.Client.RPAwareModel#handleItemState(ItemStack)}
	 */
	public boolean recreateIcon(ItemStack stk);
	
	/**
	 * This determines if the ItemStack should be rotated and scaled in the third-person perspective to look '3d'-ish, like swords and bones
	 * @param stk - the IS
	 * @return true to rotate and scale, false otherwise
	 */
	public boolean render3D(ItemStack stk);
}
