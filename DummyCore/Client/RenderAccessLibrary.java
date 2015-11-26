package DummyCore.Client;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.vecmath.Matrix4f;

import DummyCore.Utils.Notifier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;

@SuppressWarnings("deprecation")
public class RenderAccessLibrary {

	/**
	 * Renders nothing as both the Block and the ItemStack. Useful for your IItemRenderers and TileEntityRenderers
	 */
	public static final int RENDER_ID_NONE = 0xffffffff;
	/**
	 * Renders a default cube, with AABB taken from the block's bounds. Useful for any normal cube?
	 */
	public static final int RENDER_ID_CUBE = 0x00;
	/**
	 * Renders crossed squares in an X pattern. Useful for tallgrass
	 */
	public static final int RENDER_ID_CROSS = 0x01;
	/**
	 * Renders both a cube and crossed squares in an X pattern. Useful for leaves
	 */
	public static final int RENDER_ID_CUBE_AND_CROSS = 0x02;
	/**
	 * Renders crossed squares in a + pattern. Useful for something?
	 */
	public static final int RENDER_ID_HORIZONTAL_CROSS = 0x03;
	/**
	 * Renders both the X cross and a + cross. useful for things like flowers.
	 */
	public static final int RENDER_ID_CROSSES = 0x04;
	/**
	 * Renders horizontal faces with a slight offset. Useful for things like old crops
	 */
	public static final int RENDER_ID_FACES_WITH_OFFSET = 0x05;
	/**
	 * Renders both faces with a slight horizontal offset and a cross in an X pattern. Useful for crops.
	 */
	public static final int RENDER_ID_CROPS = 0x06;
	/**
	 * Renders all faces, but slightly offsets the horizontal ones. Useful for things like cacti
	 */
	public static final int RENDER_ID_FACES_WITH_HORIZONTAL_OFFSET = 0x07;
	/**
	 * Renders crossed squares in an X pattern, and 1 face connected to the block. Useful for things like stems.
	 * @see {@link DummyCore.Client.IBlockConnector}
	 */
	public static final int RENDER_ID_CONNECTED_TO_BLOCK = 0x08;
	/**
	 * Renders a slightly better than vanilla anvil with a specific rotation. Useful for anvils, duh!
	 * @see {@link DummyCore.Client.IRotationProvider}
	 */
	public static final int RENDER_ID_ANVIL = 0x09;
	
	public static final Hashtable<Integer,ArrayList<ISimpleBlockRenderingHandler>> renderers = new Hashtable<Integer,ArrayList<ISimpleBlockRenderingHandler>>();
	public static final Hashtable<Item,ArrayList<IItemRenderer>> irenderers = new Hashtable<Item,ArrayList<IItemRenderer>>();
	public static final Hashtable<Item,IModelMatrixHandler> mHandlers = new Hashtable<Item,IModelMatrixHandler>();
	
	/**
	 * Registers a specified OBJECT as a ModelMatrixHandler. If one already exists for the given item - overrides it.
	 * @param i - the item to register the ModelMatrixHandler for. Can work with blocks, but that is ify.
	 * @param immh - your handler
	 */
	public static void registerItemMatrixHandler(Item i, IModelMatrixHandler immh)
	{
		if(mHandlers.containsKey(i))
			Notifier.notifyErrorCustomMod("DCRenderLibrary", "Some mod has already registered "+i+" for it's IMMH"+irenderers.get(i)+"! DC will now override the registered handler, but this might cause problems! Contact the respective mod author!(Suspected:"+Loader.instance().activeModContainer().getModId()+"@"+Loader.instance().activeModContainer().getName()+"#"+Loader.instance().activeModContainer().getDisplayVersion()+")");
		
		mHandlers.put(i, immh);
	}
	
