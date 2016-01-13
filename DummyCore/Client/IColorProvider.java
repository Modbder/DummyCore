package DummyCore.Client;

import DummyCore.Utils.IOldCubicBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * Use this in your block if it has something to do with colors of the block, not the texture
 *
 */
public interface IColorProvider extends IOldCubicBlock {

	public abstract int getColorFor(IBlockAccess world, int x, int y, int z, EnumFacing side);
}
