package DummyCore.Events;

import DummyCore.Utils.DummyData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired via MinecraftForge.EVENT_BUS
 * <br>It is fired when a client buttonpress packet arrives at SERVER
 * <br>This is only called on Side.SERVER
 * @author modbder
 *
 */
public class DummyEvent_OnClientGUIButtonPress extends Event{
	
	/**
	 * The ID of the button pressed in the GUI
	 */
	public int buttonID;
	/**
	 * A path to the GUI class the button got triggered in
	 */
	public String client_ParentClassPath;
	/**
	 * The path to the button class itself
	 */
	public String client_ButtonClassPath;
	/**
	 * The player who pressed the button
	 */
	public EntityPlayer presser;
	/**
	 * X Y and Z positions, if the modmaker chose to define them
	 */
	public int x, y, z;
	/**
	 * Any kind of additional press data.
	 */
	public DummyData[] additionalData;
	
	public DummyEvent_OnClientGUIButtonPress(int bID, String s, String s_1, EntityPlayer player,int dx, int dy, int dz, DummyData[] data)
	{
		buttonID = bID;
		client_ParentClassPath = s;
		client_ButtonClassPath = s_1;
		presser = player;
		x = dx;
		y = dy;
		z = dz;
		additionalData = data;
	}

}
