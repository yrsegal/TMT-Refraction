package com.teamwizardry.refraction.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.client.render.RenderLaserUtil;
import com.teamwizardry.refraction.common.light.ReflectionTracker;

/**
 * Created by TheCodeWarrior
 */
public class LaserRenderer {
	public static final LaserRenderer INSTANCE = new LaserRenderer();
	
	protected Map<LaserRenderInfo, Integer> lasers = new ConcurrentHashMap<>();
	
	public static class LaserRenderInfo {
		public Vec3d start, end;
		public Color color;
		
		public LaserRenderInfo(Vec3d start, Vec3d end, Color color) {
			this.start = start;
			this.end = end;
			this.color = color == null ? Color.WHITE : color;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			
			LaserRenderInfo that = (LaserRenderInfo) o;
			
			if (!start.equals(that.start)) return false;
			if (!end.equals(that.end)) return false;
			return color.equals(that.color);
			
		}
		
		@Override
		public int hashCode() {
			int result = start.hashCode();
			result = 31 * result + end.hashCode();
			result = 31 * result + color.hashCode();
			return result;
		}
	}
	
	public static void add(Vec3d start, Vec3d end, Color color) {
		INSTANCE.lasers.put(new LaserRenderInfo(start, end, color), 20);
	}
	
	
	private LaserRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		EntityPlayer rootPlayer = Minecraft.getMinecraft().thePlayer;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
		double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
		double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();
		GlStateManager.translate(-x, -y, -z);
		
		// vvv actual rendering stuff vvv
		
		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 1f/255f);
		ReflectionTracker tracker = ReflectionTracker.getInstance(Minecraft.getMinecraft().theWorld);
		if(tracker != null) {
			RenderLaserUtil.startRenderingLasers();
			for (LaserRenderInfo info : lasers.keySet()) {
				RenderLaserUtil.renderLaser(info.color, info.start, info.end);
			}
			RenderLaserUtil.finishRenderingLasers();
		}
		GlStateManager.disableBlend();
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
		
		lasers.entrySet().removeIf((e) -> e.getValue() <= 0);
	}
	
	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			lasers.entrySet().stream().forEach((e) -> e.setValue(e.getValue()-1));
	}
}
