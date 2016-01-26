package DummyCore.Utils;

import java.util.Random;

import org.lwjgl.opengl.GL11;

/**
 * 
 * @author Modbder
 * @version From DummyCore 1.9
 * @Description used to create a lightning graphical effect between 2 points.
 */
public class Lightning {
	
	public Random rnd;
	public Coord3D[] lightningVecsStart;
	public Coord3D[] lightningVecsEnd;
	public Coord3D start;
	public Coord3D end;
	public float renderTicksExisted;
	public float[] getLightningColor = new float[3];
	
	/**
	 * Creates a lightning
	 * @param rand - the random on which your lightning will be built upon.
	 * @param begin - the beginning point of the lightning. This NEVER should relate to coordinates in the world! Instead, it can be 0,0,0, if you want the lightning to be created from the bottom corner of a block, for example.
	 * @param stop - the end point of the lightning. This NEVER should relate to coordinates in the world! Instead, it can be 0,10,0, if you want the lightning to go 10 blocks higher.
	 * @param curve - the amount of curving in your lightning. Usually somewhat around 0.3F if you want the lightning to look realistic. If you put 0.0F it will be a straight line, and 1.0F can lead to weird stuff.
	 * @param color - the color array, representing the color of the lightning.
	 */
	public Lightning(Random rand, Coord3D begin, Coord3D stop,float curve, float... color)
	{
		rnd = rand;
		start = begin;
		end = stop;
		getLightningColor = color;
		curve(curve);
	}
	
	//Internal
	private void curve(float factor)
	{
		float distX = end.x-start.x;
		float distY = end.y-start.y;
		float distZ = end.z-start.z;
		float genDistance = (float) Math.sqrt(distX*distX+distY*distY+distZ*distZ);
		lightningVecsStart = new Coord3D[64];
		lightningVecsEnd = new Coord3D[64];
		generateLightningBetween2Points(start, end, 0, 8, factor);
		generateLightningBetween2Points(lightningVecsStart[3], new Coord3D(end.x*(factor*10),end.y,end.z*(factor*10)), 8, 4, factor*3);
		int genIndex = 0;
		for(int i = 0; i < 12; ++i)
		{
			int rndSteps = rnd.nextInt(2);
			genIndex += (1+rndSteps);
			generateLightningBetween2Points(lightningVecsStart[i], new Coord3D(lightningVecsStart[i].x+MathUtils.randomFloat(rnd)*(genDistance/4), lightningVecsStart[i].y+MathUtils.randomFloat(rnd)*(genDistance/4),lightningVecsStart[i].z+MathUtils.randomFloat(rnd)*(genDistance/4)),12+i,1+rndSteps,factor/2);
		}
		for(int i = 12; i < 12+genIndex; ++i)
		{
			if(lightningVecsStart[i] != null)
			generateLightningBetween2Points(lightningVecsStart[i], new Coord3D(lightningVecsStart[i].x+MathUtils.randomFloat(rnd)*(genDistance/8), lightningVecsStart[i].y+MathUtils.randomFloat(rnd)*(genDistance/8),lightningVecsStart[i].z+MathUtils.randomFloat(rnd)*(genDistance/8)),12+genIndex+i,0,factor);
		}
	}
	
	//Internal
	private void generateLightningBetween2Points(Coord3D from, Coord3D to,int beginVecIndex, int steps, float curve)
	{
		float distX = to.x-from.x;
		float distY = to.y-from.y;
		float distZ = to.z-from.z;
		for(int i = 0; i < steps; ++i)
		{
			if(i == 0)
			{
				lightningVecsStart[beginVecIndex+i] = from;
				lightningVecsEnd[beginVecIndex+i] = new Coord3D(lightningVecsStart[beginVecIndex+i].x+distX/steps+MathUtils.randomFloat(rnd)*curve,lightningVecsStart[beginVecIndex+i].y+distY/steps+MathUtils.randomFloat(rnd)*curve,lightningVecsStart[beginVecIndex+i].z+distZ/steps+MathUtils.randomFloat(rnd)*curve);
			}else
			{
				lightningVecsStart[beginVecIndex+i] = lightningVecsEnd[beginVecIndex+i-1];
				lightningVecsEnd[beginVecIndex+i] = new Coord3D(lightningVecsStart[beginVecIndex+i].x+distX/steps+MathUtils.randomFloat(rnd)*curve,lightningVecsStart[beginVecIndex+i].y+distY/steps+MathUtils.randomFloat(rnd)*curve,lightningVecsStart[beginVecIndex+i].z+distZ/steps+MathUtils.randomFloat(rnd)*curve);
			}
		}
	
	}
	
	/**
	 * Use this anywhere in your render code to actually render the lightning.
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTicks
	 */
	public void render(double x, double y, double z, float partialTicks)
	{
		if(renderTicksExisted >= this.lightningVecsStart.length)
			renderTicksExisted = this.lightningVecsStart.length;
        GL11.glPushMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glTranslated(x, y, z);
        
		GL11.glLineWidth(1);
		GL11.glPushMatrix();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(this.getLightningColor[0],this.getLightningColor[1],this.getLightningColor[2],0.8F);
        
		for(int i = 0; i < renderTicksExisted; ++i)
			if(this.lightningVecsStart[i] != null)
	            this.renderBeam(lightningVecsStart[i], lightningVecsEnd[i], 1F);
		
        GL11.glEnd();
        
        GL11.glLineWidth(3);
        
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(1,1,1,0.2F);
        
		for(int i = 0; i < renderTicksExisted; ++i)
			if(this.lightningVecsStart[i] != null)
	            this.renderBeam(lightningVecsStart[i], lightningVecsEnd[i], 0.8F);
        
		GL11.glEnd();
		GL11.glColor4d(1,1,1,1);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
		
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
	}
	
	public void renderBeam(Coord3D begin, Coord3D stop, float type)
	{
        GL11.glVertex3d(begin.x, begin.y, begin.z);
        GL11.glVertex3d(stop.x, stop.y, stop.z);
	}


}
