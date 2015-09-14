package DummyCore.Utils;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class TimerHijack {
	
	public static final String mcpName = "timer";
	public static final String deobfName = "field_71428_T";
	public static final String obfName = "Q";
	
	@SideOnly(Side.CLIENT)
	public static Timer mcTimer;

	@SideOnly(Side.CLIENT)
	public static void initMCTimer()
	{
		mcTimer = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[]{mcpName, obfName, deobfName});
	}
}
