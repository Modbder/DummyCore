package DummyCore.Client;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author modbder
 * @Description
 * This is the IItemRenderer as we know it from 1.7
 * Pretty much the same, but has no RenderHelper anymore(sadly)
 * However has an additional handleTransformsFor method.
 * @see
 * {@link DummyCore.Client.RenderAccessLibrary#registerItemRenderingHandler(net.minecraft.item.Item, IItemRenderer)}
 */
@SuppressWarnings("deprecation")
public interface IItemRenderer {
	
	/**
	 * Returns if this renderer should be used for the given TransformType
	 * @param item - the ItemStack being rendered
	 * @param type - the TransformType to render with
	 * @return True to render the item using this IIR, false to try other variants
	 */
	public boolean handleRenderType(ItemStack item, TransformType type);
	
	/**
	 * This is what renders your item. Do any rendering stuff here
	 * @param type - the render type. Note that a new TransformType is introduced - {@link DummyCore.Client.RenderAccessLibrary#ENTITY} 
	 * @param item - the IS to render
	 */
	public void renderItem(TransformType type, ItemStack item);
	
	/**
	 * Handles all possible transformations(rotation, position and scale) BEFORE the IS is rendered
	 * @param item - the IS being rendered
	 * @param type he render type. Note that this method is <b>NOT</b> being called for {@link DummyCore.Client.RenderAccessLibrary#ENTITY}
	 * @return the matrix with all encoded transforms or null to not apply any transforms.
	 * @see {@link net.minecraftforge.client.ForgeHooksClient#getMatrix(net.minecraft.client.renderer.block.model.ItemTransformVec3f)}
	 * {@link net.minecraft.client.renderer.block.model.ItemTransformVec3f}
	 */
	public Matrix4f handleTransformsFor(ItemStack item, TransformType type);
}
