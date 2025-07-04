package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MalkuthGiantSwordSlash extends Entity {

    public static int TIME_TO_RISE = 60;
    public static int TIME_TO_HIT = 30;


    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthGiantSwordSlash.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    public MalkuthGiantSwordSlash(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){

        }else{

        }
    }

    public float getCurrentMoveUpTime(float pticks){
        return Math.clamp(this.tickCount + pticks,0, TIME_TO_RISE);
    }

    public float getCurrentHitTime(float pticks){
        return Math.clamp(this.tickCount - TIME_TO_RISE + pticks,0, TIME_TO_HIT);
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType type){
        this.entityData.set(MALKUTH_ATTACK_TYPE, type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tag.putString("mtype",this.getAttackType().name());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("mtype")) {
            this.setAttackType(MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

}
