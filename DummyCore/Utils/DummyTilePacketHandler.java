package DummyCore.Utils;

import DummyCore.Core.CoreInitialiser;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class DummyTilePacketHandler implements IMessageHandler<DummyPacketIMSG_Tile, IMessage> {

	@Override
	public IMessage onMessage(DummyPacketIMSG_Tile message, MessageContext ctx) 
	{
		Side s = ctx.side;
		int packetID = -10;
		if(message.dataTag.hasKey("packetID"))
		{
			packetID = message.dataTag.getInteger("packetID");
			message.dataTag.removeTag("packetID");
		}
		S35PacketUpdateTileEntity genPkt = new S35PacketUpdateTileEntity(message.dataTag.getInteger("x"),message.dataTag.getInteger("y"),message.dataTag.getInteger("z"),packetID,message.dataTag);
		if(s == Side.CLIENT)
		{
			ctx.getClientHandler().handleUpdateTileEntity(genPkt);
		}else
		{
			
		}
		return null;
	}
	
	public static void sendToAll(DummyPacketIMSG_Tile message)
	{
		CoreInitialiser.network.sendToAll(message);
	}
	
	public static void sendToAllAround(DummyPacketIMSG_Tile message, TargetPoint pnt)
	{
		CoreInitialiser.network.sendToAllAround(message, pnt);
	}
	
	public static void sendToAllAround(DummyPacketIMSG_Tile message, int dim)
	{
		CoreInitialiser.network.sendToDimension(message, dim);
	}
	
	public static void sendToPlayer(DummyPacketIMSG_Tile message, EntityPlayerMP player)
	{
		CoreInitialiser.network.sendTo(message, (EntityPlayerMP) player);
	}
	
	public static void sendToServer(DummyPacketIMSG_Tile message)
	{
		CoreInitialiser.network.sendToServer(message);
	}

}