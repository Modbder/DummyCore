package DummyCore.CreativeTabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Items.ItemRegistry;
import DummyCore.Utils.DummyConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @version From DummyCore 1.0
 * @author Modbder
 * Do not change anything here! Used to work with Items. 
 */
public final class CreativePageItems extends CreativeTabs{
	public int delayTime = 0;
	public ItemStack displayStack = new ItemStack((Item) Items.iron_axe,1,0);
	private static Random rand = new Random(965448655);
	private final String tabLabel;
	public List<ItemStack> itemList = new ArrayList();
	public int tries = 0;
	
	
	public CreativePageItems(String m) {
		super(m + " Items");
		tabLabel = m + " Items";
	}
	
	@Override
    public ItemStack getIconItemStack()
    {
		CoreInitialiser.proxy.choseDisplayStack(this);
    	return this.displayStack;
    }
    
    private void chooseRandomStack()
    {
    	
    }
    
    public List<ItemStack> initialiseItemsList()
    {
    	++tries;
    	if(this.itemList.isEmpty() && tries <= 1)
    	{
	    	int i = 0;
	    	for(int t = 0; t < Item.itemRegistry.getKeys().size(); ++t)
	    	{
	    		Item itm = (Item) Item.itemRegistry.getObject(Item.itemRegistry.getKeys().toArray()[t]);
	    		if(itm != null && itm.getCreativeTab() == this)
	    		{
	    			List<ItemStack> lst = new ArrayList();
    				itm.getSubItems(itm,this,lst);
    				if(!lst.isEmpty())
    				{
    					for(ItemStack stk : lst)
    					{
    						if(stk != null)
    						{
    							this.itemList.add(stk);
    						}
    					}
    				}
	    		}
	    	}
	    	return this.itemList;
    	}else
    	{
    		return this.itemList;
    	}
    	
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public String getTranslatedTabLabel()
    {
        return this.tabLabel;
    }

	@Override
	public Item getTabIconItem() {
		// TODO Auto-generated method stub
		return (Item) Item.itemRegistry.getObject("minecraft:iron_axe");
	}
}