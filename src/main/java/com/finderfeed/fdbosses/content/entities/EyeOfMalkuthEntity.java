package com.finderfeed.fdbosses.content.entities;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EyeOfMalkuthEntity extends EyeOfEnder {

    public EyeOfMalkuthEntity(EntityType<? extends EyeOfEnder> type, Level p_36958_) {
        super(type, p_36958_);
    }

    public EyeOfMalkuthEntity(Level level, double x, double y, double z) {
        this(BossEntities.EYE_OF_MALKUTH.get(),level);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();


        if (level().isClientSide){
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX();
            double d1 = this.getY();
            double d2 = this.getZ();


            Vector3f color;
            if (level().random.nextFloat() > 0.5){
                color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.FIRE, random);
            }else{
                color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.ICE, random);
            }

            ParticleOptions options = BallParticleOptions.builder()
                    .size(0.2f)
                    .brightness(2)
                    .scalingOptions(2, 0, 20)
                    .color(color.x,color.y,color.z)
                    .build();


            this.level()
                    .addParticle(
                            options,
                            d0 - vec3.x * 0.5 + this.random.nextDouble() * 0.6 - 0.3,
                            d1 - vec3.y * 0.5,
                            d2 - vec3.z * 0.5 + this.random.nextDouble() * 0.6 - 0.3,
                            vec3.x * 0.25f,
                            vec3.y * 0.25f,
                            vec3.z * 0.25f
                    );
        }
    }

}
