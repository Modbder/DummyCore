package DummyCore.Utils;

import java.util.List;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * Makes the marked block use 'old' rendering system, where the model is being dynamically generated at a runtime, rather than being loaded once.
 * <br>In fact the system is not 'old' at all - it used to simply render the block as the user would tell it to, the current one allows a dynamical model generation(see SBRHAwareModel)
 * <br>In order for this to work for your blocks you must either register them using DC's {@link DummyCore.Blocks.BlocksRegistry} or call {@link DummyCore.Utils.OldTextureHandler#addOldBlock(String, Block)} method somewhere at the preinit phase.
 */
public interface IOldCubicBlock {
	
	/**
	 * Gets the Icon for the given side and meta. This is usually only called for ItemStacks
	 * @param side - the side
	 * @param meta - the meta
	 * @return The Icon for given side and meta
	 */
	public Icon getIcon(int side, int meta);
	
	/**
	 * Gets the Icon for the given args. This is always called for blocks
	 * @param world - the world we are in
	 * @param x - x position of the block being rendered
	 * @param y - y position of the block being rendered
	 * @param z - z position of the block being rendered
	 * @param side - the side to get the icon on. This is the same as EnumFacing.side.ordinal()
	 * @return The Icon for the given args
	 */
	public Icon getIcon(IBlockAccess world, int x, int y, int z, int side);
	
	/**
	 * List all possible BlockStates for your block here. Empty lists are not allowed. If your block only has 1 state use Arrays.asList(getDefaultState());
	 * @param b the block
	 * @return ArrayList with all possible BlockStates for block b
	 */
	public List<IBlockState> listPossibleStates(Block b);
	
	/**
	 * This is being automatically called for your blocks each time the registration of textures is necessary. Init all your Icon objects here.
	 * @param ir the registry.
	 */
	public void registerBlockIcons(IconRegister ir);

	/**
	 * This controls the way your block is handled within the DC's rendering system.
	 * <br>You can use some default renderIDs like 0, which is a standart cube
	 * <br>To see all possible built-in render IDs see RenderAccessLibrary
	 * <br>Alternatively a custom render id might be used to render a block via yours/someone else's ISimpleBlockRenderingHandler
	 * @return The corresponding render id
	 */
	public int getDCRenderID();
}
