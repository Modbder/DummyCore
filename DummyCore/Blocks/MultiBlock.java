package DummyCore.Blocks;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import DummyCore.Utils.IDummyMultiBlock;
import DummyCore.Utils.IDummyMultiItem;
import DummyCore.Utils.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description Used to handle all the multiblocks. Do not change anything here! Some explanation to the required functions will be given.
 */
public class MultiBlock extends Block{

	protected static List<String> blockNames = new ArrayList();
	protected static List<String> blockNamesUnlocalisedList = new ArrayList();
	protected static Hashtable<String,Integer> blockNamesUnlocalised = new Hashtable();
	protected static List<String> blockTextures = new ArrayList();
	protected static int id = -1;
	public static int blockuid;
	protected static Icon[] icon;
	protected static IDummyMultiBlock[] handler = new IDummyMultiBlock[1024];
	public MultiBlock(int par1) {
		super(par1, Material.circuits);
		blockuid = par1;
	}
	
	private static void addBlockName(int i, String name)
	{
		blockNames.add(i, name);
	}
	
	private static void addBlockNameMain(int i, String name)
	{
		blockNamesUnlocalisedList.add(i, name);
	}
	
	private static void addBlockTexture(int i, String texture)
	{
		blockTextures.add(i, texture);
	}
	
	private static int getNextFreeID()
	{
		return ++id;
	}
	
	private static void registerMain(int i, String s)
	{
		blockNamesUnlocalised.put(s, i);
		addBlockNameMain(i,s);
	}
	
	/**
	 * Used to get the metadata(aka. damage) of your block.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your block. Remember, that this is the in-code name!
	 * @return the metadata, that was given to your block.
	 */
	public static int getMetadataByName(String unlocalisedName)
	{
		int i = -1;
		if(blockNamesUnlocalised.containsKey(unlocalisedName))
		{
			i = blockNamesUnlocalised.get(unlocalisedName);
		}else
		{
			throw new RuntimeException("No blocks with "+unlocalisedName+" index were found!");
		}
		return i;
	}
	
	/**
	 * Used to get the corresponding ItemStack of your block.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your block. Remember, that this is the in-code name!
	 * @param size - the size of this stack. max is 64.
	 * @return automatically generated ItemStack for your block, with MultiBlock id, metadata of your block and size that was given.
	 */
	public static ItemStack getStackByName(String unlocalisedName, int size)
	{
		ItemStack s = new ItemStack(blockuid,getMetadataByName(unlocalisedName),size);
		return s;
	}
	
	/**
	 * Used to register multiblocks without handler.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your block. Remember, that this is the in-code name!
	 * @param inGameName - in-game name of the block.
	 * @param texturePath - path to the texture file, that will be used for this block. Should be modname:texturename
	 */
	public static void registerMultiBlock(String unlocalisedName, String inGameName, String texturePath)
	{
		int blockMeta = getNextFreeID();
		registerMain(blockMeta,unlocalisedName);
		addBlockName(blockMeta,inGameName);
		addBlockTexture(blockMeta,texturePath);
	}
	
	/**
	 * Used to register multiblocks with handler.
	 * @param unlocalisedName - name of your block. Remember, that this is the in-code name!
	 * @param inGameName - in-game name of the block.
	 * @param texturePath - path to the texture file, that will be used for this block. Should be modname:texturename
	 * @param multiblockHandler - should be an initialized! object, that implements IDummyMultiBlock. Usually it is just an empty .class file. Used to handle different actions, happening with your multiblock. Always remember to check for your block's name! 
	 */
	public static void registerMultiBlock(String unlocalisedName, String inGameName, String texturePath, IDummyMultiBlock multiblockHandler)
	{
		registerMultiBlock(unlocalisedName,inGameName,texturePath);
		handler[getMetadataByName(unlocalisedName)] = multiblockHandler;
	}
	
	@Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getBlockHardness(MiscUtils.getBlock(par1World, par2, par3, par4),par1World, par2, par3, par4);
		}
        return this.blockHardness;
    }
	
    @SideOnly(Side.CLIENT)
    @Override
    public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getBlockBrightness(MiscUtils.getBlock(par1IBlockAccess, par2, par3, par4),par1IBlockAccess, par2, par3, par4);
		}
        return super.getBlockBrightness(par1IBlockAccess, par2, par3, par4);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getBlockTexture(MiscUtils.getBlock(par1IBlockAccess, par2, par3, par4), par1IBlockAccess, par2, par3, par4, par5);
		}
        return icon[par1IBlockAccess.getBlockMetadata(par2,par3,par4)];
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getSelectedBoundingBoxFromPool(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4);
		}
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

	public static String getUnlocalisedNameByMetadata(int meta)
	{
		return blockNamesUnlocalisedList.get(meta);
	}
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getCollisionBoundingBoxFromPool(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4);
		}
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) 
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].randomDisplayTick(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4, par5Random);
		}
    	super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
    }

    @Override
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].onBlockDestroyedByPlayer(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4, par5);
		}
    	super.onBlockDestroyedByPlayer(par1World, par2, par3, par4, par5);
    }
    
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) 
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].onNeighborBlockChange(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4, par5);
		}
    	super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    }
    
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) 
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].onBlockAdded(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4);
		}
    	super.onBlockAdded(par1World, par2, par3, par4);
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].onBlockActivated(MiscUtils.getBlock(par1World, par2, par3, par4), par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
		}
    	return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) 
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].setBlockBoundsBasedOnState(MiscUtils.getBlock(par1IBlockAccess, par2, par3, par4), par1IBlockAccess, par2, par3, par4);
		}
    	super.setBlockBoundsBasedOnState(par1IBlockAccess, par2, par3, par4);
    }
    
    @Override
    public int damageDropped(int par1)
    {
        return par1;
    }

}
