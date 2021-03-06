package com.teamwizardry.refraction.common.light;

import java.util.HashSet;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.google.common.collect.HashMultimap;

public class BlockTracker
{
	public static HashMultimap<BlockPos, Beam> locations;
	
	public static void init()
	{
		locations = HashMultimap.create();
	}

	public static void addBeam(Beam beam)
	{
		HashSet<BlockPos> possible = new HashSet<BlockPos>();
		Vec3d slope = beam.finalLoc.subtract(beam.initLoc).normalize();
		Vec3d curPos = beam.initLoc;
		boolean finished = false;
		while (!finished)
		{
			Vec3d nextPos = curPos.add(slope);
			for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(curPos), new BlockPos(nextPos)))
				possible.add(pos);
			if (curPos.distanceTo(beam.finalLoc) <= curPos.distanceTo(nextPos))
				finished = true;
			curPos = nextPos;
		}
		
		Vec3d invSlope = new Vec3d(1 / slope.xCoord, 1 / slope.yCoord, 1 / slope.zCoord);
		
		for (BlockPos pos : possible)
		{
			if (collides(beam, pos, invSlope))
				locations.put(pos, beam);
		}
	}

	private static boolean collides(Beam beam, BlockPos pos, Vec3d invSlope)
	{
		boolean signX = invSlope.xCoord < 0;
		boolean signY = invSlope.yCoord < 0;
		boolean signZ = invSlope.zCoord < 0;
		
		double txMin, txMax, tyMin, tyMax, tzMin, tzMax;
		AxisAlignedBB axis = new AxisAlignedBB(pos);
		
		txMin = ((signX ? axis.maxX : axis.minX) - beam.initLoc.xCoord) * invSlope.xCoord;
		txMax = ((signX ? axis.minX : axis.maxX) - beam.initLoc.xCoord) * invSlope.xCoord;
		tyMin = ((signY ? axis.maxY : axis.minY) - beam.initLoc.yCoord) * invSlope.yCoord;
		tyMax = ((signY ? axis.minY : axis.maxY) - beam.initLoc.yCoord) * invSlope.yCoord;
		tzMin = ((signZ ? axis.maxZ : axis.minZ) - beam.initLoc.zCoord) * invSlope.zCoord;
		tzMax = ((signZ ? axis.minZ : axis.maxZ) - beam.initLoc.zCoord) * invSlope.zCoord;
		
		if (txMin > txMax || tyMin > txMax) return false;
		if (tyMin > txMin) txMin = tyMin;
		if (tyMax < txMax) txMax = tyMax;
		if (txMin > tzMax || tzMin > txMax) return false;
		if (tzMin > txMin) txMin = tzMin;
		if (tzMax < txMax) txMax = tzMax;
		
		return true;
	}
}
