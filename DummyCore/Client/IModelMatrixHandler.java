package DummyCore.Client;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

/**
 * This allows you to have DC generated item models with dynamical rotation.
 * @author modbder
 *
 */
@SuppressWarnings("deprecation")
public interface IModelMatrixHandler {
	
	/**
	 * Gets the current matrix for the TransformType of your item
	 * @param cameraTransformType - current TransformType
	 * @param is - the ItemStack
	 * @return the matrix with all encoded transforms or null to not apply any transforms.
	 *	@see {@link net.minecraftforge.client.ForgeHooksClient#getMatrix(net.minecraft.client.renderer.block.model.ItemTransformVec3f)}
	 * {@link net.minecraft.client.renderer.block.model.ItemTransformVec3f}
	 */
	public Matrix4f handlePerspective(TransformType cameraTransformType, ItemStack is);
}
