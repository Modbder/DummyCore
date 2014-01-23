package DummyCore.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;

public class DummyPacketHandler implements IPacketHandler{

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
    	int x = 0;
    	int y = 0;
    	int z = 0;
    	String gameData = "data";
    	System.out.println("data");
    	try {
			Side s = FMLCommonHandler.instance().getEffectiveSide();
			if(s == Side.CLIENT)
			{
				ByteArrayInputStream b = new ByteArrayInputStream(packet.data, 0, packet.length);
				DataInputStream dis = new DataInputStream(b);
				if(packet.channel.contains("DC.Packet.C"))
				{
					x = dis.readInt();
					y = dis.readInt();
					z = dis.readInt();
					gameData = dis.readUTF();
					TileEntity t = FMLClientHandler.instance().getClient().theWorld.getBlockTileEntity(x, y, z);
					if(t instanceof ITEHasGameData && gameData != "data")
					{
						((ITEHasGameData)t).setData(DataStorage.parseData(gameData));
					}
				}
			}else
			{
				ByteArrayInputStream b = new ByteArrayInputStream(packet.data, 0, packet.length);
				DataInputStream dis = new DataInputStream(b);
				if(packet.channel.contains("DC.Packet.S"))
				{
					x = dis.readInt();
					y = dis.readInt();
					z = dis.readInt();
					gameData = dis.readUTF();
					TileEntity t = FMLServerHandler.instance().getServer().getEntityWorld().getBlockTileEntity(x, y, z);
					if(t instanceof ITEHasGameData && gameData != "data")
					{
						((ITEHasGameData)t).setData(DataStorage.parseData(gameData));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
