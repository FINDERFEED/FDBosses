package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_monolith;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChesedMonolith extends FDLivingEntity implements AutoSerializable, ChesedBossBuddy {

    public static final EntityDataAccessor<Boolean> DEACTIVATED = SynchedEntityData.defineId(ChesedMonolith.class, EntityDataSerializers.BOOLEAN);


    @SerializableField
    private boolean deactivated = false;

    @SerializableField
    private boolean immuneToAttacks = false;

    public ChesedMonolith(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){
            if (!this.isDeactivated()){
                this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_MONOLITH_IDLE).build());
                this.getAnimationSystem().stopAnimation("TURN_OFF");
            }else{
                this.getAnimationSystem().stopAnimation("IDLE");
                this.getAnimationSystem().startAnimation("TURN_OFF", AnimationTicker.builder(BossAnims.CHESED_MONOLITH_TURN_OFF).build());
            }
        }else{
            this.entityData.set(DEACTIVATED,deactivated);
        }
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }

  

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEACTIVATED,false);
    }

    public boolean isDeactivated() {
        if (!level().isClientSide){
            return deactivated;
        }else {
            return this.entityData.get(DEACTIVATED);
        }
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {


        if ((this.isDeactivated() || this.isImmuneToAttacks()) && !src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

        return super.hurt(src, damage);
    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(0,0,0);
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void die(DamageSource src) {
        if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)){
            super.die(src);
        }else {
            if (level() instanceof ServerLevel serverLevel) {
                this.setHealth(this.getMaxHealth());
                this.setDeactivated(true);

                BallParticleOptions options = BallParticleOptions.builder()
                        .size(0.5f)
                        .color(0.3f, 1f, 1f,1f)
                        .physics(false)
                        .friction(0.7f)
                        .scalingOptions(0,0,10 + random.nextInt(4))
                        .build();
                serverLevel.sendParticles(options,this.getX(),this.getY() + 2.5,this.getZ(),30, 0.1f,0.1f,0.1f,0.25f);
                var li = LightningParticleOptions.builder()
                        .color(50 + random.nextInt(40), 183 + random.nextInt(60), 200 + random.nextInt(50))
                        .quadSize(0.2f)
                        .lifetime(10)
                        .randomRoll(true)
                        .build();
                serverLevel.sendParticles(li,this.getX(),this.getY() + 2.5,this.getZ(),20, 0.1f,0.1f,0.1f,0.05f);



            }
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return BossSounds.MONOLITH_HIT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return BossSounds.MONOLITH_HIT.get();
    }

    public boolean isImmuneToAttacks() {
        return immuneToAttacks;
    }

    public void setImmuneToAttacks(boolean immuneToAttacks) {
        this.immuneToAttacks = immuneToAttacks;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    @Override
    public void push(Entity p_21294_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    protected void pushEntities() {

    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);

        boolean deactivated = tag.getBoolean("deactivated");
        this.setDeactivated(deactivated);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        boolean deactivated = this.isDeactivated();
        tag.putBoolean("deactivated",deactivated);
    }

}
