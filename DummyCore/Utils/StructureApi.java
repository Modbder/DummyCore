package DummyCore.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import DummyCore.Core.Core;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This is an API for structure-like code. Allows storing structures from world into .str files to parse later
 * @author modbder
 *
 */
public class StructureApi 
{
	@Deprecated
	public static NBTTagCompound createStructureTagIgnoreMetadata(World w,AxisAlignedBB aabb, boolean whitelist, Block... blocks)
	{
		return(createStructureTagIgnoreMetadata(w,new ExtendedAABB(aabb),whitelist,blocks));
	}
	
	/**
	 * Creates an NBTTag with all structure information in the given cuboid, world and blocks
	 * @param w - the world
	 * @param aabb - the cuboid
	 * @param whitelist - if the array of blocks below is a whitelist rather than a blacklist
	 * @param blocks an array of blocks
	 * @return a formed StructureNBTTag
	 */
	@SuppressWarnings("deprecation")
	public static NBTTagCompound createStructureTagIgnoreMetadata(World w,ExtendedAABB aabb, boolean whitelist, Block... blocks)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		if(aabb.minX > aabb.maxX)
		{
			double mX = aabb.minX;
			aabb.minX = aabb.maxX;
			aabb.maxX = mX;
		}
		
		if(aabb.minY > aabb.maxY)
		{
			double mY = aabb.minY;
			aabb.minY = aabb.maxY;
			aabb.maxY = mY;
		}
		
		if(aabb.minZ > aabb.maxZ)
		{
			double mZ = aabb.minZ;
			aabb.minZ = aabb.maxZ;
			aabb.maxZ = mZ;
		}
		
		for(int dx = (int) aabb.minX; dx <= aabb.maxX; ++dx)
		{
			for(int dy = (int) aabb.minY; dy <= aabb.maxY; ++dy)
			{
				for(int dz = (int) aabb.minZ; dz <= aabb.maxZ; ++dz)
				{
					Block b = w.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
					if(b != null)
					{
						boolean include = false;
						if(whitelist)
						{
							for(int i = 0; i < blocks.length; ++i)
							{
								if(blocks[i].equals(b))
								{
									include = true;
									break;
								}	
							}
						}else
						{
							include = true;
							for(int i = 0; i < blocks.length; ++i)
							{
								if(blocks[i].equals(b))
								{
									include = false;
									break;
								}	
							}
						}
						
						if(include)
						{
							String coords = String.valueOf(dx-aabb.minX-(aabb.maxX-aabb.minX)/2)+"|"+String.valueOf(dy-aabb.minY)+"|"+String.valueOf(dz-aabb.minZ-(aabb.maxZ-aabb.minZ)/2);
							if(!tag.hasKey(coords))
								tag.setString(coords, GameRegistry.findUniqueIdentifierFor(b).toString());
						}
					}
				}
			}
		}
		
