package DummyCore.Utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GIFImage
{
	public int frames;
	public BufferedImage[] images;
	public int[] imagesGLIDs;
	
	public GIFImage(ResourceLocation texture)
	{
		InputStream is = null;
		try
		{
			//Start of Forge's code
			is = Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(texture);
			ImageInputStream stream = ImageIO.createImageInputStream(is);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if(!readers.hasNext()) throw new IOException("No suitable reader found for image" + texture);
            ImageReader reader = readers.next();
            reader.setInput(stream);
            frames = reader.getNumImages(true);
            //End of Forge's code
            
            BufferedImage[] images = new BufferedImage[frames];
            imagesGLIDs = new int[frames];
            for(int i = 0; i < frames; i++)
                images[i] = reader.read(i);
               
            reader.dispose();
               
            for(int i = 0; i < frames; i++)
            	imagesGLIDs[i] = loadTexture(images[i]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}
		
	public void drawOnScreen(int frame, double minU, double minV, double maxU, double maxV, int sizeX, int sizeY)
	{
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, imagesGLIDs[frame]);
    	GL11.glBegin(GL11.GL_QUADS);
        {
        	GL11.glTexCoord2d(minU, minV);
           	GL11.glVertex2f(0, 0);
              
           	GL11.glTexCoord2d(maxU, minV);
            GL11.glVertex2f(sizeX, 0);
              
            GL11.glTexCoord2d(maxU, maxV);
            GL11.glVertex2f(sizeX, sizeY);
              
            GL11.glTexCoord2d(minU, maxV);
            GL11.glVertex2f(0, sizeY);
        }
        GL11.glEnd();
	}		
	
	public static final int PIXEL_FORMAT_RGBA = 4;
	private int loadTexture(BufferedImage image)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
	    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
	    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * PIXEL_FORMAT_RGBA);
        
	    for(int y = 0; y < image.getHeight(); y++){
	        for(int x = 0; x < image.getWidth(); x++){
	            int pixel = pixels[y * image.getWidth() + x];
	            buffer.put((byte) ((pixel >> 16) & 0xFF));
	            buffer.put((byte) ((pixel >> 8) & 0xFF));
	            buffer.put((byte) (pixel & 0xFF));
	            buffer.put((byte) ((pixel >> 24) & 0xFF));
	        }
	    }
	    
		buffer.flip();
	    int textureID = GL11.glGenTextures();
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	        
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	        
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	    return textureID;
	}
	
}