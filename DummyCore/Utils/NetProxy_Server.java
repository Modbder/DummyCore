package DummyCore.Utils;

import java.lang.reflect.Constructor;

import DummyCore.CreativeTabs.CreativePageBlocks;
import DummyCore.CreativeTabs.CreativePageItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.IGuiHandler;

//Internal
public class NetProxy_Server implements IGuiHandler{
	
	public void injectOldItemBlockModel(Block b){}
	
	public void handlePacketS35(S35PacketUpdateTileEntity pkt)
	{
		
	}
	
	public EntityPlayer getPlayerOnSide(INetHandler handler)
	{
		if(handler instanceof NetHandlerPlayServer)
		{
			return ((NetHandlerPlayServer)handler).playerEntity;
		}
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return null;
	}
	
	public World getWorldForDim(int dim)
	{
		return DimensionManager.getWorld(dim);
	}
	
	public World getClientWorld()
	{
		return null;
	}
	
	public Integer[] createPossibleMetadataCacheFromBlock(Block b)
	{
		return new Integer[]{0};
	}
	
	public void registerInfo()
	{
		
	}
	
	public void registerInit()
	{
		
	}
	
	public void removeMissingTextureErrors()
	{
		
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,int x, int y, int z) {
		try
		{
			Class<?> containerClass = Class.forName(GuiContainerLibrary.containers.get(ID));
			Constructor<?> constrctr = containerClass.getConstructor(InventoryPlayer.class, TileEntity.class);
			return constrctr.newInstance(player.inventory,world.getTileEntity(new BlockPos(x, y, z)));
		}catch(Exception e)
		{
			Notifier.notifySimple("Unable to open Container for ID "+ID);
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		return null;
	}
	
	public void initShaders(ResourceLocation rLoc)
	{
		
	}
	
	public void choseDisplayStack(CreativePageBlocks blocks)
	{
		
	}
	
	public void choseDisplayStack(CreativePageItems items)
	{
		
	}

}
