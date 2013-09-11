package DummyCore.Items;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import DummyCore.Utils.IDummyMultiItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * @author Modbder
 * @version From DummyCore 1.1
 * @Description Used to handle all the multiitems. Do not change anything here! Some explanation to the required functions will be given.
 */
public class MultiItem extends Item{
	private static List<String> itemNames = new ArrayList();
	private static List<String> itemNamesUnlocalisedList = new ArrayList();
	private static Hashtable<String,Integer> itemNamesUnlocalised = new Hashtable();
	private static List<String> itemTextures = new ArrayList();
	private static int id = -1;
	public static int itemuid;
	private static Icon[] icon;
	private static IDummyMultiItem[] handler = new IDummyMultiItem[1024];
	
	public MultiItem(int par1) {
		super(par1);
		setHasSubtypes(true);
		setMaxDamage(0);
		itemuid = par1;
		// TODO Auto-generated constructor stub
	}
	
	private static void addItemName(int i, String name)
	{
		itemNames.add(i, name);
	}
	
	private static void addItemNameMain(int i, String name)
	{
		itemNamesUnlocalisedList.add(i, name);
	}
	
	private static void addItemTexture(int i, String texture)
	{
		itemTextures.add(i, texture);
	}
	
	private static int getNextFreeID()
	{
		return ++id;
	}
	
	private static void registerMain(int i, String s)
	{
		itemNamesUnlocalised.put(s, i);
		addItemNameMain(i,s);
	}
	
	/**
	 * Used to get the metadata(aka. damage) of your item.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your item. Remember, that this is the in-code name!
	 * @return the metadata, that was given to your item.
	 */
	public static int getMetadataByName(String unlocalisedName)
	{
		int i = -1;
		if(itemNamesUnlocalised.containsKey(unlocalisedName))
		{
			i = itemNamesUnlocalised.get(unlocalisedName);
		}else
		{
			throw new RuntimeException("No items with "+unlocalisedName+" index were found!");
		}
		return i;
	}
	
	/**
	 * Used to get the corresponding ItemStack of your item.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your item. Remember, that this is the in-code name!
	 * @param size - the size of this stack. max is 64.
	 * @return automatically generated ItemStack for your item, with MultiItem id, metadata of your item and size that was given.
	 */
	public static ItemStack getStackByName(String unlocalisedName, int size)
	{
		ItemStack s = new ItemStack(itemuid,getMetadataByName(unlocalisedName),size);
		return s;
	}
	
	/**
	 * This function is the opposite to the getMetadataByName. Used to get name of the item using it's metadata(aka. damage)
	 * @version From DummyCore 1.1
	 * @param meta - the metadata(aka. damage) of the item
	 * @return The corresponding in-code! name.
	 */
	public static String getUnlocalisedNameByMetadata(int meta)
	{
		return itemNamesUnlocalisedList.get(meta);
	}
	
	/**
	 * Used to register multiitems without handler.
	 * @version From DummyCore 1.1
	 * @param unlocalisedName - name of your item. Remember, that this is the in-code name!
	 * @param inGameName - in-game name of the item.
	 * @param texturePath - path to the texture file, that will be used for this item. Should be modname:texturename
	 */
	public static void registerMultiItem(String unlocalisedName, String inGameName, String texturePath)
	{
		int itemMeta = getNextFreeID();
		registerMain(itemMeta,unlocalisedName);
		addItemName(itemMeta,inGameName);
		addItemTexture(itemMeta,texturePath);
	}
	
	/**
	 * Used to register multiitems with handler.
	 * @param unlocalisedName - name of your item. Remember, that this is the in-code name!
	 * @param inGameName - in-game name of the item.
	 * @param texturePath - path to the texture file, that will be used for this item. Should be modname:texturename
	 * @param multiItemHandler - should be an initialized! object, that implements IDummyMultiItem. Usually it is just an empty .class file. Used to handle different actions, happening with your multiitem. Always remember to check for your item's name! 
	 */
	public static void registerMultiItem(String unlocalisedName, String inGameName, String texturePath, IDummyMultiItem multiItemHandler)
	{
		registerMultiItem(unlocalisedName,inGameName,texturePath);
		handler[getMetadataByName(unlocalisedName)] = multiItemHandler;
	}

	@Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
		}
        return false;
    }
	
	@Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
		}
        return par1ItemStack;
    }
	
	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].hitEntity(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);
		}
        return false;
    }
	
	@Override
    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLivingBase par7EntityLivingBase)
    {
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].onBlockDestroyed(par1ItemStack, par2World, par3, par4, par5, par6, par7EntityLivingBase);
		}
        return false;
    }
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		}
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				handler[i].addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		}
	}
	
	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack)
	{
		for(int i = 0; i < 1024; ++i)
		{
			if(handler[i] != null)
				return handler[i].getRarity(par1ItemStack);
		}
        return EnumRarity.common;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
		int amount = 0;
		for(int i = 0; i < itemNamesUnlocalisedList.size(); ++i)
		{
			if(itemNamesUnlocalisedList.get(i) != null)
			{
				++amount;
			}
		}
		if(amount > 0)
		{
			icon = new Icon[amount];
			for(int i = 0; i < itemTextures.size(); ++i)
			{
				if(itemTextures.get(i) != null)
				{
					icon[i] = par1IconRegister.registerIcon(itemTextures.get(i));
				}
			}
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1)
    {
        return icon[par1];
    }
	@Override
	@SideOnly(Side.CLIENT)
    public String getItemDisplayName(ItemStack par1ItemStack)
    {
        return itemNames.get(par1ItemStack.getItemDamage());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(int i = 0; i < itemNamesUnlocalisedList.size(); ++i)
        	if(itemNamesUnlocalisedList.get(i) != null)
        		par3List.add(i, new ItemStack(par1,1,i));
    }
	
}
