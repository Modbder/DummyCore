package DummyCore.Utils;

public class Pair<T1, T2> {
	
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

}
