package DummyCore.CreativeTabs;

import java.util.Random;
import java.util.UUID;

import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Items.ItemRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @version From DummyCore 1.0
 * @author Modbder
 * Do not change anything here! Used to work with Items. 
 */
public final class CreativePageItems extends CreativeTabs{
	private int delayTime = 0;
	private ItemStack displayStack = new ItemStack(Item.axeIron,1,0);
	private static Random rand = new Random(965448655);
	private final String tabLabel;
	
	public CreativePageItems(String m) {
		super(m + " Items");
		tabLabel = m + " Items";
	}
	
    public ItemStack getIconItemStack()
    {
    	this.chooseRandomStack();
    	return this.displayStack;
    }
    
    private void chooseRandomStack()
    {
    	++this.delayTime;
    	if(this.delayTime >= 40)
    	{
    		this.delayTime = 0;
    		Item[] itemList = initialiseItemsList();
    		if(itemList != null && itemList.length >= 1)
    		{
    			int random = rand.nextInt(itemList.length);
    			if(itemList[random] != null)
    				if(itemList[random].getIconFromDamage(0) != null)
    					this.displayStack = new ItemStack(itemList[random],1,0);
    		}
    	}
    }
    
    private Item[] initialiseItemsList()
    {
    	int i = 0;
    	for(int t = 0; t < Item.itemsList.length; ++t)
    	{
    		Item b = Item.itemsList[t];
    		if(b != null)
    		{
    			if(b.getCreativeTab() != null && b.getCreativeTab() instanceof CreativePageItems && b.getIconFromDamage(0) != null && (ItemRegistry.itemsList.get(b) == this.getTabLabel()))
    			{
    				++i;
    			}
    		}
    	}
    	Item[] itemsList = new Item[i];
    	int r = 0;
    	for(int t = 0; t < Item.itemsList.length; ++t)
    	{
    		Item b = Item.itemsList[t];
    		if(b != null)
    		{
    			if(b.getCreativeTab() != null && b.getCreativeTab() instanceof CreativePageItems && b.getIconFromDamage(0) != null && (ItemRegistry.itemsList.get(b) == this.getTabLabel()))
    			{
    				itemsList[r] = b;
    				++r;
    			}
    		}
    	}
    	
        return itemsList;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String getTranslatedTabLabel()
    {
        return this.tabLabel;
    }
}