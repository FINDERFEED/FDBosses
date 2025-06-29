package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.FDTagHelper;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MalkuthCannonEntity extends FDLivingEntity implements AutoSerializable {

    private List<Vec3> shootTargets = new ArrayList<>();

    @SerializableField
    private MalkuthAttackType malkuthCannonType = MalkuthAttackType.FIRE;

    @SerializableField
    private int shootTickCount = 0;

    public MalkuthCannonEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        malkuthCannonType = MalkuthAttackType.ICE;
        this.setNoGravity(true);
        this.getAnimationSystem().startAnimation("SUMMON", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_SUMMON).build());
    }

    public MalkuthCannonEntity summon(Level level, Vec3 pos, Vec3 lookAt, MalkuthAttackType malkuthAttackType){
        MalkuthCannonEntity malkuthCannonEntity = new MalkuthCannonEntity(BossEntities.MALKUTH_CANNON.get(), level);
        malkuthCannonEntity.setPos(pos);
        malkuthCannonEntity.malkuthCannonType = malkuthAttackType;
        malkuthCannonEntity.lookAt(EntityAnchorArgument.Anchor.FEET, lookAt);
        level.addFreshEntity(malkuthCannonEntity);
        return malkuthCannonEntity;
    }

    public void shoot(List<Vec3> targets){
        if (level().isClientSide) return;
        if (shootTargets.isEmpty()){
            this.shootTargets = new ArrayList<>(targets);
            this.shootTickCount = 20;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (!shootTargets.isEmpty()){

                if (shootTickCount == 20){
                    this.getAnimationSystem().startAnimation("SHOOT", AnimationTicker.builder(BossAnims.MALKUTH_CANNON_SHOOT).build());
                }else if (shootTickCount == 16){

                    Vec3 frwd = this.getForward().multiply(1,0,1).normalize();

                    Vec3 summonPos = this.position().add(frwd.x * 1.2,1.75,frwd.z * 1.2);

                    BossUtil.malkuthCannonShoot((ServerLevel) level(), this.malkuthCannonType, summonPos, frwd.add(0,0.6f,0), 100);

                    for (Vec3 target : this.shootTargets){
                        Vec3 speed = BossUtil.calculateMortarProjectileVelocity(summonPos, target, -(float)LivingEntity.DEFAULT_BASE_GRAVITY, 40);
                        MalkuthCannonProjectile.summon(level(), summonPos, speed, 1000, malkuthCannonType);
                    }

                }else if (shootTickCount <= 0){
                    this.shootTargets.clear();
                }

                shootTickCount--;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if (src.is(DamageTypes.FELL_OUT_OF_WORLD) || src.is(DamageTypes.GENERIC_KILL)) return super.hurt(src,damage);
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
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
    public void setDeltaMovement(Vec3 p_20257_) {

    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        FDTagHelper.saveVec3List(tag, "targets", this.shootTargets);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        this.shootTargets = FDTagHelper.loadVec3List(tag, "targets");
    }
}
