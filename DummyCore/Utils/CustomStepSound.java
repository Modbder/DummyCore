package DummyCore.Utils;

import net.minecraft.block.Block.SoundType;

/**
 * A simple wrapper for custom step sounds for blocks
 * @author modbder
 *
 */
public class CustomStepSound extends SoundType{

	public CustomStepSound(String name, float volume, float frequency) {
		super(name, volume, frequency);
	}

}
