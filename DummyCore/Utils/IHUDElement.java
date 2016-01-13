package DummyCore.Utils;

import net.minecraft.client.gui.ScaledResolution;

public interface IHUDElement {
	
	public abstract int getXOffset();
	
	public abstract int getYOffset();
	
	public abstract EnumGuiPosition offsetPoint();
	
	public abstract boolean display();
	
	public abstract void draw(int i, int j, float partialTicks, ScaledResolution res);

	public abstract boolean displayInGUIs();
}
