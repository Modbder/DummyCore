package DummyCore.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;
import DummyCore.Core.CoreInitialiser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.ReflectionHelper;
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
	public static final String genUUIDString = "CB3F55A9-6DCC-4FF8-AAC7-9B87A33";
	public static final Hashtable<String, String> descriptionTable = new Hashtable<String, String>();
	public static final Hashtable<String, EnumChatFormatting> descriptionCTable = new Hashtable<String, EnumChatFormatting>();
	public static final Hashtable<List<?>, String> descriptionNTable = new Hashtable<List<?>, String>();
	public static final Hashtable<List<?>, EnumChatFormatting> descriptionNCTable = new Hashtable<List<?>, EnumChatFormatting>();
	public static final Hashtable<String, String> registeredClientData = new Hashtable<String, String>();
	public static final Hashtable<String, String> registeredClientWorldData = new Hashtable<String, String>();
	public static final Hashtable<String, String> registeredServerData = new Hashtable<String, String>();
	public static final Hashtable<String, String> registeredServerWorldData = new Hashtable<String, String>();
	public static final List<BlockPosition> unbreakableBlocks = new ArrayList<BlockPosition>();
	public static final List<ScheduledServerAction> actions = new ArrayList<ScheduledServerAction>();
	
	//ShaderGroups IDs - 
		//0 - Pixelated
		//1 -  Smooth
		//2 - Bright, Highly blured
		//3 - High contrast, Pixel outline
		//4 - Bright, Medium blured
		//5 - Bright, Black&white only, Pixel Outline
		//6 - Default, ++Colors
		//7 - 3D anaglyph
		//8 - Upside-down
		//9 - Inverted Colors
		//10 - Television Screen
		//11 - Small pixel outline, Small blur
		//12 - Moving image overlay
		//13 - Default, Television screen overlay
		//14 - Pixel outline, White-Black colors inverted, other stay the same
		//15 - Highly pixelated
		//16 - Default, --Colors
		//17 - Television Screen, Green vision, Highly pixelated
		//18 - Blured vision
		//19 - Drugs
		//20 - Pixels highly smoothened
		//21 - Small blur
		//22 - List Index End
	public static final ResourceLocation[] defaultShaders = new ResourceLocation[] {new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json")};

	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
	@SideOnly(Side.CLIENT)
	public static void bindTexture(String mod, String texture)
	{
		DrawUtils.bindTexture(mod, texture);
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
	public static void dropItemsOnBlockBreak(World par1World, int par2, int par3, int par4, Block par5, int par6)
	{
		//Was causing too much issues, had to add a try/catch statement...
		try
		{
			IInventory inv = (IInventory)par1World.getTileEntity(par2, par3, par4);
	
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
	                        EntityItem entityitem = new EntityItem(par1World, (double)((float)par2 + f), (double)((float)par3 + f1), (double)((float)par4 + f2), new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
	
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
	    	}
		}catch(Exception ex)
		{
			Notifier.notifyCustomMod("DummyCore", "[ERROR]Trying to drop items upon block breaking, but caught an exception:");
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Used to check, if the Forge Ore Dictionary contains the given name in it. 
	 * @version From DummyCore 1.4
	 * @param oreName - the ore name to search
	 * @return true if OreDictionary cantains the given ore, false if not.
	 */
	public static boolean oreDictionaryContains(String oreName)
	{
		return !OreDictionary.getOres(oreName).isEmpty();
	}
	
	/**
	 * Used to sync the given tile entity with the given side using DummyCore packet handler. 
	 * @version From DummyCore 1.4
	 * @param t - the tileentity to sync.
	 * @param s - the side, that will accept the packet.
	 */
	public static void syncTileEntity(ITEHasGameData t, Side s)
	{
		String dataString = "||mod:DummyCore.TileSync"+t.getPosition()+t.getData();
		DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
		if(s == Side.CLIENT)
		{
			DummyPacketHandler.sendToAll(simplePacket);
		}
		if(s == Side.SERVER)
		{
			DummyPacketHandler.sendToServer(simplePacket);
		}
	}  
	
	public static void syncTileEntity(NBTTagCompound tileTag, int packetID)
	{
		DummyPacketIMSG_Tile simplePacket = new DummyPacketIMSG_Tile(tileTag);
		CoreInitialiser.network.sendToAll(simplePacket);
	}  
	
	/**
	 * No longer functional. Please, remove the references from your code and use Minecraft's attrubute system instead! 
	 * */
	@Deprecated
	public static void makeItemIgnoreDamage(Item i){}
	
	/**
	 * No longer functional. Please, remove the references from your code and use Minecraft's attrubute system instead! 
	 * */
	@Deprecated
	public static void registerItemModifier(Item id, int meta,String type, String last5ofUUID,double value,IAttribute attrib,int operation){}
	
	/**
	 * Used to apply any attribute to the Player.
	 * 
	 * @param p - The player to apply the attribute at
	 * @param attrib - the attribute to modify
	 * @param uuidLast5Symbols - last 5 symbols of the unique ID of your modifier. Should be unique per item, however not required strictly. The String needs to hold 5 symbols, and allowed symbols are - 0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F
	 * @param modifier - the value, that the attribute will get modified for
	 * @param remove - should actually remove the attribute from the player, and not add it
	 * @param operation - the operation on the attribute(0 is numberical modification(aka currentAttributeValue+yourValue) and 2 is percentage modification(aka currentAttributeValue*yourValue))
	 * @param type - the condition, at which the modifier should be applied. 3 Conditions exist - inventory(the item needs to be in Player's inventory),hold(Player needs to hold the item) and armor(item needs to be in Player's armor slots)
	 */
	public static void applyPlayerModifier(EntityPlayer p,IAttribute attrib, String uuidLast5Symbols, double modifier, boolean remove, int operation, String type)
	{
		if(p.getAttributeMap().getAttributeInstance(attrib).getModifier(UUID.fromString(genUUIDString+uuidLast5Symbols)) == null)
		{
			if(!remove)
				p.getAttributeMap().getAttributeInstance(attrib).applyModifier(new AttributeModifier(UUID.fromString(genUUIDString+uuidLast5Symbols),"dam."+type+"."+attrib.getAttributeUnlocalizedName(), modifier, operation));
		}else if(remove)
		{
			if(p.getAttributeMap().getAttributeInstance(attrib).getModifier(UUID.fromString(genUUIDString+uuidLast5Symbols)) != null)
				p.getAttributeMap().getAttributeInstance(attrib).removeModifier(p.getAttributeMap().getAttributeInstance(attrib).getModifier(UUID.fromString(genUUIDString+uuidLast5Symbols)));
		}
	}
	
	@Deprecated
	public static void registerDescriptionFor(String unlocalisedName, String descr, EnumChatFormatting color){}

	@Deprecated
	public static void registerDescriptionFor(String id, int meta, String descr, EnumChatFormatting color){}
	
	/**
	 * Used to send packets from SERVER to CLIENT.
	 * @version From DummyCore 1.7
	 * @param w - the worldObj that we are operating in
	 * @param pkt - the packet to send
	 * @param x - the X coordinate
	 * @param y - the Y coordinate
	 * @param z - the Z coordinate
	 * @param dimId - the ID of the dimension to look the players. 
	 * @param distance - the distance at which the players will get found.
	 */
	@SuppressWarnings("unchecked")
	public static void sendPacketToAllAround(World w,Packet pkt, int x, int y, int z, int dimId, double distance)
	{
		List<EntityPlayer> playerLst = w.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x-0.5D, y-0.5D, z-0.5D, x+0.5D, y+0.5D, z+0.5D).expand(distance, distance, distance));
		if(!playerLst.isEmpty())
		{
			for(int i = 0; i < playerLst.size(); ++i)
			{
				EntityPlayer player = playerLst.get(i);
				if(player instanceof EntityPlayerMP)
				{
					if(pkt instanceof S35PacketUpdateTileEntity)
					{
						NBTTagCompound tileTag = new NBTTagCompound();
						w.getTileEntity(x, y, z).writeToNBT(tileTag);
						CoreInitialiser.network.sendTo(new DummyPacketIMSG_Tile(tileTag,-10), (EntityPlayerMP) player);
					}else
					{
						if(player.dimension == dimId)
							((EntityPlayerMP)player).getServerForPlayer().func_73046_m().getConfigurationManager().sendPacketToAllPlayers(pkt);
					}
				}else
				{
					Notifier.notifyDebug("Trying to send packet "+pkt+" to all around on Client side, probably a bug, ending the packet send try");
				}
			}
		}
	}
	
	/**
	 * Used to send packets from SERVER to CLIENT.
	 * @version From DummyCore 1.7
	 * @param w - the worldObj that we are operating in
	 * @param distance - the distance at which the players will get found.
	 */
	@SuppressWarnings("unchecked")
	public static void sendPacketToAll(World w,Packet pkt)
	{
		List<EntityPlayer> playerLst = w.playerEntities;
		if(!playerLst.isEmpty())
		{
			for(int i = 0; i < playerLst.size(); ++i)
			{
				EntityPlayer player = playerLst.get(i);
				if(player instanceof EntityPlayerMP)
				{
						((EntityPlayerMP)player).playerNetServerHandler.sendPacket(pkt);
				}else
				{
					Notifier.notifyDebug("Trying to send packet "+pkt+" to all on Client side, probably a bug, ending the packet send try");
				}
			}
		}
	}
	
	/**
	 * Used to send packets from SERVER to CLIENT.
	 * @version From DummyCore 1.7
	 * @param w - the worldObj that we are operating in
	 * @param pkt - the packet to send
	 * @param dimId - the ID of the dimension to look the players. 
	 */
	@SuppressWarnings("unchecked")
	public static void sendPacketToAllInDim(World w,Packet pkt, int dimId)
	{
		List<EntityPlayer> playerLst = w.playerEntities;
		if(!playerLst.isEmpty())
		{
			for(int i = 0; i < playerLst.size(); ++i)
			{
				EntityPlayer player = playerLst.get(i);
				if(player instanceof EntityPlayerMP)
				{
					if(player.dimension == dimId)
						((EntityPlayerMP)player).playerNetServerHandler.sendPacket(pkt);
				}else
				{
					Notifier.notifyDebug("Trying to send packet "+pkt+" to all in dimension "+dimId+" on Client side, probably a bug, ending the packet send try");
				}
			}
		}
	}
	
	/**
	 * Used to send packets from SERVER to CLIENT.
	 * @version From DummyCore 1.7
	 * @param w - the worldObj that we are operating in
	 * @param pkt - the packet to send
	 * @param player - the player to whom we are sending the packet.
	 */
	public static void sendPacketToPlayer(World w,Packet pkt,EntityPlayer player)
	{
		if(player instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(pkt);
		}else
		{
			Notifier.notifyDebug("Trying to send packet "+pkt+" to player "+player+"||"+player.getDisplayName()+" on Client side, probably a bug, ending the packet send try");
		}
	}
	
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean drawScaledTexturedRect_Items(int x, int y, IIcon icon, int width, int height, float zLevel)
    {
    	return DrawUtils.drawScaledTexturedRect_Items(x, y, icon, width, height, zLevel);
    }
	
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean drawScaledTexturedRect(int x, int y, IIcon icon, int width, int height, float zLevel)
    {
		return DrawUtils.drawScaledTexturedRect(x, y, icon, width, height, zLevel);
    }

	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static void drawTexture(int x, int y, IIcon icon, int width, int height, float zLevel)
    {
		DrawUtils.drawTexture(x, y, icon, width, height, zLevel);
    }
    
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static void drawTexture_Items(int x, int y, IIcon icon, int width, int height, float zLevel)
    {
		DrawUtils.drawTexture_Items(x, y, icon, width, height, zLevel);
    }
    
    /**
     * Used to check if the given class actually has the named method. Used when working with APIs of different mods(actually not)
     * @param c - the class
     * @param mName - the name of the method
     * @param classes - actual parameters of the method
     * @return true if the given method exist, false if not
     */
    public static boolean classHasMethod(Class<?> c, String mName, Class<?>... classes)
    {
    	try {
			Method m = c.getMethod(mName, classes);
			return m != null;
		} catch (Exception e) {
			return false;
		}
    }
    
	/**
	 * Have you ever thought that saving inventories to NBTTag takes too much code? Here is a nifty solution to do so!
	 * @param t - the TileEntity
	 * @param saveTag - the tag
	 */
	public static void saveInventory(TileEntity t, NBTTagCompound saveTag)
	{
		if(t instanceof IInventory)
		{
			IInventory tile = (IInventory) t;
	        NBTTagList nbttaglist = new NBTTagList();
	        for (int i = 0; i < tile.getSizeInventory(); ++i)
	        {
	            if (tile.getStackInSlot(i) != null)
	            {
	                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	                nbttagcompound1.setByte("Slot", (byte)i);
	                tile.getStackInSlot(i).writeToNBT(nbttagcompound1);
	                nbttaglist.appendTag(nbttagcompound1);
	            }
	        }
	        saveTag.setTag("Items", nbttaglist);
		}
	}
	
	/**
	 * Have you ever thought that loading inventories from NBTTag takes too much code? Here is a nifty solution to do so!
	 * @param t - the TileEntity
	 * @param loadTag - the tag
	 */
	public static void loadInventory(TileEntity t, NBTTagCompound loadTag)
	{
		if(t instanceof IInventory)
		{
			IInventory tile = (IInventory) t;
			for(int i = 0; i < tile.getSizeInventory(); ++i)
			{
				tile.setInventorySlotContents(i, null);
			}
	        NBTTagList nbttaglist = loadTag.getTagList("Items", 10);
	        for (int i = 0; i < nbttaglist.tagCount(); ++i)
	        {
	            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
	            byte b0 = nbttagcompound1.getByte("Slot");
	
	            if (b0 >= 0 && b0 < tile.getSizeInventory())
	            {
	            	tile.setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(nbttagcompound1));
	            }
	        }
		}
	}
	
	/**
	 * Actually changes the BiomeGenBase at the given coordinates. It still requires Client to update the BlockRenderer at the position!
	 * @param w - World
	 * @param biome - the biome you are changing to
	 * @param x - xCoordinate of the BLOCK
	 * @param z - zCoordinate of the BLOCK
	 */
	public static void changeBiome(World w, BiomeGenBase biome, int x, int z)
	{
		Chunk chunk = w.getChunkFromBlockCoords(x,z);
		byte[] b = chunk.getBiomeArray();
		byte cbiome = b[(z & 0xf) << 4 | x & 0xf]; //What is even going on here? Can this code be a little bit more readable?
		cbiome = (byte)(biome.biomeID & 0xff);
		b[(z & 0xf) << 4 | x & 0xf] = cbiome; //Looks like not.
		chunk.setBiomeArray(b);
		notifyBiomeChange(x,z,biome.biomeID);
	}
	
	/**
	 * Actually creates the given particles for ALL players
	 * @param particleName - the name of the particle
	 * @param posX - xCoord of the particle
	 * @param posY - yCoord of the particle
	 * @param posZ - zCoord of the particle
	 * @param par5 - particle 1 gen int. Can be motion or color(depends on the particle).
	 * @param par6 - particle 1 gen int. Can be motion or color(depends on the particle).
	 * @param par7 - particle 1 gen int. Can be motion or color(depends on the particle).
	 */
	public static void spawnParticlesOnServer(String particleName, float posX, float posY, float posZ, double par5, double par6, double par7)
	{
		String dataString = "||mod:DummyCore.Particle";
		DummyData name = new DummyData("particleName",particleName);
		DummyData xpos = new DummyData("positionX",posX);
		DummyData ypos = new DummyData("positionX",posY);
		DummyData zpos = new DummyData("positionX",posZ);
		DummyData xmot = new DummyData("par1",par5);
		DummyData ymot = new DummyData("par2",par6);
		DummyData zmot = new DummyData("par3",par7);
		DataStorage.addDataToString(name);
		DataStorage.addDataToString(xpos);
		DataStorage.addDataToString(ypos);
		DataStorage.addDataToString(zpos);
		DataStorage.addDataToString(xmot);
		DataStorage.addDataToString(ymot);
		DataStorage.addDataToString(zmot);
		String newDataString = DataStorage.getDataString();
		dataString+=newDataString;
		DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
		DummyPacketHandler.sendToAll(simplePacket);
	}
	
	/**
	 * Plays a sound to all players nearby
	 * @version From DummyFore 2.0
	 * @param x - sound x
	 * @param y - sound y
	 * @param z - sound z
	 * @param sound - the sound itself
	 * @param volume - sound volume
	 * @param pitch - sound pitch
	 * @param radius - radius to play the sound in
	 * @param dim - dimension to play the sound in
	 */
	public static void playSoundOnServerToAllNearby(double x, double y, double z, String sound, float volume, float pitch, double radius, int dim)
	{
		DummyData aaa = new DummyData("x",x);
		DummyData aab = new DummyData("y",y);
		DummyData aac = new DummyData("z",z);
		DummyData aad = new DummyData("vol",volume);
		DummyData aae = new DummyData("pitch",pitch);
		DummyData aaf = new DummyData("sound",sound);
		DummyPacketIMSG pkt = new DummyPacketIMSG("||mod:DummyCore.Sound"+aaa+""+aab+""+aac+""+aad+""+aae+""+aaf);
		DummyPacketHandler.sendToAllAround(pkt, new TargetPoint(dim, x, y, z, radius));
	}
	
	/**
	 * Adds a potion effect to the player.<BR> If the effect exists - increases the duration.<BR> If the duration is over specified amount adds +1 level. 
	 * @param mob - the entity to add the effect
	 * @param potion - the potion to apply
	 * @param index - the duration
	 * @param index2 - the duration to add +1 level at
	 * @version From DummyCore 2.0
	 */
	public static void calculateAndAddPE(EntityLivingBase mob, Potion potion, int index, int index2)
	{
		boolean hasEffect = mob.getActivePotionEffect(potion) != null;
		if(hasEffect)
		{
			int currentDuration = mob.getActivePotionEffect(potion).getDuration();
			int newDuration = currentDuration+index2;
			int newModifier = currentDuration/index;
			mob.removePotionEffect(potion.id);
			mob.addPotionEffect(new PotionEffect(potion.id,newDuration,newModifier));
		}else
		{
			mob.addPotionEffect(new PotionEffect(potion.id,index2,0));
		}
	}
	
	/**
	 * Compares if 2 itemstacks are equal on the oredict side
	 * @param stk
	 * @param stk1
	 * @return
	 * @version From DummyCore 2.0
	 */
	public static boolean oreDictionaryCompare(ItemStack stk, ItemStack stk1)
	{
		if(stk == null || stk1 == null)
			return false;
		
		if(OreDictionary.getOreIDs(stk) == null || OreDictionary.getOreIDs(stk).length == 0 || OreDictionary.getOreIDs(stk1) == null || OreDictionary.getOreIDs(stk1).length == 0)
			return false;
		
		int[] ids = OreDictionary.getOreIDs(stk);
		int[] ids1 = OreDictionary.getOreIDs(stk1);
		
		for(int i = 0; i < ids.length; ++i)
		{
			for(int j = 0; j < ids1.length; ++j)
			{
				if(ids[i] == ids1[j])
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds a specific action for the server to execute after some time
	 * @param ssa
	 */
	public static void addScheduledAction(ScheduledServerAction ssa)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
			Notifier.notifyError("Trying to add a scheduled server action not on server side, aborting!");
		
		actions.add(ssa);
	}
	
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    public static void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_, int zLevel)
    {
		DrawUtils.drawTexturedModalRect(p_73729_1_, p_73729_2_, p_73729_3_, p_73729_4_, p_73729_5_, p_73729_6_, zLevel);
    }
    
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static void renderItemStack_Full(ItemStack stk,double posX, double posY, double posZ, double screenPosX, double screenPosY, double screenPosZ, float rotation, float rotationZ, float colorRed, float colorGreen, float colorBlue, float offsetX, float offsetY, float offsetZ)
    {
		DrawUtils.renderItemStack_Full(stk, posX, posY, posZ, screenPosX, screenPosY, screenPosZ, rotation, rotationZ, colorRed, colorGreen, colorBlue, offsetX, offsetY, offsetZ, false);
    }
    
	/**
	 * <b>Deprecated!</b> Use DrawUtils from now!
	 */
	@Deprecated
    @SideOnly(Side.CLIENT)
    public static void renderItemStack(ItemStack stk,double posX, double posY, double posZ, double screenPosX, double screenPosY, double screenPosZ, float rotation, float colorRed, float colorGreen, float colorBlue, int renderPass, int itemsAmount)
    {
		DrawUtils.renderItemStack(stk, posX, posY, posZ, screenPosX, screenPosY, screenPosZ, rotation, colorRed, colorGreen, colorBlue, renderPass, itemsAmount, false);
    }
    
    /**
     * Clones the given Entity, including it's full NBTTag
     * @param e - the entity to clone
     * @return The cloned entity
     */
    public static Entity cloneEntity(Entity e)
    {
    	Entity retEntity = null;
    	try
    	{
    		retEntity = e.getClass().getConstructor(World.class).newInstance(e.worldObj);
    		retEntity.copyDataFrom(e, true);
    	}
    	catch(Exception exc)
    	{
    		return retEntity;
    	}
    	return retEntity;
    }
    
    /**
     * Changes biome at the given coordinates. Unlike the previous function this one takes the biomeID, not the biome itself and isn't world dependant
     * @param x - x position of the block
     * @param z - z position of the block
     * @param biomeID - the new BiomeID
     */
    public static void notifyBiomeChange(int x, int z, int biomeID)
    {
    	String dataString = "||mod:DummyCore.BiomeChange";
		DummyData xpos = new DummyData("positionX",x);
		DummyData zpos = new DummyData("positionZ",z);
		DummyData id = new DummyData("biomeID",biomeID);
		DataStorage.addDataToString(xpos);
		DataStorage.addDataToString(zpos);
		DataStorage.addDataToString(id);
		String newDataString = DataStorage.getDataString();
		dataString+=newDataString;
		DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
		DummyPacketHandler.sendToAll(simplePacket);
    }
    
    /**
     * Imitates the armor absorbption for the given damage. Can be used, if you damage your target inderectly, but still want the damage to get reduced by armor
     * @param base - The damaged Entity
     * @param dam - the damage source
     * @param amount - the amount of the damage
     * @return New amount of damage(the old one reduced by armor)
     */
    public static float multiplyDamageByArmorAbsorbption(EntityLivingBase base, DamageSource dam, float amount)
    {
        if (!dam.isUnblockable())
        {
            int i = 25 - base.getTotalArmorValue();
            float f1 = amount * (float)i;
            amount = f1 / 25.0F;
        }
        return amount;
    }
    
    /**
     * Imitates the damage increasement by things like Strength potion and Sharpness|Power enchantments. 
     * @param base - The damaged Entity
     * @param dam - the damage source
     * @param amount - the amount of the damage
     * @return New amount of damage(the old one reduced by armor)
     */
    public static float applyPotionDamageCalculations(EntityLivingBase base, DamageSource dam, float amount)
    {
        if (dam.isDamageAbsolute())
        {
            return amount;
        }
        else
        {
            int i;
            int j;
            float f1;

            if (base.isPotionActive(Potion.resistance) && dam != DamageSource.outOfWorld)
            {
                i = (base.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
                j = 25 - i;
                f1 = amount * (float)j;
                amount = f1 / 25.0F;
            }

            if (amount <= 0.0F)
            {
                return 0.0F;
            }
            else
            {
                i = EnchantmentHelper.getEnchantmentModifierDamage(base.getLastActiveItems(), dam);

                if (i > 20)
                {
                    i = 20;
                }

                if (i > 0 && i <= 20)
                {
                    j = 25 - i;
                    f1 = amount * (float)j;
                    amount = f1 / 25.0F;
                }

                return amount;
            }
        }
    }
    
    /**
     * Damages the given Entity ignoring the Forge EntityHurt and EntityBeeingDamaged events.
     * @param base - The damaged Entity
     * @param dam - the damage source
     * @param amount - the amount of the damage
     */
    public static void damageEntityIgnoreEvent(EntityLivingBase base, DamageSource dam, float amount)
    {
        if (!base.isEntityInvulnerable())
        {
            if (amount <= 0) return;
            amount = multiplyDamageByArmorAbsorbption(base,dam,amount);
            amount = applyPotionDamageCalculations(base,dam,amount);
            float f1 = amount;
            amount = Math.max(amount - base.getAbsorptionAmount(), 0.0F);
            base.setAbsorptionAmount(base.getAbsorptionAmount() - (f1 - amount));

            if (amount != 0.0F)
            {
                float f2 = base.getHealth();
                base.setHealth(f2 - amount);
                base.func_110142_aN().func_94547_a(dam, f2, amount);
                base.setAbsorptionAmount(base.getAbsorptionAmount() - amount);
            }
        }
    }
    
    /**
     * Allows changes of variables declared like private final || private static final. Advanced. Do not use if you do not know what you are doing!
     * Sometimes considered as a dirty hacking of the java code. I agree. There is nothing more dirty, than just removing the FINAL modifier of the variable. It's like Java can't even do anything, no matter the protection given.
     * This should not be done. However, in vanilla MC it is pretty much the only way to do so, so I can't help it.
     * The only thing, that would be worse is using ASM to remotely change the compiled final variable. That is the most disgusting thing you can do with Java, I believe.
     * @param classToAccess - the class in wich you are changing the variable
     * @param instance - if you want to modify non-static field you should put the instance of the class here. Leave null for static
     * @param value - what you actually want to be set in the variable field
     * @param fieldNames - the names of the field you are changing. Should be both for obfuscated and compiled code.
     */
    public static void setPrivateFinalValue(Class<?> classToAccess, Object instance, Object value, String fieldNames[])
    {
        Field field = ReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));
        try
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(instance, value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Extends the default mc potionArray(which is declared as public static final Potion[] potionTypes = new Potion[32]) by the given amount
     * @param byAmount - how much to extends for
     * @return the first free index in the new potionArray.
     */
    public static int extendPotionArray(int byAmount)
    {
    	int potionOffset = Potion.potionTypes.length;
		Potion[] potionTypes = new Potion[potionOffset + byAmount];
		System.arraycopy(Potion.potionTypes, 0, potionTypes, 0, potionOffset);
		setPrivateFinalValue(Potion.class,null,potionTypes,ObfuscationReflectionHelper.remapFieldNames(Potion.class.getName(), new String[] {"potionTypes","field_76425_a","a"}));
		for(int i = 0; i < Potion.potionTypes.length; ++i)
			if(Potion.potionTypes[i] == null)
				return i;
		
		return -1;
    }
    
    /**
     * Sets the block at the given coordinates to unbreakable || breakable
     * @param w - the World
     * @param x - the x of the block
     * @param y - the y of the block
     * @param z - the z of the block
     * @param remove - should actually set the block to breakable(true) of to unbreakable(false)
     */
    public static void setBlockUnbreakable(World w, int x, int y, int z, boolean remove)
    {
    	if(!isBlockUnbreakable(w,x,y,z) && !remove)
    	{
    		BlockPosition pos = new BlockPosition(w, x, y, z);
    		unbreakableBlocks.add(pos);
    	}else
    	{
    		for(int i = 0; i < unbreakableBlocks.size(); ++i)
	        {
	        	BlockPosition pos = unbreakableBlocks.get(i);
	        	if(pos.x == x && pos.y == y && pos.z == z && pos.wrld.provider.dimensionId == w.provider.dimensionId)
	        	{
	        		unbreakableBlocks.remove(pos);
	        		break;
	        	}
	        }
    	}
    }
    
    /**
     * Checks if the block at the given coordinates is unbreakable
     * @param w - the World
     * @param x - the x of the block
     * @param y - the y of the block
     * @param z - the z of the block
     * @return True if the player can break the block, false if not
     */
    public static boolean isBlockUnbreakable(World w, int x, int y, int z)
    {
    	for(int i = 0; i < unbreakableBlocks.size(); ++i)
    	{
    		BlockPosition pos = unbreakableBlocks.get(i);
    		if(pos.x == x && pos.y == y && pos.z == z && pos.wrld.provider.dimensionId == w.provider.dimensionId)
    			return true;
    	}
    	return false;
    }
    
    /**
     * Sends the packet to the server, that notifies the server about GUI button pressed. This can be actually used for any GUI, not only in world, but why would you like to do it?
     * @param buttonID - the ID on the button in the code. Can be get via yourGuiButton.id
     * @param parentClass - the GUI class, that contains the button
     * @param buttonClass - the GUI class of the button
     * @param presser - the player, who presses the button. Usually Minecraft.getMinecraft().thePlayer but sometimes you may want to send packets of other SMP players(maybe?)
     */
    @SideOnly(Side.CLIENT)
    public static void handleButtonPress(int buttonID, Class<? extends Gui> parentClass, Class<? extends GuiButton> buttonClass, EntityPlayer presser, int bX, int bY, int bZ)
    {
    	handleButtonPress(buttonID, parentClass, buttonClass, presser, bX, bY, bZ, "||data:no data");
    }
    
    /**
     * Sends the packet to the server, that notifies the server about GUI button pressed. This can be actually used for any GUI, not only in world, but why would you like to do it?
     * @param buttonID - the ID on the button in the code. Can be get via yourGuiButton.id
     * @param parentClass - the GUI class, that contains the button
     * @param buttonClass - the GUI class of the button
     * @param presser - the player, who presses the button. Usually Minecraft.getMinecraft().thePlayer but sometimes you may want to send packets of other SMP players(maybe?)
     * @param additionalData - Some additional data, that you might want to carry around. Should be a String, representing the DummyData, otherwise will get added tp the Z coordinate and make it unreadable.
     */
    @SideOnly(Side.CLIENT)
    public static void handleButtonPress(int buttonID, Class<? extends Gui> parentClass, Class<? extends GuiButton> buttonClass, EntityPlayer presser, int bX, int bY, int bZ, String additionalData)
    {
    	String dataString = "||mod:DummyCore.guiButton";
		DummyData id = new DummyData("id",buttonID);
		DummyData parent = new DummyData("parent",parentClass.getName());
		DummyData button = new DummyData("button",buttonClass.getName());
		DummyData player = new DummyData("player",presser.getCommandSenderName());
		DummyData dx = new DummyData("x",bX);
		DummyData dy = new DummyData("y",bY);
		DummyData dz = new DummyData("z",bZ);
		DataStorage.addDataToString(id);
		DataStorage.addDataToString(parent);
		DataStorage.addDataToString(button);
		DataStorage.addDataToString(player);
		DataStorage.addDataToString(dx);
		DataStorage.addDataToString(dy);
		DataStorage.addDataToString(dz);
		String newDataString = DataStorage.getDataString();
		dataString+=newDataString+additionalData;
		DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
		DummyPacketHandler.sendToServer(simplePacket);
    }
    
    /**
     * Searches for the first block, that matches the given condition at the given coordinates in the given Y range. 
     * @param w - the WorldObj where we are searching the block at
     * @param toSearch - the block that we are searching for
     * @param x - the X coordinate
     * @param z - the Z coordinate
     * @param maxY - max Y value to search. 
     * @param minY - min Y value to search
     * @param metadata - the metadata of the block to search. can be -1 or OreDectionary.WILDCARD_VALUE to ignore metadata
     * @param shouldHaveAirAbove - should the block that we find have only air blocks above it
     * @return the actual Y coordinate, or -1 if no blocks were found.
     */
    public static int search_firstBlock(World w,Block toSearch,int x, int z, int maxY, int minY, int metadata, boolean shouldHaveAirAbove)
    {
    	int y = maxY;
    	while(y > minY)
    	{
    		Block b = w.getBlock(x, y, z);
    		int meta = w.getBlockMetadata(x, y, z);
    		if(b != null && b != Blocks.air)
    		{
    			if(b == toSearch && (metadata == -1 || metadata == OreDictionary.WILDCARD_VALUE || metadata == meta))
    			{
    				return y;
    			}else if(shouldHaveAirAbove)
    			{
    				return -1;
    			}
    		}
    		--y;
    	}
    	return -1;
    }
    
    public static void openGui(World w, int x, int y, int z, EntityPlayer player, int guiID)
    {
    	player.openGui(CoreInitialiser.instance, guiID, w, x, y, z);
    }
    
    public static void setShaders(int shaderID)
    {
    	if(shaderID >= defaultShaders.length)shaderID = defaultShaders.length-1;
    	if(shaderID < 0)setShaders(null);else CoreInitialiser.proxy.initShaders(defaultShaders[shaderID]);
    }
    
    public static void setShaders(ResourceLocation shaders)
    {
    	CoreInitialiser.proxy.initShaders(shaders);
    }
    
    public static boolean classExists(String className)
    {
    	try
    	{
    		return Class.forName(className) != null;
    	}
    	catch(ClassNotFoundException cnfe)
    	{
    		return false;
    	}
    }
    
	public static EntityLivingBase getClosestEntity(List<EntityLivingBase> mobs, double x, double y, double z)
	{
		double minDistance = Double.MAX_VALUE;
		EntityLivingBase retEntity = null;
		
		for(EntityLivingBase elb : mobs)
		{
			double distance = elb.getDistance(x, y, z);
			if(distance < minDistance)
			{
				retEntity = elb;
				minDistance = distance;
			}
		}
		
		return retEntity;
	}
}
