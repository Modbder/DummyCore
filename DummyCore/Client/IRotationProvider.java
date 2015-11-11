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
	
	public EnumFacing getRotation(IBlockAccess world, int x, int y, int z, IBlockState state);

}
