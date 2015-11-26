package DummyCore.Events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This is fired via MinecraftForge.EVENT_BUS
 * <br>This is fired when a server/client receives a packet sent from DC
 * @author Modbder
 * @version From DummyCore 1.7
 */
public class DummyEvent_OnPacketRecieved extends Event{

	/**
	 * The side the packet was accepted at
	 */
	public final Side effectiveSide;
	
	/**
	 * Packet data in a DummyData format
	 */
	public final String recievedData;
	
	/**
	 * The player that has recieved the packet. Can be null if the receiver is the server itself.
	 */
	public final EntityPlayer recievedEntity;
	
	public DummyEvent_OnPacketRecieved(Side s, String str, EntityPlayer pl)
	{
		effectiveSide = s;
		recievedData = str;
		recievedEntity = pl;
	}
}
