package DummyCore.Utils;
import DummyCore.Core.Core;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ColoredLightHandler extends Entity{
	private int renderColor;
	private float colorSize;
	
	public ColoredLightHandler(World par1World) {
		super(par1World);
		setSize(0.4F,0.4F);
		this.renderColor = 0;
		this.colorSize = 0;
		this.noClip = true;
		this.ignoreFrustumCheck = true;
	}
	
	public ColoredLightHandler(World par1World, int par2) {
		this(par1World);
		this.renderColor = par2;
	}
	
	public ColoredLightHandler(World par1World, int par2, float par3) {
		this(par1World);
		this.renderColor = par2;
		this.colorSize = par3;
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
		this.renderColor = i;
	}
	
	
	private void setColorSize(float i)
	{
		if(!this.worldObj.isRemote)
		{
			this.dataWatcher.updateObject(13, i);
		}
		this.colorSize = i;
	}
	
	public int getRenderColor()
	{
		return this.dataWatcher.getWatchableObjectInt(12);
	}
	
	public float getRenderColorSize()
	{
		return this.dataWatcher.func_111145_d(13);
	}
	
	@Override
    public void moveEntity(double par1, double par3, double par5){}
	
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
