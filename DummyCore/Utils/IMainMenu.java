package DummyCore.Utils;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public interface IMainMenu {
	
	public abstract ResourceLocation[] getBackgroundImages();
	
	public abstract boolean shouldRenderFullScreen(int imageIndex);
	
	public abstract List<GuiButton> getButtonList();
	
	public abstract void render();
	
	public abstract List getTextFor(EnumGuiPosition e);
	
	public abstract ResourceLocation[] getLetters();
	
	public abstract boolean renderUnstandartLetters();
	
	public abstract FontRenderer textRenderer();

}
