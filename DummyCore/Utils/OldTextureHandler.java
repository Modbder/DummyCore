package DummyCore.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DummyCore.Client.IIRAwareModel;
import DummyCore.Client.IPosAwareModel;
import DummyCore.Client.RPAwareModel;
import DummyCore.Client.RenderAccessLibrary;
import DummyCore.Client.SBRHAwareModel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

@SuppressWarnings("deprecation")
public class OldTextureHandler {

	public static final ArrayList<Pair<String,Block>> oldBlocksToRender = new ArrayList<Pair<String,Block>>();
	public static final Map<List<Object>,SBRHAwareModel> itemblockModels = new HashMap<List<Object>,SBRHAwareModel>();
	public static final ArrayList<Item> metadataIgnorantStacks = new ArrayList<Item>();
	public static final ArrayList<Pair<String,Item>> oldItemsToRender = new ArrayList<Pair<String,Item>>();
	public static final Map<Item,RPAwareModel> itemsModels = new HashMap<Item,RPAwareModel>();
	public static final Map<Item,IIRAwareModel> iitemsModels = new HashMap<Item,IIRAwareModel>();
	
	public static boolean renderIS(ItemStack stk)
	{
		if(RenderAccessLibrary.hasHandlerFor(stk))
		{
			RenderAccessLibrary.handleISRendering(stk);
			return true;
		}
		
		return false;
	}
	
	public static void addOldItem(String owner, Item i)
	{
		oldItemsToRender.add(new Pair<String,Item>(owner,i));
	}
	
	public static void addOldBlock(String owner, Block b)
	{
		oldBlocksToRender.add(new Pair<String,Block>(owner,b));
	}
	
	public static IBakedModel getIIRModel(ItemStack stk)
	{
		if(iitemsModels.containsKey(stk.getItem()))
			return iitemsModels.get(stk.getItem());
		
		IIRAwareModel iiram = new IIRAwareModel(stk.getItem());
		iiram.rendered = stk;
		iitemsModels.put(stk.getItem(), iiram);
		return iiram;
	}
	
	public static IBakedModel getModelForIS(ItemStack is, IBakedModel model)
	{
		if(is == null)
			return model;
		
		if(model == null)
		{
			if(is.getItem() instanceof ItemBlock)
			{
				if(RenderAccessLibrary.irenderers.containsKey(is.getItem()))
					return getIIRModel(is);
				
				int meta = metadataIgnorantStacks.contains(is.getItem()) ? 0 : is.getMetadata();
				List<Object> l = Arrays.asList(is.getItem(),meta);
				if(itemblockModels.containsKey(l))
					return itemblockModels.get(l).copy();
			}else
				if(itemsModels.containsKey(is.getItem()))
					return itemsModels.get(is.getItem()).copy();
				else
					if(RenderAccessLibrary.irenderers.containsKey(is.getItem()))
						return getIIRModel(is);
		}
		return model;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reloadResourceManager(Map m)
	{
		if(DummyConfig.displaySorryNotificationUponRMReload)
		{
			Notifier.notifyWarn("DummyCore will now inject it's model wrappers into the game, thus allowing rendering of blocks without having to create .json files");
			Notifier.notifyWarn("Modbder is aware that this disallows the resource pack makers to edit the models of blocks added with this wrapper");
			Notifier.notifyWarn("However, Modbder is not going to create 2 .json files for each block added, so he chose to limit the resourcepack makers rather than wasting 90% of his time making useless jsons");
			Notifier.notifyWarn("If you are offended by this - Modbder is sorry, but he does not have much time to mess with jsons");
		}
		
		Notifier.notifyInfo("Starting to inject ISBRH IBakedModel wrappers...");
		itemblockModels.clear();
		metadataIgnorantStacks.clear();
		itemsModels.clear();
		RPAwareModel.mapped.clear();
		iitemsModels.clear();
		
		for(Pair<String,Block> p : oldBlocksToRender)
		{
			Item i = Item.getItemFromBlock(p.getSecond());
			List<IBlockState> lst = IOldCubicBlock.class.cast(p.getSecond()).listPossibleStates(p.getSecond());
			if(lst == null || lst.isEmpty())
			{
				m.put(p.getSecond().getDefaultState(), new SBRHAwareModel(p.getSecond(),p.getSecond().getDefaultState()));
				metadataIgnorantStacks.add(i);
				itemblockModels.put(Arrays.asList(i,0), new SBRHAwareModel(p.getSecond(),p.getSecond().getDefaultState()));
			}
			else
			{
				for(IBlockState ibs : lst)
				{
					m.put(ibs, new SBRHAwareModel(p.getSecond(),ibs));
					itemblockModels.put(Arrays.asList(i,p.getSecond().getMetaFromState(ibs)), new SBRHAwareModel(p.getSecond(),ibs));
				}
			}
		}
		
		Notifier.notifyInfo("Finished injecting ISBRH IBakedModel wrappers. Injected "+oldBlocksToRender.size()+" wrappers");
		
		Notifier.notifyInfo("Starting to inject item IBakedModel wrappers...");
		
		for(Pair<String,Item> p : oldItemsToRender)
			itemsModels.put(p.getSecond(), new RPAwareModel(p.getSecond()));
		
		Notifier.notifyInfo("Finished injecting item IBakedModel wrappers. Injected "+oldItemsToRender.size()+" wrappers");
		
		
	}
	
	public static IBakedModel handleIWR(IBakedModel model, IBlockAccess world, BlockPos pos)
	{
		if(model instanceof IPosAwareModel)
			return IPosAwareModel.class.cast(model).getModelFromWorldPos(world.getBlockState(pos), world, pos);
		
		return model;
	}
}
