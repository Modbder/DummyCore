package DummyCore.CreativeTabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Utils.DummyConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @version From DummyCore 1.0
 * @author Modbder
 * Do not change anything here! Used to work with Blocks. 
 */
public final class CreativePageBlocks extends CreativeTabs{
	public int delayTime = 0;
	public ItemStack displayStack = new ItemStack(Blocks.crafting_table,1,0);
	private static Random rand = new Random(543643456);
	private final String tabLabel;
	public List<ItemStack> blockList = new ArrayList();
	public int tries = 0;
	
	public CreativePageBlocks(String m) {
		super(m + " Blocks");
		tabLabel = m + " Blocks";
	}
	
    public ItemStack getIconItemStack()
    {
    	CoreInitialiser.proxy.choseDisplayStack(this);
    	return this.displayStack;
    }
    
    private void chooseRandomStack()
    {
    	
    }
    
    public List<ItemStack> initialiseBlocksList()
    {
    	++tries;
    	if(this.blockList.isEmpty() && tries <= 1)
    	{
	    	int i = 0;
	    	for(int t = 0; t < Block.blockRegistry.getKeys().size(); ++t)
	    	{
	    		Block b = Block.getBlockFromName((String) Block.blockRegistry.getKeys().toArray()[t]);
	    		if(b != null && b.getCreativeTabToDisplayOn() == this)
	    		{
	    			Item itm = Item.getItemFromBlock(b);
	    			if(itm != null)
	    			{
	    				List<ItemStack> lst = new ArrayList();
	    				itm.getSubItems(itm,this,lst);
	    				if(!lst.isEmpty())
	    				{
	    					for(ItemStack stk : lst)
	    					{
	    						if(stk != null)
	    						{
	    							this.blockList.add(stk);
	    						}
	    					}
	    						
	    				}
	    			}
	    			
	    		}
	    	}
	    	
	        return blockList;
    	}else
    	{
    		return this.blockList;
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
		return displayStack.getItem();
	}
}