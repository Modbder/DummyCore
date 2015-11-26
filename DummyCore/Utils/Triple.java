package DummyCore.Utils;

/**
 * The same as Pair,but a triple
 * @author modbder
 *
 */
public class Triple<T1, T2, T3> {
	
	public T1 obj1;
	public T2 obj2;
	public T3 obj3;
	
	public T1 getFirst()
	{
		return obj1;
	}
	
	public T2 getSecond()
	{
		return obj2;
	}
	
	public T3 getThird()
	{
		return obj3;
	}
	
	public Triple(T1 o1, T2 o2, T3 o3)
	{
		obj1 = o1;
		obj2 = o2;
		obj3 = o3;
	}

	@Override
	public String toString()
	{
		return obj1.toString()+","+obj2.toString()+","+obj3.toString();
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj != null && obj.toString().equals(this.toString());
	}
}
