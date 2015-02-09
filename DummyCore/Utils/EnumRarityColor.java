package DummyCore.Utils;

public enum EnumRarityColor {
	BROKEN("8","Broken"),
	COMMON("f","Common"),
	GOOD("a","Good"),
	UNCOMMON("2","Uncommon"),
	RARE("9","Rare"),
	UNIQUE("d","Unique"),
	EPIC("e","Epic"),
	LEGENDARY("6","Legendary"),
	EXCEPTIONAL("b","Exceptional"),
	PERFECT("3","Perfect"),
	ULTIMATE("c","Ultimate"),
	TURQUOISE("4","Turquoise"),
	;
	EnumRarityColor(String s, String s1)
	{
		value = s;
		name = s1;
	}
	
	private String value;
	private String name;
	
	public String getRarityColor()
	{
		String ret = new String();
		ret += "\247";
		ret+=value;
		return ret;
	}
	
	public String getName()
	{
		return name;
	}
	
	public static EnumRarityColor getColorByHex(String hex)
	{
		EnumRarityColor retColor = BROKEN;
		for(int i = 0; i < values().length;++i)
		{
			EnumRarityColor color = values()[i];
			if(color.value.equalsIgnoreCase(hex))
				return color;
		}
		return retColor;
	}

}
