package DummyCore.ASM;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import DummyCore.Utils.Notifier;
import net.minecraft.launchwrapper.IClassTransformer;

public class DCASMManager implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName,byte[] basicClass) 
	{
		if(basicClass != null) //If the class we are loading exists
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
		return basicClass;
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
