package DummyCore.Client;

import java.util.ArrayList;

import DummyCore.Utils.ExtendedAABB;
import DummyCore.Utils.Notifier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

/**
 * 
 * @author modbder
 * @Description
 * This is basically RenderBlocks as it was in 1.7, but instead of rendering this creates models, which are later rendered by the default render methods - this ensures compatibility
 */
public class DynamicModelBakery {

	/**
	 * Current model of the block worked with
	 */
	public final SBRHAwareModel workedWith;
	/**
	 * Not used by DC. Left for convenience
	 */
	public final FaceBakery bakery = new FaceBakery();
	/**
	 * If this is not null all render attempts will use this as the Icon. Otherwise regular calls will get the desired icon
	 */
	public Icon overrideBlockIcon;
	/**
	 * This is true if the block is being rendered in the world, false if it is actually an ItemBlock
	 */
	public boolean inWorldRendering;
	/**
	 * Current world. If inWorldRendering is false this is null
	 */
	public IBlockAccess world;
	/**
	 * Current coordinates. If inWorldRendering is false these are 0
	 */
	public int x,y,z;
	/**
	 * The render ExtendedAABB. Usually is either 0,0,0,1,1,1(full cube), or determined by the rendered block's AABB bounds
	 */
	public ExtendedAABB renderBB = new ExtendedAABB(0,0,0,1,1,1);
	/**
	 * Render offsets. Basically a GL11.glTranslated for blocks
	 */
	public double offsetX,offsetY,offsetZ;
	/**
	 * Color of the face being rendered. If this is -1 the color is default(0xffffff), else the face will be the same color as this
	 */
	public int faceTint = -1;
	/**
	 * If this is true the faces rendering will be inverted(minU and maxU will be swapped, as well as minRX and maxRX)
	 */
	public boolean inverseRender = false;
	/**
	 * If this is true faces will get added as free quads rather than face specific quads which will make default render engine to render them even if they shouldn't be rendered at the given face
	 */
	public boolean renderAllFaces = false;
	
	/**
	 * Constructs a new DynamicModelBakery for the given model. This constructor assumes that the ItemBlock is being rendered.
	 * @param offendor - the model to create
	 */
	public DynamicModelBakery(SBRHAwareModel offendor)
	{
		this(offendor,false,Minecraft.getMinecraft().theWorld,0,0,0);
	}
	
	/**
	 * Constructs a new DynamicModelBakery for the given model. This constructor is the default and assumes the block is being rendered in the world
	 * @param offendor - the model to create
	 * @param world - if the rendering occures in the world, false if the ItemBlock is being rendered
	 * @param w - the World object to render the model in
	 * @param x - the x coordinate of the block being rendered
	 * @param y - the y coordinate of the block being rendered
	 * @param z - the z coordinate of the block being rendered
	 */
	public DynamicModelBakery(SBRHAwareModel offendor, boolean world, IBlockAccess w, int x, int y, int z)
	{
		workedWith = offendor;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = w;
		inWorldRendering = world;
	}
	
	/**
	 * Unused. Returns the brightness for the given face
	 * @param face - the face to work with
	 * @return The corresponding brightness multiplier
	 */
    public float getFaceBrightness(EnumFacing face)
    {
        switch (face.ordinal())
        {
            case 2:
                return 0.5F;
            case 1:
                return 1.0F;
            case 5:
            case 6:
                return 0.8F;
            case 3:
            case 4:
                return 0.6F;
            default:
                return 0.5F;
        }
    }
	
    /**
     * Returns the face color multiplied by the default shade value
     * @param face - the face to work with
     * @return The multiplied face color
     */
    public int getFaceShadeColor(EnumFacing face)
    {
        float f = getFaceBrightness(face);
        int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
        return -16777216 | i << 16 | i << 8 | i;
    }
    
    /**
     * Clears all render offsets to 0
     */
    public void clearRenderOffsets()
    {
    	offsetX = offsetY = offsetZ = 0;
    }
    
    /**
     * Sets the offsets for the rendering
     * @param x - the x offset 
     * @param y - the y offset
     * @param z - the z offset
     */
    public void setRenderOffsets(double x, double y, double z)
    {
    	offsetX = x;
    	offsetY = y;
    	offsetZ = z;
    }
    
