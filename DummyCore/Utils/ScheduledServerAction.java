package DummyCore.Utils;

/**
 * This is a simple Runnable-like class which is used for scheduled actions
 * @author modbder
 *
 */
public abstract class ScheduledServerAction 
{
	public int actionTime;
	
	/**
	 * Creates an action with a given time delay
	 * @param time
	 */
	public ScheduledServerAction(int time)
	{
		actionTime = time;
	}
	
	/**
	 * Runnable.run, anyone? The same.
	 */
	public abstract void execute();
}
