package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahRotatingWeaponsHandler;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeburahEntity extends FDLivingEntity {

    protected GeburahRotatingWeaponsHandler rotatingWeaponsHandler;

    private GeburahRayController rayController;

    public GeburahEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.rotatingWeaponsHandler = new GeburahRotatingWeaponsHandler(this);
        this.rayController = new GeburahRayController(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide){
            this.particles();
        }

        if (level().isClientSide) {
            this.rotatingWeaponsHandler.tick();

            if (this.rotatingWeaponsHandler.finishedRotation()) {
                this.rotatingWeaponsHandler.rotateWeaponsBy(
                        level().random.nextFloat() * 200f - 100f,
                        20
                );
            }
        }else{
            this.getRayController().tick();
        }

    }

    public Vec3 getCorePosition(){
        return this.position().add(0,21.5f,0);
    }

    public GeburahRayController getRayController() {
        return rayController;
    }

    private void particles(){

        if (level().getGameTime() % 3 == 0) {
            BallParticleOptions ballParticle = BallParticleOptions.builder()
                    .color(1f,0.8f, 0.3f)
                    .scalingOptions(0, 0, 20)
                    .brightness(1)
                    .size(2f)
                    .build();

            Vec3 corePos = this.getCorePosition();

            level().addParticle(ballParticle, true, corePos.x, corePos.y, corePos.z, 0, 0, 0);

        }
    }

    public GeburahRotatingWeaponsHandler getRotatingWeaponsHandler() {
        return rotatingWeaponsHandler;
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
