package DummyCore.Utils;
import DummyCore.Core.Core;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ColoredLightHandler extends Entity{
	public ColoredLightHandler(World par1World) {
		super(par1World);
		setSize(0.4F,0.4F);
		this.noClip = true;
		this.ignoreFrustumCheck = true;
	}
	
	public ColoredLightHandler(World par1World, int par2) {
		this(par1World);
	}
	
	public ColoredLightHandler(World par1World, int par2, float par3) {
		this(par1World);
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(12, 0);
		this.dataWatcher.addObject(13, 0.0F);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		setColor(nbttagcompound.getInteger("color"));
		setColorSize(nbttagcompound.getFloat("colorSize"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("color", getRenderColor());
		nbttagcompound.setFloat("colorSize", getRenderColorSize());
	}
	
	private void setColor(int i)
	{
		if(!this.worldObj.isRemote)
		{
			this.dataWatcher.updateObject(12, i);
		}
	}
	
	
	private void setColorSize(float i)
	{
		if(!this.worldObj.isRemote)
		{
			this.dataWatcher.updateObject(13, i);
		}
	}
	
	public int getRenderColor()
	{
		return this.dataWatcher.getWatchableObjectInt(12);
	}
	
	public float getRenderColorSize()
	{
		return this.dataWatcher.getWatchableObjectFloat(13);
	}
	
	@Override
    public void moveEntity(double par1, double par3, double par5){}
	
	@SuppressWarnings("deprecation")
	@Override
    public void onUpdate()
    {
		this.posY = (int)this.posY;
		Block b = MiscUtils.getBlock(worldObj, (int)posX, (int)posY-1, (int)posZ);
		Block b1 = MiscUtils.getBlock(worldObj, (int)posX, (int)posY, (int)posZ);
		if((b == null && b1 == null) || (!Core.lightBlocks.contains(b) && !Core.lightBlocks.contains(b1)))
		{
			this.setDead();
		}
        super.onUpdate();
    }

}
