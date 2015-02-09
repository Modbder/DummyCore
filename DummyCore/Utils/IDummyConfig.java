package DummyCore.Utils;

import net.minecraftforge.common.config.Configuration;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.0
 * @Description Your config class must implement this to work. Used for easier way of working with configs and for DummyCore handling.
 */
public interface IDummyConfig {
	
	/**
	 * Automatically called every time your mod is registered in the DummyCore.
	 * @version From DummyCore 1.0
	 * @param config - Forge configuration file automatically created.
	 */
	public abstract void load(Configuration config);

}
