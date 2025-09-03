package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MalkuthRepairCrystal extends FDEntity implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> CRYSTAL_TYPE = SynchedEntityData.defineId(MalkuthRepairCrystal.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    public static final EntityDataAccessor<Boolean> HIDDEN = SynchedEntityData.defineId(MalkuthRepairCrystal.class, EntityDataSerializers.BOOLEAN);

    public MalkuthRepairCrystal(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_REPAIR_CRYSTAL_SUMMON)
                        .setLoopMode(Animation.LoopMode.ONCE)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void setHidden(boolean hidden){
        this.entityData.set(HIDDEN, hidden);
        if (hidden){
            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_REPAIR_CRYSTAL_SUMMON)
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                            .setToNullTransitionTime(0)
                    .reversed()
                    .build());
        }else{
            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.MALKUTH_REPAIR_CRYSTAL_SUMMON)
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .setToNullTransitionTime(0)
                    .build());
        }
    }

    public boolean isHidden(){
        return this.entityData.get(HIDDEN);
    }

    public MalkuthAttackType getCrystalType(){
        return this.entityData.get(CRYSTAL_TYPE);
    }

    public void setCrystalType(MalkuthAttackType crystalType){
        this.entityData.set(CRYSTAL_TYPE, crystalType);
    }

    public void destroyAndSummonRepairMaterial(){

        if (this.isHidden()) return;

        MalkuthRepairEntity malkuthRepairEntity = MalkuthRepairEntity.summon(level(), this.position().add(0,3,0), this.entityData.get(CRYSTAL_TYPE));

        var color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(this.getCrystalType());

        ((ServerLevel)level()).sendParticles(BallParticleOptions.builder()
                        .color(color.x,color.y,color.z)
                        .brightness(2)
                        .size(0.5f)
                        .scalingOptions(0,0,20)
                        .friction(0.6f)
                .build(), this.getX(), this.getY() + 1.5f, this.getZ(), 100, 0.2f,0.2f,0.2f,0.5);

        this.remove(RemovalReason.DISCARDED);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        data.define(CRYSTAL_TYPE, MalkuthAttackType.FIRE);
        data.define(HIDDEN, false);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("crystalType", this.getCrystalType().name());
        tag.putBoolean("hidden", this.entityData.get(HIDDEN));
        this.autoSave(tag);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("crystalType")) {
            this.setCrystalType(MalkuthAttackType.valueOf(tag.getString("crystalType")));
        }
        this.entityData.set(HIDDEN, tag.getBoolean("hidden"));
        this.setHidden(this.entityData.get(HIDDEN));
        this.autoLoad(tag);
    }

}
