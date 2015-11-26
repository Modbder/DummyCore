package DummyCore.Client;

import DummyCore.Utils.IOldCubicBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * This interface is used for rendering blocks with rotation possibilities. 
 * @see
 * {@link DummyCore.Client.ISBRH.RenderAnvil}
 */
public interface IRotationProvider extends IOldCubicBlock {
	
	/**
	 * Gets the rotation for the given position on the world
	 * @param world - the world we are in
	 * @param x - x position of the block
	 * @param y - y position of the block
	 * @param z - z position of the block
	 * @param state - the current BlockState
	 * @return the side to rotate to
	 */
	public EnumFacing getRotation(IBlockAccess world, int x, int y, int z, IBlockState state);

}
