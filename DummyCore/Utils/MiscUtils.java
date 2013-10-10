package DummyCore.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.network.PacketDispatcher;
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
		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);	
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
		return e.getHealth();
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
	
	
	/**
	 * Adds the given ItemStack to the given inventory, also updating the inventory.
	 * @version From DummyCore 1.4
	 * @param s - the stack to add
	 * @param i - the inventory for the stack
	 * @param remote - use world.isRemote here. This function is usually called on both sides, however, you can call it on server only, it will be ok, then put false in this field
	 * @return true if adding was successful, false if not.
	 */
	public static boolean addItemToInventory(ItemStack s, IInventory i, boolean remote)
	{
		
		for(int p = 0; p < i.getSizeInventory();++p)
		{
			if(i.getStackInSlot(p) != null && i.getStackInSlot(p).itemID == s.itemID && i.getStackInSlot(p).getItemDamage() == s.getItemDamage() && i.getStackInSlot(p).stackSize+s.stackSize<=64)
			{
				ItemStack slot = i.getStackInSlot(p);
				System.out.println(slot);
				if(!remote)
				{
					i.setInventorySlotContents(p, new ItemStack(slot.itemID,slot.stackSize+s.stackSize,slot.getItemDamage()));
				}
				i.onInventoryChanged();
				return true;
			}
		}
		for(int p = 0; p < i.getSizeInventory();++p)
		{
			if(i.getStackInSlot(p) == null && !remote)
			{
				i.setInventorySlotContents(p, s);
				i.onInventoryChanged();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used to check, if the Forge Ore Dictionary contains the given name in it. 
	 * @version From DummyCore 1.4
	 * @param oreName - the ore name to search
	 * @return true if OreDictionary cantains the given ore, false if not.
	 */
	public static boolean oreDictionaryContains(String oreName)
	{
		String[] names = OreDictionary.getOreNames();
		boolean ret = false;
		for(int i = 0; i < names.length; ++i)
		{
			if(names[i].contains(oreName) || names[i] == oreName)
			{
				ret = true;
			}
		}
		return ret;
	}
	
	/**
	 * Used to sync the given tile entity with the given side using DummyCore packet handler. 
	 * @version From DummyCore 1.4
	 * @param t - the tileentity to sync.
	 * @param s - the side, that will accept the packet.
	 */
	public static void syncTileEntity(ITEHasGameData t, Side s)
	{
		String packetname = "DC.Packet.";
		if(s == Side.CLIENT)packetname += "C";
		if(s == Side.SERVER)packetname += "S";
    	Packet250CustomPayload  m = new Packet250CustomPayload();
    	ByteArrayOutputStream bos = new ByteArrayOutputStream(255);
    	DataOutputStream outputStream = new DataOutputStream(bos);
    	try 
    	{
    	    outputStream.writeInt((int) t.getPosition().x);
    	    outputStream.writeInt((int) t.getPosition().y);
    	    outputStream.writeInt((int) t.getPosition().z);
    	    outputStream.writeUTF(t.getData());
    	}catch (Exception ex)
    	{
            ex.printStackTrace();
            return;
       	}
    	m.channel = packetname;
    	m.data = bos.toByteArray();
    	m.length = bos.size();
    	if(s == Side.SERVER)
    		PacketDispatcher.sendPacketToServer(m);;
    	if(s == Side.CLIENT)
    		PacketDispatcher.sendPacketToAllPlayers(m);
	}   
}
