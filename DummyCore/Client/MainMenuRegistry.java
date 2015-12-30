package DummyCore.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.io.IOUtils;

import DummyCore.Core.Core;
import DummyCore.Utils.DummyConfig;
import DummyCore.Utils.DummyData;
import DummyCore.Utils.IMainMenu;
import DummyCore.Utils.LoadingUtils;
import DummyCore.Utils.Notifier;

import com.google.common.collect.Iterators;

/**
 * This allows you to register your Main Menus using DC
 * @author modbder
 *
 */
public class MainMenuRegistry {
	
	public static List<Class<? extends GuiScreen>> menuList = new ArrayList<Class<? extends GuiScreen>>();
	public static List<NBTTagCompound> tagsToMenu = new ArrayList<NBTTagCompound>();
	
	public static List<DummyData> menuInfoLst = new ArrayList<DummyData>();
	
	public static boolean isGuiDisplayed;
	
	public static GuiScreen currentScreen;
	
	public static Class<MainMenuRegistry> clazz = MainMenuRegistry.class;
	
	/**
	 * Registers your menu using DC main menu registry system.
	 * @param menu - the CLASS of your menu. The object will be created when needed. Your class must have an empty constructor(with no params)!
	 */
	public static void registerNewGui(Class<? extends GuiScreen> menu)
	{
		if(IMainMenu.class.isAssignableFrom(menu))
		{
			menuList.add(menu);
			menuInfoLst.add(new DummyData(menu.getName(),"No description provided by author ;("));
		}else
		{
			Notifier.notifyCustomMod("DummyCore", "Attempting to register "+menu+" as a main menu, but the registered object does not implements IMainMenu!");
		}
	}
	
	/**
	 * Registers your menu using DC main menu registry system.
	 * @param menu - the CLASS of your menu. The object will be created when needed. Your class must have an empty constructor(with no params)!
	 * @param name - the name of your menu that will be displayed in the menu list
	 * @param description - the description of your menu that will be displayed when your menu is selected
	 */
	public static void registerNewGui(Class<? extends GuiScreen> menu, String name, String description)
	{
		if(IMainMenu.class.isAssignableFrom(menu))
		{
			menuList.add(menu);
			menuInfoLst.add(new DummyData(name,description));
		}else
		{
			Notifier.notifyCustomMod("DummyCore", "Attempting to register "+menu+" as a main menu, but the registered object does not implements IMainMenu!");
		}
	}
	
