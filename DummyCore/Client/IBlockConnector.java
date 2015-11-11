package DummyCore.Client;

import DummyCore.Utils.IOldCubicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * This is only used for rendering checks.
 * <br>This allows a block to be visually 'connected' to another block
 * <br>This will render an additional icon if the checks are correct.
 * @see {@link DummyCore.Client.ISBRH.RenderConnectedToBlock}
 * {@link DummyCore.Client.DynamicModelBakery#addConnectedBlockFaces()}
 */
public interface IBlockConnector extends IOldCubicBlock{
	
	/**
	 * Determines if the block at the given position should be connected to another one.
	 * @param world - The world we are in
	 * @param pos - The position to connect/not connect to
	 * @param originalPos - The position of the block being rendered
	 * @param face - The current face we are checking against. It always goes in a N-S-W-E order
	 * @param state - The BlockState of the block being rendered
	 * @return True if the given side should render 'connected' false otherwise
	 */
	public boolean connectsTo(IBlockAccess world, BlockPos pos, BlockPos originalPos, EnumFacing face, IBlockState state);	

	/**
	 * Gets the 'connected' icon to render. You do not need to have 2 rotation variants - the necessary transformations are automatically applied.
	 * @param world - The world we are in
	 * @param x - x position of the block
	 * @param y - y position of the block
	 * @param z - z position of the block
	 * @return The Icon to render
	 */
	public Icon getConnectionIcon(IBlockAccess world, int x, int y, int z);
}
