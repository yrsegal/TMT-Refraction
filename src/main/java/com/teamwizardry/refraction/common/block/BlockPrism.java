package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.math.Geometry;
import com.teamwizardry.librarianlib.math.Matrix4;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.common.light.ILaserTrace;
import com.teamwizardry.refraction.common.raytrace.Tri;
import com.teamwizardry.refraction.common.tile.TilePrism;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class BlockPrism extends BlockDirectional implements ITileEntityProvider, ILaserTrace {

	public BlockPrism() {
		super(Material.GLASS);
		setHardness(1F);
		setSoundType(SoundType.GLASS);
		setUnlocalizedName("prism");
		setRegistryName("prism");
		GameRegistry.register(this);
		GameRegistry.registerTileEntity(TilePrism.class, "prism");
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setCreativeTab(Refraction.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePrism();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		return true;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, facing);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing;
		switch (meta & 7) {
			case 0:
				enumfacing = EnumFacing.DOWN;
				break;
			case 1:
				enumfacing = EnumFacing.EAST;
				break;
			case 2:
				enumfacing = EnumFacing.WEST;
				break;
			case 3:
				enumfacing = EnumFacing.SOUTH;
				break;
			case 4:
				enumfacing = EnumFacing.NORTH;
				break;
			case 5:
			default:
				enumfacing = EnumFacing.UP;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i;
		switch (state.getValue(FACING).ordinal()) {
			case 0:
				i = 0;
				break;
			case 1:
				i = 1;
				break;
			case 2:
				i = 2;
				break;
			case 3:
				i = 3;
				break;
			case 4:
				i = 4;
				break;
			case 5:
				i = 5;
				break;
			default:
				i = 0;
				break;
		}
		return i;
	}


	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}


	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public BlockPrism.RayTraceResultData<Vec3d> collisionRayTraceLaser(IBlockState blockState, World worldIn, BlockPos pos, Vec3d startRaw, Vec3d endRaw) {
		
		EnumFacing facing = blockState.getValue(FACING);
		
		Matrix4 matrixA = new Matrix4();
		Matrix4 matrixB = new Matrix4();
//		matrixA.translate(new Vec3d(-0.5, -0.5, -0.5));
//		matrixB.translate(new Vec3d(0.5, 0.5, 0.5));
		switch(facing) {
			case UP:
			case DOWN:
			case EAST:
				break;
			case NORTH:
				matrixA.rotate(Math.toRadians(270), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(270), new Vec3d(0, 1, 0));
				break;
			case SOUTH:
				matrixA.rotate(Math.toRadians(90), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(90), new Vec3d(0, 1, 0));
				break;
			case WEST:
				matrixA.rotate(Math.toRadians(180), new Vec3d(0, -1, 0));
				matrixB.rotate(Math.toRadians(180), new Vec3d(0, 1, 0));
				break;
		}
//		matrixA.translate(new Vec3d(0.5, 0.5, 0.5));
//		matrixB.translate(new Vec3d(-0.5, -0.5, -0.5));
		
		Vec3d
			a = new Vec3d(0, 0, 0),
			b = new Vec3d(1, 0, 0.5),
			c = new Vec3d(0, 0, 1),
			A = a.addVector(0, 1, 0),
			B = b.addVector(0, 1, 0),
			C = c.addVector(0, 1, 0);
		
		Tri[] tris = new Tri[] {
			new Tri(a, b, c),
			new Tri(A, C, B),
			
			new Tri(a, c, C),
			new Tri(a, C, A),
			
			new Tri(a, A, B),
			new Tri(a, B, b),
			
			new Tri(b, B, C),
			new Tri(b, C, c),
		};
		
		Vec3d start = matrixA.apply(startRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);
		Vec3d end = matrixA.apply(endRaw.subtract(new Vec3d(pos)).subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5);
		
		Tri hitTri = null;
		Vec3d hit = null;
		double shortestSq = Double.POSITIVE_INFINITY;
		
		for(Tri tri : tris) {
			Vec3d v = tri.trace(start, end);
			if(v != null) {
				double distSq = start.subtract(v).lengthSquared();
				if(distSq < shortestSq) {
					hit = v;
					shortestSq = distSq;
					hitTri = tri;
				}
			}
		}
		
		if(hit == null)
			return null;
		
		return new RayTraceResultData<Vec3d>(matrixB.apply(hit.subtract(0.5, 0.5, 0.5)).addVector(0.5, 0.5, 0.5).add(new Vec3d(pos)), EnumFacing.UP, pos).data(matrixB.apply(hitTri.normal()));
	}
	
	public static class RayTraceResultData<T> extends RayTraceResult {
		
		public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
		{
			this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
		}
		
		public RayTraceResultData(Vec3d hitVecIn, EnumFacing sideHitIn)
		{
			this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
		}
		
		public RayTraceResultData(Entity entityIn)
		{
			this(entityIn, new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ));
		}
		
		public RayTraceResultData(RayTraceResult.Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
		{
			super(typeIn, hitVecIn, sideHitIn, blockPosIn);
		}
		
		public RayTraceResultData(Entity entityHitIn, Vec3d hitVecIn)
		{
			super(entityHitIn, hitVecIn);
		}
		
		public T data;
		
		public RayTraceResultData<T> data(T data) {
			this.data = data;
			return this;
		}
		
	}
}