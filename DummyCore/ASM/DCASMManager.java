package DummyCore.ASM;

import java.util.Arrays;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import DummyCore.Utils.CustomTXTConfig;
import DummyCore.Utils.Notifier;

public class DCASMManager implements IClassTransformer{
	
	public static final String FMLClientHandlerName = "cpw.mods.fml.client.FMLClientHandler";
	public static final String logMissingTextureErrorsName = "logMissingTextureErrors";
	public static final String logMissingTextureErrorsDesc = "()V";
	public static final String NetHandlerPlayClientName = "NetHandlerPlayClient";
	public static final String NotchNetHandlerPlayClientName = "fv";
	public static final String ObfNetHandlerPlayClientName = "NetHandlerPlayClient";
	public static final String NetHandlerPlayClient_MethodHandleSpawnMobName = "handleSpawnMob";
	public static final String ObfNetHandlerPlayClient_MethodHandleSpawnMobName = "func_147281_a";
	public static final String NotchNetHandlerPlayClient_MethodHandleSpawnMobName = "a";
	public static final String NetHandlerPlayClient_MethodHandleSpawnMobDesc = "(Lnet/minecraft/network/play/server/S0FPacketSpawnMob;)V";
	public static final String ObfNetHandlerPlayClient_MethodHandleSpawnMobDesc = "(Lnet/minecraft/network/play/server/S0FPacketSpawnMob;)V";
	public static final String NotchNetHandlerPlayClient_MethodHandleSpawnMobDesc = "(Lfz;)V";
	

	@Override
	public byte[] transform(String name, String transformedName,byte[] basicClass) 
	{
		
		if(basicClass != null) //If the class we are loading exists
		{
			if(name.equals(FMLClientHandlerName))
				return handleForgeClientHandlerClass(name,transformedName,basicClass);
			else
			{
				if(name.endsWith(NetHandlerPlayClientName) || name.endsWith(NotchNetHandlerPlayClientName) || name.endsWith(ObfNetHandlerPlayClientName))
				{
					handleNetHandlerPlayClientClass(name,transformedName,basicClass);
				}
				else
				{
					ClassNode classNode = new ClassNode(); //Creating a most basic bytecode->runtime command helper.
					ClassReader classReader = new ClassReader(basicClass); //Parsing the bytecode to runtime commands
					classReader.accept(classNode, 0); //Giving our helper a parsed list of commands without any code modifications.
					if(classNode.invisibleAnnotations != null && classNode.invisibleAnnotations.size() > 0) //If we have some invisible annotations on our class.
					{
						boolean checkClass = false;
						for(int i = 0; i < classNode.invisibleAnnotations.size(); ++i) //Looking through all annotations presented on the class
						{
							AnnotationNode node = classNode.invisibleAnnotations.get(i);
							if(node.desc.equalsIgnoreCase("LDummyCore/Utils/DCASMCheck;")) //If the given annotation is a DummyCore annotation, that sygnals, that the class needs to be checked.
							{
								checkClass = true;
								break; //We have to send our class outside the loop to make sure there is no ConcurrentModificationException.
							}
								
						}
						if(checkClass)
							return handleClass(name,transformedName,basicClass,classNode,classReader); //If class requires inspection we are sending it into our method
					}
				}
			}
		}
		
		return basicClass;
	}
	
