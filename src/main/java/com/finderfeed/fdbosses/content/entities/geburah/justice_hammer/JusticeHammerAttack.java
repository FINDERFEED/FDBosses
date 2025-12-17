package com.finderfeed.fdbosses.content.entities.geburah.justice_hammer;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticleOptions;
import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticleOptions;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class JusticeHammerAttack extends FDEntity {

    public static int ATTACK_WIDTH = 7;
    public static int ATTACK_LENGTH = 13;

    public static ComplexEasingFunction EASING;
    protected static ComplexEasingFunction EASING2;

    public static JusticeHammerAttack summon(Level level, Vec3 pos, Vec3 direction){
        JusticeHammerAttack justiceHammerAttack = new JusticeHammerAttack(BossEntities.JUSTICE_HAMMER.get(), level);

        ATTACK_LENGTH = 12;
        ATTACK_WIDTH = 9;

        justiceHammerAttack.setPos(pos);
        justiceHammerAttack.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(direction.multiply(1,0,1).normalize().scale(200)));


        level.addFreshEntity(justiceHammerAttack);
        return justiceHammerAttack;
    }

    public JusticeHammerAttack(EntityType<?> type, Level level) {
        super(type, level);
        if (EASING == null || EASING2 == null || true){
            int animTime = BossAnims.JUSTICE_HAMMER_SMACK.get().getAnimTime();

            int fadeOut = 10;
            int fadeIn = 30;

            EASING = ComplexEasingFunction.builder()
                    .addArea(fadeIn, FDEasings::easeIn)
                    .addArea(animTime - fadeIn, FDEasings::one)
                    .addArea(fadeOut, FDEasings::reversedLinear)
                    .build();

            EASING2 = ComplexEasingFunction.builder()
                    .addArea(animTime - 5, (f)->0f)
                    .addArea(2, FDEasings::easeOut)
                    .addArea(fadeOut - 1, FDEasings::reversedEaseOut)
                    .build();

        }

        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.JUSTICE_HAMMER_SMACK)
                        .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                .build());


    }

    @Override
    public void tick() {
        super.tick();
        int animTime = BossAnims.JUSTICE_HAMMER_SMACK.get().getAnimTime();
        int attackTime = animTime - 3;
        if (!level().isClientSide){



            if (this.tickCount == attackTime - 3){
                this.smack(ATTACK_LENGTH, ATTACK_WIDTH);
            }else if (this.tickCount == animTime + 10){
                this.setRemoved(RemovalReason.DISCARDED);
            }

            if (this.tickCount == 1){
                Vec3 direction = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 pos = this.position().subtract(direction.scale(ATTACK_LENGTH/2f)).add(0,-0.1,0);

                RectanglePreparationParticleOptions options = new RectanglePreparationParticleOptions(
                        direction, ATTACK_LENGTH, ATTACK_WIDTH / 2f, BossAnims.JUSTICE_HAMMER_SMACK.get().getAnimTime() - 10, 10,10,0.3f,0.8f,1f,0.15f
                );

                FDLibCalls.sendParticles((ServerLevel) level(), options, pos.add(0,0.15f,0), 120);

            }

        }else{
            if (this.tickCount == attackTime - 1){

                float r = 0.3f;
                float g = 0.7f;
                float b = 1f;

                for (int i = 0; i < 200; i++){

                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .size(0.15f)
                            .scalingOptions(0,0,5 + random.nextInt(5))
                            .friction(0.6f)
                            .brightness(2)
                            .color(r,g,b)
                            .build();

                    float xOffset = random.nextFloat() * ATTACK_WIDTH - ATTACK_WIDTH / 2f;
                    float zOffset = random.nextFloat() * ATTACK_LENGTH - ATTACK_LENGTH / 2f;

                    Vec3 offs = new Vec3(xOffset,0,zOffset).yRot((float)Math.toRadians(-this.getYRot()));

                    level().addParticle(ballParticleOptions,
                            this.getX() + offs.x,
                            this.getY(),
                            this.getZ() + offs.z,
                            0, random.nextFloat(),0
                    );



                }

            }
        }
    }

    private void smack(float lsize, float wsize){

        Vec3 direction = this.getLookAngle().multiply(1,0,1).normalize();

        Vec3 pos = this.position().subtract(direction.scale(lsize/2)).add(0,-0.1,0);


        var entities = BossTargetFinder.getEntitiesInHorizontalBox(LivingEntity.class, level(), pos, new Vec2((float) direction.x,(float) direction.z), lsize, wsize,4, (living)->{
            return true;
        });

        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().geburahConfig.justiceHammerDamage);
        for (var entity : entities){
            entity.hurt(BossDamageSources.GEBURAH_JUSTICE_HAMMER_SOURCE, damage);
        }

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .outTime(7)
                        .stayTime(0)
                        .inTime(0)
                        .amplitude(0.5f)
                        .frequency(10f)
                .build(),this.position(),60);


    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

}
