package DummyCore.Utils;

import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;

public class Notifier {
	
	//======================================String Beginning Indexes===============================================//
	private static String info = "[INFO]";
	private static String warning = "[WARNING]";
	private static String error = "[ERROR]";
	private static String severe = "[SEVERE]";
	//======================================String Second Indexes===============================================//
	private static String dev = "[DEV]";
	private static String debug = "[DEBUG]";
	private static String stdout = "[STDOUT]";
	//======================================String Ending Indexes===============================================//
	private static String system = "[SYSTEM]";
	private static String mod = "[DummyCore]";
	
	private static StringBuilder builder = new StringBuilder();
	
	public static void notify(String... s)
	{
		String begin = "";
		String mid = "";
		String end = "";
		String mesg = s[3];
		mid = s[1];
		end = s[2];
		String out = buildString(begin,mid,end,mesg);
		publish(out);
	}
	
	public static void notifyDev(String s)
	{
		notify("",dev,mod,s);
	}
	
	public static void notifyDevCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify("",dev,modname,s);
	}
	
	public static void notifyDebugCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify("",debug,modname,s);
	}
	
	public static void notifyDebug(String s)
	{
		notify("",debug,mod,s);
	}
	
	public static void notifySystem(String s)
	{
		notify("","",system,s);
	}
	
	public static void notifySimple(String s)
	{
		notify("","",mod,s);
	}
	
	public static void notifyCustomMod(String modname,String s)
	{
		modname = "["+modname+"]";
		notify("","",modname,s);
	}
	
	public static String buildString(String...strings)
	{
		return strings[0] + " " + strings[1] + " " + strings[2] + " " + strings[3] + " ";
	}
	
	public static void publish(String s)
	{
		System.out.println(s);
	}

}
