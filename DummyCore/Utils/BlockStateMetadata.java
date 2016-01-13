package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;

/**
 * A wrapper around IBlockState system, which allows the usage of numeric metadata with more ease
 * @author modbder
 *
 */
public class BlockStateMetadata {

	 public static final PropertyInteger METADATA = PropertyInteger.create("metadataOld", 0, 15);
	 
	 /**
	  * Checks if the given BlockState has a metadata property
	  * @param state - the IBlockState
	  * @return true if the blockstate has a metadata property, false if not
	  */
	 public static boolean isMetadataState(IBlockState state)
	 {
		 try
		 {
			 return state.getValue(METADATA) != null;
		 }
		 catch(Exception e)
		 {
			 return false;
		 }
	 }
	 
	 /**
	  * Checks if the given Block has a metadata property
	  * @param b - the Block
	  * @return true if the blockstate has a metadata property, false if not
	  */
	 public static boolean isMetadataBlock(Block b)
	 {
		 try
		 {
			 return b.getDefaultState().getValue(METADATA) != null;
		 }
		 catch(Exception e)
		 {
			 return false;
		 }
	 }
	 
	 /**
	  * Gets a numeric metadata from a block at the given BlockPos
	  * @param world - the World
	  * @param pos - the position of the block
	  * @return A numeric metadata
	  */
	 public static int getBlockMetadata(IBlockAccess world,BlockPos pos)
	 {
		 return world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
	 }
	 
	 /**
	  * Gets a numeric metadata from a block at the given position
	  * @param world - the World
	  * @param x - the x position of the block
	  * @param y - the y position of the block
	  * @param z - the z position of the block
	  * @return A numeric metadata
	  */
	 public static int getBlockMetadata(IBlockAccess world, int x, int y, int z)
	 {
		 return getBlockMetadata(world, new BlockPos(x,y,z));
	 }
	 
	 /**
	  * Creates a BlockState for a given block with the metadata property
	  * @param b - the block
	  * @return a BlockState object with metadata property
	  */
	 public static IBlockState createDefaultBlockState(Block b)
	 {
		 return b.getBlockState().getBaseState().withProperty(METADATA, 0);
	 }
	 
	 /**
	  * Gets a numeric metadata from a blockstate with metadata property
	  * @param state - the blockstate
	  * @return The numeric metadata
	  */
	 public static int getMetaFromState(IBlockState state)
	 {
		 return state.getValue(METADATA);
	 }
}


