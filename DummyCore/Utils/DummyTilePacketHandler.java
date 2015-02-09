package DummyCore.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import DummyCore.Core.CoreInitialiser;
import DummyCore.Events.DummyEvent_OnPacketRecieved;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToMessageCodec;

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