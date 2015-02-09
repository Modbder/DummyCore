package DummyCore.Utils;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IAttributeModifier {
	
	public abstract String getType(ItemStack stack, EntityPlayer p);
	
	public abstract double getValue(ItemStack stack, EntityPlayer p);
	
	public abstract IAttribute getAttribute(ItemStack stack, EntityPlayer p);
	
	public abstract String last5OfUUID(ItemStack stack, EntityPlayer p);
	
	public abstract int getOperation(ItemStack stack, EntityPlayer p);

}
