package DummyCore.Utils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import DummyCore.Core.CoreInitialiser;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A simple IBlockAccess wrapper to, let's say, render a structure in a book, for example
 * <br>Most methods are undocumented, since a)They are self-describing, and b)You can always check what they do in vanilla's World class
 * @author ljfa-ag, modbder
 */
public class DummyBlockAccess implements IBlockAccess{

    public Block[][][] block;
    public byte[][][] meta;
    public TileEntity[][][] tile;
    public int xSize, ySize, zSize;
    public BiomeGenBase dummyBiome = BiomeGenBase.ocean;
    public World defaultWorld;
    public Hashtable<Block,Integer[]> cachedMetas = new Hashtable<Block,Integer[]>();
    public int cycleTimer = 60;
    public Random cycleRandom = new Random();
    public Hashtable<Pair<Block,Integer>,Integer> cachedBlocksAmounts = new Hashtable<Pair<Block,Integer>,Integer>();
	public int maxX, minX, maxY, minY, maxZ, minZ;
	public WorldWrapper wrapper;
    
    /**
     * Creates a DummyBlockAccess with given size
     * @param sizeX - x Size
     * @param sizeY - y Size
     * @param sizeZ - z Size
     */
    public DummyBlockAccess(int sizeX, int sizeY, int sizeZ)
    {
    	wrapper = new WorldWrapper(this);
    	block = new Block[sizeX][sizeY][sizeZ];
        meta = new byte[sizeX][sizeY][sizeZ];
        tile = new TileEntity[sizeX][sizeY][sizeZ];
        xSize = sizeX;
        ySize = sizeY;
        zSize = sizeZ;
        maxX = xSize;
        minX = 0;
        maxY = ySize;
        minY = 0;
        maxZ = zSize;
        minZ = 0;
        defaultWorld = CoreInitialiser.proxy.getClientWorld();
    }
    
    /**
     * Creates a DummyBlockAccess object from a given StructureTag
     * @param structureTag - a valid(StructureAPI) NBTTag
     * @return a new DummyBlockAccess object
     */
    public static DummyBlockAccess fromStructureNBT(NBTTagCompound structureTag)
    {
    	int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
		Set<String> keySet = structureTag.getKeySet();
		Stack<String> keys = new Stack<String>();
		Stack<DummyBlockPosition> dbp = new Stack<DummyBlockPosition>();
		Iterator<String> $i = keySet.iterator();
		while($i.hasNext())
			keys.add($i.next());
		
		while(!keys.isEmpty())
		{
			String s = keys.pop();
			int dx = (int) Double.parseDouble(s.substring(0, s.indexOf('|')));
			int dy = (int) Double.parseDouble(s.substring(s.indexOf('|')+1,s.lastIndexOf('|')));
			int dz = (int) Double.parseDouble(s.substring(s.lastIndexOf('|')+1));
			if(dx < minX)
				minX = dx;
			if(dy < minY)
				minY = dy;
			if(dz < minZ)
				minZ = dz;
			if(dx > maxX)
				maxX = dx;
			if(dy > maxY)
				maxY = dy;
			if(dz > maxZ)
				maxZ = dz;
			String st = structureTag.getString(s);
			String bid;
			if(st.indexOf('|') != -1)
				bid = st.substring(0, st.indexOf('|'));
			else
				bid = st;
			int meta;
			if(st.indexOf('|') != -1)
				meta = Integer.parseInt(st.substring(st.indexOf('|')+1));
			else
				meta = OreDictionary.WILDCARD_VALUE;
			dbp.push(new DummyBlockPosition(dx, dy, dz, bid, meta));
		}
		
		DummyBlockAccess dba = new DummyBlockAccess(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
		
		while(!dbp.isEmpty())
		{
			DummyBlockPosition pos = dbp.pop();
			String bid = pos.blockID;
			Block b = GameRegistry.findBlock(bid.substring(0,bid.indexOf(':')), bid.substring(bid.indexOf(':')+1));
			if(b.hasTileEntity(b.getStateFromMeta(pos.meta)))
			{
				TileEntity tile = b.createTileEntity(dba.defaultWorld, b.getStateFromMeta(pos.meta));
				dba.setTileEntity(pos.x - minX, pos.y - minY, pos.z - minZ, tile);
			}
			dba.setBlock(pos.x - minX, pos.y - minY, pos.z - minZ, b, pos.meta);
		}
		
		return dba;
    }
    
    /**
     * Sets the cycle timer for blocks metadata as i
     * @param i - the new time
     * @return current DummyBlockAccess
     */
    public DummyBlockAccess setCycleTime(int i)
    {
    	cycleTimer = i;
    	return this;
    }
    
    /**
     * Sets a default world over to something esle rather than Minecraft.getMinecraft().theWorld
     * @param w new world to set to
     * @return current DummyBlockAccess
     */
    public DummyBlockAccess setDefaultWorld(World w)
    {
    	defaultWorld = w;
    	return this;
    }
    
    /**
     * Sets a default biome to something else rather than OCEAN
     * @param bgb new biome to set
     * @return current DummyBlockAccess
     */
    public DummyBlockAccess setBiomeToRender(BiomeGenBase bgb)
    {
    	dummyBiome = bgb;
    	return this;
    }
    
    public boolean isInRange(int x, int y, int z) {
        return 0 <= x && x < xSize && 0 <= y && y < ySize && 0 <= z && z < zSize && x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ;
    }
    
    public void setBlock(int x, int y, int z, Block b)
    {
    	if(isInRange(x,y,z)){block[x][y][z] = b;}
    }
    
    public void setMetadata(int x, int y, int z, int m)
    {
    	if(isInRange(x,y,z)){meta[x][y][z] = (byte) m;}
    }
    
    public void setBlock(int x, int y, int z, Block b, int m)
    {
    	if(!cachedBlocksAmounts.containsKey(new Pair<Block,Integer>(b,m)))
    		cachedBlocksAmounts.put(new Pair<Block,Integer>(b,m), 1);
    	else
    		cachedBlocksAmounts.put(new Pair<Block,Integer>(b,m), cachedBlocksAmounts.get(new Pair<Block,Integer>(b,m))+1);
    	setBlock(x,y,z,b);
    	setMetadata(x,y,z,m);
    }
    
	public Block getBlock(int x, int y, int z) {
		return isInRange(x,y,z) ? block[x][y][z] == null ? Blocks.air : block[x][y][z] : Blocks.air;
	}
	
	public void setTileEntity(int x, int y, int z, TileEntity t){
		if(isInRange(x,y,z)){tile[x][y][z] = t;}
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		return isInRange(x,y,z) ? tile[x][y][z] : null;
	}

	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int side) {
		return 0;
	}

