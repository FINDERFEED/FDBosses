package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class GeburahEntity extends FDLivingEntity {

    public GeburahEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide){
            this.particles();
        }

    }

    private void particles(){

        if (level().getGameTime() % 3 == 0) {
            BallParticleOptions ballParticle = BallParticleOptions.builder()
                    .color(1f,0.8f, 0.3f)
                    .scalingOptions(0, 0, 20)
                    .brightness(1)
                    .size(2f)
                    .build();

            level().addParticle(ballParticle, true, this.getX(), this.getY() + 21.5f, this.getZ(), 0, 0, 0);

        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

}
