package DummyCore.Client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

/**
 * Internal. The button for changing the main menu.
 * @author modbder
 *
 */
public class GuiButton_ChangeGUI extends GuiButton{

	public GuiButton_ChangeGUI(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_,int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
		super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
	}
	
	@Override
    public void playPressSound(SoundHandler p_146113_1_)
    {
		p_146113_1_.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
		Minecraft.getMinecraft().displayGuiScreen(new GuiMenuList(Minecraft.getMinecraft().currentScreen));
    }

}