	public byte[] handleNetHandlerPlayClientClass(String name, String transformedName,byte[] basicClass)
	{
		int obfuscationType = name.endsWith(NetHandlerPlayClientName) ? 0 : name.endsWith(ObfNetHandlerPlayClientName) ? 1 : 2;
		String S0FPacketSpawnMob = obfuscationType == 0 || obfuscationType == 1 ? "net/minecraft/network/play/server/S0FPacketSpawnMob" : "fz";
		String EntityLivingBase = obfuscationType == 0 ? "net/minecraft/entity/EntityLivingBase" : obfuscationType == 1 ? "net/minecraft/entity/EntityLivingBase" : "sv";
		if(!CustomTXTConfig.init)
			CustomTXTConfig.createCFG();
		if(CustomTXTConfig.mappings.containsKey("fixS0FSpawnMobPacketCrash") && CustomTXTConfig.mappings.get("fixS0FSpawnMobPacketCrash").contains("true"))
		{
			byte[] defaultBytes = basicClass.clone();
			
			try
			{
				ClassNode cn = new ClassNode(); //<---- Actually creates the EMPTY class
				ClassReader cr = new ClassReader(basicClass); //<---- Bytecode -> Instructions
				cr.accept(cn, ClassReader.EXPAND_FRAMES); //<---- giving cn the instruction list. 0 is for flags, we want none.
			
				List<MethodNode> methods = cn.methods;
				for(int i = 0; i < methods.size(); ++i)
				{
					MethodNode method = methods.get(i);
					if(method.name.equals(NetHandlerPlayClient_MethodHandleSpawnMobName) || method.name.equals(ObfNetHandlerPlayClient_MethodHandleSpawnMobName) || method.name.equals(NotchNetHandlerPlayClient_MethodHandleSpawnMobName))
					{
						if(method.desc.equals(NetHandlerPlayClient_MethodHandleSpawnMobDesc) || method.desc.equals(ObfNetHandlerPlayClient_MethodHandleSpawnMobDesc) || method.desc.equals(NotchNetHandlerPlayClient_MethodHandleSpawnMobDesc))
						{
							AbstractInsnNode insertAfter = null;
							for(int j = 0; j < method.instructions.size(); ++j)
							{
								AbstractInsnNode ain = method.instructions.get(j);
								if(ain instanceof TypeInsnNode && ain.getOpcode() == Opcodes.CHECKCAST)
								{
									if(method.instructions.size() > j+1)
									{
										AbstractInsnNode ain1 = method.instructions.get(j+1);
										if(ain1 instanceof VarInsnNode && ain1.getOpcode() == Opcodes.ASTORE && ain.getType() == Type.OBJECT)
										{
											insertAfter = ain1;
											break;
										}
									}
								}
							}
							if(insertAfter != null)
							{
								InsnList inst = new InsnList();
								inst.add(new LabelNode());
								inst.add(new VarInsnNode(Opcodes.ALOAD,Type.OBJECT));
								LabelNode ifStart = new LabelNode();
								inst.add(new JumpInsnNode(Opcodes.IFNONNULL,ifStart));
								inst.add(new LabelNode());
								inst.add(new LdcInsnNode("Vanilla->DummyCore"));
								inst.add(new LdcInsnNode("Handled an attempt to spawn a null mob on client, aborting! This might break everything completely though."));
								inst.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Utils/Notifier", "notifyErrorCustomMod", "(Ljava/lang/String;Ljava/lang/String;)V", false));
								inst.add(new LabelNode());
								inst.add(new InsnNode(Opcodes.RETURN));
								inst.add(ifStart);
								inst.add(new FrameNode(Opcodes.F_FULL, 8, new Object[] {name.replace('.', '/'), S0FPacketSpawnMob, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.DOUBLE, Opcodes.FLOAT, Opcodes.FLOAT, EntityLivingBase}, 0, new Object[] {}));
								
								method.instructions.insert(insertAfter, inst);
							}
						}
					}
				}
				
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); //<---- Creating the way we are writing Instructions into bytecode. COMPUTE_MAXS will automatically calculate STACK size and LOCAL count for each method. 
				cn.accept(cw); //<---- Writing all modifications
				
				byte[] bytes = cw.toByteArray();
				defaultBytes = null;
				return bytes;
			}
			catch(Exception e)
			{
				LogManager.getLogger().error("[DummyCore][ASM]Failed to modify "+NetHandlerPlayClientName,e);
				return defaultBytes;
			}
		}
		
		return basicClass;
	}
	
	public byte[] handleForgeClientHandlerClass(String name, String transformedName,byte[] basicClass)
	{
		if(!CustomTXTConfig.init)
			CustomTXTConfig.createCFG();
		if(!CustomTXTConfig.mappings.containsKey("insertDCCallInTextureLoader") || !CustomTXTConfig.mappings.get("insertDCCallInTextureLoader").equals("true"))
			return basicClass;
		
		byte[] defaultBytes = basicClass.clone();
		try
		{
			ClassNode cn = new ClassNode(); //<---- Actually creates the EMPTY class
			ClassReader cr = new ClassReader(basicClass); //<---- Bytecode -> Instructions
			cr.accept(cn, ClassReader.EXPAND_FRAMES); //<---- giving cn the instruction list. 0 is for flags, we want none.
		
			List<MethodNode> methods = cn.methods;
			for(int i = 0; i < methods.size(); ++i)
			{
				MethodNode method = methods.get(i);
				if(method.name.equals(logMissingTextureErrorsName) && method.desc.equals(logMissingTextureErrorsDesc))
				{
					method.instructions.insert(new LabelNode());
					method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "DummyCore/Core/CoreInitialiser", "fmlLogMissingTextures", "()V", false));
				}
			}
			
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); //<---- Creating the way we are writing Instructions into bytecode. COMPUTE_MAXS will automatically calculate STACK size and LOCAL count for each method. 
			cn.accept(cw); //<---- Writing all modifications
			
			byte[] bytes = cw.toByteArray();
			defaultBytes = null;
			return bytes;
		}
		catch(Exception e)
		{
			LogManager.getLogger().error("[DummyCore][ASM]Failed to modify "+FMLClientHandlerName,e);
			return defaultBytes;
		}
	}
	
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

	boolean classExists(String s)
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
