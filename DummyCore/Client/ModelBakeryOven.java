package DummyCore.Client;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * A simple utility to bake vertexData for the models
 * @author modbder
 *
 */
public class ModelBakeryOven {

	public int[] data;
	public int quad = 0;
	public int shadeColor = -1;
	public int uselessInt = 0;
	public EnumFacing workedWith;
	public boolean swapRB;
	public short millisWaited;
	
	public static final ModelBakeryOven instance = new ModelBakeryOven();
	public static Thread caller;
	
	/**
	 * Starts the drawing for the given face and without any custom color
	 * @param face - the face to draw on
	 */
	public void start(EnumFacing face)
	{
		this.start(face,-1);
	}
	
	/**
	 * Starts the drawing for the given face and without any custom color
	 * @param face - the face to draw on
	 */
	public void start(EnumFacing face, int shadeColor)
	{
		this.start(face,shadeColor,false);
	}
	
	/**
	 * Starts the drawing for the given face and with custom color
	 * @param face - the face to draw on
	 * @param shadeColor - the color to draw with
	 */
	public void start(EnumFacing face, int shadeColor, boolean swapRB)
	{
		/*
		 * So, the ModelbakeryOven's instance is a static final var, meaning that multiple threads can cause concurrent actions over it
		 * That is fine, since the Oven's function is pure math, and takes very little nanos to process
		 * However, if there are a lot of complex objects rendering at the same time(just a lot of blocks rendered by DC in one place)...
		 * Well, due to concurrency there can be glitches.
		 * So this implementation makes sure there is no concurrency. It basically locks the oven until one thread finishes using it before allowing other thread
		 * Since it locks the thread for 1 milli it is still very fast, even when it caches concurrency.
		 * However, we have to make sure that it is not locked due to a bad programming error...
		 * Since who knows what could happen...
		 * Well, I think that if it takes more than a second for a thread to generate math...
		 * It is either a very bad error, or an extremely complex math
		 * Either way if that happens - I just crash the game
		 * Since that means that generating a model of a block took at least a full second!
		 */
		//===========================================================================================================//
		if(caller == null || caller == Thread.currentThread()){
			caller = Thread.currentThread();
		}else{
			while(caller != null){
				try{Thread.sleep(1L);}catch(Exception e){e.printStackTrace();}
				++millisWaited;
				if(millisWaited > 1000L)
					throw new IllegalStateException("Something took too much time using the ModelBakeryOven!");
			}
		}
		//===========================================================================================================//
		workedWith = face;
		quad = 0;
		this.shadeColor = shadeColor;
		this.swapRB = swapRB;
		data = new int[28];
	}
	
	/**
	 * Internal. Fix for faces(in vanilla different faces of a block have different brightness multiplier)
	 * @param face
	 * @return
	 */
    public float getFaceBrightness(EnumFacing face)
    {
        switch (face.ordinal())
        {
            case 0:
                return 0.5F;
            case 1:
                return 1.0F;
            case 3:
            case 2:
                return 0.8F;
            case 4:
            case 5:
                return 0.6F;
            default:
                return 0.5F;
        }
    }
	
    /**
	 * Internal. Fix for faces(in vanilla different faces of a block have different brightness multiplier)
	 * @param face
	 * @return
     */
    public int getFaceShadeColor(EnumFacing face)
    {
        float f = getFaceBrightness(face);
        int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }
    
    /**
	 * Internal. For some reason R and B are swapped in the hex representing the color.
	 * @param face
	 * @return
     */
    public int getFaceSwappedColor(EnumFacing face, int color)
    {
    	if(color == 0xffffff)
    		return 0xffffff;
    	
    	if(color == 0xffffffff)
    		return 0xffffffff;
    	
    	int hA = (color & 0xFF000000) >> 24;
        if(hA == 0)
        	hA = 255;
    	int hB = (color & 0xFF0000) >> 16;
        int hG = (color & 0xFF00) >> 8;
        int hR = (color & 0xFF);
    	return (hA << 24) + (hR << 16) + (hG << 8) + hB;
    }
	
    /**
     * Adds a vertex with UV(the same format as Tesselator) to the vertex data
     * @param x - the x
     * @param y - the y
     * @param z - the z
     * @param u - the u
     * @param v - the v
     * @see {@link net.minecraft.client.renderer.WorldRenderer}
     */
	public void addVertexWithUV(double x, double y, double z, double u, double v)
	{
		int l = quad*7;
		data[l] = Float.floatToRawIntBits((float) x);
		data[l+1] = Float.floatToRawIntBits((float) y);
		data[l+2] = Float.floatToRawIntBits((float) z);
		data[l+3] = shadeColor == -1 ? getFaceShadeColor(workedWith) : swapRB ? getFaceSwappedColor(workedWith,shadeColor) : shadeColor;
		data[l+4] = Float.floatToRawIntBits((float) u);
		data[l+5] = Float.floatToRawIntBits((float) v);
		data[l+6] = uselessInt;
		if(quad < 3)
			++quad;
	}
	
	/**
	 * Compresses and fixes all drawn vertexes into an understandable MC vertex array for the model
	 * @return 
	 */
	public int[] done()
	{
		int[] vertexData = data.clone();
		ForgeHooksClient.fillNormal(vertexData, workedWith);
		caller = null;
		millisWaited = 0;
		return vertexData;
	}
}
