package DummyCore.Utils;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

public class ASMManager {

	public static boolean obf(){
		return FMLForgePlugin.RUNTIME_DEOBF;
	}
	
	public static String chooseByEnvironment(String deobf, String obf)
	{
		return obf() ? obf : deobf;
	}
	
	public static boolean strictCompareByEnvironment(String name, String deobf, String obf)
	{
		String comparedTo = chooseByEnvironment(deobf,obf);
		return comparedTo.equalsIgnoreCase(name);
	}
	
	public static boolean compareByEnvironment(String name, String deobf, String obf)
	{
		String comparedTo = chooseByEnvironment(deobf,obf);
		return comparedTo.toLowerCase().contains(name.toLowerCase());
	}
	
	public static boolean checkClassForFieldByName(ClassNode cn, String name)
	{
		return checkClassForFieldByName(cn,name,name);
	}
	
	public static FieldNode getFieldByName(ClassNode cn, String name)
	{
		return getFieldByName(cn,name,name);
	}
	
	public static boolean checkClassForFieldByName(ClassNode cn, String deobfName, String obfName)
	{
		return checkClassForField(cn,deobfName,obfName,null,null);
	}
	
	public static FieldNode getFieldByName(ClassNode cn, String deobfName, String obfName)
	{
		return getField(cn,deobfName,obfName,null,null);
	}
	
	public static boolean checkClassForField(ClassNode cn, String name, String desc)
	{
		return checkClassForField(cn,name,name,desc,desc);
	}
	
	public static FieldNode getField(ClassNode cn, String name, String desc)
	{
		return getField(cn,name,name,desc,desc);
	}
	
	public static boolean checkClassForField(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		return getField(cn,deobfName,obfName,deobfDesc,obfDesc) != null;
	}
	
	public static FieldNode getField(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		String fieldName = null;
		String fieldDesc = null;
		
		if(!MiscUtils.checkSameAndNullStrings(deobfName,obfName))
			fieldName = chooseByEnvironment(deobfName,obfName);
		if(!MiscUtils.checkSameAndNullStrings(deobfDesc,obfDesc))
			fieldDesc = chooseByEnvironment(deobfDesc,obfDesc);
		
		if(MiscUtils.checkSameAndNullStrings(fieldName, fieldDesc))
			return null;
		
		for(FieldNode fn : cn.fields)
			if((fieldName == null || fn.name.equals(fieldName)) && (fieldDesc == null || fn.desc.equals(fieldDesc)))
				return fn;
		
		return null;
	}
	
	public static boolean checkClassForMethodDesc(ClassNode cn, String desc)
	{
		return checkClassForMethodDesc(cn,desc,desc);
	}
	
	public static MethodNode getMethodByDesc(ClassNode cn, String desc)
	{
		return getMethodByDesc(cn,desc,desc);
	}
	
	public static boolean checkClassForMethodDesc(ClassNode cn, String deobfDesc, String obfDesc)
	{
		return checkClassForMethod(cn,null,null,deobfDesc,obfDesc);
	}
	
	public static MethodNode getMethodByDesc(ClassNode cn, String deobfDesc, String obfDesc)
	{
		return getMethod(cn,null,null,deobfDesc,obfDesc);
	}
	
	public static boolean checkClassForMethodName(ClassNode cn, String name)
	{
		return checkClassForMethodName(cn,name,name);
	}
	
	public static MethodNode getMethodByName(ClassNode cn, String name)
	{
		return getMethodByName(cn,name,name);
	}
	
	public static boolean checkClassForMethodName(ClassNode cn, String deobfName, String obfName)
	{
		return checkClassForMethod(cn,deobfName,obfName,null,null);
	}
	
	public static MethodNode getMethodByName(ClassNode cn, String deobfName, String obfName)
	{
		return getMethod(cn,deobfName,obfName,null,null);
	}
	
	public static boolean checkClassForMethod(ClassNode cn, String name, String desc)
	{
		return checkClassForMethod(cn,name,name,desc,desc);
	}
	
	public static MethodNode getMethod(ClassNode cn, String name, String desc)
	{
		return getMethod(cn,name,name,desc,desc);
	}
	
	public static MethodNode getMethod(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		String methodName = null;
		String methodDesc = null;
		if(!MiscUtils.checkSameAndNullStrings(deobfName,obfName))
			methodName = chooseByEnvironment(deobfName,obfName);
		if(!MiscUtils.checkSameAndNullStrings(deobfDesc,obfDesc))
			methodDesc = chooseByEnvironment(deobfDesc,obfDesc);
		
		if(MiscUtils.checkSameAndNullStrings(methodName, methodDesc))
			return null;
		for(MethodNode mn : cn.methods)
			if((methodName == null || methodName.equals(mn.name)) && (methodDesc == null || methodDesc.equals(mn.desc)))
				return mn;
		
		return null;
	}
	
	public static boolean checkClassForMethod(ClassNode cn, String deobfName, String obfName, String deobfDesc, String obfDesc)
	{
		return getMethod(cn,deobfName,obfName,deobfDesc,obfDesc) != null;
	}
	
	public static boolean checkAnnotationForClass(ClassNode cn, String name)
	{
		return checkAnnotationForClass(cn,name,name);
	}
	
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
	
	public static boolean checkAnnotationForMethod(MethodNode mn, String name)
	{
		return checkAnnotationForMethod(mn,name,name);
	}
	
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
	
	public static boolean checkAnnotationForField(FieldNode fn, String name)
	{
		return checkAnnotationForField(fn,name,name);
	}
	
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
