package DummyCore.Client;

import java.util.Hashtable;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * This registers all your needed icons.
 * @author modbder
 *
 */
public class IconRegister{
	
	public static final IconRegister instance = new IconRegister();
	public static TextureMap currentMap;
	public static final Hashtable<String,Icon> registered = new Hashtable<String,Icon>();
	
	public Icon registerBlockIcon(String path)
	{
		if(path.indexOf(':') == -1)
			return registerBlockIcon("minecraft",path);
		return registerBlockIcon(path.substring(0, path.indexOf(':')),path.substring(path.indexOf(':')+1));
	}
	
	public Icon registerBlockIcon(String domain, String path)
	{
		path = "blocks/" + path;
		if(!registered.containsKey(domain+":"+path))
			registered.put(domain+":"+path, new Icon(currentMap.registerSprite(new ResourceLocation(domain,path))));
		return registered.get(domain+":"+path);
	}
	
	public Icon registerItemIcon(String path)
	{
		if(path.indexOf(':') == -1)
			return registerItemIcon("minecraft",path);
		return registerItemIcon(path.substring(0, path.indexOf(':')),path.substring(path.indexOf(':')+1));
	}
	
	public Icon registerItemIcon(String domain, String path)
	{
		path = "items/" + path;
		if(!registered.containsKey(domain+":"+path))
			registered.put(domain+":"+path, new Icon(currentMap.registerSprite(new ResourceLocation(domain,path))));
		return registered.get(domain+":"+path);
	}

}
