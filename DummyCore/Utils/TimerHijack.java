package DummyCore.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Just a simple way to get MC's Timer object
 * @author modbder
 *
 */
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
