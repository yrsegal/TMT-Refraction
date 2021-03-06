package com.teamwizardry.refraction.common.proxy;

import com.teamwizardry.librarianlib.network.PacketHandler;
import com.teamwizardry.refraction.client.fx.SparkleFX;
import com.teamwizardry.refraction.common.CatChaseHandler;
import com.teamwizardry.refraction.common.light.BlockTracker;
import com.teamwizardry.refraction.common.network.PacketLaserFX;
import com.teamwizardry.refraction.init.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

/**
 * Created by LordSaad44
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		CatChaseHandler.INSTANCE.getClass(); // load the class
		ModSounds.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();

		PacketHandler.INSTANCE.register(PacketLaserFX.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		CraftingRecipes.init();
		AssemblyRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent event) {
		BlockTracker.init();
	}

	public boolean isClient() {
		return false;
	}

	public SparkleFX spawnParticleSparkle(World worldIn, double posXIn, double posYIn, double posZIn) {
		return null;
	}
	
	public MinecraftServer getServer() {
		return FMLServerHandler.instance().getServer();
	}
}
