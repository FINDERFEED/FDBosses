package com.finderfeed.fdbosses.entities.chesed_boss.chesed_monolith;

import com.finderfeed.fdbosses.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChesedMonolith extends FDLivingEntity implements AutoSerializable, ChesedBossBuddy {

    @SerializableField
    private boolean deactivated = false;

    private boolean immuneToAttacks = false;

    public ChesedMonolith(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();
    }

    public void setDeactivated(boolean deactivated) {
        if (level().isClientSide) throw new RuntimeException("Cannot call this on client!");
        if (!deactivated){
            this.getSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.CHESED_MONOLITH_IDLE).build());
            this.getSystem().stopAnimation("TURN_OFF");
        }else{
            this.getSystem().stopAnimation("IDLE");
            this.getSystem().startAnimation("TURN_OFF", AnimationTicker.builder(BossAnims.CHESED_MONOLITH_TURN_OFF).build());
        }
        this.deactivated = deactivated;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide){
            this.setDeactivated(this.isDeactivated());
        }
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {

        if (this.isImmuneToAttacks() && !src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

        return super.hurt(src, damage);
    }

    @Override
    public void die(DamageSource src) {
        if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)){
            super.die(src);
        }else {
            if (!level().isClientSide) {
                this.setHealth(this.getMaxHealth());
                this.setDeactivated(true);
            }
        }
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
    public boolean save(CompoundTag tag) {
        this.autoSave(tag);
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.autoLoad(tag);
    }
}
