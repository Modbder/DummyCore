package DummyCore.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionProvider {
	
	public static final ReflectionProvider instance = new ReflectionProvider();
	
	private Class<?> workingWith;
	private Object accessed;
	
	private boolean isAccessing;
	private boolean hardAccess;
	
	public boolean setTo(String name, Object setTo)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			f.set(accessed, setTo);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public float getFloatFrom(String name)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			float ret = f.getFloat(accessed);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return ret;
		}
		catch(Exception e)
		{
			return Float.NaN;
		}
	}
	
	public double getDoubleFrom(String name)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			double ret = f.getDouble(accessed);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return ret;
		}
		catch(Exception e)
		{
			return Double.NaN;
		}
	}
	
	public int getIntFrom(String name)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			int ret = f.getInt(accessed);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return ret;
		}
		catch(Exception e)
		{
			return Integer.MIN_VALUE;
		}
	}
	
	public boolean getBoolFrom(String name)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			boolean ret = f.getBoolean(accessed);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return ret;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public Object getFrom(String name)
	{
		c();
		
		try
		{
			Field f = getField(name);
			boolean accessed = f.isAccessible();
			if(!accessed && hardAccess)
				f.setAccessible(true);
			
			Object ret = f.get(accessed);
			
			if(hardAccess)
				f.setAccessible(accessed);
			
			return ret;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public Object create(Class<?>[] parParams, Object[] parObj)
	{
		c();
		
		try{
			
			Constructor<?> c = getConstructor(parParams);
			boolean accessed = c.isAccessible();
			if(!accessed && hardAccess)
				c.setAccessible(true);
			
			Object ret = c.newInstance(parObj);
			
			if(hardAccess)
				c.setAccessible(accessed);
			
			return ret;
			
		}catch(Exception e){
			return null;
		}
	}
	
	public Object invoke(String name, Class<?>[] parParams, Object[] parObj)
	{
		c();
		
		try{
			
			Method m = getMethod(name,parParams);
			boolean accessed = m.isAccessible();
			if(!accessed && hardAccess)
				m.setAccessible(true);
			
			Object ret = m.invoke(accessed, parObj);
			
			if(hardAccess)
				m.setAccessible(accessed);
			
			return ret;
			
		}catch(Exception e){
			return null;
		}
	}
	
	public Constructor<?> getConstructor(Class<?>... params)
	{
		c();
		
		try{
			return workingWith.getConstructor(params);
		}catch(Exception e){
			try{
				return workingWith.getDeclaredConstructor(params);
			}catch(Exception e1){
				return null;
			}
		}
	}
	
	public Method getMethod(String name, Class<?>... params)
	{
		c();
		
		try{
			return workingWith.getMethod(name, params);
		}catch(Exception e){
			try{
				return workingWith.getDeclaredMethod(name, params);
			}catch(Exception e1){
				return null;
			}
		}
	}
	
	public Field getField(String name)
	{
		c();
		
		try{
			return workingWith.getField(name);
		}catch(Exception e){
			try{
				return workingWith.getDeclaredField(name);
			}catch(Exception e1){
				return null;
			}
		}
	}
	
	public Class<?> getSetClass()
	{
		c();
		
		return workingWith;
	}
	
	public boolean setClass(String classPath)
	{
		c();
		
		try
		{
			workingWith = Class.forName(classPath);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public boolean setClass(Object clazz)
	{
		c();

		workingWith = clazz.getClass();
		accessed = clazz;
		
		return true;
	}
	
	public void access(Object obj)
	{
		c();
		
		accessed = obj;
	}
	
	private boolean c()
	{
		if(!isAccessing)
			throw new IllegalStateException("Reflection Provider not running!");
		
		return isAccessing;
	}
	
	public void start()
	{
		if(isAccessing)
			throw new IllegalStateException("Reflection Provider already running!");
		
		isAccessing = true;
	}
	
	public void end()
	{
		if(!isAccessing)
			throw new IllegalStateException("Reflection Provider not running!");
		
		isAccessing = false;
		workingWith = null;
		accessed = null;
		hardAccess = false;
	}
	
	public void enableHardAccess(boolean b)
	{
		c();
		
		hardAccess = b;
	}
	
	public Object getCurrentObj()
	{
		c();
		
		return accessed;
	}
	
	public Object setCurrentObj(Object obj)
	{
		c();
		accessed = obj;
		return accessed;
	}

}
