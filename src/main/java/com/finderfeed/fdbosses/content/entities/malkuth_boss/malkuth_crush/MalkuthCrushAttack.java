package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MalkuthCrushAttack extends FDEntity implements AutoSerializable {

    @SerializableField
    private float damage;

    public static MalkuthCrushAttack summon(Level level, Vec3 pos, float damage){
        MalkuthCrushAttack malkuthCrushAttack = new MalkuthCrushAttack(BossEntities.MALKUTH_CRUSH.get(), level);
        malkuthCrushAttack.setPos(pos);
        malkuthCrushAttack.damage = damage;
        level.addFreshEntity(malkuthCrushAttack);
        return malkuthCrushAttack;
    }

    public MalkuthCrushAttack(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("ATTACK", AnimationTicker.builder(BossAnims.MALKUTH_CRUSH_ATTACK_CRUSH).setSpeed(1.25f).build());
    }

    @Override
    public void tick() {

        if (firstTick){
            if (level().isClientSide) {
                this.doParticles();
            }
        }
        super.tick();
        if (!level().isClientSide){
            if (tickCount > BossAnims.MALKUTH_CRUSH_ATTACK_CRUSH.get().getAnimTime()){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void doParticles(){

    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
    }
}
