package com.finderfeed.fdbosses.content.entities.chesed_boss.flying_block_entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlyingBlockEntity extends Entity {

    public static final EntityDataAccessor<BlockState> STATE = SynchedEntityData.defineId(FlyingBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> ROTATION_SPEED = SynchedEntityData.defineId(FlyingBlockEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> FRICTION = SynchedEntityData.defineId(FlyingBlockEntity.class, EntityDataSerializers.FLOAT);

    public int noPhysicsTime = 0;

    public FlyingBlockEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (!level().isClientSide){
            if (noPhysicsTime > 0){
                noPhysicsTime--;
                noPhysics = true;
            }else{
                noPhysics = false;
            }
            if (this.tickCount >= 2000 || this.onGround() || this.getBlockState().isAir()){
                this.discard();
            }
        }
        float friction = this.getAirFriction();
        this.setDeltaMovement(
                this.getDeltaMovement().multiply(friction,1f,friction)
        );
        this.applyGravity();
        this.move(MoverType.SELF,this.getDeltaMovement());
    }

    public void setNoPhysicsTime(int noPhysicsTime) {
        this.noPhysicsTime = noPhysicsTime;
    }

    public int getNoPhysicsTime() {
        return noPhysicsTime;
    }

    public float getRotationSpeed() {
        return this.entityData.get(ROTATION_SPEED);
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.entityData.set(ROTATION_SPEED,rotationSpeed);
    }

    public float getAirFriction() {
        return this.entityData.get(FRICTION);
    }

    public void setAirFriction(float friction) {
        this.entityData.set(FRICTION,friction);
    }

    public BlockState getBlockState(){
        return this.entityData.get(STATE);
    }

    public void setBlockState(BlockState state){
        this.entityData.set(STATE,state);
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        data
                .define(ROTATION_SPEED,1f)
                .define(FRICTION,1f)
                .define(STATE, Blocks.STONE.defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("rotationSpeed",this.getRotationSpeed());
        tag.put("state", NbtUtils.writeBlockState(this.getBlockState()));
        tag.putInt("noPhysicsTime",this.getNoPhysicsTime());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.setRotationSpeed(tag.getFloat("rotationSpeed"));
        this.setBlockState(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),tag.getCompound("state")));
        this.setNoPhysicsTime(tag.getInt("noPhysicsTime"));
    }
}
