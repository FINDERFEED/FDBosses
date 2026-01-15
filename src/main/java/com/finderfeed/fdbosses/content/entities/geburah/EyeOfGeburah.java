package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EyeOfGeburah extends EyeOfEnder {

    public EyeOfGeburah(EntityType<? extends EyeOfEnder> type, Level p_36958_) {
        super(type, p_36958_);
    }

    public EyeOfGeburah(Level level, double x, double y, double z) {
        this(BossEntities.EYE_OF_CHESED.get(),level);
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

            ParticleOptions options;
            if (level().random.nextFloat() > 0.25){
                options = BallParticleOptions.builder()
                        .size(0.15f)
                        .brightness(3)
                        .scalingOptions(2, 0, 20)
                        .color(0.2f,0.5f,1f)
                        .build();
            }else{
                options = ParticleTypes.END_ROD;
            }

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