	public int getBlockMetadata(int x, int y, int z) {
		return isInRange(x,y,z) ? meta[x][y][z] == -1 || meta[x][y][z] == OreDictionary.WILDCARD_VALUE ? getMetadataFromWildcardForBlock(getBlock(x,y,z)) : meta[x][y][z] : 0;
	}
	
	public int getMetadataFromWildcardForBlock(Block b)
	{
		if(!cachedMetas.containsKey(b))
		{
			Integer[] i = CoreInitialiser.proxy.createPossibleMetadataCacheFromBlock(b);
			cachedMetas.put(b, i);
			return getMetadataFromWildcardForBlock(b);
		}
		
		cycleRandom.setSeed(defaultWorld.getSeed() + defaultWorld.getWorldTime()/cycleTimer);
		Integer[] i = cachedMetas.get(b);
		return i[cycleRandom.nextInt(i.length)];
	}

	public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
		return 0;
	}

	public boolean isAirBlock(int x, int y, int z) {
		return isInRange(x,y,z) ? block[x][y][z] == null || block[x][y][z].isAir(this, new BlockPos(x, y, z)) : true;
	}

	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return dummyBiome;
	}

	public int getHeight() {
		return 0;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	public boolean isSideSolid(int x, int y, int z, EnumFacing side, boolean _default) {
		return isInRange(x,y,z) ? block[x][y][z] == null ? _default : block[x][y][z].isSideSolid(this, new BlockPos(x, y, z), side) : _default;
	}
	
	public static class DummyBlockPosition
	{
		public int x,y,z;
		public String blockID;
		public int meta;
		
		public DummyBlockPosition(int px, int py, int pz, String id, int m)
		{
			x = px; y = py; z = pz; blockID = id; meta = m;
		}
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return getTileEntity(pos.getX(),pos.getY(),pos.getZ());
	}

	@Override
	public int getCombinedLight(BlockPos pos, int light) {
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return isInRange(pos.getX(),pos.getY(),pos.getZ()) ? getBlock(pos.getX(),pos.getY(),pos.getZ()).getActualState(getBlock(pos.getX(),pos.getY(),pos.getZ()).getStateFromMeta(getBlockMetadata(pos.getX(),pos.getY(),pos.getZ())), this, pos) : Blocks.air.getDefaultState();
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		return isAirBlock(pos.getX(),pos.getY(),pos.getZ());
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
		return getBiomeGenForCoords(pos.getX(),pos.getZ());
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.CUSTOMIZED;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		return isSideSolid(pos.getX(),pos.getY(),pos.getZ(),side,_default);
	}

	public static class WorldWrapper extends World
	{
		public DummyBlockAccess access;
		
		public WorldWrapper(DummyBlockAccess dba)
		{
			super(null,null,CoreInitialiser.proxy.getWorldForDim(0).provider,null,true);
			access = dba;
		}
		
		protected WorldWrapper(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,Profiler profilerIn, boolean client) {
			super(saveHandlerIn, info, providerIn, profilerIn, client);
		}
		
	    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos)
	    {
	    	return access.getBiomeGenForCoords(pos);
	    }
	    
	    public BiomeGenBase getBiomeGenForCoordsBody(final BlockPos pos)
	    {
	    	return access.getBiomeGenForCoords(pos);
	    }
	    
	    public WorldChunkManager getWorldChunkManager()
	    {
	    	return CoreInitialiser.proxy.getWorldForDim(0).getWorldChunkManager();
	    }
	    
	    protected IChunkProvider createChunkProvider()
	    {
	    	return new ChunkProviderClient(CoreInitialiser.proxy.getWorldForDim(0));
	    }
	    
	    public void initialize(WorldSettings settings){}
	    
	    public void setInitialSpawnLocation(){}
	    
	    public Block getGroundAboveSeaLevel(BlockPos pos){
	        BlockPos blockpos;

	        for (blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ()); !access.isAirBlock(blockpos.up()); blockpos = blockpos.up())
	        {
	            ;
	        }

	        return access.getBlockState(blockpos).getBlock();
	    }
	    
	    public boolean isAirBlock(BlockPos pos)
	    {
	    	return access.isAirBlock(pos);
	    }
	    
	    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty)
	    {
	    	return access.isInRange(pos.getX(), pos.getY(), pos.getZ());
	    }
	    
	    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
	    {
	    	return access.isInRange(x*16, 0, z*16);
	    }
	    
	    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ)
	    {
	    	return CoreInitialiser.proxy.getWorldForDim(0).getChunkFromChunkCoords(chunkX, chunkZ);
	    }
	    
	    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
	    {
	    	access.setBlock(pos.getX(), pos.getY(), pos.getZ(), newState.getBlock(), newState.getBlock().getMetaFromState(newState));
	    	return true;
	    }
	    
	    public void markAndNotifyBlock(BlockPos pos, Chunk chunk, IBlockState old, IBlockState new_, int flags){}
	    
	    public boolean setBlockToAir(BlockPos pos)
	    {
	    	access.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.air);
	    	return true;
	    }
	    
	    public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2){}
	    
	    public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn){}
	    
	    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos){return 15;}
	    
	    public float getLightBrightness(BlockPos pos){return 1F;}
	    
	    public IBlockState getBlockState(BlockPos pos)
	    {
	    	return access.getBlockState(pos);
	    }
	    
	    public boolean isDaytime(){return true;}
	    
	    public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch){}
	    
	    public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch){}
	    
	    public boolean spawnEntityInWorld(Entity entityIn){return true;}
	    
	    public String getProviderName(){return "DBWW";}
	    
	    public TileEntity getTileEntity(BlockPos pos){return access.getTileEntity(pos);}
	    
	    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn)
	    {
	    	if(access.getTileEntity(pos) != tileEntityIn)
	    		access.setTileEntity(pos.getX(), pos.getY(), pos.getZ(), tileEntityIn);
	    	tileEntityIn.setWorldObj(this);
	    	tileEntityIn.setPos(pos);
	    }
	    
	    public void removeTileEntity(BlockPos pos){access.setTileEntity(pos.getX(), pos.getY(), pos.getZ(), null);}
	    
	    protected int getRenderDistanceChunks(){return 8;}
	}
}
