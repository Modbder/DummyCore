package DummyCore.Blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

/**
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description This is used for MultiBlocks handling. Do not change anything here! No explanation will be given to any functions in here, because you do not need this.
 */
public class ItemMultiBlock extends ItemBlock{

	public ItemMultiBlock(int par1) {
		super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
	}
	
	@Override
    public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(MultiBlock.handler[i] != null && i == par7ItemStack.getItemDamage())
				return MultiBlock.handler[i].canPlaceItemBlockOnSide(par1World, par2, par3, par4, par5, par6EntityPlayer, par7ItemStack);
		}
    	return super.canPlaceItemBlockOnSide(par1World, par2, par3, par4, par5, par6EntityPlayer, par7ItemStack);
    }
    
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
		int amount = 0;
		for(int i = 0; i < MultiBlock.blockNamesUnlocalisedList.size(); ++i)
		{
			if(MultiBlock.blockNamesUnlocalisedList.get(i) != null)
			{
				++amount;
			}
		}
		if(amount > 0)
		{
			MultiBlock.icon = new Icon[amount];
			for(int i = 0; i < MultiBlock.blockTextures.size(); ++i)
			{
				if(MultiBlock.blockTextures.get(i) != null)
				{
					MultiBlock.icon[i] = par1IconRegister.registerIcon(MultiBlock.blockTextures.get(i));
				}
			}
			for(int i = 0; i < 1024; ++i)
			{
				if(MultiBlock.handler[i] != null)
					MultiBlock.handler[i].registerIcons(par1IconRegister);
			}
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1)
    {
        return MultiBlock.icon[par1];
    }
	@Override
	@SideOnly(Side.CLIENT)
    public String getItemDisplayName(ItemStack par1ItemStack)
    {
        return MultiBlock.blockNames.get(par1ItemStack.getItemDamage());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(int i = 0; i < MultiBlock.blockNamesUnlocalisedList.size(); ++i)
        	if(MultiBlock.blockNamesUnlocalisedList.get(i) != null)
        		par3List.add(i, new ItemStack(par1,1,i));
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		for(int i = 0; i < 1024; ++i)
		{
			if(MultiBlock.handler[i] != null && i == par1ItemStack.getItemDamage())
				MultiBlock.handler[i].onItemBlockUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		}
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		
		for(int i = 0; i < 1024; ++i)
		{
			if(MultiBlock.handler[i] != null && par1ItemStack.getItemDamage() == i)
			{
				par3List = MultiBlock.handler[i].addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
			}
		}
	}
	
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack)
	{
		for(int i = 0; i < 1024; ++i)
		{
			if(MultiBlock.handler[i] != null && par1ItemStack.getItemDamage() == i)
				return MultiBlock.handler[i].getRarity(par1ItemStack);
		}
        return EnumRarity.common;
	}

	@Override
    public int getMetadata(int par1)
    {
        return par1;
    }

}
