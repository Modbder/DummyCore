package DummyCore.Utils;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S0FPacketSpawnMob;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * 
 * @author modbder
 * @Description This class is a helper for me.
 */
@SuppressWarnings("unused")
final class ASMInfoClass {

	private class ASMClassStructure
	{
		//header = 0xCAFEBABE + Version
		
		//constant pool = Constants(field/method names, etc)
		
		//access rights = Private/protected flags
		
		//Interfaces LIST
		
		//Fields LIST
		
		//Methods LIST
		
		//Attributes
	}
	
	private class AssemblerCommands
	{
		//ldc = Const -> Stack
		//iadd = i0+i1 -> Stack
		//goto = go to line
		//if_icmpeq = if
		//jsr, ret = subprogram, return from it.
		//athrow = Exception
		//istore = Local variable -> Stack
		//iload = Local variable <- Stack
		//iastore = i0 -> i[]
		//getfield = Local <- field
		//putfield = Local -> field
		//getstatic = Local <- static field
		//putstatic = Local -> static field
		//invokestatic = invoke a static method
		//invokevirtual = invoke a method
		//invokespecial = invoke a PARENT or a PRIVATE method
		//invokeinterface = invoke an INTERFACE(ABSTRACT) method
		//new = new object
		//newarray = new array
		//anewarray(multinewarray) = link array?
		//f2i = Float to Integer(i2d,i2f,i2l,etc)
		//instanceof = is a instanceof b
		//checkcast = is possible to (a)b
	}
	
	private class Signatures
	{
		//Every method/field/etc has a CONSTANT in class's CONST field(look above).
		//Everything is converted into signatures.
		//public static void main( String[] argv ) = ([java/lang/String;)V
		//Everything in () are PARAMS. [ indicates an ARRAY. java/lang/String = link to PARAM. If PARAM is not an object, it will be I = int, F = float, D = double, etc.
		//; indicates APRAM end. V = return type. V = void, I = int, etc. Objects are done via LINK.
	}
	
	private class Attributes
	{
		//Methods have their stats listed in CODE in the Attributes(look above)
		//Methods have next data:
		//MAX Stack size(not sure if the same for java 8?)
		//Local variable COUNT
		//InsNode array.
		//If compiled with a DEBUG flag will have additional info:
		//Local variable NAME
		//POSITION of source-code lines
	}
	
	private class ASMUsageRules
	{
		//Using of ASM needs to follow simple lines
		
		//1. PARSER of the .class file MUST be called first. Will analyze the .class
		
		//Lookup the DCASMManager:
		/*
		    ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass); <---- Here it is
			classReader.accept(classNode, 0); <---- CALLING the parser
			...
		 */
		
		//2. Event Producer(for CORE ASM type(we are using a TREE, which is built over CORE))
		
		//It is usually combined with the PARSER for the TREE.
		//Lookup the DCASMManager:
		/*
		    ...
			ClassReader classReader = new ClassReader(basicClass); <---- Here it is
			...
		 */
		
		//3. The CLASS WRITER. MUST be called !!!BEFORE!!! ASM Modifications!
		//Lookup the DCASMManager:
		/*
			...
			Notifier.notifyCustomMod("DummyCoreASM", "Class "+name+" has given the next interfaces to check: "+Arrays.asList(checkedClss));
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); <---- Here it is
			...
		 */
		//The class CAN be analyzed BEFORE calling a WRITER. However, you MUST initialize one BEFORE ASM Modifications!
		
		//4. Class ADAPTERS(event FILTERS). No such thing for DCASMManager.
		
		//Using CORE ASM is done with a specific USE ORDER:
		//1. Creating a class READER, event READER, analyzer, WRITER, etc.
		//2. Injecting BYTECODE and CHANING it using the ADAPTERS.
		//3. Retrieving the BYTECODE back to the JVM
	}
	
	private class ASMPackages
	{
		//org.objectweb.asm && org.objectweb.asm.sinature -> CORE ASM. READERS and WRITERS.
		//org.objectweb.asm.util -> Utils for CORE ASM
		//org.objectweb.asm.commons -> common HELPING functions for modifying bytecode using CORE ASM.
		//org.objectweb.asm.tree -> TREE ASM.
		//org.objectweb.asm.tree.analysis -> common ANALYSIS functions for TREE ASM
	}
	
	private class ASMStructure
	{
		//ASM is based upon modifying SINGLE class feature AT A TIME.
		//ClassVisitor is what is used to do that.
		//Every method there has something to do with the corresponding CLASS file structure piece(look above)
		//Simple object can be accessed without something special.
		//More complex need to be VISITED, and that returns a specific VISITOR to modify THAT EXACT feature.
		//Modifying an annotation takes ClassVisitor.visitAnnotation, which returns a specific AnnotationVisitor!
		
		//ASM key modification things are:
		//ClassReader = Transforms the bytecode array into EVENTS.
		//ClassWriter = Transforms the EVENTS into bytecode array.
		//ClassAdapter = Filters EVENTS for the ClassWriter.
	}
}
