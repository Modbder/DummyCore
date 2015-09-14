package DummyCore.CreativeTabs;

import java.util.ArrayList;
import java.util.List;
import DummyCore.Core.CoreInitialiser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @version From DummyCore 1.0
 * @author Modbder
 * Do not change anything here! Used to work with Blocks. 
 */
public final class CreativePageBlocks extends CreativeTabs{
	public int delayTime = 0;
	public ItemStack displayStack = new ItemStack(Blocks.crafting_table,1,0);
	private final String tabLabel;
	public List<ItemStack> blockList = new ArrayList<ItemStack>();
	public int tries = 0;
	public ItemStack overrideDisplayStack;
	
	public CreativePageBlocks(String m) {
		super(m + " Blocks");
		tabLabel = m + " Blocks";
	}
	
    public ItemStack getIconItemStack()
    {
    	if(overrideDisplayStack != null)
    		return overrideDisplayStack;
    	CoreInitialiser.proxy.choseDisplayStack(this);
    	return this.displayStack;
    }
    
    public List<ItemStack> initialiseBlocksList()
    {
    	++tries;
    	if(this.blockList.isEmpty() && tries <= 1)
    	{
	    	for(int t = 0; t < Block.blockRegistry.getKeys().size(); ++t)
	    	{
	    		Block b = Block.getBlockFromName((String) Block.blockRegistry.getKeys().toArray()[t]);
	    		if(b != null && b.getCreativeTabToDisplayOn() == this)
	    		{
	    			Item itm = Item.getItemFromBlock(b);
	    			if(itm != null)
	    			{
	    				List<ItemStack> lst = new ArrayList<ItemStack>();
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