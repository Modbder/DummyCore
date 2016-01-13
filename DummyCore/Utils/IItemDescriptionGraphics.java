package DummyCore.Utils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemDescriptionGraphics {
	
	public abstract boolean doDisplay(ItemStack is, EntityPlayer player);
	
	public abstract boolean displayInDescription(ItemStack is, EntityPlayer player);
	
	public abstract int getXSize(ItemStack is, EntityPlayer player);
	public abstract int getYSize(ItemStack is, EntityPlayer player);

	public abstract void draw(GuiScreen parent, ItemStack is, EntityPlayer player, int x, int y, int mouseX, int mouseY, float partialTicks, boolean offscreen);
}
