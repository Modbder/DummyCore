package DummyCore.Utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CompoundAABB {

	public ArrayList<ExtendedAABB> bounds = new ArrayList<ExtendedAABB>();
	
	public CompoundAABB(ExtendedAABB...aabbs)
	{
		bounds.addAll(PrimitiveUtils.listOf(aabbs));
	}
	
	public CompoundAABB concat(ExtendedAABB aabb)
	{
		bounds.add(aabb);
		return this;
	}
	
	public CompoundAABB expand(int mx, int my, int mz, int max, int may, int maz)
	{
		CompoundAABB newAabb = new CompoundAABB();
		newAabb.bounds.addAll(bounds);
		for(ExtendedAABB eab : newAabb.bounds)
		{
			eab.minX -= mx;
			eab.minY -= my;
			eab.minZ -= mz;
			eab.maxX += mx;
			eab.maxY += my;
			eab.maxZ += mz;
		}
		return newAabb;
	}
	
	public void replace(World worldIn, Block replace, Block to)
	{
		ArrayList<String> beenTo = new ArrayList<String>(128);
		for(ExtendedAABB eab : bounds)
		{
			for(int dx = (int) eab.minX; dx <= eab.maxX; ++dx)
			{
				for(int dy = (int) eab.minY; dy <= eab.maxY; ++dy)
				{
					for(int dz = (int) eab.minZ; dz <= eab.maxZ; ++dz)
					{
						if(beenTo.contains(dx+"|"+dy+"|"+dz))
							continue;
						
						beenTo.add(dx+"|"+dy+"|"+dz);
						Block b = worldIn.getBlockState(new BlockPos(dx,dy,dz)).getBlock();
						if(b.equals(replace))
							worldIn.setBlockState(new BlockPos(dx,dy,dz), to.getDefaultState());
					}
				}
			}
		}
		beenTo = null;
	}
	
	public void fill(World worldIn, Block fillWith)
	{
		ArrayList<String> beenTo = new ArrayList<String>(128);
		for(ExtendedAABB eab : bounds)
		{
			for(int dx = (int) eab.minX; dx <= eab.maxX; ++dx)
			{
				for(int dy = (int) eab.minY; dy <= eab.maxY; ++dy)
				{
					for(int dz = (int) eab.minZ; dz <= eab.maxZ; ++dz)
					{
						if(beenTo.contains(dx+"|"+dy+"|"+dz))
							continue;
						
						beenTo.add(dx+"|"+dy+"|"+dz);
						if(worldIn.isAirBlock(new BlockPos(dx,dy,dz)))
							worldIn.setBlockState(new BlockPos(dx,dy,dz), fillWith.getDefaultState());
					}
				}
			}
		}
		beenTo = null;
	}
	
	public int count(World worldIn,Block... blocks)
	{
		int counted = 0;
		ArrayList<String> beenTo = new ArrayList<String>(128);
		for(ExtendedAABB eab : bounds)
		{
			for(int dx = (int) eab.minX; dx <= eab.maxX; ++dx)
			{
				for(int dy = (int) eab.minY; dy <= eab.maxY; ++dy)
				{
					for(int dz = (int) eab.minZ; dz <= eab.maxZ; ++dz)
					{
						if(beenTo.contains(dx+"|"+dy+"|"+dz))
							continue;
						
						beenTo.add(dx+"|"+dy+"|"+dz);
						Block b = worldIn.getBlockState(new BlockPos(dx,dy,dz)).getBlock();
						if(PrimitiveUtils.checkArray(blocks, b))
							++counted;
					}
				}
			}
		}
		beenTo = null;
		return counted;
	}
	
	public static CompoundAABB of(ExtendedAABB...aabbs)
	{
		return new CompoundAABB(aabbs);
	}
}
