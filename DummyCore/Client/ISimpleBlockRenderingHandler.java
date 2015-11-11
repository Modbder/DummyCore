package DummyCore.Client;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISimpleBlockRenderingHandler {
	
	public void renderWorldBlock(IBlockAccess world, Block b, BlockPos pos, DynamicModelBakery bakery, SBRHAwareModel model);

	public void renderInventoryBlock(ItemStack stack, DynamicModelBakery bakery, SBRHAwareModel model);
	
	public int getRenderID();
	
	public boolean render3DInInventory();
}
