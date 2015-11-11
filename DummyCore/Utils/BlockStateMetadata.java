package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;

public class BlockStateMetadata {

	 public static final PropertyEnum METADATA = PropertyEnum.create("metadataOld", MetadataValues.class);
	 
	 public static int getBlockMetadata(IBlockAccess world,BlockPos pos)
	 {
		 return getBlockMetadata(world,pos.getX(),pos.getY(),pos.getZ());
	 }
	 
	 public static int getBlockMetadata(IBlockAccess world, int x, int y, int z)
	 {
		 return world.getBlockState(new BlockPos(x,y,z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(x,y,z)));
	 }
	 
	 public static IBlockState createDefaultBlockState(Block b)
	 {
		 return b.getBlockState().getBaseState().withProperty(METADATA, MetadataValues.M0);
	 }
	 
	 public static int getMetaFromState(IBlockState state)
	 {
		 return MetadataValues.class.cast(state.getValue(METADATA)).getMetadata();
	 }
	 
	 public static enum MetadataValues implements IStringSerializable
	 {
		 M0(0),M1(1),M2(2),M3(3),M4(4),M5(5),M6(6),M7(7),M8(8),M9(9),M10(10),M11(11),M12(12),M13(13),M14(14),M15(15);
		 
		 MetadataValues(int i)
		 {
			 meta = i;
		 }
		 
		 public int meta;
		 
		 public int getMetadata()
		 {
			 return meta;
		 }

		@Override
		public String getName() {
			return String.valueOf(meta);
		}
	 }
}


