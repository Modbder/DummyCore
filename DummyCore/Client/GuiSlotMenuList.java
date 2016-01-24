package DummyCore.Client;

import DummyCore.Utils.DummyData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

/**
 * This is basically a GuiModList
 * @author modbder, forge team
 *
 */
public class GuiSlotMenuList extends GuiScrollingList {
	
    private GuiMenuList parent;

    public GuiSlotMenuList(GuiMenuList parent, int listWidth)
    {
        super(Minecraft.getMinecraft(), listWidth, parent.height, 32, parent.height - 66 + 4, 10, 35,Minecraft.getMinecraft().displayWidth,Minecraft.getMinecraft().displayHeight);
        this.parent=parent;
    }

    @Override
    protected int getSize()
    {
        return MainMenuRegistry.menuList.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
        this.parent.selectIndex(var1);
    }

    @Override
    protected boolean isSelected(int var1)
    {
        return this.parent.indexSelected(var1);
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawDefaultBackground();
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        DummyData data = MainMenuRegistry.menuInfoLst.get(listIndex);
        this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(data.fieldName, listWidth - 10), this.left + 3 , var3 + 2, 0xFFFFFF);
        this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(data.fieldValue, listWidth - 10), this.left + 3 , var3 + 12, 0xCCCCCC);
    }

}
