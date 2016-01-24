package DummyCore.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import DummyCore.Utils.IOldCubicBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

/**
 * 
 * @author modbder
 * @Description
 * This is a version of block model, that can change dynamically at a runtime. 
 * <br>It is used internaly in DC to return the old ISBRH functionality from 1.7
 * <br>However it no longer operates with Tessellator - it now operates with the model generation
 * <br>This way it is more compatible with other mods/functions that make use of models
 * <br>It *should* also be compatible with things like Optifine
 * <br>This class is internal and is not intended to be used outside of DummyCore
 */
@SuppressWarnings("deprecation")
public class SBRHAwareModel implements ISmartBlockModel, ISmartItemModel, IPosAwareModel, IPerspectiveAwareModel, IFlexibleBakedModel{

	public Block rendered;
	public IOldCubicBlock interfaced;
	public Icon iconFromState;
	public IBlockState blockState;
	public Hashtable<EnumFacing,ArrayList<BakedQuad>> faces = new Hashtable<EnumFacing,ArrayList<BakedQuad>>();
	public ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
	public static final Map<Integer,Boolean> render3D = new HashMap<Integer,Boolean>();
	
	private SBRHAwareModel(){
	}
	
	public SBRHAwareModel(Block b,IBlockState state)
	{
		blockState = state;
		rendered = b;
		interfaced = (IOldCubicBlock) b;
		//iconFromState = interfaced.getIcon(0, b.getMetaFromState(state));
		for(EnumFacing face : EnumFacing.values())
			faces.put(face, new ArrayList<BakedQuad>());
	}
	
	public SBRHAwareModel copy()
	{
		SBRHAwareModel sbam = new SBRHAwareModel();
		
		sbam.blockState = this.blockState;
		iconFromState = interfaced.getIcon(0, rendered.getMetaFromState(blockState));
		sbam.iconFromState = this.iconFromState;
		sbam.interfaced = this.interfaced;
		sbam.rendered = this.rendered;
		for(EnumFacing face : EnumFacing.values())
			sbam.faces.put(face, new ArrayList<BakedQuad>());
		
		return sbam;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		if(!render3D.containsKey(interfaced.getDCRenderID()))
			fillRender3DData(interfaced.getDCRenderID());
		return render3D.get(interfaced.getDCRenderID());
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		
		try{if(iconFromState == null) iconFromState = this.interfaced.getIcon(0, rendered.getMetaFromState(blockState));return iconFromState.actualTexture;}catch(Exception e){return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel().getParticleTexture();}
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		return RenderAccessLibrary.createDynamicalModelForIS(this, new ItemStack(rendered,1,rendered.getMetaFromState(state)));
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing side) {
		return faces.get(side);
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return quads;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		return RenderAccessLibrary.createDynamicalModelForIS(this, stack);
	}

	@Override
	public IBakedModel getModelFromWorldPos(IBlockState state, IBlockAccess world, BlockPos pos) {
		return RenderAccessLibrary.createDynamicalModelForWorldBlock(this, state, world, pos);
	}

	@Override
	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if(cameraTransformType == TransformType.THIRD_PERSON)
			return Pair.of(this, OLDBLOCKTHIRDPERSON);
		
		return Pair.of(this, null);
	}

	public static final Matrix4f OLDBLOCKTHIRDPERSON = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(3.3F,1,-0.3F), new Vector3f(0F,0.1F,-0.15F), new Vector3f(0.35F, 0.35F, 0.35F)));

	@Override
	public VertexFormat getFormat() {
		return DefaultVertexFormats.BLOCK;
	}
	
	public static void fillRender3DData(int renderID)
	{
		if(!RenderAccessLibrary.renderers.containsKey(renderID))
			render3D.put(renderID, false);
		else
		{
			ArrayList<ISimpleBlockRenderingHandler> handlers = RenderAccessLibrary.renderers.get(renderID);
			if(handlers == null || handlers.isEmpty())
				render3D.put(renderID, false);
			else
			{
				boolean prioritizeTrue = false;
				for(ISimpleBlockRenderingHandler isbrh : handlers)
					if(isbrh.render3DInInventory())
						prioritizeTrue = true;
				
				render3D.put(renderID, prioritizeTrue);
			}
		}
	}
}
