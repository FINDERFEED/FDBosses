package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MalkuthBoulderEntity extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthBoulderEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());
    public static final EntityDataAccessor<Integer> PREPARE_TIME = SynchedEntityData.defineId(MalkuthBoulderEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> PREPARE_HEIGHT = SynchedEntityData.defineId(MalkuthBoulderEntity.class, EntityDataSerializers.FLOAT);

    @SerializableField
    private ProjectileMovementPath movementPath;

    @SerializableField
    private boolean shouldMoveToTarget = false;

    public static MalkuthBoulderEntity summon(Level level, Vec3 pos, int prepareTime, float prepareHeight, ProjectileMovementPath movementPath, MalkuthAttackType malkuthAttackType){
        MalkuthBoulderEntity malkuthBoulderEntity = new MalkuthBoulderEntity(BossEntities.MALKUTH_BOULDER.get(), level);
        malkuthBoulderEntity.setPos(pos);
        malkuthBoulderEntity.movementPath = movementPath;
        malkuthBoulderEntity.entityData.set(PREPARE_TIME, prepareTime);
        malkuthBoulderEntity.entityData.set(PREPARE_HEIGHT, prepareHeight);
        malkuthBoulderEntity.setMalkuthAttackType(malkuthAttackType);
        level.addFreshEntity(malkuthBoulderEntity);
        return malkuthBoulderEntity;
    }

    public MalkuthBoulderEntity(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){

        }else{
            if (this.isAllowedToMoveToTarget()){
                if (!this.movementPath.isFinished()) {
                    this.movementPath.tick(this);
                }else{
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

    public void setShouldMoveToTarget(boolean shouldMoveToTarget) {
        this.shouldMoveToTarget = shouldMoveToTarget;
    }

    public boolean isAllowedToMoveToTarget() {
        return shouldMoveToTarget;
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(MALKUTH_ATTACK_TYPE, malkuthAttackType);
    }

    public MalkuthAttackType getMalkuthAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    protected int getPrepareTime(){
        return this.entityData.get(PREPARE_TIME);
    }

    protected void setPrepareTime(int time){
        this.entityData.set(PREPARE_TIME, time);
    }

    public float getPrepareHeight() {
        return this.entityData.get(PREPARE_HEIGHT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326181_) {
        super.defineSynchedData(p_326181_);
        p_326181_.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
        p_326181_.define(PREPARE_TIME, 20);
        p_326181_.define(PREPARE_HEIGHT, 3f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("mtype")){
            this.setMalkuthAttackType(MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
        this.entityData.set(PREPARE_TIME, tag.getInt("prepareTime"));
        this.entityData.set(PREPARE_HEIGHT, tag.getFloat("prepareHeight"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        tag.putString("mtype",this.getMalkuthAttackType().name());
        tag.putInt("prepareTime", this.entityData.get(PREPARE_TIME));
        tag.putFloat("prepareHeight", this.entityData.get(PREPARE_HEIGHT));
    }
}
