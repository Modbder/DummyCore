package DummyCore.ASM;

import static DummyCore.Core.CoreInitialiser.mcVersion;

import java.io.File;
import java.util.Map;

import DummyCore.Core.Core;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

/**
 * 
 * @author modbder
 * @Description Internal
 */
@MCVersion(value=mcVersion)
public class DCLoadingPlugin implements IFMLLoadingPlugin, IFMLCallHook{

	public DCLoadingPlugin()
	{
		if(Core.mcDir != null)
			return;
		
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

	@Override
	public Void call() throws Exception {
		return null;
	}

}
