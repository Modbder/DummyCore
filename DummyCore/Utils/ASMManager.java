package DummyCore.Utils;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

public class ASMManager {

	/**
	 * Use this to determine the notch obf names in your mappings. This is only useful ever for ASM, since the code after ASM modifications is changed to MCP mappings
	 * <br>Example
	 * <br>To transform a net/minecraft/client/renderer/tileentity/TileEntityItemStackRenderer/renderByItem you would use a getMethod helper
	 * <br>To take notch names into account you would make it like this:
	 * <br>ASMManager.getMethod(classNode, "renderByItem", "func_179022_a!&!a", "(Lnet/minecraft/item/ItemStack;)V", "(Lnet/minecraft/item/ItemStack;)V!&!(Lamj;)V");
	 * <br>Where
	 * <br>classNode is your class
	 * <br>renderByItem is a DEOBFUSCATED(FML) name of the method
	 * <br>func_179022_a!&!a is our combination of both OBFUSCATED(MCP) and OBFUSCATED(Notch) names
	 * <br>func_179022_a!&!a translates to [func_179022_a , a], where <b>func_179022_a</b> is an MCP name and <b>a</b> is notch's name
	 * <br>(Lnet/minecraft/item/ItemStack;)V is a DEOBFUSCATED(FML) description of the method
	 * <br>(Lnet/minecraft/item/ItemStack;)V!&!(Lamj;)V is our combination of both OBFUSCATED(MCP) and OBFUSCATED(Notch) descriptions
	 * <br>(Lnet/minecraft/item/ItemStack;)V!&!(Lamj;)V translates to [(Lnet/minecraft/item/ItemStack;)V, (Lamj;)V], where <b>(Lnet/minecraft/item/ItemStack;)V</b> is an MCP desc, and <b>(Lamj;)V</b> is notch's desc 
	 */
	public static String REGEX_NOTCH_FROM_MCP = "!&!";
	
	public static boolean obf(){
		return FMLForgePlugin.RUNTIME_DEOBF;
	}
	
	/**
	 * Chooses a string based on the environment(dev or obfuscated)
	 * @param deobf - the deobfuscated var
	 * @param obf - the obfuscated var
	 * @return Is based on the environment you are in
	 */
	public static String chooseByEnvironment(String deobf, String obf)
	{
		return obf() ? obf : deobf;
	}
	
	/**
	 * Compares 2 strings - chooses 1 from the environment, and then checks if the chosen one is the same as the first. Useful for ASM class name comparing
	 * @param name - the string you are comparing with
	 * @param deobf - the deobfuscated string
	 * @param obf - the obfuscated string
	 * @return true if param 1 and the chosen string were equal, false if not
	 */
	public static boolean strictCompareByEnvironment(String name, String deobf, String obf)
	{
		String comparedTo = chooseByEnvironment(deobf.replace('/', '.'),obf.replace('/', '.'));
		return comparedTo.equalsIgnoreCase(name.replace('/', '.'));
	}
	
	/**
	 * Compares 2 strings - chooses 1 from the environment, and then checks if the first par contains the chosen string. Useful for ASM class name comparing
	 * @param name - the string you are comparing with
	 * @param deobf - the deobfuscated string
	 * @param obf - the obfuscated string 
	 * @return true if param 1 contains the chosen string, false if not
	 */
	public static boolean compareByEnvironment(String name, String deobf, String obf)
	{
		String comparedTo = chooseByEnvironment(deobf.replace('/', '.'),obf.replace('/', '.'));
		return comparedTo.toLowerCase().contains(name.replace('/', '.').toLowerCase());
	}
	
	/**
	 * Checks if the given ClassNode contains a field with a given name
	 * @param cn - the ClassNode
	 * @param name - the field name
	 * @return true if such a field exists, false if not
	 */
	public static boolean checkClassForFieldByName(ClassNode cn, String name)
	{
		return checkClassForFieldByName(cn,name,name);
	}
	
	/**
	 * Returns the FieldNode with a given name from the given ClassNode if exists, null otherwise
	 * @param cn - the ClassNode
	 * @param name - the field name
	 * @return a FieldNode object, null if not found
	 */
	public static FieldNode getFieldByName(ClassNode cn, String name)
	{
		return getFieldByName(cn,name,name);
	}
	
	/**
	 * Checks if the given ClassNode contains a field with a name, chosen by environment
	 * @param cn - the ClassNode
	 * @param deobfName - the field name in DEV(deobfuscated) environment
	 * @param obfName - the field name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return true if such a field exists, false if not
	 */
	public static boolean checkClassForFieldByName(ClassNode cn, String deobfName, String obfName)
	{
		return checkClassForField(cn,deobfName,obfName,null,null);
	}
	
