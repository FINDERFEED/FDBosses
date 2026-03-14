package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_monolith;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChesedMonolith extends FDEntity implements AutoSerializable, ChesedBossBuddy {

    public static final EntityDataAccessor<Boolean> DEACTIVATED = SynchedEntityData.defineId(ChesedMonolith.class, EntityDataSerializers.BOOLEAN);
    private static final float BASE_MAX_HEALTH = 50.0f;


    @SerializableField
    private boolean deactivated = false;

    @SerializableField
    private boolean immuneToAttacks = false;

    @SerializableField
    private float health = BASE_MAX_HEALTH;

    @SerializableField
    private float maxHealthBonus = 0;

    public ChesedMonolith(EntityType<?> type, Level level) {
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
        if (!level().isClientSide) {
            this.entityData.set(DEACTIVATED, deactivated);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DEACTIVATED,false);
    }

    public boolean isDeactivated() {
        if (!level().isClientSide){
            return deactivated;
        }else {
            return this.entityData.get(DEACTIVATED);
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if ((this.isDeactivated() || this.isImmuneToAttacks()) && !src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

        if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            this.remove(RemovalReason.KILLED);
            return true;
        }

        if (level().isClientSide) {
            return true;
        }

        SoundEvent sound = this.getHurtSound(src);
        if (sound != null) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, 1f, 1f);
        }

        this.health -= damage;
        if (this.health <= 0) {
            this.die(src);
        }
        return true;
    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(0,0,0);
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(Vec3.ZERO);
    }

    public void die(DamageSource src) {
        if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)){
            this.remove(RemovalReason.KILLED);
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
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return BossSounds.MONOLITH_HIT.get();
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return BossSounds.MONOLITH_HIT.get();
    }

    public boolean isImmuneToAttacks() {
        return immuneToAttacks;
    }

    public void setImmuneToAttacks(boolean immuneToAttacks) {
        this.immuneToAttacks = immuneToAttacks;
    }

    public float getMaxHealth() {
        return BASE_MAX_HEALTH + maxHealthBonus;
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, this.getMaxHealth()));
    }

    public float getHealth() {
        return health;
    }

    public void setMaxHealthBonus(float maxHealthBonus) {
        this.maxHealthBonus = maxHealthBonus;
        this.health = Math.min(this.health, this.getMaxHealth());
    }

    @Override
    public boolean isNoGravity() {
        return true;
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
