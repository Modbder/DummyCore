package DummyCore.Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.HexDump;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.HexEncoder;
import org.bouncycastle.util.encoders.HexTranslator;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.resources.ResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Blocks.ItemMultiBlock;
import DummyCore.Blocks.MultiBlock;
import DummyCore.Client.Renderer.RendererColoredLight;
import DummyCore.Items.ItemRegistry;
import DummyCore.Items.MultiItem;
import DummyCore.Utils.ColoredLightHandler;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.EnumLightColor;
import DummyCore.Utils.MathUtils;
import DummyCore.Utils.Notifier;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Do not change anythig here! This is used to initialise MultiBlocks and MultiItems.
 * @author Home
 * @version From DummyCore 1.0
 */
@Mod(modid = "DummyCore", name = "DummyCore", version = "1.1dev", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = "DummyCore")
public class CoreInitialiser extends DummyModContainer{
	public static final CoreInitialiser instance = new CoreInitialiser();
	private static DummyConfig cfg = new DummyConfig();
	@Metadata
	public static ModMetadata meta = new ModMetadata()
	{
        String modId  = "DummyCore";
        String name  = "DummyCore";
        String version = "1.0dev";
        String credits = "Modbder";
        List<String> authorList  = Arrays.asList(new String[] {
            "Dummy Thinking Team; Modbder; TheDen2099"
        });
        String description  ="Dummy Core is a required package to launch mods made by Dummy Thinking team.";
	};
	
	public CoreInitialiser()
	{
		super(meta);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{
		for(int i = 0; i < 16; ++i)
		{
			Core.lightColors.add(EnumLightColor.values()[i]);
		}
		Core.registerModAbsolute(getClass(), "DummyCore", e.getModConfigurationDirectory().getAbsolutePath(),cfg);
		mItem = (MultiItem)new MultiItem(cfg.MultiItemUID).setUnlocalizedName("DummyCore.MultiItem");
		mItem.setCreativeTab(Core.getItemTabForMod(getClass()));
		ItemRegistry.registerMultiItem("dummyDebugItem", "Debug Item From Dummy Core", "cake", null, getClass());
		ItemRegistry.itemsList.put(mItem, Core.getItemTabForMod(getClass()).getTabLabel());
		mBlock = (MultiBlock)new MultiBlock(cfg.MultiBlockUID).setHardness(1.0F).setResistance(1.0F).setUnlocalizedName("DummyCore.MultiBlock");
		mBlock.setCreativeTab(Core.getBlockTabForMod(getClass()));
		BlocksRegistry.registerMultiBlock("dummyDebugBlock", "Debug Block From Dummy Core", "comparator_on", null, getClass());
		GameRegistry.registerBlock(mBlock, ItemMultiBlock.class, Core.getModName(Core.getIdForMod(getClass()))+".block."+mBlock.getUnlocalizedName());
		BlocksRegistry.blocksList.put(mBlock, Core.getBlockTabForMod(getClass()).getTabLabel());
		EntityRegistry.registerModEntity(ColoredLightHandler.class, "DummyCore.ColoredLightHandler", 54, this, 64, 1, false);
		Side s = FMLCommonHandler.instance().getEffectiveSide();
		if(s == Side.CLIENT)
		{
			RenderingRegistry.registerEntityRenderingHandler(ColoredLightHandler.class, new RendererColoredLight());
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		Notifier.notifySimple(MultiItem.items+" MultiItems registered.");
		Notifier.notifySimple(MultiBlock.blocks+" MultiBlocks registered.");
	}
	public static MultiItem mItem;
	public static MultiBlock mBlock;
	
}
