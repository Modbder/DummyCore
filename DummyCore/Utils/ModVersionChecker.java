package DummyCore.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.PatternSyntaxException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Core.DCMod;

public class ModVersionChecker {
	
	public static final ArrayList<DCMod> vCheckRequesters = new ArrayList<DCMod>();
	public static final Hashtable<DCMod,String> latestVersions = new Hashtable<DCMod,String>();
	public static final Hashtable<DCMod,String> modsURIs = new Hashtable<DCMod,String>();
	
	public static void addRequest(Class<?> mod, String uri)
	{
		if(Core.isModRegistered(mod))
		{
			DCMod dcmod = Core.getModFromClass(mod);
			vCheckRequesters.add(dcmod);
			modsURIs.put(dcmod, uri);
		}else
			Notifier.notifyError("[DCVersionChecker]Catched an attempt to add a version check request for a non registered mod!");
	}
	
	public static void dispatchModChecks()
	{
		for(int i = 0; i < vCheckRequesters.size(); ++i)
			requestModVCheck(vCheckRequesters.get(i));
	}
	
	public static void requestModVCheck(DCMod mod)
	{
		if(vCheckRequesters.contains(mod))
			new ThreadURICheck(mod,modsURIs.get(mod)).start();
	}
	
	public static void respondToURICheckSuccess(DCMod mod)
	{
		String currentVersion = mod.version;
		String checkedVersion = latestVersions.get(mod);
		if(isVersionDMValid(checkedVersion) && isVersionDMValid(currentVersion))
		{
			String[] versions = checkedVersion.split(".");
			String[] cVersions = mod.version.split(".");
			for(int i = 0; i < versions.length; ++i)
				versions[i] = removeButNumbers(versions[i]);
			
			for(int i = 0; i < cVersions.length; ++i)
				cVersions[i] = removeButNumbers(cVersions[i]);
			
			for(int i = 0; i < 4; ++i)
			{
				int ii = i;
				if(ii == 0)
					ii = 2;
				else
					if(ii == 2)
						ii = 0;
				String pcurrentVersion = cVersions[ii];
				String pcheckedVersion = versions[ii];
				int current = Integer.parseInt(pcurrentVersion);
				int latest = Integer.parseInt(pcheckedVersion);
				if(current < latest)
				{
					tryNotifyDMOutdatedMod(mod,checkedVersion,ii);
					break;
				}
			}
		}else
		{
			String pcurrentVersion = removeButNumbers(currentVersion);
			String pcheckedVersion = removeButNumbers(checkedVersion);
			int current = Integer.parseInt(pcurrentVersion);
			int latest = Integer.parseInt(pcheckedVersion);
			if(current < latest)
				tryNotifySimpleOutdatedMod(mod,checkedVersion);
		}
		vCheckRequesters.remove(mod);
		latestVersions.remove(mod);
		modsURIs.remove(mod);
	}
	
	public static void tryNotifyDMOutdatedMod(DCMod mod, String latest, int step)
	{
		EntityPlayer player = CoreInitialiser.proxy.getClientPlayer();
		if(player != null)
		{
			String steppedString = "";
			switch(step)
			{
				case 0:
				{
					steppedString = String.format("%s is horribly outdated(Your version is %s, latest is %s) has bugs and no longer supported! Update as soon as possible!", mod.ufName, mod.version, latest);
					break;
				}
				case 1:
				{
					steppedString = String.format("%s is outdated(Your version is %s, latest is %s)! Consider updating!", mod.ufName, mod.version, latest);
					break;
				}
				case 2:
				{
					steppedString = String.format("%s is available for another version of the game(Your version is %s, latest is %s)!", mod.ufName, mod.version, latest);
					break;
				}
				case 3:
				{
					steppedString = String.format("%s has a new version available(Your version is %s, latest is %s)!", mod.ufName, mod.version, latest);
					break;
				}
			}
			player.addChatMessage(new ChatComponentText(steppedString).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}
	}
	
	
	public static void tryNotifySimpleOutdatedMod(DCMod mod, String latest)
	{
		EntityPlayer player = CoreInitialiser.proxy.getClientPlayer();
		if(player != null)
			player.addChatMessage(new ChatComponentText(String.format("%s is outdated(Your version is %s, latest is %s)! Consider updating!", mod.ufName, mod.version, latest)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
	}
	
	public static boolean isVersionDMValid(String version)
	{
		try{
			return version.split(".").length >= 4;
		}catch(PatternSyntaxException pse)
		{
			pse.printStackTrace();
			Notifier.notifyError("[DCVersionChecker]"+version+" is not a valid DM version");
		}
		return false;
	}
	
	public static String removeButNumbers(String s)
	{
		String result = "";
		
		char[] chars = s.toCharArray();
		for(char c : chars)
			if(Character.isDigit(c))
				result += c;
		
		return result;
	}
	
	public static class ThreadURICheck extends Thread
	{
		public String uristr;
		public DCMod mod;
		public ThreadURICheck(DCMod mod, String uri)
		{
			super("[DCVercionCheckDaemon]For mod: "+mod.ufName);
			this.setDaemon(true); //<- if this thread is forever asleep due to network issues
			this.setPriority(MIN_PRIORITY); //<- we do not need to rush
			this.mod = mod;
			this.uristr = uri;
		}
		
		@Override
		public void run()
		{
			try
			{
				URI uri = new URI(uristr);
				InputStream is = uri.toURL().openStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String version = br.readLine();
				ModVersionChecker.latestVersions.put(mod, version);
				is.close();
				ModVersionChecker.respondToURICheckSuccess(mod);
				interrupt();
			}catch(URISyntaxException urise){
				urise.printStackTrace();
				Notifier.notifyErrorCustomMod("[DCVercionCheckDaemon]"+mod.ufName, uristr+" is not a valid URI!");
				interrupt();
			}catch (MalformedURLException murle){
				murle.printStackTrace();
				Notifier.notifyErrorCustomMod("[DCVercionCheckDaemon]"+mod.ufName, uristr+" is not a valid URL!");
				interrupt();
			}catch (IOException ioe){
				ioe.printStackTrace();
				Notifier.notifyErrorCustomMod("[DCVercionCheckDaemon]"+mod.ufName, "Couldn't read file at "+uristr);
				interrupt();
			}
		}
	}

}
