package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MalkuthRepairCrystal extends FDEntity implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> CRYSTAL_TYPE = SynchedEntityData.defineId(MalkuthRepairCrystal.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private int deathTicks = -1;

    public MalkuthRepairCrystal(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_REPAIR_CRYSTAL_SUMMON)
                        .setLoopMode(Animation.LoopMode.ONCE)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (deathTicks != -1 && deathTicks-- <= 0){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    public MalkuthAttackType getCrystalType(){
        return this.entityData.get(CRYSTAL_TYPE);
    }

    public void setCrystalType(MalkuthAttackType crystalType){
        this.entityData.set(CRYSTAL_TYPE, crystalType);
    }

    public void destroyAndSummonRepairMaterial(){

        MalkuthRepairEntity malkuthRepairEntity = MalkuthRepairEntity.summon(level(), this.position().add(0,3,0), this.entityData.get(CRYSTAL_TYPE));

        this.remove(RemovalReason.DISCARDED);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        data.define(CRYSTAL_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("crystalType", this.getCrystalType().name());
        this.autoSave(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("crystalType")) {
            this.setCrystalType(MalkuthAttackType.valueOf(tag.getString("crystalType")));
        }
        this.autoLoad(tag);
    }

}
