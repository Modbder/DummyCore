package DummyCore.Client;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This is basically your ISBRH from 1.7. This allows your blocks to have a dynamical model based on the world location.
 * @author modbder
 *
 */
public interface ISimpleBlockRenderingHandler {
	
	/**
	 * Renders a block in world. 
	 * @param world - the world the block is rendered in
	 * @param b - the block to render
	 * @param pos - position of the block in the world
	 * @param bakery - the DynamicalModelBakery object
	 * @param model - the model we are working with
	 */
	public void renderWorldBlock(IBlockAccess world, Block b, BlockPos pos, DynamicModelBakery bakery, SBRHAwareModel model);

	/**
	 * Renders a block in the inventory
	 * @param stack - the ItemStack to render
	 * @param bakery - the DynamicalModelBakery object
	 * @param model - the model we are working with
	 */
	public void renderInventoryBlock(ItemStack stack, DynamicModelBakery bakery, SBRHAwareModel model);
	
	/**
	 * @return The render ID for your handler. Can be the same as other render IDs
	 */
	public int getRenderID();
	
	/**
	 * @return True to render your block 3d in the inventory GUI, false if not
	 */
	public boolean render3DInInventory();
}
