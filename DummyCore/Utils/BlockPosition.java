package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
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
		blk = w.getBlock(posX, posY, posZ);
		metadata = w.getBlockMetadata(posX, posY, posZ);
		blockTile = w.getTileEntity(posX, posY, posZ);
	}

}
