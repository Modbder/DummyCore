package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

public class DummyMenuAPI {
	int height, width, ticks, scheduledTick, imageIndex;
	Minecraft mc;
	public static final List<GuiButton> STANDART_MC_BUTTONS = new ArrayList<GuiButton>();
	public static final ResourceLocation STANDART_MC_LETTERS = new ResourceLocation("textures/gui/title/minecraft.png");
	
	public DummyMenuAPI(int height, int width)
	{
		this.width = width;
		this.height = height;
		mc = Minecraft.getMinecraft();
		initialiseButtonList();
	}
	
	public void renderStandartMenu(IMainMenu menu)
	{
		ResourceLocation[] loc = menu.getLetters();
		
	}
	
	private void initialiseButtonList()
	{
		int i = height / 4 + 48;
		STANDART_MC_BUTTONS.clear();
		STANDART_MC_BUTTONS.add(new GuiButton(0, width / 2 - 100, i + 72 + 12, 98, 20, I18n.getString("menu.options")));
        if (this.mc.isDemo())
        {
            this.addDemoButtons(i, 24);
        }
        else
        {
            this.addSingleplayerMultiplayerButtons(i, 24);
        }
        STANDART_MC_BUTTONS.add(new GuiButton(0, this.width / 2 - 100, i + 72 + 12, 98, 20, I18n.getString("menu.options")));
        STANDART_MC_BUTTONS.add(new GuiButton(4, this.width / 2 + 2, i + 72 + 12, 98, 20, I18n.getString("menu.quit")));
        STANDART_MC_BUTTONS.add(new GuiButtonLanguage(5, this.width / 2 - 124, i + 72 + 12));
	}
	
    private void addDemoButtons(int par1, int par2)
    {
    	GuiButton buttonResetDemo;
        STANDART_MC_BUTTONS.add(new GuiButton(11, this.width / 2 - 100, par1, I18n.getString("menu.playdemo")));
        STANDART_MC_BUTTONS.add(buttonResetDemo = new GuiButton(12, this.width / 2 - 100, par1 + par2 * 1, I18n.getString("menu.resetdemo")));
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

        if (worldinfo == null)
        {
            buttonResetDemo.enabled = false;
        }
    }
    
    private void addSingleplayerMultiplayerButtons(int par1, int par2)
    {
    	GuiButton fmlModButton;
    	GuiButton minecraftRealmsButton;
        STANDART_MC_BUTTONS.add(new GuiButton(1, this.width / 2 - 100, par1, I18n.getString("menu.singleplayer")));
        STANDART_MC_BUTTONS.add(new GuiButton(2, this.width / 2 - 100, par1 + par2 * 1, I18n.getString("menu.multiplayer")));
        //If Minecraft Realms is enabled, halve the size of both buttons and set them next to eachother.
        fmlModButton = new GuiButton(6, this.width / 2 - 100, par1 + par2 * 2, "Mods");
        STANDART_MC_BUTTONS.add(fmlModButton);
        minecraftRealmsButton = new GuiButton(14, this.width / 2 - 100, par1 + par2 * 2, I18n.getString("menu.online"));
        minecraftRealmsButton.xPosition = this.width / 2 - 100;
        STANDART_MC_BUTTONS.add(minecraftRealmsButton);
        minecraftRealmsButton.drawButton = false;
    }

}
