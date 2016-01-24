package DummyCore.Utils;

import java.util.ArrayList;
import java.util.Hashtable;

import org.lwjgl.opengl.GL11;

import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Core.DCMod;
import DummyCore.Utils.DCParticleEngine.LayerEntry.LayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @author modbder
 * My simple ParticleEngine for rendering mod particles outside of vanilla's particle system. Allows for more than 4k particles.
 * Also allows more blend/alpha control, has Forge event support, etc.
 */
public class DCParticleEngine {
	
	public static final Hashtable<LayerEntry,ArrayList<EntityFX>> particles = new Hashtable<LayerEntry,ArrayList<EntityFX>>();
	public static final ArrayList<ParticleTicket> tickets = new ArrayList<ParticleTicket>();
	public static final Hashtable<DCMod,ParticleTicket> ticketData = new Hashtable<DCMod,ParticleTicket>();
	public static final ArrayList<EntityFX> allParticles = new ArrayList<EntityFX>();
	
	/**
	 * 
	 * @author modbder
	 * Layer system for particles. Particles render in layers for optimization, where every layer is 1 draw call.
	 * 1 layer can only have 1 texture and 1 set of params for blend/alpha control.
	 * If some of your particles require blend, and others don't - use multiple layers.
	 */
	public static class LayerEntry
	{
		public ParticleTicket owner;
		public ResourceLocation texture;
		public boolean blend;
		public boolean alpha;
		public int blend_src;
		public int blend_dst;
		public int alpha_index;
		public float alpha_func;
		public int maxParticlesFor;
		
		/**
		 * Creates a new LayerEntry with the given texture
		 * @param loc - a pointer to your texture
		 */
		public LayerEntry(ResourceLocation loc)
		{
			texture = loc;
		}
		
		/**
		 * Enables the blend
		 * @return this
		 */
		public LayerEntry enableBlend()
		{
			blend = true;
			return this;
		}
		
		/**
		 * Enables the alpha
		 * @return this
		 */
		public LayerEntry enableAlpha()
		{
			alpha = true;
			return this;
		}
		
		/**
		 * Binds a set of src and dst blend func to this layer
		 * @param i - src func
		 * @param j - dst func
		 * @return this
		 */
		public LayerEntry blendFunc(int i, int j)
		{
			blend_src = i;
			blend_dst = j;
			return this;
		}
		
		/**
		 * Binds a set of alpha func and modifier to this layer
		 * @param i - the func
		 * @param f - the modifier
		 * @return this
		 */
		public LayerEntry alphaFunc(int i, float f)
		{
			alpha_index = i;
			alpha_func = f;
			return this;
		}
		
		/**
		 * Sets the maximum number of particles for the layer to be rendered on
		 * @param i - new maximum
		 * @return this
		 */
		public LayerEntry setMaxParticles(int i)
		{
			maxParticlesFor = i;
			return this;
		}
		
		/**
		 * A simple forge event. Cancel to cancel the render for layer of particles. You can use this to do additional GL stuff
		 * @author modbder
		 *
		 */
		public static class LayerEvent extends Event
		{
			public final LayerEntry layer;
			public LayerEvent(LayerEntry par1)
			{
				layer = par1;
			}
			
			@Cancelable
			public static class Pre extends LayerEvent{

				public Pre(LayerEntry par1) {
					super(par1);
				}
			}
			
			public static class Post extends LayerEvent{

				public Post(LayerEntry par1) {
					super(par1);
				}
			}
		}
	}
	
	/**
	 * Each mod is supposed to have one of these if it is using a DC's particle system.
	 * @author modbder
	 *
	 */
	public static class ParticleTicket
	{
		public DCMod owner;
		public final ArrayList<LayerEntry> layers = new ArrayList<LayerEntry>();
		public int maxParticlesForMod;
		
		/**
		 * Constructs a new ticket for the given mod
		 * @param mod - the mod object
		 */
		public ParticleTicket(DCMod mod)
		{
			owner = mod;
		}
		
		/**
		 * Sets the maximum allowed particles for a ticket. -1 is no limit. This is what will be compared to the amount of particles on ALL layers!
		 * @param i - the new maximum amount
		 * @return this
		 */
		public ParticleTicket setMaxParticles(int i)
		{
			maxParticlesForMod = i;
			return this;
		}
		
		/**
		 * Appends a new LayerEntry for your particles to render on
		 * @param layer - the LayerEntry to add
		 * @return this
		 */
		public ParticleTicket appendLayer(LayerEntry layer)
		{
			layers.add(layer);
			layer.owner = this;
			return this;
		}
	}
	
	/**
	 * Creates a new ticket for mod, found by class. If there is already a ticket registered returns it instead. If the class object is not a valid DCMod returns null
	 * @param mod - the class of your mod
	 * @return null if the given class is not a DCMod, a ticket otherwise
	 */
	public static ParticleTicket obtainTicketForMod(Class<?> mod)
	{
		if(Core.isModRegistered(mod))
			return obtainTicketForMod(Core.getModFromClass(mod));
		return null;
	}
	
