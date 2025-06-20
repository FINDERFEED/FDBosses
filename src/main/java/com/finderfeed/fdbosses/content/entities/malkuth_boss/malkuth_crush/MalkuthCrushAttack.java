package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector2f;

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

        int count = 30;

        float angle = FDMathUtil.FPI * 2 / count;

        for (int i = 0; i < count;i++){

            float currentAngle = angle * i;

            // ball particles
            for (float dist = 0; dist < 3;dist+= 0.5f){
                float m = dist + random.nextFloat() * 0.25f;

                Vector2d randomAngle = this.randomAngle(angle,currentAngle);

                float r;
                float g;
                float b;

                if (random.nextFloat() > 0.5f){
                    r = random.nextFloat() * 0.2f;
                    g = 0.7f + random.nextFloat() * 0.1f;
                    b = 0.9f + random.nextFloat() * 0.1f;
                }else{
                    r = 0.9f + random.nextFloat() * 0.1f;
                    g = 0.2f + random.nextFloat() * 0.2f;
                    b = random.nextFloat() * 0.2f;
                }

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(0.2f + random.nextFloat() * 0.2f)
                        .color(r,g,b)
                        .scalingOptions(0,0,10 + random.nextInt(4))
                        .friction(0.6f)
                        .build();

                float hspeed = (0.1f + random.nextFloat() * 0.2f) * 2;
                float vspeed = (0.05f + random.nextFloat() * 0.5f) * 2;
                level().addParticle(ballParticleOptions,true,
                        this.getX() + randomAngle.x * m,
                        this.getY(),
                        this.getZ() + randomAngle.y * m,
                        randomAngle.x * hspeed,
                        vspeed,
                        randomAngle.y * hspeed
                );
            }

            //lava and shit
            for (float dist = 1; dist < 3.5f; dist += 1f){
                float m = dist + random.nextFloat() * 0.5f;
                Vector2d randomAngle = this.randomAngle(angle,currentAngle);

                if (random.nextFloat() > 0.5f) {
                    level().addParticle(ParticleTypes.LAVA, true,
                            this.getX() + randomAngle.x * m,
                            this.getY(),
                            this.getZ() + randomAngle.y * m,
                            0, 0, 0
                    );
                }else{

                    ParticleOptions particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(),20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,
                            (float)Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);

                    float hspeed = (0.1f + random.nextFloat() * 0.1f);
                    float vspeed = (0.4f + random.nextFloat() * 0.25f);
                    level().addParticle(particleOptions,true,
                            this.getX() + randomAngle.x * m,
                            this.getY(),
                            this.getZ() + randomAngle.y * m,
                            randomAngle.x * hspeed,
                            vspeed,
                            randomAngle.y * hspeed
                    );

                }

            }



        }

    }

    private Vector2d randomAngle(float angle, float currentAngle){
        float a = currentAngle + (random.nextFloat() * 2 - 1) * angle / 2;
        return new Vector2d(
                Math.sin(a),
                Math.cos(a)
        );
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
