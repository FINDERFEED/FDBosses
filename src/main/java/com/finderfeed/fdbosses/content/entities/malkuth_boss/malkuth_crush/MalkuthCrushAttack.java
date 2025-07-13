package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
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
import org.joml.Vector3f;

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


                if (random.nextFloat() > 0.66f) {
                    level().addParticle(ParticleTypes.LAVA, true,
                            this.getX() + randomAngle.x * m,
                            this.getY(),
                            this.getZ() + randomAngle.y * m,
                            0, 0, 0
                    );
                }else{

                    ParticleOptions particleOptions;

                    if (random.nextFloat() > 0.5f){
                        particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(),20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,
                                (float)Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                    }else{
                        particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,
                                (float)Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                    }

                    float hspeed = (0.1f + random.nextFloat() * 0.05f);
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




            Vector2d randomAngle = this.randomAngle(angle,currentAngle);

            int cr = random.nextInt(50);

            BigSmokeParticleOptions smokeParticleOptions = BigSmokeParticleOptions.builder()
                    .size(1f + random.nextFloat() * 0.5f)
                    .friction(0.75f)
                    .minSpeed(0.025f)
                    .color(50 + cr,50 + cr,50 + cr)
                    .lifetime(0,5,30 + random.nextInt(5))
                    .build();

            float rndOffs = random.nextFloat() * 0.5f;
            float rndSpeed = random.nextFloat() * 1 + 0.25f;

            level().addParticle(smokeParticleOptions, true,
                    this.getX() + randomAngle.x * rndOffs,
                    this.getY() + random.nextFloat() * 0.5f,
                    this.getZ() + randomAngle.y * rndOffs,
                    randomAngle.x * rndSpeed,
                    random.nextFloat() * 0.025f + 0.025f,
                    randomAngle.y * rndSpeed
            );





        }





        for (int i = 0; i < 15;i++) {
            this.stripeParticles(MalkuthAttackType.FIRE);
            this.stripeParticles(MalkuthAttackType.ICE);
        }
    }

    private void stripeParticles(MalkuthAttackType type){


        float rndRadius = 1 + FDEasings.easeOut(random.nextFloat()) * 4f;
        Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

        Vec3 dir = rnd.normalize();

        float startOffsetRand = 0.1f + random.nextFloat() * 0.5f;
        Vec3 startOffset = dir.multiply(startOffsetRand,startOffsetRand,startOffsetRand);

        Vec3 stripePos = this.position().add(startOffset);

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(type);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstPointOffset = 2f + random.nextFloat() * 1f;

        StripeParticleOptions stripeParticleOptions = new StripeParticleOptions(fireColorStart,fireColor, 5 + random.nextInt(10), 50, 0.05f, 0.75f,
                new Vec3(0.01f,0,0),
                dir.multiply(firstPointOffset,0,firstPointOffset).add(0,0.5,0),
                rnd.add(0,1.5f + random.nextFloat() * 2,0)
        );

        level().addParticle(stripeParticleOptions, true, stripePos.x,stripePos.y,stripePos.z,0,0,0);

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
