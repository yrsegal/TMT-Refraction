package com.teamwizardry.refraction.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.refraction.init.ModItems;

/**
 * Created by TheCodeWarrior
 */
public class ScrewdriverOverlay {
	public static final ScrewdriverOverlay INSTANCE = new ScrewdriverOverlay();
	
	private ScrewdriverOverlay() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			ItemStack stack = getItemInHand(ModItems.SCREW_DRIVER);
			if(stack == null)
				return;
			
			double SQRT2 = Math.sqrt(0.5);
			
			double angle = ModItems.SCREW_DRIVER.getRotationMultiplier(stack);
			int textIndex = ModItems.SCREW_DRIVER.getRotationIndex(stack);
			double anglePer = 5.0;
			String text = I18n.format("gui.screw_driver.angle." + textIndex);
			
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			
			int circleRadius = 75;
			int posX = res.getScaledWidth();
			int posY = res.getScaledHeight();
			
			if(angle < 5) {
				double radiusAdd = 500;
				posX += SQRT2 * radiusAdd;
				posY += SQRT2 * radiusAdd;
				circleRadius += radiusAdd;
			}
			
			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0, 0, 0);
			GlStateManager.translate(posX, posY, 0);
			
			Vec2d vec = new Vec2d(0, -circleRadius);
			vec = rot(vec, -angle/2 - 45);
			
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vb = tessellator.getBuffer();
			
			vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
			vb.pos(0, 0, 0).endVertex();
			
			double ang = angle;
			
			do {
				Vec2d v = rot(vec, ang);
				vb.pos(v.x, v.y, 0).endVertex();
				ang -= anglePer;
			} while(ang > 0);
			
			vb.pos(vec.x, vec.y, 0).endVertex();
			
			tessellator.draw();
			GlStateManager.enableTexture2D();
			
			int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
			Minecraft.getMinecraft().fontRendererObj.drawString(text, -width-(int)(circleRadius*SQRT2), -9-(int)(circleRadius*SQRT2), 0x000000, false);
			
			GlStateManager.color(1, 1, 1);
			GlStateManager.popMatrix();
			
		}
	}
	
	private Vec2d rot(Vec2d vec, double deg) {
		double theta = Math.toRadians(deg);
		
		double cs = Math.cos(theta);
		double sn = Math.sin(theta);
		
		return new Vec2d(vec.x * cs - vec.y * sn, vec.x * sn + vec.y * cs);
	}
	
	private ItemStack getItemInHand(Item item) {
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		if(stack == null)
			stack = Minecraft.getMinecraft().thePlayer.getHeldItemOffhand();
		
		if(stack == null)
			return null;
		if(stack.getItem() != item)
			return null;
		
		return stack;
	}
}
