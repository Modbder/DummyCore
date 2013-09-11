package DummyCore.CreativeTabs;

import java.util.Random;
import java.util.UUID;

import DummyCore.Blocks.BlocksRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * @version From DummyCore 1.0
 * @author Modbder
 * Do not change anything here! Used to work with Blocks. 
 */
public final class CreativePageBlocks extends CreativeTabs{
	private int delayTime = 0;
	private ItemStack displayStack = new ItemStack(Block.workbench,1,0);
	private static Random rand = new Random(543643456);
	private final String tabLabel;
	
	public CreativePageBlocks(String m) {
		super(m + " Blocks");
		tabLabel = m + " Blocks";
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
    		Block[] blockList = initialiseBlocksList();
    		if(blockList != null && blockList.length >= 1)
    		{
    			int random = rand.nextInt(blockList.length);
    			if(blockList[random] != null)
    				if(blockList[random].getIcon(0,0) != null)
    					this.displayStack = new ItemStack(blockList[random],1,0);
    		}
    	}
    }
    
    private Block[] initialiseBlocksList()
    {
    	int i = 0;
    	for(int t = 0; t < Block.blocksList.length; ++t)
    	{
    		Block b = Block.blocksList[t];
    		if(b != null)
    		{
    			if(b.getCreativeTabToDisplayOn() != null && b.getCreativeTabToDisplayOn() instanceof CreativePageBlocks && b.getIcon(0,0) != null && (BlocksRegistry.blocksList.get(b) == this.getTabLabel()))
    			{
    				++i;
    			}
    		}
    	}
    	Block[] blockList = new Block[i];
    	int r = 0;
    	for(int t = 0; t < Block.blocksList.length; ++t)
    	{
    		Block b = Block.blocksList[t];
    		if(b != null)
    		{
    			if(b.getCreativeTabToDisplayOn() != null && b.getCreativeTabToDisplayOn() instanceof CreativePageBlocks && b.getIcon(0,0) != null && (BlocksRegistry.blocksList.get(b) == this.getTabLabel()))
    			{
    				blockList[r] = b;
    				++r;
    			}
    		}
    	}
    	
        return blockList;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public String getTranslatedTabLabel()
    {
        return this.tabLabel;
    }
}