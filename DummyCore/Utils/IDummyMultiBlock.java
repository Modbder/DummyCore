package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * Please remember to check your item's unlocalized name before any actions using MultiBlock.getUnlocalisedNameByMetadata(meta), otherwise every multiblock will function the same!
 * @warning Will get removed due to 1.7.2 changes
 */
@Deprecated
public interface IDummyMultiBlock {
	
	public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack);
	
    public void onItemBlockUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5);

    public List addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4);
    
    public EnumRarity getRarity(ItemStack par1ItemStack);
    
    public float getBlockHardness(Block b, World par1World, int par2, int par3, int par4);
    
    public float getBlockBrightness(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4);
    
    public void registerIcons(IIconRegister reg);
    
    public IIcon getBlockTexture(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5);
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(Block b, World par1World, int par2, int par3, int par4);
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(Block b, World par1World, int par2, int par3, int par4);
    
    public void randomDisplayTick(Block b, World par1World, int par2, int par3, int par4, Random par5Random);
    
    public void onBlockDestroyedByPlayer(Block b, World par1World, int par2, int par3, int par4, int par5);
    
    public void onNeighborBlockChange(Block b, World par1World, int par2, int par3, int par4, Block par5);
    
    public void onBlockAdded(Block b, World par1World, int par2, int par3, int par4);
    
    public boolean onBlockActivated(Block b, World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9);
    
    public void setBlockBoundsBasedOnState(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4);
    
    public IIcon getIcon(int par1, int par2);
    
    public int getRenderColor(int par1);
    
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune);
    
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random);
}
