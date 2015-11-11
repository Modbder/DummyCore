package DummyCore.Client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * A version of IBakedModel that can be changed based on it's position in the world
 */
@SuppressWarnings("deprecation")
public interface IPosAwareModel extends IBakedModel{
	
	/**
	 * Gets the model corresponding to the block's position in the world and blockstate
	 * @param state current blockstate
	 * @param world current world
	 * @param pos current position
	 * @return A new model with all needed transformations applied.
	 */
	public IBakedModel getModelFromWorldPos(IBlockState state, IBlockAccess world, BlockPos pos);

}
