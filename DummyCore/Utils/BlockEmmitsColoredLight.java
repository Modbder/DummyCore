package DummyCore.Utils;

import DummyCore.Core.Core;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
/**
 * @version From DummyCore 1.1
 * @author Modbder
 * @Description make your blocks extend this to make the emmit colored lightning. Warning! Colored lightning is still in beta-testing, and for sure contains lots of bugs!
 */
public class BlockEmmitsColoredLight extends BlockContainer{

	public BlockEmmitsColoredLight(int par1, Material par2Material) {
		super(par1, par2Material);
		if(!Core.lightBlocks.contains(this))
			Core.lightBlocks.add(this);
	}
	
	/**
	 * Used to get the color of the light. Look in EnumLightColor to see more of the colors.
	 * @version From DummyCore 1.1
	 * @param world - the world object.
	 * @param x - x coordinate of the block
	 * @param y - y coordinate of the block
	 * @param z- z coordinate of the block
	 * @return the color of the light the block will emit.
	 */
	public EnumLightColor getColor(IBlockAccess world, int x, int y, int z)
	{
		return EnumLightColor.WHITE;
	}
	
	/**
	 * Used to get the brightness of the light. By default equivalents to the light amount, the block gives
	 * @version From DummyCore 1.1
	 * @param world - the world object.
	 * @param x - x coordinate of the block
	 * @param y - y coordinate of the block
	 * @param z- z coordinate of the block
	 * @return number from 0 to 15, corresponding to the light value
	 */
	public float getLightBrightness(IBlockAccess world, int x, int y, int z)
	{
		return this.lightValue[world.getBlockId(x, y, z)];
	}
	
	@Override
    public int onBlockPlaced(World w, int x, int y, int z, int par5, float par6, float par7, float par8, int par9)
    {
    	ColoredLightHandler light = new ColoredLightHandler(w, getColor(w,x,y,z).getLightID(), getLightBrightness(w,x,y,z));
    	light.setPosition(x+0.5F, y+0.5F, z+0.5F);
    	if(!w.isRemote)
    		w.spawnEntityInWorld(light);
        return super.onBlockPlaced(w, x, y, z, par5, par6, par7, par8, par9);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

}
