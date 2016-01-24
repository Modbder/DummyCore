package DummyCore.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

/**
 * 
 * @author Modbder
 *
 * @Description Used in code where you want to reference either ItemStack or it's OreDictionary replacement.
 */
public class UnformedItemStack {
	
	public List<ItemStack> possibleStacks = new ArrayList<ItemStack>();
	
	public UnformedItemStack()
	{
		
	}
	
	public UnformedItemStack(String...lst )
	{
		for(String s : lst)
			possibleStacks.addAll(OreDictionary.getOres(s));
			
		sort();
	}
	
	public UnformedItemStack(ItemStack is)
	{
		possibleStacks.add(is);
		sort();
	}
	
	public UnformedItemStack(String oreDictName)
	{
		possibleStacks.addAll(OreDictionary.getOres(oreDictName));
		sort();
	}
	
	public UnformedItemStack(List<?> lst)
	{
		for(Object obj1 : lst)
		{
			if(obj1 instanceof ItemStack)
			{
				possibleStacks.add((ItemStack)obj1);
			}
			else if(obj1 instanceof String)
			{
				possibleStacks.addAll(OreDictionary.getOres((String)obj1));
			}
		}
		sort();
	}
	
	public UnformedItemStack(ItemStack[] stk)
	{
		possibleStacks.addAll(Arrays.asList(stk));
		sort();
	}
	
	public UnformedItemStack(Block b)
	{
		possibleStacks.add(new ItemStack(b,1,OreDictionary.WILDCARD_VALUE));
		sort();
	}
	
	public UnformedItemStack(Item i)
	{
		possibleStacks.add(new ItemStack(i,1,OreDictionary.WILDCARD_VALUE));
		sort();
	}
	
	public UnformedItemStack(Object obj)
	{
		if(obj instanceof String[])
			for(String s : (String[])obj)
				possibleStacks.addAll(OreDictionary.getOres(s));
		if(obj instanceof ItemStack)
			possibleStacks.add((ItemStack)obj);
		if(obj instanceof String)
			possibleStacks.addAll(OreDictionary.getOres((String)obj));
		if(obj instanceof List<?>)
		{
			for(Object obj1 : (List<?>)obj)
			{
				if(obj1 instanceof ItemStack)
				{
					possibleStacks.add((ItemStack)obj1);
				}
				else if(obj1 instanceof String)
				{
					possibleStacks.addAll(OreDictionary.getOres((String)obj1));
				}
			}
		}		
		if(obj instanceof ItemStack[])
			possibleStacks.addAll(Arrays.asList((ItemStack[])obj));
		if(obj instanceof Block)
			possibleStacks.add(new ItemStack((Block)obj,1,OreDictionary.WILDCARD_VALUE));
		if(obj instanceof Item)
			possibleStacks.add(new ItemStack((Item)obj,1,OreDictionary.WILDCARD_VALUE));
		sort();
	}
	
	public boolean matches(UnformedItemStack uis)
	{
		if(uis.possibleStacks.size() == this.possibleStacks.size())
		{
			forThis:for(int i = 0; i < possibleStacks.size(); ++i)
			{
				ItemStack is = possibleStacks.get(i);
				for(int j = 0; j < uis.possibleStacks.size(); ++j)
				{
					ItemStack is1 =  uis.possibleStacks.get(i);
					if(MiscUtils.compareItemStacks(is, is1))
						continue forThis;
				}
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean itemStackMatches(ItemStack is)
	{
		if(is == null)
			return false;
		for(ItemStack s : possibleStacks)
		{
			if(is.isItemEqual(s) || (is.getItem().equals(s.getItem()) && s.getItemDamage() == OreDictionary.WILDCARD_VALUE))
				return true;
		}
		return false;
	}
	
	public String toString()
	{
		String str = "";
		for(ItemStack s : possibleStacks)str += s;
		return str;
	}

	public UnformedItemStack copy()
	{
		return new UnformedItemStack(this.possibleStacks);
	}
	
	public void nullify()
	{
		for(ItemStack s : possibleStacks)s.stackSize = 0;
	}
	
	public void sort()
	{
		List<ItemStack> possibleStacksCopy = new ArrayList<ItemStack>();
		possibleStacksCopy.addAll(possibleStacks);
		possibleStacks.clear();
		for(int i = 0; i < possibleStacksCopy.size();++i)
		{
			ItemStack is = possibleStacksCopy.get(i);
			if(is != null && !possibleStacks.contains(is))possibleStacks.add(is);
		}
		possibleStacksCopy.clear();
		possibleStacksCopy = null;
	}
	
	public ItemStack getISToDraw(long time)
	{
		int size = this.possibleStacks.size();
		if(size <= 0)return null;
		return this.possibleStacks.get(((int)(time/30))%size);
	}
	
	public void writeToNBTTagCompound(NBTTagCompound tag)
	{
		NBTTagList items = new NBTTagList();
		for(ItemStack is : this.possibleStacks)
		{
			NBTTagCompound itemTag = new NBTTagCompound();
			is.writeToNBT(itemTag);
			items.appendTag(itemTag);
		}
		tag.setTag("unformedISList", items);
	}
	
	public void readFromNBTTagCompound(NBTTagCompound tag)
	{
		NBTTagList items = tag.getTagList("unformedISList", 10);
		for(int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound itemTag = items.getCompoundTagAt(i);
			ItemStack is = ItemStack.loadItemStackFromNBT(itemTag);
			this.possibleStacks.add(is);
		}
		this.sort();
	}
}
