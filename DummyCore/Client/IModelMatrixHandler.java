package DummyCore.Client;

import javax.vecmath.Matrix4f;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

@SuppressWarnings("deprecation")
public interface IModelMatrixHandler {
	
	public Matrix4f handlePerspective(TransformType cameraTransformType, ItemStack is);
}
