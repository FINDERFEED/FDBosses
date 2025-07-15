package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MalkuthPlatform extends FDEntity {

    public static final EntityDataAccessor<Boolean> CAN_BE_COLLIDED_WITH = SynchedEntityData.defineId(MalkuthPlatform.class, EntityDataSerializers.BOOLEAN);

    public MalkuthPlatform(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (tickCount > 20){
                this.entityData.set(CAN_BE_COLLIDED_WITH, true);
            }
        }
    }

    @Override
    public void kill() {
        super.kill();
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    public void push(Entity p_20293_) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return this.entityData.get(CAN_BE_COLLIDED_WITH);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CAN_BE_COLLIDED_WITH, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(CAN_BE_COLLIDED_WITH, tag.getBoolean("can_collide"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("can_collide", this.entityData.get(CAN_BE_COLLIDED_WITH));
    }
}