	/**
	 * Registeres a specified OBJECT as your ItemRenderer. If one already exists for the given item - adds it as an alternative
	 * <br>Every time the item is rendered the code iterates through all possible renderers for the given item
	 * <br>It then calls {@linkplain DummyCore.Client.IItemRenderer#handleRenderType(ItemStack, TransformType)} for that object
	 * <br>If that returns true - that IItemRenderer is used, and the iteration stops. If that returns false the iteration continues.
	 * <br>If the new IItemRenderer is added to already registered item - it is inserted at the beginning of the iteration list
	 * <br>This way modmakers have full control over item rendering, and can override the IItemRenderers of other modmakers.
	 * <br>However, use {@linkplain DummyCore.Client.IItemRenderer#handleRenderType(ItemStack, TransformType)} with care! 
	 * <br>If you are overriding the renderer for that item that method should <b>not</b> return true always, you should only return true when your rendering is necessary!
	 * @param i - the Item to register the rendering for. Can be an Item.getItemFromBlock(Block yourBlock) to register the rendering for the block.
	 * @param iir - the rendering handler.
	 */
	public static void registerItemRenderingHandler(Item i, IItemRenderer iir)
	{
		if(irenderers.containsKey(i))
		{
			Notifier.notifyErrorCustomMod("DCRenderLibrary", "Some mod has already registered "+i+" for it's IIR"+irenderers.get(i)+"! This might cause problems in the future! Contact the respective mod author!(Suspected:"+Loader.instance().activeModContainer().getModId()+"@"+Loader.instance().activeModContainer().getName()+"#"+Loader.instance().activeModContainer().getDisplayVersion()+")");
			irenderers.get(i).add(0,iir);
		}
		else
		{
			ArrayList<IItemRenderer> lst = new ArrayList<IItemRenderer>();
			lst.add(iir);
			irenderers.put(i, lst);
		}
	}
	
	/**
	 * The same as {@link RenderAccessLibrary#registerRenderingHandler(int, ISimpleBlockRenderingHandler)}, but with the ID taken from the Handler
	 * @param isbrh - the render handler
	 */
	public static void registerRenderingHandler(ISimpleBlockRenderingHandler isbrh)
	{
		registerRenderingHandler(isbrh.getRenderID(),isbrh);
	}
	
	/**
	 * This is used to register an OBJECT as a SimpleBlockRenderingHandler, which allows your block to have a dynamical model based on the world conditions(like a specific TileEntity variable/ajacent blocks/whatever)
	 * <br>If one is already registered under the specific ID your handler will be added to queue of the rendering
	 * <br>Each time the block in the world/inventory is rendered the code iterates the list of ISimpleBlockRenderingHandlers under the specific ID
	 * <br>It calls all necessary render hooks for <b>each</b> handler in the list
	 * <br>The only thing to remember about this - the DynamicModelBakery object stays <b>THE SAME</b> for all handlers
	 * <br>That's why in your rendering handler you should reset all your specific settings(offset/overide icon/AABB/inversion/etc) in the end of your code ;)
	 * @param id - the ID(integer) to register the handler with
	 * @param isbrh - the handler itself
	 */
	public static void registerRenderingHandler(int id, ISimpleBlockRenderingHandler isbrh)
	{
		if(renderers.containsKey(id))
		{
			Notifier.notifyErrorCustomMod("DCRenderLibrary", "Some mod has already registered "+id+" for it's ISBRH"+renderers.get(id)+"! This might cause problems in the future! Contact the respective mod author!(Suspected:"+Loader.instance().activeModContainer().getModId()+"@"+Loader.instance().activeModContainer().getName()+"#"+Loader.instance().activeModContainer().getDisplayVersion()+")");
			renderers.get(id).add(isbrh);
		}
		else
		{
			ArrayList<ISimpleBlockRenderingHandler> lst = new ArrayList<ISimpleBlockRenderingHandler>();
			lst.add(isbrh);
			renderers.put(id, lst);
		}
	}
	
