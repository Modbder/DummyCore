package DummyCore.Utils;

/**
 * Is just a pair of 2 objects, duh
 * @author modbder
 */
public class Pair<T1, T2>{
	
	public T1 obj1;
	public T2 obj2;
	
	public T1 getFirst()
	{
		return obj1;
	}
	
	public T2 getSecond()
	{
		return obj2;
	}
	
	public Pair(T1 o1, T2 o2)
	{
		obj1 = o1;
		obj2 = o2;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public String toString()
	{
		return obj1.toString()+","+obj2.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj.toString().equals(this.toString());
	}
}
