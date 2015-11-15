package DummyCore.Client;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;

@SuppressWarnings("deprecation")
public class IIRAwareModel implements IPerspectiveAwareModel,ISmartItemModel{

	public final Item registeredFor;
	public ItemStack rendered;
	
	public IIRAwareModel(Item itm)
	{
		registeredFor = itm;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return rendered != null ? Icon.fromItem(rendered).actualTexture : Icon.fromItem(registeredFor).actualTexture;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		rendered = stack;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing side) {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BakedQuad> getGeneralQuads() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Pair<IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		return Pair.of(IBakedModel.class.cast(this), RenderAccessLibrary.handleTransformationFor(rendered, cameraTransformType));
	}

}
