package DummyCore.Client;

import org.lwjgl.opengl.GL11;

import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * This is basically a GuiModList
 * @author modbder, forge team
 *
 */
public class GuiMenuList extends GuiScreen{
	
    private GuiScreen mainMenu;
    private GuiSlotMenuList modList;
    private int selected = -1;
    private Class<?> selectedMod;
    private int listWidth;
    public GuiMenuList(GuiScreen mainMenu)
    {
        this.mainMenu=mainMenu;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui()
    {
        for (DummyData data : MainMenuRegistry.menuInfoLst) {
            listWidth=Math.max(listWidth,getFontRenderer().getStringWidth(data.fieldName) + 10);
            listWidth=Math.max(listWidth,getFontRenderer().getStringWidth(data.fieldValue) + 10);
        }
        listWidth=Math.min(listWidth, 150);
        this.buttonList.add(new GuiButton(6, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        this.modList=new GuiSlotMenuList(this, listWidth);
        this.modList.registerScrollButtons(this.buttonList, 7, 8);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
        {
            switch (button.id)
            {
                case 6:
                {
                    this.mc.displayGuiScreen(this.mainMenu);
                    return;
                }
			default:
				break;
            }
        }
        try{super.actionPerformed(button);}catch(Exception ex){ex.printStackTrace();}
    }

    public int drawLine(String line, int offset, int shifty)
    {
        this.fontRendererObj.drawString(line, offset, shifty, 0xd7edea);
        return shifty + 10;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
        this.drawCenteredString(this.fontRendererObj, "Menu List", this.width / 2, 16, 0xFFFFFF);
        int offset = this.listWidth  + 20;
        if (selectedMod != null) 
        {
        	DummyData data = MainMenuRegistry.menuInfoLst.get(selected);
            GL11.glEnable(GL11.GL_BLEND);
        	{
                offset = ( this.listWidth + this.width ) / 2;
                this.drawCenteredString(this.fontRendererObj, data.fieldName, offset, 35, 0xFFFFFF);
                this.drawCenteredString(this.fontRendererObj, data.fieldValue, offset, 45, 0xFFFFFF);
            }
            GL11.glDisable(GL11.GL_BLEND);
        }
        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);
    }

    Minecraft getMinecraftInstance() {
        /** Reference to the Minecraft object. */
        return mc;
    }

    FontRenderer getFontRenderer() {
        /** The FontRenderer used by GuiScreen */
        return fontRendererObj;
    }

    /**
     * @param var1
     */
    public void selectIndex(int var1)
    {
        this.selected=var1;
        if (var1>=0 && var1<=MainMenuRegistry.menuList.size()) {
            this.selectedMod=MainMenuRegistry.menuList.get(selected);
        } else {
            this.selectedMod=null;
        }
        DummyConfig.setMainMenu(selected);
    }

    public boolean indexSelected(int var1)
    {
        return var1==selected;
    }
}
