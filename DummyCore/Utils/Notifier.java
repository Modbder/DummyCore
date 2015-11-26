package DummyCore.Utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A small logging wrapper of mine. You should use org.apache.logging
 * @author modbder
 *
 */
public class Notifier {
	
	//======================================String Beginning Indexes===============================================//
	public static final String info = "[INFO]";
	public static final String warning = "[WARNING]";
	public static final String error = "[ERROR]";
	public static final String fatal = "[FATAL]";
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
		notify(Level.TRACE,s);
	}
	
	public static void notify(Level l,String... s)
	{
		publish(l,buildString("[DCNotifier]",s[1],s[2],s[3]));
	}
	
	public static void notifyDev(String s)
	{
		notify(Level.DEBUG,"",dev,mod,s);
	}
	
	public static void notifyDevCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify(Level.DEBUG,"",dev,modname,s);
	}
	
	public static void notifyDebugCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify(Level.DEBUG,"",debug,modname,s);
	}
	
	public static void notifyDebug(String s)
	{
		notify(Level.DEBUG,"",debug,mod,s);
	}
	
	public static void notifyWarnCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify(Level.WARN,"",warning,modname,s);
	}
	
	public static void notifyWarn(String s)
	{
		notify(Level.WARN,"",warning,mod,s);
	}
	
	public static void notifyErrorCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify(Level.ERROR,"",error,modname,s);
	}
	
	public static void notifyError(String s)
	{
		notify(Level.ERROR,"",error,mod,s);
	}
	
	public static void notifyFatalCustomMod(String modname, String s)
	{
		modname = "["+modname+"]";
		notify(Level.FATAL,"",fatal,modname,s);
	}
	
	public static void notifyFatal(String s)
	{
		notify(Level.FATAL,"",fatal,mod,s);
	}
	
	public static void notifySystem(String s)
	{
		notify(Level.TRACE,"","",system,s);
	}
	
	public static void notifySimple(String s)
	{
		notify(Level.TRACE,"","",mod,s);
	}
	
	public static void notifyInfo(String s)
	{
		notify(Level.INFO,"","",mod,s);
	}
	
	public static void notifyCustomMod(String modname,String s)
	{
		modname = "["+modname+"]";
		notify(Level.INFO,"","",modname,s);
	}
	
	public static String buildString(String...strings)
	{
		return strings[0] + " " + strings[1] + " " + strings[2] + " " + strings[3] + " ";
	}
	
	public static void publish(Level l,String s)
	{
		if(DummyConfig.enableNotifierLogging)
			logger.log(l, s);
	}
	
	public static void publish(String s)
	{
		publish(Level.TRACE,s);
	}

}
