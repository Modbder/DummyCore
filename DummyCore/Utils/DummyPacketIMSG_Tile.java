package DummyCore.Utils;

import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class DummyPacketIMSG_Tile implements IMessage{
	
	public NBTTagCompound dataTag;
	
	public DummyPacketIMSG_Tile()
	{
		
	}
	
	public DummyPacketIMSG_Tile(NBTTagCompound data, int id)
	{
		dataTag = data;
		dataTag.setInteger("packetID", id);
	}
	
	public DummyPacketIMSG_Tile(NBTTagCompound data)
	{
		dataTag = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dataTag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, dataTag);
		
	}

}
