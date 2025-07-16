package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
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
    private int movingToTargetTime = -1;

    @SerializableField
    private int currentMovingToTargetTime = 0;

    @SerializableField
    private Vec3 startedMovingFromPos = Vec3.ZERO;

    @SerializableField
    private int accelerationTicks = 0;

    public static MalkuthFireball summon(MalkuthAttackType type, Level level, Vec3 pos, Vec3 flyToPos, Vec3 targetPos){
        MalkuthFireball malkuthFireball = new MalkuthFireball(BossEntities.MALKUTH_FIREBALL.get(), level);

        malkuthFireball.setAttackType(type);
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
        if (!this.isMovingToTarget()) {
            float peakSpeed = 2f;
            float minspeed = 0.1f;

            float maxDist = 10;

            Vec3 pos = this.position();

            Vec3 target = this.moveToPos;

            double dist = pos.distanceTo(target);
            if (dist >= minspeed) {

                Vec3 between = target.subtract(pos).normalize();

                float p = (float) Math.clamp(dist / maxDist, 0, 1);

                float speedValue = FDMathUtil.lerp(minspeed, peakSpeed, p);

                Vec3 speed = between.multiply(speedValue, speedValue, speedValue);

                this.setDeltaMovement(speed);

            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
        }else{

            if (accelerationTicks-- >= 0){

                if (movingToTargetTime == currentMovingToTargetTime){
                    this.setDeltaMovement(Vec3.ZERO);
                }

                Vec3 startPos = this.startedMovingFromPos;
                Vec3 targetPos = this.targetPos;

                Vec3 between = targetPos.subtract(startPos);

                double S = between.length();

                double t = movingToTargetTime / 2f;

                double v = S / (t * 1.5f);
                double a = v / t;

                Vec3 current = this.getDeltaMovement();

                Vec3 next = current.add(between.normalize().multiply(a,a,a));

                this.setDeltaMovement(next);
            }


            if (this.currentMovingToTargetTime-- <= 0){
                this.explode();
            }

        }
    }

    public void explode(){
        this.remove(RemovalReason.DISCARDED);
    }

    public void setMoveToTarget(int moveTime){
        this.movingToTargetTime = moveTime;
        this.currentMovingToTargetTime = moveTime;
        this.accelerationTicks = moveTime / 2;
        this.startedMovingFromPos = this.position();
    }

    public boolean isMovingToTarget(){
        return this.movingToTargetTime != -1;
    }

    private void particles(){


        Vec3 between = new Vec3(
                this.getX() - xo,
                this.getY() - yo,
                this.getZ() - zo
        );

        double length = between.length();


        Vec3 pos = this.position().add(0, this.getBbHeight()  / 2, 0);


        float maxrad = 0.5f;

        for (float g = -0.001f; g < length * 0.8f; g+= maxrad) {


            for (int i = 0; i < 10; i++) {

                Vector3f color = MalkuthEntity.getAndRandomizeColor(this.getAttackType(), random);

                ParticleOptions particleOptions;

                if (random.nextFloat() > 0.1f) {
                    particleOptions = BallParticleOptions.builder()
                            .color(color.x, color.y, color.z)
                            .scalingOptions(0, 0, 10 + random.nextInt(4))
                            .brightness(1)
                            .build();
                }else{
                    if (this.getAttackType().isFire()){
                        particleOptions = ParticleTypes.FLAME;
                    }else{
                        particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,0,2f,true);
                    }
                }

                float rndOut = maxrad / 2 + random.nextFloat() * maxrad / 2;

                Vec3 rndOffs = new Vec3(
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1
                ).normalize().multiply(rndOut, rndOut, rndOut);

                Vec3 distOffset = between.normalize().multiply(g,g,g);

                Vec3 ppos = pos.add(rndOffs)
                        .subtract(distOffset);

                float vspeed = length == 0 ? -random.nextFloat() * 0.1f : 0;

                level().addParticle(particleOptions, true, ppos.x, ppos.y, ppos.z, 0, vspeed, 0);

            }
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
