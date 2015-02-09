package DummyCore.Events;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;

public class DummyEvent_OnKeyboardKeyPressed_Server extends Event{
	
	public int keyID;
	public String keyName;
	public EntityPlayer presser;
	public boolean pressed;
	
	public DummyEvent_OnKeyboardKeyPressed_Server(int keyId, String keyname, EntityPlayer player, boolean bool)
	{
		keyID = keyId;
		keyName = keyname;
		presser = player;
		pressed = bool;
	}

}
