package DummyCore.ASM;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import DummyCore.Utils.ASMManager;
import DummyCore.Utils.LoadingUtils;
import DummyCore.Utils.Notifier;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * 
 * @author modbder
 * @Description
 * Internal. Enables features like IOldCubicBlock and IOldItem
 */
public class DCASMManager implements IClassTransformer{
	
	public DCASMManager()
	{
		try{Class.forName("DummyCore.Utils.ASMManager");}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public byte[] transform(String name, String transformedName,byte[] basicClass) 
	{
		name = transformedName;
		if(ASMManager.strictCompareByEnvironment(name, "net.minecraft.client.renderer.BlockModelShapes", "net.minecraft.client.renderer.BlockModelShapes"))
			return handleBlockModelShapes(name,basicClass);
		if(ASMManager.strictCompareByEnvironment(name, "net.minecraft.client.renderer.BlockRendererDispatcher", "net.minecraft.client.renderer.BlockRendererDispatcher"))
			return handleBlockRendererDispatcher(name,basicClass);
		if(ASMManager.strictCompareByEnvironment(name, "net.minecraft.client.renderer.ItemModelMesher", "net.minecraft.client.renderer.ItemModelMesher"))
			return handleItemModelMesher(name,basicClass);
		if(ASMManager.strictCompareByEnvironment(name, "net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer", "net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer"))
			return handleTileEntityItemStackRenderer(name,basicClass);	
		
		if(basicClass != null) //If the class we are loading exists
		{			
			ClassNode classNode = new ClassNode(); //Creating a most basic bytecode->runtime command helper.
			ClassReader classReader = new ClassReader(basicClass); //Parsing the bytecode to runtime commands
			classReader.accept(classNode, 0); //Giving our helper a parsed list of commands without any code modifications.
			if(ASMManager.checkAnnotationForClass(classNode, "LDummyCore/Utils/DCASMCheck;"))
				return handleClass(name,transformedName,basicClass,classNode,classReader); //If class requires inspection we are sending it into our method
		
		}		
		return basicClass;
	}
	
	/**
	 * Allows the IItemRenderer
	 */
	public byte[] handleTileEntityItemStackRenderer(String name,byte[] basicClass)
	{
		Notifier.notifyCustomMod("DCASM", "Transforming "+name);
		Notifier.notifyCustomMod("DCASM", "Initial byte[] count: "+basicClass.length);
		byte[] basic = basicClass.clone();
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

			MethodNode mn = ASMManager.getMethod(classNode, "renderByItem", "func_179022_a!&!a", "(Lnet/minecraft/item/ItemStack;)V", "(Lnet/minecraft/item/ItemStack;)V!&!(Lamj;)V");
			
			LabelNode iflabel = new LabelNode();
			InsnList lst = new InsnList();
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/OldTextureHandler", "renderIS", "(Lnet/minecraft/item/ItemStack;)Z", false));
			lst.add(new JumpInsnNode(Opcodes.IFEQ, iflabel));
			lst.add(new LabelNode());
			lst.add(new InsnNode(Opcodes.RETURN));
			lst.add(iflabel);
			lst.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
			
			mn.instructions.insert(mn.instructions.get(1), lst);
			
			classNode.accept(cw);
			byte[] bArray = cw.toByteArray();
			
			Notifier.notifyCustomMod("DCASM", "Finished Transforming "+name);
			Notifier.notifyCustomMod("DCASM", "Final byte[] count: "+bArray.length);
			
			return bArray;
		}
		catch(Exception e)
		{
			LoadingUtils.makeACrash("[DCASM]Fatal errors occured patching "+name+"! This modification is marked as REQUIRED, thus the loading cannot continue.", e, true);
			return basic;
		}
	}
	
