package DummyCore.Events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Unused. Need to figure out a way to make this work without DDOS-ing the server with packets >_<
 * @author modbder
 *
 */
@Deprecated
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