	/**
	 * Returns the FieldNode with a name chosen by environment from the given ClassNode if exists, null otherwise
	 * @param cn - the ClassNode
	 * @param deobfName - the field name in DEV(deobfuscated) environment
	 * @param obfName - the field name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return a FieldNode object, null if not found
	 */
	public static FieldNode getFieldByName(ClassNode cn, String deobfName, String obfName)
	{
		return getField(cn,deobfName,obfName,null,null);
	}
	
	/**
	 * Returns the FieldNode with a given name and descriptor from the given ClassNode if exists, null otherwise
	 * @param cn - the ClassNode
	 * @param name - the field's name
	 * @param desc - the field's descriptor
	 * @return true if such a field exists, false if not
	 */
	public static boolean checkClassForField(ClassNode cn, String name, String desc)
	{
		return checkClassForField(cn,name,name,desc,desc);
	}
	
	/**
	 * Checks if the FieldNode with a given name AND descriptor exists in the given ClassNode
	 * @param cn - the ClassNode
	 * @param name - the field's name
	 * @param desc - the field's descriptor
	 * @return a FieldNode object, null if not found
	 */
	public static FieldNode getField(ClassNode cn, String name, String desc)
	{
		return getField(cn,name,name,desc,desc);
	}
	
	/**
	 * Checks the given ClassNode for a FieldNode with a name and descriptor chosen by the environment. 
	 * <br>Name OR Descriptor can be null - then the search code will skip those params completely. However BOTH can't be null at the same time.
	 * @param cn - the ClassNode
	 * @param deobfName - the field name in DEV(deobfuscated) environment
	 * @param obfName - the field name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @param deobfDesc - the field descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the field descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return true if such a field exists, false if not
	 */
	public static boolean checkClassForField(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		return getField(cn,deobfName,obfName,deobfDesc,obfDesc) != null;
	}
	
	/**
	 * Checks the given ClassNode for a FieldNode with a name and descriptor chosen by the environment and returns one if found
	 * <br>Name OR Descriptor can be null - then the search code will skip those params completely. However BOTH can't be null at the same time.
	 * @param cn - the ClassNode
	 * @param deobfName - the field name in DEV(deobfuscated) environment
	 * @param obfName - the field name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @param deobfDesc - the field descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the field descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return a FieldNode object, null if not found
	 */
	public static FieldNode getField(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		String fieldName = null;
		String fieldDesc = null;
		
		if(!PrimitiveUtils.checkSameAndNullStrings(deobfName,obfName))
			fieldName = chooseByEnvironment(deobfName,obfName);
		if(!PrimitiveUtils.checkSameAndNullStrings(deobfDesc,obfDesc))
			fieldDesc = chooseByEnvironment(deobfDesc,obfDesc);
		
		String additionalFN = "";
		
		if(fieldName != null && fieldName.indexOf(REGEX_NOTCH_FROM_MCP) != -1)
		{
			String[] sstr = fieldName.split(REGEX_NOTCH_FROM_MCP);
			fieldName = sstr[0];
			additionalFN = sstr[1];
		}
		
		String additionalFD = "";
		
		if(fieldDesc != null && fieldDesc.indexOf(REGEX_NOTCH_FROM_MCP) != -1)
		{
			String[] sstr = fieldDesc.split(REGEX_NOTCH_FROM_MCP);
			fieldDesc = sstr[0];
			additionalFD = sstr[1];
		}
		
		if(PrimitiveUtils.checkSameAndNullStrings(fieldName, fieldDesc))
			return null;
		
		for(FieldNode fn : cn.fields)
			if((fieldName == null || fn.name.equals(fieldName) || fn.name.equals(additionalFN)) && (fieldDesc == null || fn.desc.equals(fieldDesc) || fn.desc.equals(additionalFD)))
				return fn;
		
		return null;
	}
	
	/**
	 * Checks if a given ClassNode has a method with a given descriptor
	 * @param cn - the ClassNode
	 * @param desc - the method's descriptor
	 * @return True if the classNode contains a method with a given descriptor, false otherwise
	 */
	public static boolean checkClassForMethodDesc(ClassNode cn, String desc)
	{
		return checkClassForMethodDesc(cn,desc,desc);
	}
	
