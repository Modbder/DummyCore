package DummyCore.Utils;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.1
 * Please remember to check your item's unlocalized name before any actions using MultiBlock.getUnlocalisedNameByMetadata(meta), otherwise every multiblock will function the same!
 */
public interface IDummyMultiBlock {
	
	public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack);
	
    public void onItemBlockUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5);

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4);
    
    public EnumRarity getRarity(ItemStack par1ItemStack);
    
    public float getBlockHardness(Block b, World par1World, int par2, int par3, int par4);
    
    public float getBlockBrightness(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4);
    
    public void registerIcons(IconRegister reg);
    
    public Icon getBlockTexture(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5);
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(Block b, World par1World, int par2, int par3, int par4);
    
    public AxisAlignedBB getSelectedBoundingBoxFromPool(Block b, World par1World, int par2, int par3, int par4);
    
    public void randomDisplayTick(Block b, World par1World, int par2, int par3, int par4, Random par5Random);
    
    public void onBlockDestroyedByPlayer(Block b, World par1World, int par2, int par3, int par4, int par5);
    
    public void onNeighborBlockChange(Block b, World par1World, int par2, int par3, int par4, int par5);
    
    public void onBlockAdded(Block b, World par1World, int par2, int par3, int par4);
    
    public boolean onBlockActivated(Block b, World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9);
    
    public void setBlockBoundsBasedOnState(Block b, IBlockAccess par1IBlockAccess, int par2, int par3, int par4);
    
    public Icon getIcon(int par1, int par2);
}
