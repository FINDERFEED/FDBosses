package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ChesedCrystalEntity extends FDLivingEntity implements ChesedBossBuddy {

    public static final EntityDataAccessor<Vec3> DIRECTION = SynchedEntityData.defineId(ChesedCrystalEntity.class, FDEDataSerializers.VEC3.get());

    private boolean dead = false;
    private int deathTime = 20;

    public ChesedCrystalEntity(EntityType<? extends FDLivingEntity> type, Level level) {
        super(type, level);
        this.getSystem().startAnimation("SPAWN", AnimationTicker.builder(BossAnims.CHESED_CRYSTAL_SPAWN.get())
                .setToNullTransitionTime(0)
                .build());
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (dead && deathTime-- < 0){
                this.setRemoved(RemovalReason.KILLED);
                this.gameEvent(GameEvent.ENTITY_DIE);
            }
        }
    }


    @Override
    public void die(DamageSource src) {
        if (!level().isClientSide){
            if (this.lastHurtByPlayer != null){
                this.lastHurtByPlayer.addEffect(new MobEffectInstance(BossEffects.CHESED_ENERGIZED,400,1,false,true));
            }
            dead = true;
            deathTime = 10;
            this.getSystem().startAnimation("DEATH", AnimationTicker.builder(BossAnims.CHESED_CRYSTAL_SPAWN.get())
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .reversed()
                    .setSpeed(2f)
                    .build());
        }
    }




    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.getEntity() instanceof Player player){
            this.setLastHurtByPlayer(player);
            float modifier = 1;
            if (!(player.getMainHandItem().getItem() instanceof AxeItem axe)) {
                modifier = 0.05f;
            }
            return super.hurt(src, amount * modifier);
        }else if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)){
            lastHurtByPlayer = null;
            return super.hurt(src, amount);
        }else{
            lastHurtByPlayer = null;
            return false;
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();

    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void applyGravity() {

    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {

    }

    @Override
    public void push(Vec3 p_347665_) {
    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {
    }

    @Override
    protected void pushEntities() {

    }

    @Override
    protected void playHurtSound(DamageSource src) {
        super.playHurtSound(src);
    }

    public Vec3 getCrystalFacingDirection(){
        return this.entityData.get(DIRECTION);
    }

    public void setCrystalFacingDirection(Vec3 v){
        this.entityData.set(DIRECTION,v);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        super.defineSynchedData(data);
        data.define(DIRECTION,new Vec3(0,1,0));
    }

}