	/**
	 * Gets a MethodNode from a given ClassNode by a given descriptor
	 * @param cn - the ClassNode
	 * @param desc - the method's descriptor
	 * @return A method with a given descriptor
	 */
	public static MethodNode getMethodByDesc(ClassNode cn, String desc)
	{
		return getMethodByDesc(cn,desc,desc);
	}
	
	/**
	 * Checks if a given ClassNode has a method with a descriptor, chosen by the environment
	 * @param cn - the ClassNode
	 * @param deobfDesc - the method descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the method descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if the classNode contains a method with a given descriptor, false otherwise
	 */
	public static boolean checkClassForMethodDesc(ClassNode cn, String deobfDesc, String obfDesc)
	{
		return checkClassForMethod(cn,null,null,deobfDesc,obfDesc);
	}
	
	/**
	 * Gets a MethodNode from a given ClassNode by a descriptor, chosen by the environment
	 * @param cn - the ClassNode
	 * @param deobfDesc - the method descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the method descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return The MethodNode
	 */
	public static MethodNode getMethodByDesc(ClassNode cn, String deobfDesc, String obfDesc)
	{
		return getMethod(cn,null,null,deobfDesc,obfDesc);
	}
	
	/**
	 * Gets a MethodNode from a given ClassNode by a given name
	 * @param cn - the ClassNode
	 * @param name - the method's name
	 * @return A method with a given name
	 */
	public static boolean checkClassForMethodName(ClassNode cn, String name)
	{
		return checkClassForMethodName(cn,name,name);
	}
	
	/**
	 * Gets a MethodNode from a given ClassNode by a given name
	 * @param cn - the ClassNode
	 * @param name - the method's name
	 * @return A method with a given name
	 */
	public static MethodNode getMethodByName(ClassNode cn, String name)
	{
		return getMethodByName(cn,name,name);
	}
	
	/**
	 * Checks if a given ClassNode has a method with a name, chosen by the environment
	 * @param cn - the ClassNode
	 * @param deobfName - the method name in DEV(deobfuscated) environment
	 * @param obfName - the method name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if the classNode contains a method with a given name, false otherwise
	 */
	public static boolean checkClassForMethodName(ClassNode cn, String deobfName, String obfName)
	{
		return checkClassForMethod(cn,deobfName,obfName,null,null);
	}
	
	/**
	 * Gets a MethodNode from a given ClassNode by a name, chosen by the environment
	 * @param cn - the ClassNode
	 * @param deobfName - the method name in DEV(deobfuscated) environment
	 * @param obfName - the method name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return The MethodNode
	 */
	public static MethodNode getMethodByName(ClassNode cn, String deobfName, String obfName)
	{
		return getMethod(cn,deobfName,obfName,null,null);
	}
	
	/**
	 * Checks if the MethodNode with a given name AND descriptor exists in the given ClassNode
	 * @param cn - the ClassNode
	 * @param name - the method's name
	 * @param desc - the method's descriptor
	 * @return True if such a method exists, false otherwise
	 */
	public static boolean checkClassForMethod(ClassNode cn, String name, String desc)
	{
		return checkClassForMethod(cn,name,name,desc,desc);
	}
	
	/**
	 * Gets a MethodNode with a given name AND descriptor from the given ClassNode
	 * @param cn - the ClassNode
	 * @param name - the method's name
	 * @param desc - the method's descriptor
	 * @return the MethodNode object or null
	 */
	public static MethodNode getMethod(ClassNode cn, String name, String desc)
	{
		return getMethod(cn,name,name,desc,desc);
	}
	
	/**
	 * Checks the given ClassNode for a MethodNode with a name and descriptor chosen by the environment and returns one if found
	 * <br>Name OR Descriptor can be null - then the search code will skip those params completely. However BOTH can't be null at the same time.
	 * @param cn - the ClassNode
	 * @param deobfName - the method name in DEV(deobfuscated) environment
	 * @param obfName - the method name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @param deobfDesc - the method descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the method descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return a MethodNode object, null if not found
	 */
	public static MethodNode getMethod(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		String methodName = null;
		String methodDesc = null;
		if(!PrimitiveUtils.checkSameAndNullStrings(deobfName,obfName))
			methodName = chooseByEnvironment(deobfName,obfName);
		if(!PrimitiveUtils.checkSameAndNullStrings(deobfDesc,obfDesc))
			methodDesc = chooseByEnvironment(deobfDesc,obfDesc);
		
		String additionalMN = "";
		if(methodName != null && methodName.contains(REGEX_NOTCH_FROM_MCP))
		{
			String[] sstr = methodName.split(REGEX_NOTCH_FROM_MCP);
			methodName = sstr[0];
			additionalMN = sstr[1];
		}
		
		String additionalMD = "";
		if(methodDesc != null && methodDesc.contains(REGEX_NOTCH_FROM_MCP))
		{
			String[] sstr = methodDesc.split(REGEX_NOTCH_FROM_MCP);
			methodDesc = sstr[0];
			additionalMD = sstr[1];
		}
		
		if(PrimitiveUtils.checkSameAndNullStrings(methodName, methodDesc))
			return null;
		for(MethodNode mn : cn.methods)
			if((methodName == null || methodName.equals(mn.name) || additionalMN.equals(mn.name)) && (methodDesc == null || methodDesc.equals(mn.desc) || additionalMD.equals(mn.desc)))
				return mn;
		
		return null;
	}
	
