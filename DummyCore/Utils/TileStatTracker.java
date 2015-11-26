package DummyCore.Utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * A tile stat tracker is an utility which can determine if the tileentity nbt was changed and, therefore, it needs syncing
 * @author modbder
 *
 */
public class TileStatTracker 	
{
	public TileEntity trackedTile;
	public NBTTagCompound trackedTag;
	
	public TileStatTracker(TileEntity tracked)
	{
		trackedTile = tracked;
	}
	
	public boolean tileNeedsSyncing()
	{
		if(trackedTile == null) return false;
		NBTTagCompound currentTag = new NBTTagCompound();
		if(trackedTag == null)
		{
			trackedTag = new NBTTagCompound();
			trackedTile.writeToNBT(trackedTag);
			return true;
		}
		
		trackedTile.writeToNBT(currentTag);
		if(currentTag.equals(trackedTag))
		{
			trackedTile.writeToNBT(trackedTag);
			return false;
		}
		
		trackedTile.writeToNBT(trackedTag);
		return true;
	}
	
}
