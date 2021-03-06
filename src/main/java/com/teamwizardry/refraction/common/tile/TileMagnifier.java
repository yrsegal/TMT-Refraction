package com.teamwizardry.refraction.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.refraction.common.light.Beam;
import com.teamwizardry.refraction.common.light.BeamConstants;
import com.teamwizardry.refraction.common.light.ILightSource;
import com.teamwizardry.refraction.common.light.ReflectionTracker;
import com.teamwizardry.refraction.init.ModBlocks;

/**
 * Created by LordSaad44
 */
public class TileMagnifier extends TileEntity implements ILightSource {

	private IBlockState state;
	
	public TileMagnifier() {
		ReflectionTracker.getInstance(worldObj).addSource(this);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());

		state = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, state, state, 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void generateBeam()
	{
		boolean hasLens = false;
		int worldTime = (int)(worldObj.getWorldTime() % 24000L);
		if (worldTime >= BeamConstants.NIGHT_START && worldTime < BeamConstants.NIGHT_END) return;
		for (int y = 1; y < 10; y++) {
			BlockPos lens = new BlockPos(pos.getX(), pos.getY() + y, pos.getZ());
			if (worldObj.getBlockState(lens).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens))
			if (worldObj.getBlockState(lens.south()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south())) continue;
			if (worldObj.getBlockState(lens.north()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north())) continue;
			if (worldObj.getBlockState(lens.east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.east())) continue;
			if (worldObj.getBlockState(lens.west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.west())) continue;
			if (worldObj.getBlockState(lens.south().west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south().west())) continue;
			if (worldObj.getBlockState(lens.south().east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.south().east())) continue;
			if (worldObj.getBlockState(lens.north().west()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north().west())) continue;
			if (worldObj.getBlockState(lens.north().east()).getBlock() != ModBlocks.LENS || !worldObj.canBlockSeeSky(lens.north().east())) continue;
			hasLens = true;
			break;
		}
		
		if (hasLens)
		{
			Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			Vec3d dir = new Vec3d(0, -1, 0);
			Color color = new Color(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, BeamConstants.SOLAR_ALPHA);
			
			new Beam(worldObj, center, dir, color);
		}
	}
}
