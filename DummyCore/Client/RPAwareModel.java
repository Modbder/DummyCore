package DummyCore.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import DummyCore.Utils.IOldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;

/**
 * Internal. A simple model for the item. Undocumented.
 * @author modbder
 *
 */
@SuppressWarnings("deprecation")
public class RPAwareModel implements ISmartItemModel, IPerspectiveAwareModel{
	public Hashtable<EnumFacing,ArrayList<BakedQuad>> faces = new Hashtable<EnumFacing,ArrayList<BakedQuad>>();
	public ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
	public Item rendered;
	public IOldItem interfaced;
	public ItemStack renderedFor;
	public static final Hashtable<Integer,IBakedModel> mapped = new Hashtable<Integer,IBakedModel>();
	
	public RPAwareModel(){
		
	}
	
	public RPAwareModel(Item i)
	{
		rendered = i;
		interfaced = (IOldItem) i;
		for(EnumFacing face : EnumFacing.values())
			faces.put(face, new ArrayList<BakedQuad>());
	}
	
	public RPAwareModel copy()
	{
		RPAwareModel rpam = new RPAwareModel();
		
		rpam.rendered = this.rendered;
		rpam.interfaced = this.interfaced;
		for(EnumFacing face : EnumFacing.values())
			rpam.faces.put(face, new ArrayList<BakedQuad>());
		
		return rpam;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getFaceQuads(EnumFacing face) {
		return faces.get(face);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getGeneralQuads() {
		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return renderedFor != null ? interfaced.getIconFromItemStack(renderedFor).actualTexture : interfaced.getIconFromDamage(0).actualTexture;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if(RenderAccessLibrary.mHandlers.containsKey(rendered))
			return Pair.of(this, RenderAccessLibrary.mHandlers.get(rendered).handlePerspective(cameraTransformType, renderedFor));
		if(cameraTransformType == TransformType.FIRST_PERSON)
			return Pair.of(this, FIRST_PERSON_FIX);
		
		if(cameraTransformType == TransformType.THIRD_PERSON)
		{
			if(interfaced.render3D(renderedFor))
				return Pair.of(this, THIRD_PERSON_3D);
			
			return Pair.of(this, THIRD_PERSON_2D);
		}
		return Pair.of(this, null);
	}
	
	public static final Matrix4f THIRD_PERSON_3D = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(0,1.5F,-0.7F),new Vector3f(0,0.03F,-0.2F),new Vector3f(1,1,1)));
	public static final Matrix4f THIRD_PERSON_2D = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(4.8F,0,0F),new Vector3f(0,.07F,-0.2F),new Vector3f(0.55F,0.55F,0.55F)));
	public static final Matrix4f FIRST_PERSON_FIX = ForgeHooksClient.getMatrix(new ItemTransformVec3f(new Vector3f(0,4,0.5F),new Vector3f(-0.1F,0.3F,0.1F),new Vector3f(1.3F,1.3F,1.3F)));

	@SuppressWarnings("rawtypes")
	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		List l = Arrays.asList(stack.getItem(),stack.getMetadata());
		renderedFor = stack;
		if(interfaced.recreateIcon(stack))
		{
			RPAwareModel rpAModel = this.copy();
			int rPasses = interfaced.getRenderPasses(stack);
			if(rPasses <= 0)
				rPasses = 1;
			
			for(int i = 0; i < rPasses;++i)
				append(rpAModel,simplyFillDefaultISQuads(i));
			
			mapped.put(l.hashCode(), rpAModel);
			
			return rpAModel;
		}
		
		if(mapped.containsKey(l.hashCode()))
			return mapped.get(l.hashCode());
		
		RPAwareModel rpAModel = this.copy();
		
		int rPasses = interfaced.getRenderPasses(stack);
		if(rPasses <= 0)
			rPasses = 1;
		
		for(int i = 0; i < rPasses;++i)
			append(rpAModel,simplyFillDefaultISQuads(i));
		
		mapped.put(l.hashCode(), rpAModel);
		
		return rpAModel;
	}
	
	public void append(IBakedModel ibm0, IBakedModel ibm1)
	{
		if(ibm0 == null || ibm1 == null)
			return;
		
		ibm0.getGeneralQuads().addAll(ibm1.getGeneralQuads());
		for(EnumFacing ef : EnumFacing.VALUES)
			ibm0.getFaceQuads(ef).addAll(ibm1.getFaceQuads(ef));
	}
	
	@SuppressWarnings({ "rawtypes" })
	public IBakedModel simplyFillDefaultISQuads(int pass)
	{
		try
		{
			Icon icon = this.interfaced.getIconFromItemStackAndRenderPass(renderedFor, pass);
			MODEL_GENERATED.textures.put("layer0", icon.getIconName());
			//MODEL_GENERATED.textures.put("layer1", icon.getIconName());
			//MODEL_GENERATED.textures.put("layer2", icon.getIconName());
			//MODEL_GENERATED.textures.put("layer3", icon.getIconName());
			//MODEL_GENERATED.textures.put("layer4", icon.getIconName());
			
			ModelBlock mb = GENERATOR.makeItemModel(Minecraft.getMinecraft().getTextureMapBlocks(), MODEL_GENERATED);
			
			SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(mb)).setTexture(icon.actualTexture);
			ModelRotation modelRotationIn = ModelRotation.X0_Y0;
			Iterator iterator = mb.getElements().iterator();
	        boolean uvLocked = false;
	        while (iterator.hasNext())
	        {
	            BlockPart blockpart = (BlockPart)iterator.next();
	            Iterator iterator1 = blockpart.mapFaces.keySet().iterator();
	
	            while (iterator1.hasNext())
	            {
	                EnumFacing enumfacing = (EnumFacing)iterator1.next();
	                BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(enumfacing);
	                TextureAtlasSprite textureatlassprite1 = interfaced.getIconFromItemStackAndRenderPass(renderedFor, pass).actualTexture;
	                
	                if (blockpartface.cullFace == null || !net.minecraftforge.client.model.TRSRTransformation.isInteger(modelRotationIn.getMatrix()))
	                    builder.addGeneralQuad(BAKERY.makeBakedQuad(blockpart.positionFrom,blockpart.positionTo, blockpartface, textureatlassprite1, enumfacing, modelRotationIn,blockpart.partRotation, uvLocked,blockpart.shade));
	                else
	                    builder.addFaceQuad(modelRotationIn.rotate(blockpartface.cullFace), BAKERY.makeBakedQuad(blockpart.positionFrom,blockpart.positionTo, blockpartface, textureatlassprite1, enumfacing, modelRotationIn,blockpart.partRotation, uvLocked,blockpart.shade));
	            }
	        }
	        
	        return builder.makeBakedModel();
		}catch(Exception ex)
		{
			return null;
		}
	}
	
	public static final FaceBakery BAKERY = new FaceBakery();
	public static final ItemModelGenerator GENERATOR = new ItemModelGenerator();
	public static final ModelBlock MODEL_GENERATED = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");

	@Override
	public VertexFormat getFormat() {
		return DefaultVertexFormats.ITEM;
	}
}
