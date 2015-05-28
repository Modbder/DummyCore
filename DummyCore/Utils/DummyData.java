package DummyCore.Utils;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.2
 * @Description used to store almost any kind of variable using a special Data. When working, remember, that you can store any Object to the corresponding name.
 */
public class DummyData {
	public final String fieldName;
	public final String fieldValue;
	public DummyData(String field, String value)
	{
		fieldName = field;
		fieldValue = value;
	}
	
	public DummyData(String field, Object value)
	{
		fieldName = field;
		fieldValue = value.toString();
	}
	
	public String toString()
	{
		String ret = "";
		ret = ret.concat("||").concat(fieldName).concat(":").concat(fieldValue.toString());
		return ret;
	}
	
	public static DummyData makeNull()
	{
		return new DummyData("null","null");
	}
}