	/**
	 * Allows the IOldItem
	 */
	public byte[] handleItemModelMesher(String name,byte[] basicClass)
	{
		Notifier.notifyCustomMod("DCASM", "Transforming "+name);
		Notifier.notifyCustomMod("DCASM", "Initial byte[] count: "+basicClass.length);
		byte[] basic = basicClass.clone();
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			MethodNode mn = ASMManager.getMethod(classNode, "getItemModel", "func_178089_a!&!a", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/resources/model/IBakedModel;!&!(Lamj;)Lcxe;");
			
			AbstractInsnNode insertAfter = null;
			for(int i = 0; i < mn.instructions.size(); ++i)
			{
				AbstractInsnNode an = mn.instructions.get(i);
				if(an.getOpcode()==Opcodes.ASTORE&&an instanceof VarInsnNode && VarInsnNode.class.cast(an).var==3)
				{
					insertAfter = an;
					break;
				}
			}
			
			InsnList lst = new InsnList();
			lst.add(new LabelNode());
			lst.add(new VarInsnNode(Opcodes.ALOAD, 1));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 3));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/OldTextureHandler", "getModelForIS", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)Lnet/minecraft/client/resources/model/IBakedModel;", false));
			lst.add(new VarInsnNode(Opcodes.ASTORE, 3));
			
			mn.instructions.insert(insertAfter, lst);
			
			classNode.accept(cw);
			byte[] bArray = cw.toByteArray();
			
			Notifier.notifyCustomMod("DCASM", "Finished Transforming "+name);
			Notifier.notifyCustomMod("DCASM", "Final byte[] count: "+bArray.length);
			
			return bArray;
		}
		catch(Exception e)
		{
			LoadingUtils.makeACrash("[DCASM]Fatal errors occured patching "+name+"! This modification is marked as REQUIRED, thus the loading cannot continue.", e, true);
			return basic;
		}
	}
	
	/**
	 * Allows the IOldCubicBlock
	 */
	public byte[] handleBlockRendererDispatcher(String name,byte[] basicClass)
	{
		Notifier.notifyCustomMod("DCASM", "Transforming "+name);
		Notifier.notifyCustomMod("DCASM", "Initial byte[] count: "+basicClass.length);
		byte[] basic = basicClass.clone();
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			MethodNode mn = ASMManager.getMethod(classNode, "getModelFromBlockState", "func_175022_a!&!a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/client/resources/model/IBakedModel;", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/client/resources/model/IBakedModel;!&!(Lbec;Lard;Ldt;)Lcxe;");
			
			InsnList lst = new InsnList();
			
			lst.add(new VarInsnNode(Opcodes.ALOAD, 2));
			lst.add(new VarInsnNode(Opcodes.ALOAD, 3));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/OldTextureHandler", "handleIWR", "(Lnet/minecraft/client/resources/model/IBakedModel;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/client/resources/model/IBakedModel;", false));
			lst.add(new VarInsnNode(Opcodes.ASTORE, 5));
			lst.add(new LabelNode());
			lst.add(new VarInsnNode(Opcodes.ALOAD, 5));
			
			mn.instructions.insert(mn.instructions.get(mn.instructions.size()-3), lst);
			classNode.accept(cw);
			byte[] bArray = cw.toByteArray();
			Notifier.notifyCustomMod("DCASM", "Finished Transforming "+name);
			Notifier.notifyCustomMod("DCASM", "Final byte[] count: "+bArray.length);
			
			return bArray;
		}
		catch(Exception e)
		{
			LoadingUtils.makeACrash("[DCASM]Fatal errors occured patching "+name+"! This modification is marked as REQUIRED, thus the loading cannot continue.", e, true);
			return basic;
		}
	}
	
	/**
	 * Allows the IOldCubicBlock
	 */
	public byte[] handleBlockModelShapes(String name,byte[] basicClass)
	{
		Notifier.notifyCustomMod("DCASM", "Transforming "+name);
		Notifier.notifyCustomMod("DCASM", "Initial byte[] count: "+basicClass.length);
		byte[] basic = basicClass.clone();
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			
			MethodNode mn = ASMManager.getMethod(classNode, "reloadModels", "func_178124_c!&!c", "()V", "()V!&!()V");
			
			InsnList lst = new InsnList();
			lst.add(new LabelNode());
			lst.add(new VarInsnNode(Opcodes.ALOAD,0));
			lst.add(new FieldInsnNode(Opcodes.GETFIELD,name.replace('.', '/'),ASMManager.chooseByEnvironment("bakedModelStore", "field_178129_a"),"Ljava/util/Map;"));
			lst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/OldTextureHandler", "reloadResourceManager", "(Ljava/util/Map;)V", false));
			
			mn.instructions.insert(mn.instructions.get(mn.instructions.size()-3), lst);
			classNode.accept(cw);
			byte[] bArray = cw.toByteArray();
			
			Notifier.notifyCustomMod("DCASM", "Finished Transforming "+name);
			Notifier.notifyCustomMod("DCASM", "Final byte[] count: "+bArray.length);
			
			return bArray;
			
		}
		catch(Exception e)
		{
			LoadingUtils.makeACrash("[DCASM]Fatal errors occured patching "+name+"! This modification is marked as REQUIRED, thus the loading cannot continue.", e, true);
			return basic;
		}
	}
	/**
	 * My dumb version of {@link net.minecraftforge.fml.common.Optional}
	 */
	public byte[] handleClass(String name, String transformedName,byte[] basicClass,ClassNode cn, ClassReader cr)
	{
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has requested a DummyCore ASM check via DummyCore/Utils/DCASMCheck annotation. Examining...");
		String[] checkedClss = new String[0]; //Initializing the interfaces variable
		for(int i = 0; i < cn.invisibleAnnotations.size(); ++i) //Checking through all annotations.
		{
			AnnotationNode node = cn.invisibleAnnotations.get(i);
			if(node.desc.equalsIgnoreCase("LDummyCore/Utils/ExistanceCheck;") && node.values != null && node.values.size() > 0) //Checking if the annotation found is the one, that makes us go through 
			{
				Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has requested a DummyCore ASM check on it's implementations via DummyCore/Utils/ExistanceCheck annotation. Examining...");
				List<?> classes = List.class.cast(node.values.get(1)); //Getting a full list of classes that we need to check for existance
				checkedClss = new String[classes.size()];
				checkedClss = String[].class.cast(classes.toArray(checkedClss));
				break;
			}
		}
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has given the next interfaces to check: "+Arrays.asList(checkedClss));
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); //Creating the ability to modify bytecode using modified instructions
		if(checkedClss.length > 0) //If we have interfaces to check
		{
			for(int i = 0; i < checkedClss.length; ++i)
			{
				if(!classExists(checkedClss[i])) //If the class was NOT found
				{
					J:for(int j = 0; j < cn.interfaces.size(); ++j) //Looping through all interfaces presented in the class
					{
						if(cn.interfaces.get(j).equalsIgnoreCase(checkedClss[i].replace('.', '/'))) //If thi is the one we are looking for
						{
							Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has a "+cn.interfaces.get(j)+" implementation, but the referenced class was not found. Removing the given interface.");
							cn.interfaces.remove(j); //Removing it.
							break J;
						}
					}
				}else
				{
					Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has a "+checkedClss[i]+" implementation, and the referenced class was found. Skipping to the next interface...");
				}
			}
		}
		cn.accept(cw); //Writing changed Instructions into bytecode helper
		Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has been checked.");
		return cw.toByteArray(); //Returning modified bytecode.
	}

	public boolean classExists(String s)
	{
		try
		{
			Class<?> c = Class.forName(s);
			return c != null;
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
	}
}