		return tag;
	}
	
	@Deprecated
	public static NBTTagCompound createStructureTag(World w,AxisAlignedBB aabb, boolean whitelist, Block... blocks)
	{
		return createStructureTag(w,new ExtendedAABB(aabb),whitelist,blocks);
	}
	
	
	/**
	 * Creates an NBTTag with all structure information in the given cuboid, world and blocks
	 * @param w - the world
	 * @param aabb - the cuboid
	 * @param whitelist - if the array of blocks below is a whitelist rather than a blacklist
	 * @param blocks an array of blocks
	 * @return a formed StructureNBTTag respecting metadata
	 */
	@SuppressWarnings("deprecation")
	public static NBTTagCompound createStructureTag(World w,ExtendedAABB aabb, boolean whitelist, Block... blocks)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		if(aabb.minX > aabb.maxX)
		{
			double mX = aabb.minX;
			aabb.minX = aabb.maxX;
			aabb.maxX = mX;
		}
		
		if(aabb.minY > aabb.maxY)
		{
			double mY = aabb.minY;
			aabb.minY = aabb.maxY;
			aabb.maxY = mY;
		}
		
		if(aabb.minZ > aabb.maxZ)
		{
			double mZ = aabb.minZ;
			aabb.minZ = aabb.maxZ;
			aabb.maxZ = mZ;
		}
		
		for(int dx = (int) aabb.minX; dx <= aabb.maxX; ++dx)
		{
			for(int dy = (int) aabb.minY; dy <= aabb.maxY; ++dy)
			{
				for(int dz = (int) aabb.minZ; dz <= aabb.maxZ; ++dz)
				{
					Block b = w.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
					if(b != null)
					{
						boolean include = false;
						if(whitelist)
						{
							for(int i = 0; i < blocks.length; ++i)
							{
								if(blocks[i].equals(b))
								{
									include = true;
									break;
								}	
							}
						}else
						{
							include = true;
							for(int i = 0; i < blocks.length; ++i)
							{
								if(blocks[i].equals(b))
								{
									include = false;
									break;
								}	
							}
						}
						
						if(include)
						{
							String coords = String.valueOf(dx-aabb.minX-(aabb.maxX-aabb.minX)/2)+"|"+String.valueOf(dy-aabb.minY)+"|"+String.valueOf(dz-aabb.minZ-(aabb.maxZ-aabb.minZ)/2);
							if(!tag.hasKey(coords))
								tag.setString(coords, GameRegistry.findUniqueIdentifierFor(b).toString()+"|"+w.getBlockState(new BlockPos(dx, dy, dz)).getBlock().getMetaFromState(w.getBlockState(new BlockPos(dx, dy, dz))));
						}
					}
				}
			}
		}
		
		return tag;
	}
	
	/**
	 * Generates a structure from the given NBTTag into a world
	 * @param w - the world
	 * @param x - x pos
	 * @param y - y pos
	 * @param z - z pos
	 * @param structureTag - the valid StructureNBTTag
	 */
	public static void nbtStructureIntoWorld(World w, int x, int y, int z, NBTTagCompound structureTag)
	{
		Set<String> keySet = structureTag.getKeySet();
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Pair<Coord3D,Pair<Block,Integer>>> structure = new ArrayList<Pair<Coord3D,Pair<Block,Integer>>>();
		Iterator<String> $i = keySet.iterator();
		while($i.hasNext())
			keys.add($i.next());
		
		for(String s : keys)
		{
			int dx = (int) Double.parseDouble(s.substring(0, s.indexOf('|')));
			int dy = (int) Double.parseDouble(s.substring(s.indexOf('|')+1,s.lastIndexOf('|')));
			int dz = (int) Double.parseDouble(s.substring(s.lastIndexOf('|')+1));
			Coord3D blockCoord = new Coord3D(dx,dy,dz);
			String st = structureTag.getString(s);
			String bid;
			if(st.indexOf('|') != -1)
				bid = st.substring(0, st.indexOf('|'));
			else
				bid = st;
			
			Block b = GameRegistry.findBlock(bid.substring(0,bid.indexOf(':')), bid.substring(bid.indexOf(':')+1));
			int meta;
			if(st.indexOf('|') != -1)
				meta = Integer.parseInt(st.substring(st.indexOf('|')+1));
			else
				meta = 0;
			
			Pair<Block, Integer> blockAndMeta = new Pair<Block, Integer>(b,meta);
			structure.add(new Pair<Coord3D,Pair<Block,Integer>>(blockCoord,blockAndMeta));
		}
		
		keys.clear();
		keys = null;
		
		for(Pair<Coord3D,Pair<Block,Integer>> put : structure)
		{
			Coord3D c = put.getFirst();
			int dx = (int) (c.x+x);
			int dy = (int) (c.y+y);
			int dz = (int) (c.z+z);
			Pair<Block,Integer> pa = put.getSecond();
			w.setBlockState(new BlockPos(dx, dy, dz), pa.getFirst().getStateFromMeta(pa.getSecond()), 2);
		}
		
		structure.clear();
		structure = null;
	}

	//Internal
	public static boolean areNBTTagsEqual(NBTTagCompound tag1, NBTTagCompound tag2)
	{
		if(tag1.hasNoTags() || tag2.hasNoTags())
			return false;
		
		Set<String> keys = tag1.getKeySet();
		int sF = keys.size();
		int sS = tag2.getKeySet().size();
		Iterator<String> $i = keys.iterator();
		
		while($i.hasNext())
		{
			String key = $i.next();
			NBTBase base1 = tag1.getTag(key);
			if(!tag2.hasKey(key))
				return false;
			NBTBase base2 = tag2.getTag(key);
			if(!compareTagsPrimitive(base1,base2))
				return false;
		}
		
		return sF == sS;
	}
	
	//Internal
	public static boolean compareTagsPrimitive(NBTBase base1, NBTBase base2)
	{
		byte id1 = base1.getId();
		byte id2 = base2.getId();
		
		if(id1 != id2)
			return false;
		
		switch(id1)
		{
			case 0:
			{
				return true;
			}
			case 1:
			{
				NBTTagByte byte1 = (NBTTagByte) base1;
				NBTTagByte byte2 = (NBTTagByte) base2;
				if(byte1.getByte() != byte2.getByte())
					return false;
				
				return true;
			}
			case 2:
			{
				NBTTagShort short1 = (NBTTagShort) base1;
				NBTTagShort short2 = (NBTTagShort) base2;
				if(short1.getShort() != short2.getShort())
					return false;
				
				return true;
			}
			case 3:
			{
				NBTTagInt int1 = (NBTTagInt) base1;
				NBTTagInt int2 = (NBTTagInt) base2;
				if(int1.getInt() != int2.getInt())
					return false;
				
				return true;
			}
			case 4:
			{
				NBTTagLong long1 = (NBTTagLong) base1;
				NBTTagLong long2 = (NBTTagLong) base2;
				if(long1.getLong() != long2.getLong())
					return false;
				
				return true;
			}
			case 5:
			{
				NBTTagFloat float1 = (NBTTagFloat) base1;
				NBTTagFloat float2 = (NBTTagFloat) base2;
				if(float1.getFloat() != float2.getFloat())
					return false;
				
				return true;
			}
			case 6:
			{
				NBTTagDouble double1 = (NBTTagDouble) base1;
				NBTTagDouble double2 = (NBTTagDouble) base2;
				if(double1.getDouble() != double2.getDouble())
					return false;
				
				return true;
			}
			case 7:
			{
				NBTTagByteArray byteArray1 = (NBTTagByteArray) base1;
				NBTTagByteArray byteArray2 = (NBTTagByteArray) base2;
				if(!byteArray1.equals(byteArray2))
					return false;
				
				return true;
			}
			case 8:
			{
				NBTTagString string1 = (NBTTagString) base1;
				NBTTagString string2 = (NBTTagString) base2;
				if(!string1.equals(string2))
					return false;
				
				return true;
			}
			case 9:
			{
				NBTTagList list1 = (NBTTagList) base1;
				NBTTagList list2 = (NBTTagList) base2;
				if(!list1.equals(list2))
					return false;
				
				return true;
			}
			case 10:
			{
				NBTTagCompound tag1 = (NBTTagCompound) base1;
				NBTTagCompound tag2 = (NBTTagCompound) base2;
				if(!tag1.equals(tag2))
					return false;
				
				return true;
			}
			case 11:
			{
				NBTTagIntArray intArray1 = (NBTTagIntArray) base1;
				NBTTagIntArray intArray2 = (NBTTagIntArray) base2;
				if(!intArray1.equals(intArray2))
					return false;
				
				return true;
			}
			default:
			{
				return true;
			}
		}
	}
	
	/**
	 * loads a .json format string from a given resourcelocation
	 * @param modid - the id of your mod
	 * @param filename - the path to the file in your assets dir. The file must be within the structure folder and needs to have a .str extension!
	 * @return
	 */
	public static String structureFromFile(String modid, String filename)
	{
		InputStream is = Core.class.getResourceAsStream("/assets/"+modid+"/structure/"+filename+".str");
		if(is != null)
		{
			String st = "";
			try
			{
				st = IOUtils.toString(is,"UTF-8");
			}
			catch(NullPointerException npe)
			{
				LogManager.getLogger(StructureApi.class).error("Can't find structure with name "+filename);
			}
			catch(IOException ioe)
			{
				LogManager.getLogger(StructureApi.class).error("Can't read structure with name "+filename);
			}
			catch(UnsupportedCharsetException uce)
			{
				FMLCommonHandler.instance().raiseException(uce, "UTF-8 Is not supported on this system! This is a critical error!", true);
			}
			finally
			{
				IOUtils.closeQuietly(is);
			}
			return st;
		}
		LogManager.getLogger(StructureApi.class).error("Can't find structure with name "+filename);
		
		return null;
	}
	
	
}

