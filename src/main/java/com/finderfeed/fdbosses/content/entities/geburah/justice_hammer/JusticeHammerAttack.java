package com.finderfeed.fdbosses.content.entities.geburah.justice_hammer;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticleOptions;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDTargetFinder;
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

    public static ComplexEasingFunction EASING;

    public static JusticeHammerAttack summon(Level level, Vec3 pos, Vec3 direction){
        JusticeHammerAttack justiceHammerAttack = new JusticeHammerAttack(BossEntities.JUSTICE_HAMMER.get(), level);

        justiceHammerAttack.setPos(pos);
        justiceHammerAttack.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(direction.multiply(1,0,1).normalize().scale(200)));


        level.addFreshEntity(justiceHammerAttack);
        return justiceHammerAttack;
    }

    public JusticeHammerAttack(EntityType<?> type, Level level) {
        super(type, level);
        if (EASING == null){
            int animTime = BossAnims.JUSTICE_HAMMER_SMACK.get().getAnimTime();
            EASING = ComplexEasingFunction.builder()
                    .addArea(20, FDEasings::easeOut)
                    .addArea(animTime - 20, FDEasings::one)
                    .addArea(10, FDEasings::reversedLinear)
                    .build();
        }

        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.JUSTICE_HAMMER_SMACK)
                        .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                .build());


    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

            int animTime = BossAnims.JUSTICE_HAMMER_SMACK.get().getAnimTime();

            float size = 12;

            if (this.tickCount == animTime - 3){
                this.smack(size);
            }else if (this.tickCount == animTime + 10){
                this.setRemoved(RemovalReason.DISCARDED);
            }

            if (this.tickCount == 1){
                Vec3 direction = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 pos = this.position().subtract(direction.scale(size/2)).add(0,-0.1,0);

                RectanglePreparationParticleOptions options = new RectanglePreparationParticleOptions(
                        direction, size, size / 2, 25, 10,10,0.3f,0.8f,1f,0.15f
                );

                FDLibCalls.sendParticles((ServerLevel) level(), options, pos.add(0,0.15f,0), 120);

            }

        }
    }

    private void smack(float size){

        Vec3 direction = this.getLookAngle().multiply(1,0,1).normalize();

        Vec3 pos = this.position().subtract(direction.scale(size/2)).add(0,-0.1,0);


        var entities = BossTargetFinder.getEntitiesInHorizontalBox(LivingEntity.class, level(), pos, new Vec2((float) direction.x,(float) direction.z), size, size,4, (living)->{
            return true;
        });

        for (var entity : entities){
            entity.hurt(level().damageSources().generic(), 100);
        }


    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

}
