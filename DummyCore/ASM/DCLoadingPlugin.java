package DummyCore.ASM;

import static DummyCore.Core.CoreInitialiser.mcVersion;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

/**
 * 
 * @author modbder
 * @Description Internal
 */
@MCVersion(value=mcVersion)
public class DCLoadingPlugin implements IFMLLoadingPlugin{

	public DCLoadingPlugin()
	{

	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"DummyCore.ASM.DCASMManager"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
