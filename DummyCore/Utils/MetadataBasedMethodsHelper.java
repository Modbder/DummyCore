package DummyCore.Utils;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MetadataBasedMethodsHelper {

	static int[] surroundings;
	
	public static void leavesDecayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
        if (!worldIn.isRemote)
        {
            if (BlockStateMetadata.getMetaFromState(state) > 7)
            {
                byte b0 = 4;
                int i = b0 + 1;
                int j = pos.getX();
                int k = pos.getY();
                int l = pos.getZ();
                byte b1 = 32;
                int i1 = b1 * b1;
                int j1 = b1 / 2;

                if (surroundings == null)
                {
                    surroundings = new int[b1 * b1 * b1];
                }

                int k1;

                if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i)))
                {
                    int l1;
                    int i2;

                    for (k1 = -b0; k1 <= b0; ++k1)
                    {
                        for (l1 = -b0; l1 <= b0; ++l1)
                        {
                            for (i2 = -b0; i2 <= b0; ++i2)
                            {
                                BlockPos tmp = new BlockPos(j + k1, k + l1, l + i2);
                                Block block = worldIn.getBlockState(tmp).getBlock();

                                if (!block.canSustainLeaves(worldIn, tmp))
                                {
                                    if (block.isLeaves(worldIn, tmp))
                                    {
                                        surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = -2;
                                    }
                                    else
                                    {
                                        surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = -1;
                                    }
                                }
                                else
                                {
                                    surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = 0;
                                }
                            }
                        }
                    }

                    for (k1 = 1; k1 <= 4; ++k1)
                    {
                        for (l1 = -b0; l1 <= b0; ++l1)
                        {
                            for (i2 = -b0; i2 <= b0; ++i2)
                            {
                                for (int j2 = -b0; j2 <= b0; ++j2)
                                {
                                    if (surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1] == k1 - 1)
                                    {
                                        if (surroundings[(l1 + j1 - 1) * i1 + (i2 + j1) * b1 + j2 + j1] == -2)
                                        {
                                            surroundings[(l1 + j1 - 1) * i1 + (i2 + j1) * b1 + j2 + j1] = k1;
                                        }

                                        if (surroundings[(l1 + j1 + 1) * i1 + (i2 + j1) * b1 + j2 + j1] == -2)
                                        {
                                            surroundings[(l1 + j1 + 1) * i1 + (i2 + j1) * b1 + j2 + j1] = k1;
                                        }

                                        if (surroundings[(l1 + j1) * i1 + (i2 + j1 - 1) * b1 + j2 + j1] == -2)
                                        {
                                            surroundings[(l1 + j1) * i1 + (i2 + j1 - 1) * b1 + j2 + j1] = k1;
                                        }

                                        if (surroundings[(l1 + j1) * i1 + (i2 + j1 + 1) * b1 + j2 + j1] == -2)
                                        {
                                            surroundings[(l1 + j1) * i1 + (i2 + j1 + 1) * b1 + j2 + j1] = k1;
                                        }

                                        if (surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + (j2 + j1 - 1)] == -2)
                                        {
                                            surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + (j2 + j1 - 1)] = k1;
                                        }

                                        if (surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1 + 1] == -2)
                                        {
                                            surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1 + 1] = k1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                k1 = surroundings[j1 * i1 + j1 * b1 + j1];

                if (k1 >= 0)
                {
                	if(worldIn.getBlockState(pos).getValue(BlockStateMetadata.METADATA) != null && BlockStateMetadata.getMetaFromState(worldIn.getBlockState(pos)) <= 7)
                		worldIn.setBlockState(pos, worldIn.getBlockState(pos).getBlock().getStateFromMeta(worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos)) + 8), 4);
                }
                else
                {
                    destroy(worldIn, pos);
                }
            }
        }
	}
	
	public static void destroy(World w, BlockPos pos)
	{
		w.getBlockState(pos).getBlock().dropBlockAsItem(w, pos, w.getBlockState(pos), 0);
		w.setBlockToAir(pos);
	}
	
	public static void breakLeaves(World worldIn, BlockPos pos, IBlockState state)
	{
        byte b0 = 1;
        int i = b0 + 1;
        int j = pos.getX();
        int k = pos.getY();
        int l = pos.getZ();

        if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i)))
            for (int i1 = -b0; i1 <= b0; ++i1)
                for (int j1 = -b0; j1 <= b0; ++j1)
                    for (int k1 = -b0; k1 <= b0; ++k1)
                    {
                        BlockPos blockpos1 = pos.add(i1, j1, k1);
                        IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

                        if (iblockstate1.getBlock().isLeaves(worldIn, blockpos1))
                            iblockstate1.getBlock().beginLeavesDecay(worldIn, blockpos1);
                    }
	}
	
	@SuppressWarnings("unchecked")
	public static void breakLog(World worldIn, BlockPos pos, IBlockState state)
	{
        byte b0 = 4;
        int i = b0 + 1;

        if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i)))
        {
            Iterator<BlockPos> iterator = BlockPos.getAllInBox(pos.add(-b0, -b0, -b0), pos.add(b0, b0, b0)).iterator();

            while (iterator.hasNext())
            {
                BlockPos blockpos1 = iterator.next();
                IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
                if (iblockstate1.getBlock().isLeaves(worldIn, blockpos1))
                    iblockstate1.getBlock().beginLeavesDecay(worldIn, blockpos1);
            }
        }
	}
	
}
