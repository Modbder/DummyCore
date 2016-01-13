package DummyCore.Utils;

/**
 * A simple Enum for... something? Convinience!
 * @author modbder
 *
 */
public enum EnumGuiPosition {
	TOPLEFT,
	TOPRIGHT,
	BOTLEFT,
	BOTRIGHT,
	CENTER,
	BOTCENTER,
	TOPCENTER,
	LEFTCENTER,
	RIGHTCENTER;
	
	EnumGuiPosition()
	{
	}
	
	public static EnumGuiPosition byName(String s)
	{
		for(EnumGuiPosition pos : values())
			if(pos.name().equalsIgnoreCase(s))
				return pos;
		
		return null;
	}

}
