package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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
