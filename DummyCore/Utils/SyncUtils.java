package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal. Undocumented.
 * @author modbder
 *
 */
public class SyncUtils {
	
	public static List<String> needsSyncing_Global = new ArrayList<String>();
	public static List<String> needsSyncing_Player = new ArrayList<String>();
	
	public static void addRequiresSync(String playerName, String modid, String dataName)
	{
		String mainSyncString = playerName+"|"+modid+":"+dataName;
		if(!needsSyncing_Player.contains(mainSyncString))
			needsSyncing_Player.add(mainSyncString);
	}
	
	public static void addRequiresSync(String modid, String dataName)
	{
		String mainSyncString = modid+":"+dataName;
		if(!needsSyncing_Global.contains(mainSyncString))
			needsSyncing_Global.add(mainSyncString);
	}
	
	public static void makeSync_LotsSmallPackets()
	{
		for(int i = 0; i < needsSyncing_Global.size(); ++i)
		{
			String syncID = needsSyncing_Global.get(i);
			int indexOfStrip = syncID.indexOf(":");
			if(indexOfStrip != -1)
			{
				String modid = syncID.substring(0,indexOfStrip);
				String dataName = syncID.substring(indexOfStrip+1);
				if(MiscUtils.registeredServerWorldData.containsKey(modid+"|"+dataName))
				{
					String dataString = "||mod:DummyCore.InfoSync"+"||"+modid+":"+dataName+"||ddata:"+MiscUtils.registeredServerWorldData.get(modid+"|"+dataName);
					DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
					DummyPacketHandler.sendToAll(simplePacket);
				}else
				{
					Notifier.notifyCustomMod(modid, "The sync packet for data "+modid+"|"+dataName+" could not be generated - the requested server data does not exist!");
				}
			}
		}
		for(int i = 0; i < needsSyncing_Player.size(); ++i)
		{
			String syncID = needsSyncing_Player.get(i);
			int indexOfStrip = syncID.indexOf(":");
			int indexOfPlayer = syncID.indexOf("|");
			if(indexOfPlayer != -1 && indexOfStrip != -1)
			{
				String playerName = syncID.substring(0, indexOfPlayer);
				String modid = syncID.substring(indexOfPlayer+1,indexOfStrip);
				String dataName = syncID.substring(indexOfStrip+1);
				if(MiscUtils.registeredServerData.containsKey(playerName+"_"+modid+"|"+dataName))
				{
					String dataString = "||mod:DummyCore.PlayerInfoSync"+"||"+"playerName:"+playerName+"||"+modid+ ":" + dataName+"||ddata:"+MiscUtils.registeredServerData.get(playerName+"_"+modid+"|"+dataName);
					DummyPacketIMSG simplePacket = new DummyPacketIMSG(dataString);
					DummyPacketHandler.sendToAll(simplePacket);
				}else
				{
					Notifier.notifyCustomMod(modid, "The sync packet for data "+playerName+"_"+modid+ "could not be generated - the requested server data does not exist!");
				}
			}
		}
		needsSyncing_Global.clear();
		needsSyncing_Player.clear();
	}

}
