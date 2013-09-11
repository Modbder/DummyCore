package DummyCore.Utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description can be used to save you some time writing different functions.
 *
 */
public class MiscUtils {
	
	/**
	 * Used to bind texture from the mod. First string is the mod id, and the second is the texture path.
	 * @version From DummyCore 1.0
	 * @param mod - the in-code modname. always use small letters!
	 * @param texture - path to your thexture.
	 */
	@SideOnly(Side.CLIENT)
	public static void bindTexture(String mod, String texture)
	{
		ResourceLocation loc = new ResourceLocation(mod,texture);
		Minecraft.getMinecraft().func_110434_K().func_110577_a(loc);	
		loc = null;
	}
	
	/**
	 * Creates a new NBTTagCompound for the given ItemStack
	 * @version From DummyCore 1.0
	 * @param stack - the ItemStack to work with.
	 */
	public static void createNBTTag(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			return;
		}
		NBTTagCompound itemTag = new NBTTagCompound();
		stack.setTagCompound(itemTag);
	}
	
	/**
	 * used to get the ItemStack's tag compound.
	 * @version From DummyCore 1.0
	 * @param stack - the ItemStack to work with.
	 * @return NBTTagCompound of the ItemStack
	 */
	public static NBTTagCompound getStackTag(ItemStack stack)
	{
		createNBTTag(stack);
		return stack.getTagCompound();
	}
	
	/**
	 * Used to drop items from IInventory when the block is broken.
	 * @version From DummyCore 1.0
	 * @param par1World - the World object
	 * @param par2 - X coordinate of the block
	 * @param par3 - Y coordinate of the block
	 * @param par4 - Z coordinate of the block
	 */
	public static void dropItemsOnBlockBreak(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		IInventory inv = (IInventory)par1World.getBlockTileEntity(par2, par3, par4);

        if (inv != null)
        {
            for (int j1 = 0; j1 < inv.getSizeInventory(); ++j1)
            {
                ItemStack itemstack = inv.getStackInSlot(j1);

                if (itemstack != null)
                {
                    float f = par1World.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = par1World.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = par1World.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int k1 = par1World.rand.nextInt(21) + 10;

                        if (k1 > itemstack.stackSize)
                        {
                            k1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= k1;
                        EntityItem entityitem = new EntityItem(par1World, (double)((float)par2 + f), (double)((float)par3 + f1), (double)((float)par4 + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)par1World.rand.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)par1World.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)par1World.rand.nextGaussian() * f3);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }

            par1World.func_96440_m(par2, par3, par4, par5);
    	}
	}
	
	/**
	 * More readable way of getting entity's health
	 * @version From DummyCore 1.0
	 * @param e - the Entity itself
	 * @return The amount of health that entity has.
	 */
	public static float getEntityHealth(EntityLivingBase e)
	{
		return e.func_110143_aJ();
	}
	
	/**
	 * Used to get the Block at the given coordinates. This returns the block itself!
	 * @version From DummyCore 1.1
	 * @param w - the world object.
	 * @param x - X coordinate of the block
	 * @param y - Y coordinate of the block
	 * @param z - Z coordinate of the block
	 * @return the Block that is at the given coordinates.
	 */
	public static Block getBlock(IBlockAccess w, int x, int y, int z)
	{
		Block ret = null;
		ret = Block.blocksList[w.getBlockId(x, y, z)];
		return ret;
	}
	
}
