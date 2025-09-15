package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_one_shot_vertical_ray;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedRayOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDUtil;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChesedOneShotVerticalRayEntity extends Entity implements AutoSerializable {

    public static final EntityDataAccessor<Integer> ATTACK_PREPARATION_TIME = SynchedEntityData.defineId(ChesedOneShotVerticalRayEntity.class,EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ChesedOneShotVerticalRayEntity.class,EntityDataSerializers.FLOAT);



    @SerializableField
    private float damage;

    @SerializableField
    private float damageRadius = 2.5f;

    public boolean softerSound = false;

    public ChesedOneShotVerticalRayEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static ChesedOneShotVerticalRayEntity summon(Level level,Vec3 pos,float damage,float height,int attackPrepTime){
        ChesedOneShotVerticalRayEntity entity = new ChesedOneShotVerticalRayEntity(BossEntities.CHESED_ONE_SHOT_VERTICAL_RAY_ATTACK.get(),level);
        entity.setDamage(damage);
        entity.setHeight(height);
        entity.setPos(pos);
        entity.setAttackPreparationTime(attackPrepTime);
        level.addFreshEntity(entity);
        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){


            if (tickCount == this.getAttackPreparationTime()) {

                level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.FAST_LIGHTNING_STRIKE.get(), SoundSource.HOSTILE, softerSound ? 0.25f : 1f, 1f);

                this.rayParticleAndShake();

                this.doDamage();

                this.remove(RemovalReason.DISCARDED);

            }else if (tickCount > this.getAttackPreparationTime()){
                this.remove(RemovalReason.DISCARDED);
            }
        }else{

            int attackPreparationTime = this.getAttackPreparationTime();

            if (tickCount < attackPreparationTime / 2f){
                this.prepareParticles(attackPreparationTime / 2);
            }else if (this.tickCount == attackPreparationTime - 1){
                this.boomParticles(this.position());
            }


        }
    }

    private void boomParticles(Vec3 pos){
        for (int i = 0; i < 50;i++){
            BallParticleOptions options = BallParticleOptions.builder()
                    .color(0.3f, 1f, 1f,1f)
                    .scalingOptions(0,10,20 + random.nextInt(3))
                    .physics(false)
                    .size(0.5f)
                    .friction(0.9f)
                    .build();

            Vec3 speed = new Vec3(0.6f * random.nextFloat(),0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2)
                    .add(
                            0,
                            random.nextFloat() * 0.3f,
                            0
                    );

            level().addParticle(options,true,pos.x,pos.y,pos.z,speed.x,speed.y,speed.z);
        }

        float sp = 2;

        for (int c = 0; c < 4;c++) {
            for (int i = 0; i < this.getHeight();i++){
                Vec3 spawn = this.position().add(0,i + random.nextFloat() - 0.5,0);

                Vec3 speed = new Vec3(sp * (random.nextFloat() * 0.5 + 0.5),0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());


                BallParticleOptions options = BallParticleOptions.builder()
                        .size(0.5f)
                        .color(0.3f, 1f, 1f,1f)
                        .physics(false)
                        .friction(0.4f)
                        .scalingOptions(0,0,10 + random.nextInt(4))
                        .build();
                level().addParticle(options,true,spawn.x,spawn.y,spawn.z,speed.x,speed.y,speed.z);
            }
        }

    }

    private void prepareParticles(int lifetime){

        float p = this.tickCount / (float) lifetime;

        for (int c = 0; c < 2;c++) {
            for (int i = 0; i < this.getHeight();i++){
                Vec3 center = this.position().add(0,i + random.nextFloat() - 0.5,0);
                Vec3 spawnOffset = new Vec3(2,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());
                Vec3 spawn = center.add(spawnOffset);

                BallParticleOptions options = BallParticleOptions.builder()
                        .particleProcessor(new CircleParticleProcessor(center,true,true,1))
                        .size(0.5f * Math.max(0.05f,p))
                        .color(0.3f, 1f, 1f,1f)
                        .physics(false)
                        .scalingOptions(lifetime,0,0)
                        .build();
                level().addParticle(options,true,spawn.x,spawn.y,spawn.z,0,0,0);
            }
        }


    }


    private void rayParticleAndShake(){
        Vec3 p = this.position();
        Vec3 end = p.add(0,this.getHeight(),0);
        ChesedRayOptions options = ChesedRayOptions.builder()
                .time(0, 7, 5)
                .lightningColor(90, 180, 255)
                .color(100, 255, 255)
                .end(end)
                .width(0.5f)
                .build();
        FDLibCalls.sendParticles((ServerLevel) level(), options, p, 60);

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(5)
                .amplitude(5f)
                .inTime(0)
                .stayTime(0)
                .outTime(5)
                .build(),p,10);


        FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                .size(20f)
                .scalingOptions(1,0,3)
                .color(150,230,255)
                .build(),p,60);

    }


    private void doDamage(){

        AABB box = new AABB(-damageRadius,0,-damageRadius,damageRadius,this.getHeight(),damageRadius).move(this.position());
        var list = level().getEntitiesOfClass(LivingEntity.class,box, BossUtil.entityInVerticalRadiusPredicate(this.position(),damageRadius));

        for (LivingEntity entity : list){

            if (entity instanceof ChesedBossBuddy) continue;

            entity.hurt(BossDamageSources.CHESED_VERTICAL_RAY_SOURCE,this.getDamage());

        }

    }

    public float getDamageRadius() {
        return damageRadius;
    }

    public void setDamageRadius(float damageRadius) {
        this.damageRadius = damageRadius;
    }

    public float getHeight() {
        return this.entityData.get(HEIGHT);
    }

    public float getDamage() {
        return damage;
    }

    public void setHeight(float height) {
        this.entityData.set(HEIGHT,height);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setAttackPreparationTime(int attackPreparationTime) {
        this.entityData.set(ATTACK_PREPARATION_TIME,attackPreparationTime);
    }

    public int getAttackPreparationTime() {
        return this.entityData.get(ATTACK_PREPARATION_TIME);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK_PREPARATION_TIME,20);
        this.entityData.define(HEIGHT,40f);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
        this.setAttackPreparationTime(tag.getInt("prepareTime"));
        this.setHeight(tag.getFloat("height"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
        tag.putInt("prepareTime",this.getAttackPreparationTime());
        tag.putFloat("height",this.getHeight());
    }
}
