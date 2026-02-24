package com.finderfeed.fdbosses.content.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;

public abstract class BossSimpleProjectile extends FDOwnableEntity {

    public static final EntityDataAccessor<Boolean> REACHED_DESTINATION = SynchedEntityData.defineId(BossSimpleProjectile.class, EntityDataSerializers.BOOLEAN);

    public int reachedDestinationTicks = 0;

    private Vec3 lastKnownDeltaMovement = Vec3.ZERO;

    public BossSimpleProjectile(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.rememberDeltaMovement();

        this.setPos(this.position().add(this.getDeltaMovement()));

        if (!level().isClientSide) {
            if (this.tickCount > this.removeTime()){
                this.remove(RemovalReason.DISCARDED);
            }
            this.processHits();
        }

        if (this.hasReachedDestination()){
            reachedDestinationTicks++;
        }

    }

    public void processHits(){
        Vec3 start = this.getBoundingBox().getCenter();
        Vec3 end = start.add(this.getDeltaMovement());
        ClipContext clipContext = new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        BlockHitResult result = level().clip(clipContext);
        if (result.getType() != HitResult.Type.MISS){
            this.onBlockHit(result);
        }else{
            var eresult = ProjectileUtil.getEntityHitResult(level(), this, start, end, new AABB(start,end), (entity)->true);
            this.onEntityHit(eresult);
        }
    }

    public abstract void onEntityHit(EntityHitResult entity);
    public abstract void onBlockHit(BlockHitResult result);

    private int removeTime(){
        return 2000;
    }

    public boolean hasReachedDestination(){
        return this.entityData.get(REACHED_DESTINATION);
    }

    public void setReachedDestination(boolean reached){
        this.entityData.set(REACHED_DESTINATION, reached);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        super.defineSynchedData(p_326003_);
        p_326003_.define(REACHED_DESTINATION, false);
    }

    private void rememberDeltaMovement(){
        Vec3 thisMovement = this.getDeltaMovement();
        if (thisMovement.length() > 0.01){
            lastKnownDeltaMovement = thisMovement;
        }
    }

    public Vec3 getLastKnownDeltaMovement() {
        return lastKnownDeltaMovement;
    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(p_20335_, p_20336_, p_20337_);
        this.rememberDeltaMovement();
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(p_20257_);
        this.rememberDeltaMovement();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setReachedDestination(tag.getBoolean("reached"));
        this.reachedDestinationTicks = tag.getInt("reached_ticks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("reached", this.hasReachedDestination());
        tag.putInt("reached_ticks", reachedDestinationTicks);
    }

}
