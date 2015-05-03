package DummyCore.Events;

import DummyCore.Utils.DummyData;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class DummyEvent_OnClientGUIButtonPress extends Event{
	
	public int buttonID;
	public String client_ParentClassPath;
	public String client_ButtonClassPath;
	public EntityPlayer presser;
	public int x, y, z;
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
