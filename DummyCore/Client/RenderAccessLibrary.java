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

	public static final int RENDER_ID_CUBE = 0x00;
	public static final int RENDER_ID_CROSS = 0x01;
	public static final int RENDER_ID_CUBE_AND_CROSS = 0x02;
	public static final int RENDER_ID_HORIZONTAL_CROSS = 0x03;
	public static final int RENDER_ID_CROSSES = 0x04;
	public static final int RENDER_ID_FACES_WITH_OFFSET = 0x05;
	public static final int RENDER_ID_CROPS = 0x06;
	public static final int RENDER_ID_FACES_WITH_HORIZONTAL_OFFSET = 0x07;
	public static final int RENDER_ID_CONNECTED_TO_BLOCK = 0x08;
	public static final int RENDER_ID_ANVIL = 0x09;
	
	public static final Hashtable<Integer,ArrayList<ISimpleBlockRenderingHandler>> renderers = new Hashtable<Integer,ArrayList<ISimpleBlockRenderingHandler>>();
	public static final Hashtable<Item,ArrayList<IItemRenderer>> irenderers = new Hashtable<Item,ArrayList<IItemRenderer>>();
	
	public static void registerItemRenderingHandler(Item i, IItemRenderer iir)
	{
		if(irenderers.containsKey(i))
		{
			Notifier.notifyErrorCustomMod("DCRenderLibrary", "Some mod has already registered "+i+" for it's IIR"+irenderers.get(i)+"! This might cause problems in the future! Contact the respective mod author!(Suspected:"+Loader.instance().activeModContainer().getModId()+"@"+Loader.instance().activeModContainer().getName()+"#"+Loader.instance().activeModContainer().getDisplayVersion()+")");
			irenderers.get(i).add(iir);
		}
		else
		{
			ArrayList<IItemRenderer> lst = new ArrayList<IItemRenderer>();
			lst.add(iir);
			irenderers.put(i, lst);
		}
	}
	
	public static void registerRenderingHandler(ISimpleBlockRenderingHandler isbrh)
	{
		registerRenderingHandler(isbrh.getRenderID(),isbrh);
	}
	
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
	
	public static IBakedModel createDynamicalModelForIS(SBRHAwareModel offendor, ItemStack stk)
	{
		SBRHAwareModel returned = offendor.copy();
		
		DynamicModelBakery dmb = new DynamicModelBakery(returned);
		dmb.doBakeModelForIS(stk);
		
		return returned;
	}
	
	public static IBakedModel createDynamicalModelForWorldBlock(SBRHAwareModel offendor, IBlockState state, IBlockAccess world, BlockPos pos)
	{
		SBRHAwareModel returned = offendor.copy();
		
		DynamicModelBakery dmb = new DynamicModelBakery(returned,true,world,pos.getX(),pos.getY(),pos.getZ());
		dmb.doBakeModelInWorld(state, world, pos);
		
		return returned;
	}
	
	public static TransformType currentIIRTransform;
	public static TransformType lastCalledWith;
	public static TransformType ENTITY = EnumHelper.addEnum(new Class[][]{{TransformType.class}},TransformType.class, "DC.HOOK.ENTITY", new Object[0]);
	/**
	 * A very dirty hack for adding the ENTITY transformation option. Sorry Lex.
	 */
	public static long lastMillsForTransformCalls;
	
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
