package DummyCore.Client.ISBRH;

import DummyCore.Client.DynamicModelBakery;
import DummyCore.Client.IBlockConnector;
import DummyCore.Client.ISimpleBlockRenderingHandler;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Client.SBRHAwareModel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RenderConnectedToBlock implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderWorldBlock(IBlockAccess world, Block b, BlockPos pos, DynamicModelBakery bakery,SBRHAwareModel model) {
		bakery.setRenderBoundsFromBlock(b);
		bakery.forceRenderAllFaces();
		if(b instanceof IBlockConnector)
			bakery.addConnectedBlockFaces();
		else
			bakery.addHorizontalFacesWithOffset(0.438);
		bakery.disableRenderAllFaces();
	}

	@Override
	public void renderInventoryBlock(ItemStack stack, DynamicModelBakery bakery, SBRHAwareModel model) {
		Block.getBlockFromItem(stack.getItem()).setBlockBoundsForItemRender();
		bakery.setRenderBoundsFromBlock(Block.getBlockFromItem(stack.getItem()));
		bakery.addHorizontalFacesWithOffset(0.438);
	}

	@Override
	public int getRenderID() {
		return RenderAccessLibrary.RENDER_ID_CONNECTED_TO_BLOCK;
	}
	
	@Override
	public boolean render3DInInventory() {
		return false;
	}
	
}
