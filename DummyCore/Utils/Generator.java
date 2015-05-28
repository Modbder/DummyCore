package DummyCore.Utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Generator 
{
	public static final Generator instance = new Generator();
	
	public World worldObj;
	public boolean isWorking;
	public boolean hasOffset;
	
	public Coord3D offset;
	
	public Block setTo;
	public int genMetadata;
	
	public int flag;
	
	public static AxisAlignedBB centerBB(AxisAlignedBB genBox)
	{
		return AxisAlignedBB.getBoundingBox(genBox.minX-((genBox.maxX-genBox.minX)/2), genBox.minY-((genBox.maxY-genBox.minY)/2), genBox.minZ-((genBox.maxZ-genBox.minZ)/2), genBox.maxX-((genBox.maxX-genBox.minX)/2), genBox.maxY-((genBox.maxY-genBox.minY)/2), genBox.maxZ-((genBox.maxZ-genBox.minZ)/2));
	}
	
	public static AxisAlignedBB normaliseBB(AxisAlignedBB genBox)
	{
		double minX = genBox.minX;
		double minY = genBox.minY;
		double minZ = genBox.minZ;
		double maxX = genBox.maxX;
		double maxY = genBox.maxY;
		double maxZ = genBox.maxZ;
		
		return AxisAlignedBB.getBoundingBox(maxX < minX ? genBox.maxX : genBox.minX, maxY < minY ? genBox.maxY : genBox.minY, maxZ < minZ ? genBox.maxZ : genBox.minZ, maxX < minX ? genBox.minX : genBox.maxX, maxY < minY ? genBox.minY : genBox.maxY, maxZ < minZ ? genBox.minZ : genBox.maxZ);
	}
	
	public ArrayList<Coord3D> getBlocksOfType(AxisAlignedBB genBox)
	{
		gen();
		
		prepareBB(genBox);
		
		ArrayList<Coord3D> lst = new ArrayList<Coord3D>();
		
		for(int x = MathHelper.floor_double(genBox.minX); x <= MathHelper.floor_double(genBox.maxX); ++x)
		{
			for(int y = MathHelper.floor_double(genBox.minY); y <= MathHelper.floor_double(genBox.maxY); ++y)
			{
				for(int z = MathHelper.floor_double(genBox.minZ); z <= MathHelper.floor_double(genBox.maxZ); ++z)
				{
					if(worldObj.getBlock(x, y, z) == setTo && worldObj.getBlockMetadata(x, y, z) == genMetadata)
					{
						Coord3D c = new Coord3D(x,y,z);
						lst.add(c);
					}
				}
			}
		}
		
		restoreBB(genBox);
		
		return lst;
	}
	
	public void setOffset(Coord3D coord)
	{
		offset = coord;
		hasOffset = true;
	}
	
	public void setOffset(int x, int y, int z)
	{
		offset = new Coord3D(x,y,z);
		hasOffset = true;
	}
	
	public void startWorldgen(World world)
	{
		if(world.isRemote)
			throw new IllegalArgumentException("Worldgen on CLIENT side is not allowed!");
		
		if(isWorking)
			throw new IllegalStateException("Already generating!");
		
		offset = null;
		hasOffset = false;
		isWorking = true;
		worldObj = world;
		setTo = Blocks.air;
		genMetadata = 0;
		flag = 2;
	}
	
	public void endWorldgen()
	{
		if(!isWorking)
			throw new IllegalStateException("Not generating!");
		
		offset = null;
		isWorking = false;
		worldObj = null;
		hasOffset = false;
		setTo = null;
		genMetadata = 0;
		flag = 0;
	}
	
	public void setFlag(int i)
	{
		gen();
		
		flag = i;
	}
	
	public boolean gen()
	{
		if(!isWorking)
			throw new IllegalStateException("Can't worlgen if not generating!");
		
		if(worldObj.isRemote)
			throw new IllegalArgumentException("Worldgen on CLIENT side is not allowed!");
		
		return isWorking;
	}
	
	
	
	public void restoreBB(AxisAlignedBB genBox)
	{
		gen();
		
		if(hasOffset)
		{
			genBox.minX -= offset.x;
			genBox.minY -= offset.y;
			genBox.minZ -= offset.z;
			genBox.maxX -= offset.x;
			genBox.maxY -= offset.y;
			genBox.maxZ -= offset.z;
		}	
	}
	
	public void prepareBB(AxisAlignedBB genBox)
	{
		gen();
		
		if(hasOffset)
		{
			genBox.minX += offset.x;
			genBox.minY += offset.y;
			genBox.minZ += offset.z;
			genBox.maxX += offset.x;
			genBox.maxY += offset.y;
			genBox.maxZ += offset.z;
		}	
	}
	
	public void setBlock(Block b)
	{
		gen();
		
		setTo = b;
		genMetadata = 0;
	}
	
	public void setMeta(int i)
	{
		gen();
		
		genMetadata = i;
	}
	
	@SuppressWarnings("unchecked")
	public void randomiseCuboid(AxisAlignedBB genBox, Pair<Block,Integer>...pairs)
	{
		gen();
		
		prepareBB(genBox);
		
		int x = MathHelper.floor_double(genBox.minX);
		int y = MathHelper.floor_double(genBox.minY);
		int z = MathHelper.floor_double(genBox.minZ);
		int eX = MathHelper.floor_double(genBox.maxX);
		int eY = MathHelper.floor_double(genBox.maxY);
		int eZ = MathHelper.floor_double(genBox.maxZ);
		
		for(int dx = x; dx <= eX; ++dx)
		{
			for(int dy = y; dy <= eY; ++dy)
			{
				for(int dz = z; dz <= eZ; ++dz)
				{
					int i = worldObj.rand.nextInt(pairs.length);
					if(worldObj.getBlock(dx,dy,dz) == setTo)
						worldObj.setBlock(dx, dy, dz, pairs[i].obj1, pairs[i].obj2, flag);
				}
			}
		}
		
		restoreBB(genBox);
	}
	
	public void addFullSphere(AxisAlignedBB genBox)
	{
		gen();
		
		genBox = normaliseBB(genBox);
		prepareBB(genBox);

	    double radiusX = (genBox.maxX-genBox.minX)/2;
	    double radiusY = (genBox.maxY-genBox.minY)/2;
	    double radiusZ = (genBox.maxZ-genBox.minZ)/2;
	    
	    int dx = MathHelper.floor_double(genBox.minX+radiusX);
	    int dy = MathHelper.floor_double(genBox.minY+radiusY);
	    int dz = MathHelper.floor_double(genBox.minZ+radiusZ);
	    
	    double invRadiusX = 1.0D / radiusX;
	    double invRadiusY = 1.0D / radiusY;
	    double invRadiusZ = 1.0D / radiusZ;
	    int ceilRadiusX = (int)Math.ceil(radiusX);
	    int ceilRadiusY = (int)Math.ceil(radiusY);
	    int ceilRadiusZ = (int)Math.ceil(radiusZ);
	    double nextXn = 0.0D;
	    boolean filled = true;
	    
	    fX:for(int x = 0; x <= ceilRadiusX; x++)
	    {
	    	double xn = nextXn;
	        nextXn = (double)(x + 1) * invRadiusX;
	        double nextYn = 0.0D;
	        fZ:for(int y = 0; y <= ceilRadiusY; y++)
	        {
	            double yn = nextYn;
	            nextYn = (double)(y + 1) * invRadiusY;
	            double nextZn = 0.0D;
	            for(int z = 0; z <= ceilRadiusZ; z++)
	            {
	                double zn = nextZn;
	                nextZn = (double)(z + 1) * invRadiusZ;
	                double distanceSq = lengthSq(xn, yn, zn);
	                if(distanceSq > 1.0D)
	                {
	                    if(z != 0)
	                        break;
	                    if(y == 0)
	                        break fX;
	                    else
	                        break fZ;
	                }
	                if(!filled && lengthSq(nextXn, yn, zn) <= 1.0D && lengthSq(xn, nextYn, zn) <= 1.0D && lengthSq(xn, yn, nextZn) <= 1.0D)
	                    continue;
	                
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz-z));
	            }
	        }
	    }
	    restoreBB(genBox);
	}
	
	public void addHollowCylinder(AxisAlignedBB genBox)
	{
		gen();
		
		genBox = normaliseBB(genBox);
		prepareBB(genBox);
		
	    double radiusX = (genBox.maxX-genBox.minX)/2;
	    double height = genBox.maxY-genBox.minY;
	    double radiusZ = (genBox.maxZ-genBox.minZ)/2;
	    
	    int dx = MathHelper.floor_double(genBox.minX+radiusX);
	    int dy = MathHelper.floor_double(genBox.minY);
	    int dz = MathHelper.floor_double(genBox.minZ+radiusZ);
	    
	    boolean filled = false;
		
        radiusX += 0.5D;
        radiusZ += 0.5D;
        if(height == 0)
            return;
        
        if(height < 0)
        {
            height = -height;
            dy = MathHelper.floor_double(-height);
        }
        if(dy < 0)
            dy = 0;
        else
        if((dy + height) - 1 > worldObj.getActualHeight())
            height = (int) ((worldObj.getActualHeight() - dy) + 1);
        double invRadiusX = 1.0D / radiusX;
        double invRadiusZ = 1.0D / radiusZ;
        int ceilRadiusX = (int)Math.ceil(radiusX);
        int ceilRadiusZ = (int)Math.ceil(radiusZ);
        double nextXn = 0.0D;
        D:for(int x = 0; x <= ceilRadiusX; x++)
        {
            double xn = nextXn;
            nextXn = (double)(x + 1) * invRadiusX;
            double nextZn = 0.0D;
            for(int z = 0; z <= ceilRadiusZ; z++)
            {
                double zn = nextZn;
                nextZn = (double)(z + 1) * invRadiusZ;
                double distanceSq = lengthSq(xn, zn);
                if(distanceSq > 1.0D)
                    if(z == 0)
                        break D;
                    else
                        break;
                if(!filled && lengthSq(nextXn, zn) <= 1.0D && lengthSq(xn, nextZn) <= 1.0D)
                    continue;
                
                for(int y = 0; y < height; y++)
                {
                   block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
                   block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
                   block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
                   block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
                }
            }
        }
        
        restoreBB(genBox);
	}
	
	public void addFullCylinder(AxisAlignedBB genBox)
	{
		gen();
		
		genBox = normaliseBB(genBox);
		prepareBB(genBox);
		
	    double radiusX = (genBox.maxX-genBox.minX)/2;
	    double height = genBox.maxY-genBox.minY;
	    double radiusZ = (genBox.maxZ-genBox.minZ)/2;
	    
	    int dx = MathHelper.floor_double(genBox.minX+radiusX);
	    int dy = MathHelper.floor_double(genBox.minY);
	    int dz = MathHelper.floor_double(genBox.minZ+radiusZ);
	    
	    boolean filled = true;
		
        radiusX += 0.5D;
        radiusZ += 0.5D;
        if(height == 0)
            return;
        
        if(height < 0)
        {
            height = -height;
            dy = MathHelper.floor_double(-height);
        }
        if(dy < 0)
            dy = 0;
        else
        if((dy + height) - 1 > worldObj.getActualHeight())
            height = (int) ((worldObj.getActualHeight() - dy) + 1);
        double invRadiusX = 1.0D / radiusX;
        double invRadiusZ = 1.0D / radiusZ;
        int ceilRadiusX = (int)Math.ceil(radiusX);
        int ceilRadiusZ = (int)Math.ceil(radiusZ);
        double nextXn = 0.0D;
        D:for(int x = 0; x <= ceilRadiusX; x++)
        {
            double xn = nextXn;
            nextXn = (double)(x + 1) * invRadiusX;
            double nextZn = 0.0D;
            for(int z = 0; z <= ceilRadiusZ; z++)
            {
                double zn = nextZn;
                nextZn = (double)(z + 1) * invRadiusZ;
                double distanceSq = lengthSq(xn, zn);
                if(distanceSq > 1.0D)
                    if(z == 0)
                        break D;
                    else
                        break;
                if(!filled && lengthSq(nextXn, zn) <= 1.0D && lengthSq(xn, nextZn) <= 1.0D)
                    continue;
                
                for(int y = 0; y < height; y++)
                {
                   block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
                   block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
                   block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
                   block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
                }
            }
        }
        
        restoreBB(genBox);
	}
	
	public void addHollowSphere(AxisAlignedBB genBox)
	{
		gen();
		
		genBox = normaliseBB(genBox);
		prepareBB(genBox);

	    double radiusX = (genBox.maxX-genBox.minX)/2;
	    double radiusY = (genBox.maxY-genBox.minY)/2;
	    double radiusZ = (genBox.maxZ-genBox.minZ)/2;
	    
	    int dx = MathHelper.floor_double(genBox.minX+radiusX);
	    int dy = MathHelper.floor_double(genBox.minY+radiusY);
	    int dz = MathHelper.floor_double(genBox.minZ+radiusZ);
	    
	    double invRadiusX = 1.0D / radiusX;
	    double invRadiusY = 1.0D / radiusY;
	    double invRadiusZ = 1.0D / radiusZ;
	    int ceilRadiusX = (int)Math.ceil(radiusX);
	    int ceilRadiusY = (int)Math.ceil(radiusY);
	    int ceilRadiusZ = (int)Math.ceil(radiusZ);
	    double nextXn = 0.0D;
	    boolean filled = false;
	    
	    fX:for(int x = 0; x <= ceilRadiusX; x++)
	    {
	    	double xn = nextXn;
	        nextXn = (double)(x + 1) * invRadiusX;
	        double nextYn = 0.0D;
	        fZ:for(int y = 0; y <= ceilRadiusY; y++)
	        {
	            double yn = nextYn;
	            nextYn = (double)(y + 1) * invRadiusY;
	            double nextZn = 0.0D;
	            for(int z = 0; z <= ceilRadiusZ; z++)
	            {
	                double zn = nextZn;
	                nextZn = (double)(z + 1) * invRadiusZ;
	                double distanceSq = lengthSq(xn, yn, zn);
	                if(distanceSq > 1.0D)
	                {
	                    if(z != 0)
	                        break;
	                    if(y == 0)
	                        break fX;
	                    else
	                        break fZ;
	                }
	                if(!filled && lengthSq(nextXn, yn, zn) <= 1.0D && lengthSq(xn, nextYn, zn) <= 1.0D && lengthSq(xn, yn, nextZn) <= 1.0D)
	                    continue;
	                
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz+z));
	                block(MathHelper.floor_float(dx+x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy+y), MathHelper.floor_float(dz-z));
	                block(MathHelper.floor_float(dx-x), MathHelper.floor_float(dy-y), MathHelper.floor_float(dz-z));
	            }
	        }
	    }
	    restoreBB(genBox);
	}
	
	public void addWallsCuboid(AxisAlignedBB genBox)
	{
		gen();
		
		//Bottom
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.minX, genBox.minY, genBox.minZ, genBox.maxX, genBox.minY, genBox.maxZ));
		//Top
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.minX, genBox.maxY, genBox.minZ, genBox.maxX, genBox.maxY, genBox.maxZ));
		//X Neg
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.minX, genBox.minY, genBox.minZ, genBox.minX, genBox.maxY, genBox.maxZ));
		//X Pos
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.maxX, genBox.minY, genBox.minZ, genBox.maxX, genBox.maxY, genBox.maxZ));
		//Z Neg
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.minX, genBox.minY, genBox.minZ, genBox.maxX, genBox.maxY, genBox.minZ));
		//Z Pos
		addCuboid(AxisAlignedBB.getBoundingBox(genBox.minX, genBox.minY, genBox.maxZ, genBox.maxX, genBox.maxY, genBox.maxZ));
	
	}
	
	public boolean block(int x, int y, int z)
	{
		gen();
		
		return worldObj.setBlock(x, y, z, setTo, genMetadata, flag);
	}
	
	public void addCuboid(AxisAlignedBB genBox)
	{
		gen();
		prepareBB(genBox);
		
		int x = MathHelper.floor_double(genBox.minX);
		int y = MathHelper.floor_double(genBox.minY);
		int z = MathHelper.floor_double(genBox.minZ);
		int eX = MathHelper.floor_double(genBox.maxX);
		int eY = MathHelper.floor_double(genBox.maxY);
		int eZ = MathHelper.floor_double(genBox.maxZ);
		
		addCuboid(x,y,z,eX,eY,eZ);
		
		restoreBB(genBox);
	}
	
	public void addCuboid(int x, int y, int z, int eX, int eY, int eZ)
	{
		gen();
		
		for(int dx = x; dx <= eX; ++dx)
		{
			for(int dy = y; dy <= eY; ++dy)
			{
				for(int dz = z; dz <= eZ; ++dz)
				{
					block(dx,dy,dz);
				}
			}
		}
	}

    private static double lengthSq(double x, double y, double z)
    {
        return x * x + y * y + z * z;
    }

    private static double lengthSq(double x, double z)
    {
        return x * x + z * z;
    }
}