	/**
	 * Internal. Modmakers should not use this, especially in their render handlers.
	 * @param offendor - the model base
	 * @param stk - the ItemStack
	 * @return - a fully baked and prepared for rendering model
	 */
	public static IBakedModel createDynamicalModelForIS(SBRHAwareModel offendor, ItemStack stk)
	{
		SBRHAwareModel returned = offendor.copy();
		
		DynamicModelBakery dmb = new DynamicModelBakery(returned);
		dmb.doBakeModelForIS(stk);
		
		return returned;
	}
	
	/**
	 * Internal. Modmakers should not use this, especially in their render handlers.
	 * @param offendor - the model base
	 * @param state - the state of the block being rendered
	 * @param world - the world we are in
	 * @param pos - the position of the block rendered
	 * @return - a fully baked and prepared for rendering model
	 */
	public static IBakedModel createDynamicalModelForWorldBlock(SBRHAwareModel offendor, IBlockState state, IBlockAccess world, BlockPos pos)
	{
		SBRHAwareModel returned = offendor.copy();
		
		DynamicModelBakery dmb = new DynamicModelBakery(returned,true,world,pos.getX(),pos.getY(),pos.getZ());
		dmb.doBakeModelInWorld(state, world, pos);
		
		return returned;
	}
	
	//Internal!
	public static TransformType currentIIRTransform;
	public static TransformType lastCalledWith;
	public static TransformType ENTITY = EnumHelper.addEnum(new Class[][]{{TransformType.class}},TransformType.class, "DC.HOOK.ENTITY", new Object[0]);
	/**
	 * A very dirty hack for adding the ENTITY transformation option. Sorry Lex.
	 */
	public static long lastMillsForTransformCalls;
	
	/**
	 * Renders the given ItemStack
	 * @param is - the stack being rendered
	 */
	public static void handleISRendering(ItemStack is)
	{
		if(System.currentTimeMillis() - lastMillsForTransformCalls > 10L)
			if(currentIIRTransform == lastCalledWith)
				currentIIRTransform = ENTITY;
		
		if(is == null || is.getItem() == null)
			return;
		
		Item itm = is.getItem();
		if(!irenderers.containsKey(itm))
			return;
		
		ArrayList<IItemRenderer> iirs = irenderers.get(itm);
		if(iirs.isEmpty())
			return;
		
		for(IItemRenderer iir : iirs)
			if(iir.handleRenderType(is, currentIIRTransform))
				iir.renderItem(currentIIRTransform, is);
		
		lastCalledWith = currentIIRTransform;
	}
	
	/**
	 * Handles the transforms(rotate,offset,scale) for the given stack
	 * @param is - the stack being rendered
	 * @param tt - current transform type
	 * @return valid Matrix4f or null
	 */
	public static Matrix4f handleTransformationFor(ItemStack is, TransformType tt)
	{
		currentIIRTransform = tt;
		lastMillsForTransformCalls = System.currentTimeMillis();
		
		if(is == null || is.getItem() == null)
			return null;
		
		Item itm = is.getItem();
		if(!irenderers.containsKey(itm))
			return null;
		
		ArrayList<IItemRenderer> iirs = irenderers.get(itm);
		if(iirs.isEmpty())
			return null;
		
		for(IItemRenderer iir : iirs)
			if(iir.handleRenderType(is, currentIIRTransform))
				return iir.handleTransformsFor(is, currentIIRTransform);
		
		return null;
	}
	
	/**
	 * If current ItemStack can be rendered via DC
	 * @param is - the ItemStack being rendered
	 * @return true if the given stack can be rendered via DC, false if not
	 */
	public static boolean hasHandlerFor(ItemStack is)
	{
		if(is == null || is.getItem() == null)
			return false;
		
		Item itm = is.getItem();
		if(!irenderers.containsKey(itm))
			return false;
		
		ArrayList<IItemRenderer> iirs = irenderers.get(itm);
		if(iirs.isEmpty())
			return false;
		
		for(IItemRenderer iir : iirs)
			if(iir.handleRenderType(is, currentIIRTransform))
				return true;
		
		return false;
	}
}
