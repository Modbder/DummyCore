package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * This used to be a helper in 1.7 code
 * <br>I guess it can still be used in 1.8
 * <br>Objects of this class represent a full data about a block in the given location
 * <br>Can be used to store some info
 * @author modbder
 *
 */
public class BlockPosition {
	
	public int x;
	public int y;
	public int z;
	public Block blk;
	public int metadata;
	public TileEntity blockTile;
	public World wrld;
	
	public BlockPosition(World w, int posX, int posY, int posZ)
	{
		wrld = w;
		x = posX;
		y = posY;
		z = posZ;
		BlockPos bp = new BlockPos(posX, posY, posZ);
		IBlockState bs = w.getBlockState(bp);
		blk = bs.getBlock();
		metadata = blk.getMetaFromState(bs);
		blockTile = w.getTileEntity(bp);
	}

}
