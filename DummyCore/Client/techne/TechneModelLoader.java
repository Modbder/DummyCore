package DummyCore.Client.techne;

import DummyCore.Client.IModelCustom;
import DummyCore.Client.IModelCustomLoader;
import DummyCore.Client.ModelFormatException;
import net.minecraft.util.ResourceLocation;

public class TechneModelLoader implements IModelCustomLoader {
    
    @Override
    public String getType()
    {
        return "Techne model";
    }

    private static final String[] types = { "tcn" };
    @Override
    public String[] getSuffixes()
    {
        return types;
    }

    @Override
    public IModelCustom loadInstance(ResourceLocation resource) throws ModelFormatException
    {
        return new TechneModel(resource);
    }

}
