package DummyCore.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.2
 * @Description used to store almost any kind of variable in a single String instance.
 */
public class DataStorage {
	private static String dataString = "";
	
	/**
	 * Adds the given DummyData to the data string
	 * @version From DummyCore 1.2
	 * @param data - the data to be added to the string
	 */
	public static void addDataToString(DummyData data)
	{
		dataString = dataString.concat(data.toString());
	}
	
	/**
	 * Returns the data string, with all data written to it. Also resets the string, so this should be used only once, after you have stored all your data.
	 * @version From DummyCore 1.2
	 * @return Correctly formated DataString
	 */
	public static String getDataString()
	{
		String ret = dataString;
		dataString = "";
		return ret;
	}
	
	/**
	 * Used to get your DummyData from the correctly formated string.
	 * @version From DummyCore 1.2
	 * @param s - the string to extract data from
	 * @return - An array of all data from the given string
	 */
	public static DummyData[] parseData(String s)
	{
		String field = "";
		Object value = null;
		List<DummyData> data = new ArrayList<DummyData>();
		for(int i = 0; i < s.length(); ++i)
		{
			if(i+2 < s.length() && s.substring(i, i+2).contains("||"))
			{
				int size = 0;
				ForSize:for(int i1 = i; i1 < s.length();++i1)
				{
					if(s.charAt(i1) == ':')
						break ForSize;
					++size;
				}
				field = s.substring(i+2, i+size);
			}
			if(i+1 < s.length() && s.substring(i, i+1).contains(":"))
			{
				int size = 0;
				ForSize:for(int i1 = i; i1 < s.length();++i1)
				{
					if(s.charAt(i1) == '|')
						break ForSize;
					++size;
				}
				value = s.substring(i+1, i+size);
			}
			if(field != "" && value != null)
			{
				DummyData date = new DummyData(field,value);
				data.add(date);
				field = "";
				value = null;
			}
		}
		DummyData[] ret = new DummyData[data.size()];
		for(int i = 0; i < ret.length; ++i)
		{
			ret[i] = data.get(i);
		}
		return ret;
	}

}