	/**
	 * Creates or gets a ParticleTicket for the given mod
	 * @param mod - the mod
	 * @return a ParticleTicket object
	 */
	public static ParticleTicket obtainTicketForMod(DCMod mod)
	{
		if(ticketData.containsKey(mod))
			return ticketData.get(mod);
		return createTicket(mod);
	}
	
	/**
	 * Creates a ParticleTicket for the given mod
	 * @param mod - the mod
	 * @return a new ParticleTicket object
	 */
	public static ParticleTicket createTicket(DCMod mod)
	{
		ParticleTicket ticket = new ParticleTicket(mod).setMaxParticles(1000);
		tickets.add(ticket);
		ticketData.put(mod, ticket);
		return ticket;
	}
	
	/**
	 * Adds a given particle to the given layer
	 * @param layer - the layer to place the particle on
	 * @param particle - the particle to add
	 */
	public static void addParticle(LayerEntry layer, EntityFX particle)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) //?????
			return;
		if(!particles.containsKey(layer))
			particles.put(layer, new ArrayList<EntityFX>());
		int currentParticlesForLayer = particles.get(layer).size();
		int currentParticlesForMod = 0;
		for(LayerEntry le : layer.owner.layers)
			if(particles.containsKey(le))
				currentParticlesForMod += particles.get(le).size();
		if(currentParticlesForLayer >= layer.maxParticlesFor || (currentParticlesForMod >= layer.owner.maxParticlesForMod && layer.owner.maxParticlesForMod != -1))
			return;
		if(particle.dimension != Minecraft.getMinecraft().thePlayer.dimension)
			return;
		allParticles.add(particle);
		particles.get(layer).add(particle);
	}
	
	//Internal
	public static void tick()
	{
		for(int i = 0; i < allParticles.size(); ++i)
		{
			EntityFX particle = allParticles.get(i);
			
			if(particle.isDead)
				allParticles.remove(i);
			else
			{
				++particle.ticksExisted;
				particle.onUpdate();
			}
		}
	}
	
	//Internal
	public static void draw(float partialTicks)
	{
		boolean alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
		boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(false);
		for(ParticleTicket ticket : tickets)
			for(LayerEntry layer : ticket.layers)
			{
				if(particles.containsKey(layer))
				{
					ArrayList<EntityFX> par = particles.get(layer);
					GlStateManager.pushMatrix();
					if(MinecraftForge.EVENT_BUS.post(new LayerEvent.Pre(layer)))
					{
						GlStateManager.popMatrix();
						continue;
					}
					if(layer.blend)
					{
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(layer.blend_src, layer.blend_dst);
					}
					if(layer.alpha)
					{
						GlStateManager.enableAlpha();
						GlStateManager.alphaFunc(layer.alpha_index, layer.alpha_func);
					}
					Minecraft.getMinecraft().renderEngine.bindTexture(layer.texture);
					EntityPlayerSP renderViewEntity = Minecraft.getMinecraft().thePlayer;
					float rotationViewX = ActiveRenderInfo.getRotationX();
			    	float rotationViewZ = ActiveRenderInfo.getRotationZ();
			    	float rotationViewXY = ActiveRenderInfo.getRotationXY();
			    	float rotationViewYZ = ActiveRenderInfo.getRotationYZ();
			    	float rotationViewXZ = ActiveRenderInfo.getRotationXZ();
			    	
			    	EntityFX.interpPosX = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
			    	EntityFX.interpPosY = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
			    	EntityFX.interpPosZ = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
			    	
			    	Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
					for(int i = 0; i < par.size(); ++i)
					{
						EntityFX particle = par.get(i);
						if(particle.isDead)
						{
							par.remove(i);
							continue;
						}
						if(particle.dimension != CoreInitialiser.proxy.getClientPlayer().dimension)
							continue;
						if(!particle.isInRangeToRenderDist(particle.getDistanceToEntity(CoreInitialiser.proxy.getClientPlayer())))
							continue;
						
						try{
							particle.renderParticle(Tessellator.getInstance().getWorldRenderer(), particle, partialTicks, rotationViewX, rotationViewXZ, rotationViewZ, rotationViewYZ, rotationViewXY);;
						}
						catch(Exception e)
						{
							particle.isDead = true;
							e.printStackTrace();
						}
					}
					TessellatorWrapper.instance.draw();
					if(layer.blend)
						GlStateManager.disableBlend();
					if(layer.alpha)
						GlStateManager.disableAlpha();
					MinecraftForge.EVENT_BUS.post(new LayerEvent.Post(layer));
					GlStateManager.popMatrix();
				}
			}
		if(alpha)
			GlStateManager.enableAlpha();
		if(blend)
			GlStateManager.enableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}
	
	//Forge event handling
	@SubscribeEvent
	public void tickWorld(TickEvent.ClientTickEvent event)
	{
		if(event.phase == Phase.END)
			tick();
	}
	
	//Forge event handling
	@SubscribeEvent
	public void renderLast(RenderWorldLastEvent event)
	{
		draw(event.partialTicks);
	}
}
