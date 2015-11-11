package DummyCore.Client.ISBRH;

import DummyCore.Client.DynamicModelBakery;
import DummyCore.Client.ISimpleBlockRenderingHandler;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Client.SBRHAwareModel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RenderHorizontalCross implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderWorldBlock(IBlockAccess world, Block b, BlockPos pos, DynamicModelBakery bakery,SBRHAwareModel model) {
		bakery.setRenderBoundsFromBlock(b);
		bakery.addHorizontalCrossedSquares();
	}

	@Override
	public void renderInventoryBlock(ItemStack stack, DynamicModelBakery bakery, SBRHAwareModel model) {
		Block.getBlockFromItem(stack.getItem()).setBlockBoundsForItemRender();
		bakery.setRenderBoundsFromBlock(Block.getBlockFromItem(stack.getItem()));
		bakery.addHorizontalCrossedSquares();
	}

	@Override
	public int getRenderID() {
		return RenderAccessLibrary.RENDER_ID_HORIZONTAL_CROSS;
	}
	
	@Override
	public boolean render3DInInventory() {
		return false;
	}
	
}
