package DummyCore.Client.ISBRH;

import DummyCore.Client.DynamicModelBakery;
import DummyCore.Client.IRotationProvider;
import DummyCore.Client.ISimpleBlockRenderingHandler;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Client.SBRHAwareModel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class RenderAnvil implements ISimpleBlockRenderingHandler {
	
	@Override
	public void renderWorldBlock(IBlockAccess world, Block b, BlockPos pos, DynamicModelBakery bakery,SBRHAwareModel model) {
		EnumFacing face = EnumFacing.UP;
		if(b instanceof IRotationProvider)
			face = IRotationProvider.class.cast(b).getRotation(world, pos.getX(), pos.getY(), pos.getZ(), world.getBlockState(pos));
		bakery.forceRenderAllFaces();
		if(face == EnumFacing.NORTH || face == EnumFacing.SOUTH)
		{
			bakery.setRenderBounds(0.2, 0.6, 0.2, 0.8, 1, 0.8);
			bakery.addCube();
			bakery.setRenderBounds(0.3, 0.75, 0.8, 0.7, 1, 0.9);
			bakery.addCube();
			bakery.setRenderBounds(0.4, 0.85, 0.9, 0.6, 1, 1);
			bakery.addCube();
			bakery.setRenderBounds(0.3, 0.75, 0.1, 0.7, 1, 0.2);
			bakery.addCube();
			bakery.setRenderBounds(0.4, 0.85, 0, 0.6, 1, 0.1);
			bakery.addCube();
			bakery.setRenderBounds(0.4, 0.3, 0.3, 0.6, 0.6, 0.7);
			bakery.addCube();
			bakery.setRenderBounds(0.3, 0.25, 0.2, 0.7, 0.3, 0.8);
			bakery.addCube();
			bakery.setRenderBounds(0.2, 0, 0.1, 0.8, 0.25, 0.9);
			bakery.addCube();
			bakery.clearRenderBounds();
		}else
		{
			bakery.setRenderBounds(0.2, 0.6, 0.2, 0.8, 1, 0.8);
			bakery.addCube();
			bakery.setRenderBounds(0.8, 0.75, 0.3, 0.9, 1, 0.7);
			bakery.addCube();
			bakery.setRenderBounds(0.9, 0.85, 0.4, 1, 1, 0.6);
			bakery.addCube();
			bakery.setRenderBounds(0.1, 0.75, 0.3, 0.2, 1, 0.7);
			bakery.addCube();
			bakery.setRenderBounds(0, 0.85, 0.4, 0.1, 1, 0.6);
			bakery.addCube();
			bakery.setRenderBounds(0.3, 0.3, 0.4, 0.7, 0.6, 0.6);
			bakery.addCube();
			bakery.setRenderBounds(0.2, 0.25, 0.3, 0.8, 0.3, 0.7);
			bakery.addCube();
			bakery.setRenderBounds(0.1, 0, 0.2, 0.9, 0.25, 0.8);
			bakery.addCube();
			bakery.clearRenderBounds();
		}
		bakery.disableRenderAllFaces();
	}

	@Override
	public void renderInventoryBlock(ItemStack stack, DynamicModelBakery bakery, SBRHAwareModel model) {
		bakery.setRenderBounds(0.2, 0.6, 0.2, 0.8, 1, 0.8);
		bakery.addCube();
		bakery.setRenderBounds(0.3, 0.75, 0.8, 0.7, 1, 0.9);
		bakery.addCube();
		bakery.setRenderBounds(0.4, 0.85, 0.9, 0.6, 1, 1);
		bakery.addCube();
		bakery.setRenderBounds(0.3, 0.75, 0.1, 0.7, 1, 0.2);
		bakery.addCube();
		bakery.setRenderBounds(0.4, 0.85, 0, 0.6, 1, 0.1);
		bakery.addCube();
		bakery.setRenderBounds(0.4, 0.3, 0.3, 0.6, 0.6, 0.7);
		bakery.addCube();
		bakery.setRenderBounds(0.3, 0.25, 0.2, 0.7, 0.3, 0.8);
		bakery.addCube();
		bakery.setRenderBounds(0.2, 0, 0.1, 0.8, 0.25, 0.9);
		bakery.addCube();
		bakery.clearRenderBounds();
	}

	@Override
	public int getRenderID() {
		return RenderAccessLibrary.RENDER_ID_ANVIL;
	}
	
	@Override
	public boolean render3DInInventory() {
		return true;
	}
	
}
