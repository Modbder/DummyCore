package DummyCore.Core;

import DummyCore.Utils.LoadingUtils;
import cpw.mods.fml.common.ICrashCallable;

public class DCCrashCallable implements ICrashCallable {

	@Override
	public String call() throws Exception {
		return  "'Special case ASM modification mods: '" +
			    "'Note, that this mods might not be involved in the crash in ANY WAY!'" +
				"'DummyCore just prints some known mods for a lot of ASM modifications'" +
				computeASMIfiers();
	}

	@Override
	public String getLabel() {
		return "[DummyCore]";
	}
	
	public String computeASMIfiers()
	{
		String retStr="";
		for(String s : LoadingUtils.knownBigASMModifiers)
			retStr += "'"+s+"'";
		return retStr;
	}

}
