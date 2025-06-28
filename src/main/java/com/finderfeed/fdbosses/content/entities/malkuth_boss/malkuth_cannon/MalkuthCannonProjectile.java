package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MalkuthCannonProjectile extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthCannonProjectile.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private MalkuthAttackType malkuthAttackType = MalkuthAttackType.FIRE;

    @SerializableField
    private int reversedAge = 3000;

    public MalkuthCannonProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public static MalkuthCannonProjectile summon(Level level, Vec3 pos, Vec3 speed, int lifetime, MalkuthAttackType projectileDamageType){
        MalkuthCannonProjectile malkuthCannonProjectile = new MalkuthCannonProjectile(BossEntities.MALKUTH_CANNON_PROJECTILE.get(), level);
        malkuthCannonProjectile.setPos(pos);
        malkuthCannonProjectile.setDeltaMovement(speed);
        malkuthCannonProjectile.reversedAge = lifetime;
        malkuthCannonProjectile.setMalkuthAttackType(projectileDamageType);
        level.addFreshEntity(malkuthCannonProjectile);
        return malkuthCannonProjectile;
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType){
        this.malkuthAttackType = malkuthAttackType;
        this.entityData.set(MALKUTH_ATTACK_TYPE, malkuthAttackType);
    }

    public MalkuthAttackType getMalkuthAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    @Override
    public void tick() {
        if (!level().isClientSide){
            this.setMalkuthAttackType(this.malkuthAttackType);
            this.applyGravity();

            if (reversedAge-- <= 0){
                this.explode();
            }
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult res) {
        super.onHitBlock(res);
        if (!level().isClientSide){
            this.explode();
        }
    }

    private void explode(){

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.setMalkuthAttackType(this.malkuthAttackType);
    }

    @Override
    protected double getDefaultGravity() {
        return LivingEntity.DEFAULT_BASE_GRAVITY;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
    }
}