	/**
	 * Internal.
	 * @param index
	 */
	public static void newMainMenu(int index)
	{
		try
		{
			if(menuList.size() < index)
			{
				index = menuList.size()-1;
			}
			DummyConfig.setMainMenu(index);
			currentScreen = menuList.get(DummyConfig.getMainMenu()).newInstance();
			Minecraft.getMinecraft().displayGuiScreen(currentScreen);
		}catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Gets the current OBJECT displayed as the main menu
	 * @return The current OBJECT displayed as the main menu
	 */
	public static GuiScreen getGuiDisplayed()
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen != null && mc.currentScreen.getClass() != GuiMainMenu.class && mc.currentScreen instanceof IMainMenu)
			return mc.currentScreen;
		if(currentScreen != null)
			return currentScreen;
		try 
		{
			return menuList.get(DummyConfig.getMainMenu()).newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Internal. 
	 */
	public static void registerMenuConfigs()
	{
		Iterator<NBTTagCompound> $i = tagsToMenu.iterator();
		while($i.hasNext())
		{
			NBTTagCompound tag = $i.next();
			registerNewGui(GuiMainMenuNBT.class,tag.getString("menuName"),tag.getString("menuDesc"));
			GuiMainMenuNBT.idToTagMapping.put(menuList.size()-1, tag);
		}
		tagsToMenu.clear();
	}
	
	/**
	 * Internal. Creates a help.txt file for NBT based menu system
	 * @param dir
	 * @throws IOException
	 */
	public static void createHelpFile(File dir) throws IOException
	{
		dir.mkdirs();
		File txtFile = new File(dir,"help.txt");
		if(!txtFile.exists())
			txtFile.createNewFile();
		PrintWriter pw = new PrintWriter(txtFile);
		pw.println(Strings.repeat('=', 128));
		pw.println("Welcome to DummyCore's main menu help file");
		pw.println("It got created automatically and exists to teach you how to create Main Menu using files");
		pw.println("You can delete this file if it is not necessary. It will get sad though :(");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 0: Bases");
		pw.println(Strings.repeat('=', 128));
		pw.println("To create your own custom main menu using DummyCore the following conditions must be met:");
		pw.println("a)You need to create a file with any name in this directory");
		pw.println("b)The file needs to have a .dcmenu extension");
		pw.println("c)The file must be a text file with UTF-8 encoding");
		pw.println("d)The file must contain valid JSON syntax within");
		pw.println("e)The file must contain both menuName and menuDesc strings declared");
		pw.println("A small example of a correct file:");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\"");
		pw.println("}");
		pw.println("");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 1: Textures");
		pw.println(Strings.repeat('=', 128));
		pw.println("You can control the way your menu renders in the game. For that it needs to have the following declared:");
		pw.println("Your file must either contain an array of NBT(FadeTime:Integer and Texture:String) called Textures, or a String called Texture");
		pw.println("Your file must also contain a MenuType Integer representing the way your texture gets rendered");
		pw.println("Valid render types:");
		pw.println("-1 Will not render your menu");
		pw.println("0 Will render the image given as a Texture String, with scaling it so it takes all the screen");
		pw.println("1 Will render 6 images declared in the Textures NBT Array(Integer FadeTime takes no effect) just as vanilla MC does");
		pw.println("2 Will render 1 small texture given as a Texture String, repeating it all across the screen");
		pw.println("3 Will render N textures given in the Textures NBT Array with Texture String, leaving 1 texture for the time given in the Textures NBT Array with the FadeTime integer");
		pw.println("4 Will render N textures given in the Textures NBT Array with fading one over another with the time given in the Textures NBT Array with the OverlayTime Integer and rendering each image FadeTime Integer ticks");
		pw.println("5 Will render a .gif image declared in Texture String(This one might require 'not 10 years outdated OpenGL', though my videocard(read - crap) works fine).");
		pw.println("[GIF]To specify delay between frames you can use FrameDelay(Integer) to specify the delay for all frames, or FramesDelay(NBT Tag containing Integers) to specify delay for each frame specific. Delay is in ticks!");
		pw.println("[GIF]FramesDelay must contain FrameNumber:FrameTime. Example would be FramesDelay{0:5,1:3,4:12}. This will set the delay for frame 0 to 5 ticks, frame 1 gets 3 ticks and frmae 4 gets 12 ticks. All others get FrameDelay, or 0 if no specified");
		pw.println("[GIF]Please, for your own mental health use Forge's loading screen gif as an example, otherwise you are going to rage struggling to make this thing work. You've been warned!");
		pw.println("You can also add 2 Integer tags: GradientStart and GradientEnd. They are your gradient colors, rendered above your image.");
		pw.println("Use HEX(4 digit!(RGBA(Red Green Blue Alpha))) color as your color and any HEX -> Deciminary converter to find out your color number");
		pw.println("If you are using menu type 2(1 image across the GUI) you need to specify the amount of times the image is repeated across the GUI(or the scale of each individual image)");
		pw.println("This is done by adding TextureRepeats(Integer) to the tag. The more the number - the smaller the image will be, but the more repeated it will be. Default MC uses 10. You may not specify this field, the default value will be used then");
		pw.println("Valid example of a simple 4 repeating textures menu:");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\",");
		pw.println("	MenuType:3,");
		pw.println("	Textures:[");
		pw.println("		{");
		pw.println("			Texture:\"examplemodid:textures/menu/MenuImage0.png\",");
		pw.println("			FadeTime:100");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"examplemodid:textures/menu/MenuImage1.png\",");
		pw.println("			FadeTime:100");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"examplemodid:textures/menu/MenuImage2.png\",");
		pw.println("			FadeTime:100");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"examplemodid:textures/menu/MenuImage3.png\",");
		pw.println("			FadeTime:100");
		pw.println("		}");
		pw.println("	]");
		pw.println("}");
		pw.println("");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 2: Music");
		pw.println(Strings.repeat('=', 128));
		pw.println("You can also include your custom music with DummyCore!");
		pw.println("To include your music you would need to declare it with an NBT Array with Music String(must be registered via sounds.json)");
		pw.println("The array itself must also be named Music and can contain up to infinite music tags");
		pw.println("The music will randomly get chosen in the menu once there is no music playing");
		pw.println("Valid example of a simple 3 music tracks menu:");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\",");
		pw.println("	Music:[");
		pw.println("		{Music:\"examplemodid:music.MenuMusic0\"},");
		pw.println("		{Music:\"examplemodid:music.MenuMusic1\"},");
		pw.println("		{Music:\"examplemodid:music.MenuMusic2\"}");
		pw.println("	]");
		pw.println("}");
		pw.println("");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 3: Custom Images");
		pw.println(Strings.repeat('=', 128));
		pw.println("DummyCore allows you to add custom images to your menu(like MC text)");
		pw.println("To add them you need to declare them within NBT Array called Images");
		pw.println("The NBT inside the array can contain the following tags:");
		pw.println("Texture(String) is a link to your image. Must be written as modid:texturepath, and can be either .png or .gif");
		pw.println("XAlignment(Integer) and YAlignment(Integer) is a way to represent the image's default position, before applying x and y translations.");
		pw.println("XAlignment(Integer) and YAlignment(Integer) can have the following Integers:");
		pw.println(" 0 - means the default position will be 0(top left)");
		pw.println(" 1 - means the default position will be in the center(middle, duh)");
		pw.println(" 2 - means the default position will be in the bottom right corner");
		pw.println(" anything>2 - means the default position will be guiSize/value");
		pw.println(" Both those entries can have different values");
		pw.println("X(Integer) and Y(Integer) is defaultPosition + X or Y, so they are your offsets");
		pw.println("TextureMinX(Integer) and TextureMinY(Integer) is the start position on your texture file(the file is considered to be 256x256, so making a bigger file will require you to perform additional calculations)");
		pw.println("TextureMaxX(Integer) and TextureMaxY(Integer) is the end position on your texture file(the file is considered to be 256x256, so making a bigger file will require you to perform additional calculations)");
		pw.println("Alternatively you can use MinU, MinV, MaxU and MaxV doubles - these represent the texturePosition/textureSize. Only use if you know how that works. Allows for more custom HD texture positioning");
		pw.println("XSize(Integer) and YSize(Integer) is the size of your texture in the GUI.");
		pw.println("Text(NBT Array) allows you to declare custom text to be displayed somewhere near your image");
		pw.println("Text(NBT Array) can have up to infinite tags. Each tag needs to contain X(Integer), Y(Integer) and Text(String) values. The text is positioned the same way the image is. Text will be rendered as a splash if it has a |Splash| prefix. Can also contain Splashes(Text) link to a splashes.txt file and Angle(Float) to rotate the splashes text");
		pw.println("Valid example of a menu with Minecraft image:");
		pw.println("Notice, that there are 2 images, due to minecraft.png actually having a split texture");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\",");
		pw.println("	Images:[");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/title/minecraft.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:0,");
		pw.println("			X:-137,");
		pw.println("			Y:30,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:0,");
		pw.println("			TextureMaxX:155,");
		pw.println("			TextureMaxY:44,");
		pw.println("			XSize:155,");
		pw.println("			YSize:44");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/title/minecraft.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:0,");
		pw.println("			X:18,");
		pw.println("			Y:30,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:45,");
		pw.println("			TextureMaxX:155,");
		pw.println("			TextureMaxY:89,");
		pw.println("			XSize:155,");
		pw.println("			YSize:44,");
		pw.println("			Text:[");
		pw.println("				{");
		pw.println("					X:112,");
		pw.println("					Y:48,");
		pw.println("					Splashes:\"minecraft:texts/splashes.txt\",");
		pw.println("					Angle:-20.0");
		pw.println("				}");
		pw.println("			]");
		pw.println("		}");
		pw.println("	]");
		pw.println("}");
		pw.println("");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 4: Custom Buttons");
		pw.println(Strings.repeat('=', 128));
		pw.println("DummyCore allows you to add custom buttons to your menu");
		pw.println("To add them you need to declare them within NBT Array called Buttons");
		pw.println("Each NBT in the array can have the following declared within:");
		pw.println(" Texture(String) is a link to the button's image. Must be written as modid:texturepath, and can be .png only");
		pw.println(" XAlignment(Integer) and YAlignment(Integer) is a way to represent the button's default position, before applying x and y translations.");
		pw.println(" XAlignment(Integer) and YAlignment(Integer) can have the following Integers:");
		pw.println("  0 - means the default position will be 0(top left)");
		pw.println("  1 - means the default position will be in the center(middle, duh)");
		pw.println("  2 - means the default position will be in the bottom right corner");
		pw.println(" anything>2 - means the default position will be guiSize/value");
		pw.println(" X(Integer) and Y(Integer) is defaultPosition + X or Y, so they are your offsets");
		pw.println(" TextureMinX(Integer) and TextureMinY(Integer) is the start position on your texture file(the file is considered to be 256x256, so making a bigger file will require you to perform additional calculations)");
		pw.println(" TextureMaxX(Integer) and TextureMaxY(Integer) is the end position on your texture file(the file is considered to be 256x256, so making a bigger file will require you to perform additional calculations)");
		pw.println(" Alternatively you can use MinU, MinV, MaxU and MaxV doubles - these represent the texturePosition/textureSize. Only use if you know how that works. Allows for more custom HD texture positioning");
		pw.println(" XSize(Integer) and YSize(Integer) is the size of your button in the GUI.");
		pw.println(" ButtonYOffset(Integer) is the amount of pixels the button texture will be offset on the texture file to get the button's disabled and mouseover textures");
		pw.println(" Alternatively can be ButtonYUVOffset double");
		pw.println(" ButtonID(Integer) is the ID of the button. Valid IDs I know of:");
		pw.println("  0 - Options Button");
		pw.println("  1 - Singleplayer Button");
		pw.println("  2 - Multiplayer Button");
		pw.println("  3 - Empty");
		pw.println("  4 - Quit Button");
		pw.println("  5 - Language Button");
		pw.println("  6 - Mod List Button");
		pw.println("  7 - Empty");
		pw.println("  8 - Empty");
		pw.println("  9 - Empty");
		pw.println("  10 - Empty");
		pw.println("  11 - Demo Singleplayer Button");
		pw.println("  12 - Demo Reset Button");
		pw.println("  13 - Empty");
		pw.println("  65535 - DummyCore's change GUI Button");
		pw.println(" Sound(String) is a link to the button's press sound. Must be a registered(via sounds.json) sound");
		pw.println(" URL(String) is a URL link to any Internet website. Pressing the button will trigger site's page opening in the presser's browser");
		pw.println(" Text(String) is a text or a localization reference for the button");
		pw.println("Valid example of a menu with default Minecraft buttons:");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\",");
		pw.println("	Buttons:[");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:-100,");
		pw.println("			Y:132,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:98,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:0,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"menu.options\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:2,");
		pw.println("			Y:132,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:98,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:4,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"menu.quit\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:-124,");
		pw.println("			Y:132,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:86,");
		pw.println("			TextureMaxX:20,");
		pw.println("			TextureMaxY:106,");
		pw.println("			XSize:20,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:5,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:-100,");
		pw.println("			Y:48,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:200,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:1,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"menu.singleplayer\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:-100,");
		pw.println("			Y:72,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:200,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:2,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"menu.multiplayer\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:-100,");
		pw.println("			Y:96,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:98,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:14,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"menu.online\"");
		pw.println("		},");
		pw.println("		{");
		pw.println("			Texture:\"minecraft:textures/gui/widgets.png\",");
		pw.println("			XAlignment:1,");
		pw.println("			YAlignment:4,");
		pw.println("			X:2,");
		pw.println("			Y:96,");
		pw.println("			TextureMinX:0,");
		pw.println("			TextureMinY:46,");
		pw.println("			TextureMaxX:200,");
		pw.println("			TextureMaxY:66,");
		pw.println("			XSize:98,");
		pw.println("			YSize:20,");
		pw.println("			ButtonYOffset:20,");
		pw.println("			ButtonID:6,");
		pw.println("			Sound:\"gui.button.press\",");
		pw.println("			Text:\"Mods\"");
		pw.println("		}");
		pw.println("	]");
		pw.println("}");
		pw.println("");
		pw.println(Strings.repeat('=', 128));
		pw.println("Part 5: Custom Text");
		pw.println(Strings.repeat('=', 128));
		pw.println("DummyCore allows you to add custom text(bottom corners) to your menu");
		pw.println("Your menu tag needs to have 2 NBT Arrays(String) in order for this to work");
		pw.println("1 array is named TextLeft and 2 - TextRight");
		pw.println("You can insert 'symbolic links' in the text array");
		pw.println("For example, having a |FML| will add FML's current version to text");
		pw.println("Valid 'symbolic liknls':");
		pw.println(" |MC| prints Minecraft's current version");
		pw.println(" |MCP| prints MinecraftCoderPack's current version");
		pw.println(" |FML| prints FML's current version");
		pw.println(" |Forge| prints Forge's current version");
		pw.println(" |SidedInfo| prints FML's special sided information");
		pw.println(" |FMLBranding| prints FML's special brandings information");
		pw.println(" |Mods| prints current amount of mods loaded");
		pw.println(" |Copyright| prints Mojang's copyright");
		pw.println("Valid example of default main menu text:");
		pw.println("");
		pw.println("{");
		pw.println("	menuName:\"I am a menu name!\",");
		pw.println("	menuDesc:\"I am a menu description!\",");
		pw.println("	TextLeft:[");
		pw.println("	\"|MC|\",");
		pw.println("	\"|MCP|\",");
		pw.println("	\"|FML|\",");
		pw.println("	\"|Forge|\",");
		pw.println("	\"|SidedInfo|\",");
		pw.println("	\"|FMLBranding|\",");
		pw.println("	\"|Mods|\"");
		pw.println("	],");
		pw.println("	TextRight:[");
		pw.println("	\"|Copyright|\"");
		pw.println("	],");
		pw.println("}");
		pw.println("");
		pw.flush();
		pw.close();
	}
	
	/**
	 * Internal. Finds all possible NBT based menu entries in the config folder
	 */
	public static void initMenuConfigs()
	{
		try
		{
			Core.mcDir = Minecraft.getMinecraft().mcDataDir;
			File dir = new File(Core.mcDir,"config");
			dir.mkdirs();
			dir = new File(dir,"dcMainMenu");
			if(!dir.exists())
				createHelpFile(dir);
			File[] files = dir.listFiles();
			Iterator<File> $i = Iterators.forArray(files);
			while($i.hasNext())
			{
				File detected = $i.next();
				if(detected.getName().endsWith(".dcmenu"))
				{
					FileInputStream fis = new FileInputStream(detected);
					if(fis != null)
					{
						try
						{
							String st = IOUtils.toString(fis,"UTF-8");
							NBTTagCompound tag = JsonToNBT.getTagFromJson(st);
							if(validateNBT(tag))
							{
								tagsToMenu.add(tag);
								Notifier.notifySimple("[MainMenuRegistry]"+detected+" Is a valid main menu and has been send to parsing queue.");
							}else
							{
								Notifier.notifyError("[MainMenuRegistry]"+detected+" Is not a valid menu!");
							}
						}
						catch(IOException ioe)
						{
							LoadingUtils.makeACrash("[MainMenuRegistry]Couldn't read file "+detected, clazz, ioe, false);
						}
						catch(UnsupportedCharsetException uce)
						{
							LoadingUtils.makeACrash("[MainMenuRegistry]UTF-8 is unsupported on your system(???)", clazz, uce, false);
						} catch (NBTException nbte) 
						{
							LoadingUtils.makeACrash("[MainMenuRegistry]"+detected+" Is not a valid menu!", clazz, nbte, false);
							nbte.printStackTrace();
						}
						finally
						{
							IOUtils.closeQuietly(fis);
						}
					}
				}
			}
		}
		catch(IOException ioe)
		{
			LoadingUtils.makeACrash("[MainMenuRegistry]Couldn't read files in your config directory", clazz, ioe, false);
		}
	}
	
	/**
	 * Internal. Validates the NBT to be a valid NBTMainMenu tag
	 * @param tag
	 * @return
	 */
	public static boolean validateNBT(NBTTagCompound tag)
	{
		return tag.hasKey("menuName") && tag.hasKey("menuDesc");
	}

}