    /**
     * Sets the render AABB from the given block's bounds
     * @param b - the block to take the bounds from
     */
    public void setRenderBoundsFromBlock(Block b)
    {
    	if(inWorldRendering)
    		b.setBlockBoundsBasedOnState(world, new BlockPos(x,y,z));
    	renderBB = new ExtendedAABB(b.getBlockBoundsMinX(),b.getBlockBoundsMinY(),b.getBlockBoundsMinZ(),b.getBlockBoundsMaxX(),b.getBlockBoundsMaxY(),b.getBlockBoundsMaxZ());
    }
    
    /**
     * Clears the render AABB to default(0,0,0,1,1,1)
     */
    public void clearRenderBounds()
    {
    	renderBB = new ExtendedAABB(0,0,0,1,1,1);
    }
    
    /**
     * Sets the render AABB to the given coords. See {@link net.minecraft.util.AxisAlignedBB#fromBounds(double, double, double, double, double, double)}
     * @param minX - min X
     * @param minY - min Y
     * @param minZ - min Z
     * @param maxX - max X
     * @param maxY - max Y
     * @param maxZ - max Z
     */
    public void setRenderBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
    	renderBB = new ExtendedAABB(minX,minY,minZ,maxX,maxY,maxZ);
    }
    
    /**
     * Choose an Icon based on the given face
     * @param face - the face to chose the icon for
     * @return the Icon object. This might be null, but the code assumes it is not.
     */
    public Icon chooseIcon(EnumFacing face)
    {
    	return overrideBlockIcon == null ? inWorldRendering ? workedWith.interfaced.getIcon(world, x, y, z, face.ordinal()) : workedWith.interfaced.getIcon(face.ordinal(), workedWith.rendered.getMetaFromState(workedWith.blockState)) : overrideBlockIcon;
    }
    
    /**
     * Sets the override Icon to use as texture for ANY face.
     * @param icon - the Icon object to override the current texture with
     */
    public void setOverrideBlockTexture(Icon icon)
    {
    	overrideBlockIcon = icon;
    }
    
    /**
     * Clears the override Icon. You *should* call this after your rendering, however you might not.
     */
    public void clearOverrideBlockTexture()
    {
    	overrideBlockIcon = null;
    }
    
    /**
     * This checks if there currently is an override Icon for the block or not
     * @return true if there is an Icon to override the block's icon with, false otherwise
     */
    public boolean hasOverrideBlockIcon()
    {
    	return overrideBlockIcon != null;
    }
    
    /**
     * Sets the color to render the block with. Is a HEX RGB format. Example: 0xffffff is white, 0x000000 is black and 0xff0000 is bright red
     * @param color the int hex RGB color
     */
    public void setOverrideBlockColor(int color)
    {
    	faceTint = color;
    }
    
    /**
     * Clears the block's override color to use the default one(0xffffff)
     */
    public void clearOverrideBlockColor()
    {
    	faceTint = -1;
    }
    
    /**
     * Adds the horizontal(XZ) only faces with a default(0.1) offset
     */
    public void addHorizontalFacesWithOffset()
    {
    	addHorizontalFacesWithOffset(0.1);
    }
    
    /**
     * Inverts the rendering, so it is done "from the inside"(swaps the minX <-> maxX(or minZ <-> maxZ) and minU <-> maxU)
     */
    public void invertRenderPoints()
    {
    	inverseRender = !inverseRender;
    }
    
    /**
     * Clears the render inversion, so it is no longer inversed
     */
    public void clearRenderInversion()
    {
    	inverseRender = false;
    }
    
    /**
     * Disables the rendering of all faces of the block, even once which should not be rendered
     */
    public void disableRenderAllFaces()
    {
    	renderAllFaces = false;
    }
    
    /**
     * Forces the renderer to render all faces of the block, even once which should not be rendered
     */
    public void forceRenderAllFaces()
    {
    	renderAllFaces = true;
    }
    
    /**
     * Renders a block connected to another block.
     * @see {@link DummyCore.Client.IBlockConnector}
     */
    public void addConnectedBlockFaces()
    {
    	this.setRenderBounds(0, 0, 0, 1, 1, 1);
    	if(this.inWorldRendering)
    	{
    		if(workedWith.rendered instanceof IBlockConnector)
    		{
    			EnumFacing face = EnumFacing.UP;
    			IBlockConnector ibc = IBlockConnector.class.cast(workedWith.rendered);
    			BlockPos bp = new BlockPos(x,y,z);
	    		for(int i = 2; i < 6; ++i)
	    		{
	    			EnumFacing st = EnumFacing.getFront(i);

	    			if(ibc.connectsTo(world, new BlockPos(x+st.getFrontOffsetX(),y+st.getFrontOffsetY(),z+st.getFrontOffsetZ()), bp, st, workedWith.blockState))
	    			{
	    				face = st;
	    				break;
	    			}
	    		}
	    		if(face != EnumFacing.UP)
	    		{
	    			this.setRenderBounds(0, 0, 0, 1, 0.5, 1);
	    	    	double minRX = offsetX + renderBB.minX;
	    	    	double maxRX = offsetX + renderBB.maxX;
	    	    	double minRY = offsetY + renderBB.minY;
	    	    	double maxRY = offsetY + renderBB.maxY;
	    	    	double minRZ = offsetZ + renderBB.minZ;
	    	    	double maxRZ = offsetZ + renderBB.maxZ;
	    	    	
	    	    	ModelBakeryOven oven = ModelBakeryOven.instance;
	    			Icon drawn = chooseIcon(EnumFacing.WEST);
	    	    	
	    	    	double minU = drawn.getMinU();
	    	    	double maxU = drawn.getMaxU();
	    	    	double maxV = drawn.getMaxV();
	    	    	double minV = drawn.getMinV();
	    	    	
	    			oven.start(EnumFacing.WEST,faceTint);
	    	    	
	    	    	oven.addVertexWithUV(minRX, maxRY, minRZ, maxU, minV);
	    	    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, minU, minV);
	    	    	oven.addVertexWithUV(maxRX, minRY, maxRZ, minU, maxV);
	    	    	oven.addVertexWithUV(minRX, minRY, minRZ, maxU, maxV);
	    	    	
	    	    	addFace(oven.done(),EnumFacing.WEST);
	    	    	
	    	    	drawn = chooseIcon(EnumFacing.EAST);
	    	    	minU = drawn.getMinU();
	    	    	maxU = drawn.getMaxU();
	    	    	maxV = drawn.getMaxV();
	    	    	minV = drawn.getMinV();
	    	    	
	    			oven.start(EnumFacing.EAST,faceTint);
	    	    	
	    	    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, maxU, minV);
	    	    	oven.addVertexWithUV(minRX, maxRY, minRZ, minU, minV);
	    	    	oven.addVertexWithUV(minRX, minRY, minRZ, minU, maxV);
	    	    	oven.addVertexWithUV(maxRX, minRY, maxRZ, maxU, maxV);
	    	    	
	    	    	addFace(oven.done(),EnumFacing.EAST);
	    	    	
	    	    	drawn = chooseIcon(EnumFacing.NORTH);
	    	    	minU = drawn.getMinU();
	    	    	maxU = drawn.getMaxU();
	    	    	maxV = drawn.getMaxV();
	    	    	minV = drawn.getMinV();
	    	    	
	    			oven.start(EnumFacing.NORTH,faceTint);
	    	    	
	    	    	oven.addVertexWithUV(maxRX, maxRY, minRZ, maxU, minV);
	    	    	oven.addVertexWithUV(minRX, maxRY, maxRZ, minU, minV);
	    	    	oven.addVertexWithUV(minRX, minRY, maxRZ, minU, maxV);
	    	    	oven.addVertexWithUV(maxRX, minRY, minRZ, maxU, maxV);
	    	    	
	    	    	addFace(oven.done(),EnumFacing.NORTH);
	    	    	
	    	    	drawn = chooseIcon(EnumFacing.SOUTH);
	    	    	minU = drawn.getMinU();
	    	    	maxU = drawn.getMaxU();
	    	    	maxV = drawn.getMaxV();
	    	    	minV = drawn.getMinV();
	    	    	
	    			oven.start(EnumFacing.SOUTH,faceTint);
	    	    	
	    	    	oven.addVertexWithUV(minRX, maxRY, maxRZ, maxU, minV);
	    	    	oven.addVertexWithUV(maxRX, maxRY, minRZ, minU, minV);
	    	    	oven.addVertexWithUV(maxRX, minRY, minRZ, minU, maxV);
	    	    	oven.addVertexWithUV(minRX, minRY, maxRZ, maxU, maxV);
	    	    	
	    	    	addFace(oven.done(),EnumFacing.SOUTH);	   
	    	    	
	    	    	this.clearRenderBounds();
	    	    	
	    	    	drawn = ibc.getConnectionIcon(world, x, y, z);
	    	    	minU = drawn.getMinU();
	    	    	maxU = drawn.getMaxU();
	    	    	maxV = drawn.getMaxV();
	    	    	minV = drawn.getMinV();
	    	    	
	    	    	minRX = offsetX + renderBB.minX;
	    	    	maxRX = offsetX + renderBB.maxX;
	    	    	minRY = offsetY + renderBB.minY;
	    	    	maxRY = offsetY + renderBB.maxY;
	    	    	minRZ = offsetZ + renderBB.minZ;
	    	    	maxRZ = offsetZ + renderBB.maxZ;
	    	    	double medRX = (minRX+maxRX) / 2;
	    	    	double medRZ = (minRZ+maxRZ) / 2;
	    	    	
	    	    	if(face==EnumFacing.WEST || face==EnumFacing.EAST)
	    	    	{
	    	    		if(face==EnumFacing.EAST)
	    	    		{
	    	    			minU = drawn.getMaxU();
	    	    			maxU = drawn.getMinU();
	    	    		}else
	    	    		{
	    	    	    	minU = drawn.getMinU();
	    	    	    	maxU = drawn.getMaxU();
	    	    		}
	    	    		oven.start(EnumFacing.NORTH,faceTint);
	    	        	
	    	        	oven.addVertexWithUV(maxRX, maxRY, medRZ, maxU, minV);
	    	        	oven.addVertexWithUV(minRX, maxRY, medRZ, minU, minV);
	    	        	oven.addVertexWithUV(minRX, minRY, medRZ, minU, maxV);
	    	        	oven.addVertexWithUV(maxRX, minRY, medRZ, maxU, maxV);
	    	        	
	    	        	addFace(oven.done(),EnumFacing.NORTH);
	    	        	
	    	    		oven.start(EnumFacing.SOUTH,faceTint);
	    	        	
	    	        	oven.addVertexWithUV(minRX, maxRY, medRZ, minU, minV);
	    	        	oven.addVertexWithUV(maxRX, maxRY, medRZ, maxU, minV);
	    	        	oven.addVertexWithUV(maxRX, minRY, medRZ, maxU, maxV);
	    	        	oven.addVertexWithUV(minRX, minRY, medRZ, minU, maxV);
	    	        	
	    	        	addFace(oven.done(),EnumFacing.SOUTH);
	    	    	}
	    	    	
	    	    	if(face==EnumFacing.SOUTH || face==EnumFacing.NORTH)
	    	    	{
	    	    		if(face==EnumFacing.SOUTH)
	    	    		{
	    	    			minU = drawn.getMaxU();
	    	    			maxU = drawn.getMinU();
	    	    		}else
	    	    		{
	    	    	    	minU = drawn.getMinU();
	    	    	    	maxU = drawn.getMaxU();
	    	    		}
	    	    		oven.start(EnumFacing.WEST,faceTint);
	    	        	
	    	        	oven.addVertexWithUV(medRX, maxRY, minRZ, minU, minV);
	    	        	oven.addVertexWithUV(medRX, maxRY, maxRZ, maxU, minV);
	    	        	oven.addVertexWithUV(medRX, minRY, maxRZ, maxU, maxV);
	    	        	oven.addVertexWithUV(medRX, minRY, minRZ, minU, maxV);
	    	        	
	    	        	addFace(oven.done(),EnumFacing.WEST);
	    	        	
	    	    		oven.start(EnumFacing.EAST,faceTint);
	    	        	
	    	        	oven.addVertexWithUV(medRX, maxRY, maxRZ, maxU, minV);
	    	        	oven.addVertexWithUV(medRX, maxRY, minRZ, minU, minV);
	    	        	oven.addVertexWithUV(medRX, minRY, minRZ, minU, maxV);
	    	        	oven.addVertexWithUV(medRX, minRY, maxRZ, maxU, maxV);
	    	        	
	    	        	addFace(oven.done(),EnumFacing.EAST);
	    	    	}
	    	    	
	    		}else
	    			this.addCrossedSquares();
    		}else
    			this.addCrossedSquares();
    	}else
    		this.addCrossedSquares();
    	this.clearRenderBounds();
    }
    
    /**
     * Renders all faces, but offsets the horizontal(XZ) by a default amount(0.0625, or 1/16)
     */
    public void addFacesWithOffsetOnHorizontalAxis()
    {
    	addFacesWithOffsetOnHorizontalAxis(0.0625D);
    }
    
    /**
     * Renders all faces, but offsets the horizontal(XZ) by a specified amount
     * @param offset - how much should the faces on the horizontal axis be offset
     */
    public void addFacesWithOffsetOnHorizontalAxis(double offset)
    {
    	this.addFaceYNeg();
    	this.addFaceYPos();
    	this.setRenderBounds(0, 0, 0, 1, 1, 1);
    	this.setRenderOffsets(offset, 0, 0);
    	this.addFaceXNeg();
    	this.setRenderOffsets(-offset, 0, 0);
    	this.addFaceXPos();
    	this.setRenderOffsets(0, 0, offset);
    	this.addFaceZNeg();
    	this.setRenderOffsets(0, 0, -offset);
    	this.addFaceZPos();
    	
    	this.clearRenderBounds();
    	this.clearRenderOffsets();
    }
    
    /**
     * Renders only the horizontal(XZ) faces of a block ofset by a specified amount
     * @param offset - how much should the faces on the horizontal axis be offset
     */
    public void addHorizontalFacesWithOffset(double offset)
    {
    	this.setRenderBounds(0, 0, 0, 1, 1, 1);
    	this.setRenderOffsets(offset, 0, 0);
    	this.addFaceXNeg();
    	this.setRenderOffsets(-offset, 0, 0);
    	this.addFaceXPos();
    	this.setRenderOffsets(0, 0, offset);
    	this.addFaceZNeg();
    	this.setRenderOffsets(0, 0, -offset);
    	this.addFaceZPos();
    	
    	invertRenderPoints();
    	this.setRenderOffsets(offset, 0, 0);
    	this.addFaceXNeg();
    	this.setRenderOffsets(-offset, 0, 0);
    	this.addFaceXPos();
    	this.setRenderOffsets(0, 0, offset);
    	this.addFaceZNeg();
    	this.setRenderOffsets(0, 0, -offset);
    	this.addFaceZPos();
    	invertRenderPoints();
    	
    	this.clearRenderBounds();
    	this.clearRenderOffsets();
    }
    
    /**
     * Renders all horizontal(XZ) faces of a block in a cross(+).
     */
    public void addHorizontalCrossedSquares()
    {
    	Icon drawn = chooseIcon(EnumFacing.UP);
    	
		double minU = drawn.getMinU();
		double maxU = drawn.getMaxU();
		double minV = drawn.getMinV();
		double maxV = drawn.getMaxV();
		
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	if(!this.inWorldRendering && !this.workedWith.isGui3d())
    	{
    		minRX = offsetX;
    		maxRX = offsetX + 1;
    		minRY = offsetY;
    		maxRY = offsetY + 1;
    		minRZ = offsetZ;
    		maxRZ = offsetZ + 1;
    	}
    	double medRX = (minRX+maxRX) / 2;
    	double medRZ = (minRZ+maxRZ) / 2;
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.WEST,faceTint);
    	
    	oven.addVertexWithUV(medRX, maxRY, minRZ, minU, minV);
    	oven.addVertexWithUV(medRX, maxRY, maxRZ, maxU, minV);
    	oven.addVertexWithUV(medRX, minRY, maxRZ, maxU, maxV);
    	oven.addVertexWithUV(medRX, minRY, minRZ, minU, maxV);
    	
    	addFace(oven.done(),EnumFacing.WEST);
    	
		oven.start(EnumFacing.EAST,faceTint);
    	
    	oven.addVertexWithUV(medRX, maxRY, maxRZ, maxU, minV);
    	oven.addVertexWithUV(medRX, maxRY, minRZ, minU, minV);
    	oven.addVertexWithUV(medRX, minRY, minRZ, minU, maxV);
    	oven.addVertexWithUV(medRX, minRY, maxRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.EAST);
    	
		oven.start(EnumFacing.NORTH,faceTint);
    	
    	oven.addVertexWithUV(maxRX, maxRY, medRZ, maxU, minV);
    	oven.addVertexWithUV(minRX, maxRY, medRZ, minU, minV);
    	oven.addVertexWithUV(minRX, minRY, medRZ, minU, maxV);
    	oven.addVertexWithUV(maxRX, minRY, medRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.NORTH);
    	
		oven.start(EnumFacing.SOUTH,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, medRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, maxRY, medRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, minRY, medRZ, maxU, maxV);
    	oven.addVertexWithUV(minRX, minRY, medRZ, minU, maxV);
    	
    	addFace(oven.done(),EnumFacing.SOUTH);
    }
    
    /**
     * Renders all horizontal(XZ) faces of a block in an X pattern(x).
     */
    public void addCrossedSquares()
    {
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(!this.inWorldRendering && !this.workedWith.isGui3d())
    	{
    		minRX = offsetX;
    		maxRX = offsetX + 1;
    		minRY = offsetY;
    		maxRY = offsetY + 1;
    		minRZ = offsetZ;
    		maxRZ = offsetZ + 1;
    	}
		
		ModelBakeryOven oven = ModelBakeryOven.instance;
		
		Icon drawn = chooseIcon(EnumFacing.WEST);
    	
    	double minU = drawn.getMinU();
    	double maxU = drawn.getMaxU();
    	double maxV = drawn.getMaxV();
    	double minV = drawn.getMinV();
		
		oven.start(EnumFacing.WEST,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, minRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, minRY, maxRZ, minU, maxV);
    	oven.addVertexWithUV(minRX, minRY, minRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.WEST);
    	
    	drawn = chooseIcon(EnumFacing.EAST);
    	minU = drawn.getMinU();
    	maxU = drawn.getMaxU();
    	maxV = drawn.getMaxV();
    	minV = drawn.getMinV();
    	
		oven.start(EnumFacing.EAST,faceTint);
    	
    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, maxU, minV);
    	oven.addVertexWithUV(minRX, maxRY, minRZ, minU, minV);
    	oven.addVertexWithUV(minRX, minRY, minRZ, minU, maxV);
    	oven.addVertexWithUV(maxRX, minRY, maxRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.EAST);
    	
    	drawn = chooseIcon(EnumFacing.NORTH);
    	minU = drawn.getMinU();
    	maxU = drawn.getMaxU();
    	maxV = drawn.getMaxV();
    	minV = drawn.getMinV();
    	
		oven.start(EnumFacing.NORTH,faceTint);
    	
    	oven.addVertexWithUV(maxRX, maxRY, minRZ, maxU, minV);
    	oven.addVertexWithUV(minRX, maxRY, maxRZ, minU, minV);
    	oven.addVertexWithUV(minRX, minRY, maxRZ, minU, maxV);
    	oven.addVertexWithUV(maxRX, minRY, minRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.NORTH);
    	
    	drawn = chooseIcon(EnumFacing.SOUTH);
    	minU = drawn.getMinU();
    	maxU = drawn.getMaxU();
    	maxV = drawn.getMaxV();
    	minV = drawn.getMinV();
    	
		oven.start(EnumFacing.SOUTH,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, maxRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, maxRY, minRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, minRY, minRZ, minU, maxV);
    	oven.addVertexWithUV(minRX, minRY, maxRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.SOUTH);
    }

    /**
     * Renders a standard 6 faces cube with a given offset and AABB
     */
    public void addCube()
    {
    	addFaceYNeg();
    	addFaceYPos();
    	
    	addFaceZNeg();
    	addFaceZPos();
    	
    	addFaceXNeg();
    	addFaceXPos();
    }
    
    /**
     * Renders an EAST(positive on the X axis) face of a block
     */
    public void addFaceXPos()
    {
    	Icon drawn = chooseIcon(EnumFacing.EAST);
    	
    	double minU = drawn.getInterpolatedU(renderBB.minZ * 16.0D);
    	double maxU = drawn.getInterpolatedU(renderBB.maxZ * 16.0D);
    	double maxV = drawn.getInterpolatedV(16.0D - renderBB.maxY * 16.0D);
    	double minV = drawn.getInterpolatedV(16.0D - renderBB.minY * 16.0D);
    	
    	if(renderBB.minZ < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minY < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxZ > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxY > 1)
    		maxV = drawn.getMaxV();	
    	
    	//double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRZ;
    		minRZ = maxRZ;
    		maxRZ = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.EAST,faceTint);
    	
    	oven.addVertexWithUV(maxRX, minRY, maxRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, minRY, minRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, maxRY, minRZ, maxU, maxV);
    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, minU, maxV);
    	
    	addFace(oven.done(),EnumFacing.EAST);
    }
    
    /**
     * Renders an WEST(negative on the X axis) face of a block
     */
    public void addFaceXNeg()
    {
    	Icon drawn = chooseIcon(EnumFacing.WEST);
    	
    	double minU = drawn.getInterpolatedU(renderBB.minZ * 16.0D);
    	double maxU = drawn.getInterpolatedU(renderBB.maxZ * 16.0D);
    	double maxV = drawn.getInterpolatedV(16.0D - renderBB.maxY * 16.0D);
    	double minV = drawn.getInterpolatedV(16.0D - renderBB.minY * 16.0D);
    	
    	if(renderBB.minZ < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minY < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxZ > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxY > 1)
    		maxV = drawn.getMaxV();	
    	
    	double minRX = offsetX + renderBB.minX;
    	//double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRZ;
    		minRZ = maxRZ;
    		maxRZ = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.WEST,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, maxRZ, maxU, maxV);
    	oven.addVertexWithUV(minRX, maxRY, minRZ, minU, maxV);
    	oven.addVertexWithUV(minRX, minRY, minRZ, minU, minV);
    	oven.addVertexWithUV(minRX, minRY, maxRZ, maxU, minV);
    	
    	addFace(oven.done(),EnumFacing.WEST);
    }
    
    /**
     * Renders an SOUTH(positive on the Z axis) face of a block
     */
    public void addFaceZPos()
    {
    	Icon drawn = chooseIcon(EnumFacing.SOUTH);
    	
    	double minU = drawn.getInterpolatedU(renderBB.minX * 16.0D);
    	double maxU = drawn.getInterpolatedU(renderBB.maxX * 16.0D);
    	double maxV = drawn.getInterpolatedV(16.0D - renderBB.maxY * 16.0D);
    	double minV = drawn.getInterpolatedV(16.0D - renderBB.minY * 16.0D);
    	
    	if(renderBB.minX < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minY < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxX > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxY > 1)
    		maxV = drawn.getMaxV();	
    	
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	//double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRX;
    		minRX = maxRX;
    		maxRX = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.SOUTH,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, maxRZ, minU, maxV);
    	oven.addVertexWithUV(minRX, minRY, maxRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, minRY, maxRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, maxU, maxV);
    	
    	addFace(oven.done(),EnumFacing.SOUTH);
    }
    
    /**
     * Renders an NORTH(negative on the Z axis) face of a block
     */
    public void addFaceZNeg()
    {
    	Icon drawn = chooseIcon(EnumFacing.NORTH);
    	double minU = drawn.getInterpolatedU(renderBB.minX * 16.0D);
    	double maxU = drawn.getInterpolatedU(renderBB.maxX * 16.0D);
    	double maxV = drawn.getInterpolatedV(16.0D - renderBB.maxY * 16.0D);
    	double minV = drawn.getInterpolatedV(16.0D - renderBB.minY * 16.0D);
    	
    	if(renderBB.minX < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minY < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxX > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxY > 1)
    		maxV = drawn.getMaxV();	
    	
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	//double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRX;
    		minRX = maxRX;
    		maxRX = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.NORTH,faceTint);
    	
    	oven.addVertexWithUV(minRX, maxRY, minRZ, maxU, maxV);
    	oven.addVertexWithUV(maxRX, maxRY, minRZ, minU, maxV);
    	oven.addVertexWithUV(maxRX, minRY, minRZ, minU, minV);
    	oven.addVertexWithUV(minRX, minRY, minRZ, maxU, minV);
    	
    	addFace(oven.done(),EnumFacing.NORTH);
    }
    
    /**
     * Renders an UP(positive on the Y axis) face of a block
     */
    public void addFaceYPos()
    {
    	Icon drawn = chooseIcon(EnumFacing.UP);
    	double minU = drawn.getInterpolatedU(renderBB.minX * 16);
    	double maxU = drawn.getInterpolatedU(renderBB.maxX * 16);
    	double minV = drawn.getInterpolatedV(renderBB.minZ * 16);
    	double maxV = drawn.getInterpolatedV(renderBB.maxZ * 16);
    	
    	if(renderBB.minX < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minZ < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxX > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxZ > 1)
    		maxV = drawn.getMaxV();	
    	
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	//double minRY = offsetY + renderBB.minY;
    	double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRX;
    		minRX = maxRX;
    		maxRX = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.UP,faceTint);
    	
    	oven.addVertexWithUV(maxRX, maxRY, maxRZ, maxU, maxV);
    	oven.addVertexWithUV(maxRX, maxRY, minRZ, maxU, minV);
    	oven.addVertexWithUV(minRX, maxRY, minRZ, minU, minV);
    	oven.addVertexWithUV(minRX, maxRY, maxRZ, minU, maxV);
    	
    	addFace(oven.done(),EnumFacing.UP);
    }
	
    /**
     * Renders a DOWN(negative on the Y axis) face of a block
     */
    public void addFaceYNeg()
    {
    	Icon drawn = chooseIcon(EnumFacing.DOWN);
    	double minU = drawn.getInterpolatedU(renderBB.minX * 16);
    	double maxU = drawn.getInterpolatedU(renderBB.maxX * 16);
    	double minV = drawn.getInterpolatedV(renderBB.minZ * 16);
    	double maxV = drawn.getInterpolatedV(renderBB.maxZ * 16);
    	
    	if(renderBB.minX < 0)
    		minU = drawn.getMinU();
    	if(renderBB.minZ < 0)
    		minV = drawn.getMinV();	
    	if(renderBB.maxX > 1)
    		maxU = drawn.getMaxU();
    	if(renderBB.maxZ > 1)
    		maxV = drawn.getMaxV();	
    	
    	double minRX = offsetX + renderBB.minX;
    	double maxRX = offsetX + renderBB.maxX;
    	double minRY = offsetY + renderBB.minY;
    	//double maxRY = offsetY + renderBB.maxY;
    	double minRZ = offsetZ + renderBB.minZ;
    	double maxRZ = offsetZ + renderBB.maxZ;
    	
    	if(inverseRender)
    	{
    		double d = minRX;
    		minRX = maxRX;
    		maxRX = d;
    		d = minU;
    		minU = maxU;
    		maxU = d;
    	}
    	
		ModelBakeryOven oven = ModelBakeryOven.instance;
		oven.start(EnumFacing.DOWN,faceTint);
    	
    	oven.addVertexWithUV(minRX, minRY, maxRZ, maxU, maxV);
    	oven.addVertexWithUV(minRX, minRY, minRZ, maxU, minV);
    	oven.addVertexWithUV(maxRX, minRY, minRZ, minU, minV);
    	oven.addVertexWithUV(maxRX, minRY, maxRZ, minU, maxV);
    	
    	addFace(oven.done(),EnumFacing.DOWN);
    	
    }   
    
    /**
     * Adds a formed vertexData array to the model
     * @param vertexData - the formed data
     * @param face - the side to add for
     * @see {@link DummyCore.Client.ModelBakeryOven}
     */
	public void addFace(int[] vertexData, EnumFacing face)
	{
		if(renderAllFaces)
		{
			addQuad(vertexData);
		}else
		{
			BakedQuad bq = new BakedQuad(vertexData, faceTint, face);
			workedWith.faces.get(face).add(bq);
		}
	}
	
	/**
	 * Adds a formed vertexData array to the model. The difference between this method and the other one is that adding vertexes using this one will make the model's faces always be rendered
	 * @param vertexData - the formed data
	 * @see {@link DummyCore.Client.ModelBakeryOven}
	 */
	public void addQuad(int[] vertexData)
	{
		BakedQuad bq = new BakedQuad(vertexData, faceTint, EnumFacing.UP);
		workedWith.quads.add(bq);
	}
	
	/**
	 * A generic function that does a recursive search through all ISBRH to render a given ItemStack. 
	 * This should not be called by a developer, since this is considered to be an internal DC's method.
	 * However it is left public for possible render haxes.
	 * @param stk - the ItemStack to render
	 */
	public void doBakeModelForIS(ItemStack stk)
	{
		int id = workedWith.interfaced.getDCRenderID();
		if(!RenderAccessLibrary.renderers.containsKey(id))
			id = 0;
		
		ArrayList<ISimpleBlockRenderingHandler> isbrhal = RenderAccessLibrary.renderers.get(id);
		for(ISimpleBlockRenderingHandler isbrh : isbrhal)
		{	
			try
			{
				isbrh.renderInventoryBlock(stk, this, workedWith);
			}
			catch(Exception e)
			{
				Notifier.notifyErrorCustomMod("DCRenderLibrary","Catched an exception whlist trying to render "+workedWith.rendered+" as IS ["+stk+"]"+" on "+isbrh);
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * A generic function that does a recursive search through all ISBRH to render a given Block. 
	 * This should not be called by a developer, since this is considered to be an internal DC's method.
	 * However it is left public for possible render haxes.
	 * @param state - the BlockState of the block to render(metadata)
	 * @param world - the world the rendering is in
	 * @param pos - current position of a block
	 */
	public void doBakeModelInWorld(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		int id = workedWith.interfaced.getDCRenderID();
		if(!RenderAccessLibrary.renderers.containsKey(id))
			id = -1;
		
		ArrayList<ISimpleBlockRenderingHandler> isbrhal = RenderAccessLibrary.renderers.get(id);
		for(ISimpleBlockRenderingHandler isbrh : isbrhal)
		{	
			try
			{
				isbrh.renderWorldBlock(world, workedWith.rendered, pos, this, workedWith);
			}
			catch(Exception e)
			{
				Notifier.notifyErrorCustomMod("DCRenderLibrary","Catched an exception whlist trying to render "+workedWith.rendered+" at ["+pos.getX()+","+pos.getY()+","+pos.getZ()+"] in "+world+" on "+isbrh);
				e.printStackTrace();
				continue;
			}
		}
	}
	
}
