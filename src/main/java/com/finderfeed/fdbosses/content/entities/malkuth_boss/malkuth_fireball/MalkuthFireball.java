package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthFireball extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthFireball.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    @SerializableField
    private Vec3 moveToPos = Vec3.ZERO;

    @SerializableField
    private Vec3 targetPos = Vec3.ZERO;

    @SerializableField
    private boolean movingToTarget = false;

    public static MalkuthFireball summon(Level level, Vec3 pos, Vec3 flyToPos, Vec3 targetPos){
        MalkuthFireball malkuthFireball = new MalkuthFireball(BossEntities.MALKUTH_FIREBALL.get(), level);

        malkuthFireball.setPos(pos);
        malkuthFireball.moveToPos = flyToPos;
        malkuthFireball.targetPos = targetPos;
        malkuthFireball.doMove();

        level.addFreshEntity(malkuthFireball);
        return malkuthFireball;
    }

    public MalkuthFireball(EntityType<? extends FDProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 1000){
            this.remove(RemovalReason.DISCARDED);
        }

        if (level().isClientSide){
            this.particles();
        }else{
            this.doMove();
        }

    }

    private void doMove(){

        float peakSpeed = 0.25f;
        float minspeed = 0.05f;
        if (this.isMovingToTarget()){
            minspeed = peakSpeed;
        }
        float maxDist = 5;

        Vec3 pos = this.position();

        Vec3 target;

        if (this.isMovingToTarget()){
            target = this.targetPos;
        }else{
            target = this.moveToPos;
        }


        double dist = pos.distanceTo(target);
        if (dist >= minspeed){

            Vec3 between = target.subtract(pos).normalize();



            float p = (float) Math.clamp(dist / maxDist, 0, 1);

            float speedValue = FDMathUtil.lerp(minspeed,peakSpeed,p);

            Vec3 speed = between.multiply(speedValue,speedValue,speedValue);

            this.setDeltaMovement(speed);

        }else{
            if (this.isMovingToTarget()){
                this.explode();
            }
        }
    }

    public void explode(){
        this.remove(RemovalReason.DISCARDED);
    }

    public void setMoveToTarget(){
        this.movingToTarget = true;
    }

    public boolean isMovingToTarget(){
        return this.movingToTarget;
    }

    private void particles(){

        Vec3 pos = this.position().add(0, this.getBbHeight()  / 2, 0);

        for (int i = 0; i < 100; i ++){

            Vector3f color = MalkuthEntity.getAndRandomizeColor(this.getAttackType(), random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .color(color.x,color.y,color.z)
                    .scalingOptions(0,0,10 + random.nextInt(4))
                    .brightness(1)
                    .build();

            float rndOut = 0.5f + random.nextFloat() * 0.75f;

            Vec3 rndOffs = new Vec3(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            ).normalize().multiply(rndOut,rndOut,rndOut);

            Vec3 ppos = pos.add(rndOffs);

            level().addParticle(ballParticleOptions, true, ppos.x,ppos.y,ppos.z,0,0,0);

        }

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType type){
        this.entityData.set(ATTACK_TYPE,type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TYPE, MalkuthAttackType.ICE);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("mtype", this.entityData.get(ATTACK_TYPE).name());
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mtype")) {
            this.entityData.set(ATTACK_TYPE, MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
        this.autoLoad(tag);
    }


}
