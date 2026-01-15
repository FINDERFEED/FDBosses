package com.finderfeed.fdbosses.content.entities.geburah.geburah_opening_floor;

import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GeburahOpeningFloor extends Entity {

    public GeburahOpeningFloor(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {

            if (tickCount > 500) {
                this.remove(RemovalReason.DISCARDED);
            }

            if (tickCount == 17) {
                level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.GEBURAH_FLOOR_OPENING.get(), SoundSource.AMBIENT, 3f, 1f);
            }

        } else {
            if (tickCount == 17) {


                for (int i = 0; i < 200;i ++){

                    float c = 0.3f + random.nextFloat() * 0.2f;

                    Vec3 v = new Vec3(FDEasings.easeOut(random.nextFloat()) * 5f,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                    BigSmokeParticleOptions bigSmokeParticle = BigSmokeParticleOptions.builder()
                            .size(1f + random.nextFloat() * 2f)
                            .lifetime(5 + random.nextInt(5),0,40 + random.nextInt(20))
                            .color(c,c,c,c)
                            .minSpeed(0.05f)
                            .friction(0.7f)
                            .build();

                    Vec3 pos = this.position().add(v).add(0,0.1f,0);

                    level().addParticle(bigSmokeParticle, true,pos.x,pos.y,pos.z,
                            random.nextFloat() * 4 - 2,
                            0,
                            random.nextFloat() * 4 - 2
                            );

                }
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

}
