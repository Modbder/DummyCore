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
public class DummyPacketHandler implements IMessageHandler<DummyPacketIMSG, IMessage> {

	@Override
	public IMessage onMessage(DummyPacketIMSG message, MessageContext ctx) 
	{
		Side s = ctx.side;
		if(s == Side.CLIENT)
		{
			MinecraftForge.EVENT_BUS.post(new DummyEvent_OnPacketRecieved(s, message.dataStr, CoreInitialiser.proxy.getPlayerOnSide(ctx.getClientHandler())));
		}else
		{
			MinecraftForge.EVENT_BUS.post(new DummyEvent_OnPacketRecieved(s, message.dataStr, CoreInitialiser.proxy.getPlayerOnSide(ctx.getServerHandler())));
		}
		return null;
	}
	
	public static void sendToAll(DummyPacketIMSG message)
	{
		CoreInitialiser.network.sendToAll(message);
	}
	
	public static void sendToAllAround(DummyPacketIMSG message, TargetPoint pnt)
	{
		CoreInitialiser.network.sendToAllAround(message, pnt);
	}
	
	public static void sendToAllAround(DummyPacketIMSG message, int dim)
	{
		CoreInitialiser.network.sendToDimension(message, dim);
	}
	
	public static void sendToPlayer(DummyPacketIMSG message, EntityPlayerMP player)
	{
		CoreInitialiser.network.sendTo(message, (EntityPlayerMP) player);
	}
	
	public static void sendToServer(DummyPacketIMSG message)
	{
		CoreInitialiser.network.sendToServer(message);
	}

}