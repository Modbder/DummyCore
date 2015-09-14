package DummyCore.ASM;

import java.io.File;
import java.util.Map;

import DummyCore.Core.Core;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import static DummyCore.Core.CoreInitialiser.mcVersion;

@MCVersion(value=mcVersion)
public class DCLoadingPlugin implements IFMLLoadingPlugin{

	public DCLoadingPlugin()
	{
		Core.mcDir = (File) FMLInjectionData.data()[6];
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{DCASMManager.class.getName()};
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
