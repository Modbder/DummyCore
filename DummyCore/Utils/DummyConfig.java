package DummyCore.Utils;

import net.minecraftforge.common.Configuration;

public class DummyConfig implements IDummyConfig{

	public int MultiItemUID;
	public int MultiBlockUID;
	public void load(Configuration config)
	{
		MultiItemUID = config.getItem("Multi Item Unique ID", 22899).getInt();
		MultiBlockUID = config.getBlock("Multi Block Unique ID", 2480).getInt();
	}
}
