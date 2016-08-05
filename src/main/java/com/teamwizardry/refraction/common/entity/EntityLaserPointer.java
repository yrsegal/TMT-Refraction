package com.teamwizardry.refraction.common.entity;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.refraction.common.item.ItemLaserPen;
import com.teamwizardry.refraction.init.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Created by TheCodeWarrior
 */
public class EntityLaserPointer extends EntityLivingBase implements IEntityAdditionalSpawnData {
	public static final DataParameter<Byte> AXIS_HIT = EntityDataManager.<Byte>createKey(EntityLaserPointer.class, DataSerializers.BYTE);
	
	WeakReference<EntityPlayer> player;
	
	public EntityLaserPointer(World worldIn, EntityPlayer player) {
		super(worldIn);
		this.player = new WeakReference<>(player);
		this.setSize(0.1F, 0.1F);
	}
	
	public EntityLaserPointer(World worldIn) {
		super(worldIn);
		this.setSize(0.1F, 0.1F);
	}
	
	
	
	@Override
	public void onEntityUpdate() {
	}
	
	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		// noop
	}
	
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return false;
	}
	
	@Override
	public void onUpdate() {
		updateRayPos();
	}
	
	public void updateRayPos() {
		if(player == null || player.get() == null) {
			this.setDead();
		} else if( player.get().getActiveItemStack() == null || player.get().getActiveItemStack().getItem() != ModItems.LASER_PEN ) {
			this.setDead();
		} else {
			RayTraceResult res = player.get().rayTrace(ItemLaserPen.RANGE, 1);
			Vec3d pos = null;
			if(res != null) {
				pos = res.hitVec;
				this.dataManager.set(AXIS_HIT, (byte)res.sideHit.getAxis().ordinal());
			} else {
				pos = player.get().getLook(1).scale(ItemLaserPen.RANGE).add(player.get().getPositionEyes(1));
				this.dataManager.set(AXIS_HIT, (byte)255);
				
			}
			this.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
		}
	}
	
	@Override
	public EnumHandSide getPrimaryHand() {
		return null;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(AXIS_HIT, Byte.valueOf((byte)0));
	}
	
	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return ImmutableList.of();
	}
	
	@Nullable
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return null;
	}
	
	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		boolean b = player == null || player.get() == null;
		buffer.writeBoolean(b);
		if(!b) {
			buffer.writeLong(player.get().getPersistentID().getMostSignificantBits());
			buffer.writeLong(player.get().getPersistentID().getLeastSignificantBits());
		}
	}
	
	@Override
	public void readSpawnData(ByteBuf buffer) {
		boolean b = buffer.readBoolean();
		if(!b) {
			UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
			player = new WeakReference<>(worldObj.getPlayerEntityByUUID(uuid));
		}
	}
}
