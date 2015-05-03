package DummyCore.Utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Notifier {
	
	//======================================String Beginning Indexes===============================================//
	public static final String info = "[INFO]";
	public static final String warning = "[WARNING]";
	public static final String error = "[ERROR]";
	public static final String severe = "[SEVERE]";
	//======================================String Second Indexes===============================================//
	public static final String dev = "[DEV]";
	public static final String debug = "[DEBUG]";
	public static final String stdout = "[STDOUT]";
	//======================================String Ending Indexes===============================================//
	public static final String system = "[SYSTEM]";
	public static final String mod = "[DummyCore]";
	
	public static final Logger logger = LogManager.getLogger();
	
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
		org.apache.logging.log4j.core.Logger log = (org.apache.logging.log4j.core.Logger) logger;
		log.setLevel(Level.INFO);
		if(DummyConfig.enableNotifierLogging)
			logger.log(Level.INFO, s);
	}

}
