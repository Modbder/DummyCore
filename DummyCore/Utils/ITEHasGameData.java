package DummyCore.Utils;

/**
 * An abstract interface to allow you to easily sync your tiles
 * @author modbder
 *
 */
public interface ITEHasGameData {
	
	/**
	 * 
	 * @return all your data about the tile in a valid DummyData string
	 */
	public abstract String getData();
	
	/**
	 * This is recieved on the client. Use the given array to restore the values you've written
	 * @param data
	 */
	public abstract void setData(DummyData[] data);
	
	/**
	 * 
	 * @return the Coord3D object representing the position of your tileEntity
	 */
	public abstract Coord3D getPosition();

}