	/**
	 * Checks the given ClassNode for a MethodNode with a name and descriptor chosen by the environment
	 * <br>Name OR Descriptor can be null - then the search code will skip those params completely. However BOTH can't be null at the same time.
	 * @param cn - the ClassNode
	 * @param deobfName - the method name in DEV(deobfuscated) environment
	 * @param obfName - the method name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @param deobfDesc - the method descriptor in DEV(deobfuscated) environment
	 * @param obfDesc - the method descriptor in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if such MethodNode object exists, false otherwise
	 */
	public static boolean checkClassForMethod(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		return getMethod(cn,deobfName,obfName,deobfDesc,obfDesc) != null;
	}
	
	/**
	 * Checks if the given ClassNode has an annotation with a given name
	 * @param cn - the ClassNode
	 * @param name - the name of the annotation
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForClass(ClassNode cn, String name)
	{
		return checkAnnotationForClass(cn,name,name);
	}
	
	/**
	 * Checks if the given ClassNode has an annotation with a name chosen by environment
	 * @param cn - the ClassNode
	 * @param deobfName - the annotation name in DEV(deobfuscated) environment
	 * @param obfName - the annotation name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForClass(ClassNode cn, String deobfName, String obfName)
	{
		String searchedFor = chooseByEnvironment(deobfName,obfName);
		if(cn.invisibleAnnotations != null && cn.invisibleAnnotations.size() > 0)
			for(AnnotationNode an : cn.invisibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		if(cn.visibleAnnotations != null && cn.visibleAnnotations.size() > 0)
			for(AnnotationNode an : cn.visibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		return false;
	}
	
	/**
	 * Checks if the given MethodNode has an annotation with a given name
	 * @param mn - the MethodNode
	 * @param name - the name of the annotation
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForMethod(MethodNode mn, String name)
	{
		return checkAnnotationForMethod(mn,name,name);
	}
	
	/**
	 * Checks if the given MethodNode has an annotation with a name chosen by environment
	 * @param mn - the MethodNode
	 * @param deobfName - the annotation name in DEV(deobfuscated) environment
	 * @param obfName - the annotation name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForMethod(MethodNode mn, String deobfName, String obfName)
	{
		String searchedFor = chooseByEnvironment(deobfName,obfName);
		if(mn.invisibleAnnotations != null && mn.invisibleAnnotations.size() > 0)
			for(AnnotationNode an : mn.invisibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		if(mn.visibleAnnotations != null && mn.visibleAnnotations.size() > 0)
			for(AnnotationNode an : mn.visibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		return false;
	}
	
	/**
	 * Checks if the given FieldNode has an annotation with a given name
	 * @param fn - the FieldNode
	 * @param name - the name of the annotation
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForField(FieldNode fn, String name)
	{
		return checkAnnotationForField(fn,name,name);
	}
	
	/**
	 * Checks if the given MethodNode has an annotation with a name chosen by environment
	 * @param fn - the FieldNode
	 * @param deobfName - the annotation name in DEV(deobfuscated) environment
	 * @param obfName - the annotation name in OBF environment. See {@link #REGEX_NOTCH_FROM_MCP}
	 * @return True if such annotation is found, false otherwise
	 */
	public static boolean checkAnnotationForField(FieldNode fn, String deobfName, String obfName)
	{
		String searchedFor = chooseByEnvironment(deobfName,obfName);
		if(fn.invisibleAnnotations != null && fn.invisibleAnnotations.size() > 0)
			for(AnnotationNode an : fn.invisibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		if(fn.visibleAnnotations != null && fn.visibleAnnotations.size() > 0)
			for(AnnotationNode an : fn.visibleAnnotations)
				if(an.desc.equals(searchedFor))
					return true;
		
		return false;
	}
}
