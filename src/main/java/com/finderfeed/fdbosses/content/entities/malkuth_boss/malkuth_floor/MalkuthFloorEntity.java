package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class MalkuthFloorEntity extends Entity {

    public static final float RADIUS = 25f;

    public MalkuthFloorEntity(EntityType<?> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.summonParticles();
            this.particles();
        }
    }

    private void summonParticles(){
        if (tickCount == 1) {
            float segmentSize = 1f;

            float radius = RADIUS;

            for (float currentRad = segmentSize; currentRad <= radius; currentRad += segmentSize) {

                float currentArcLength = FDMathUtil.FPI * currentRad;

                for (float i = 0; i <= currentArcLength; i += segmentSize) {

                    float p = i / currentArcLength;

                    float currentAngle = FDMathUtil.FPI * p;

                    Vec3 currentOffset = new Vec3(currentRad, 0, 0)
                            .yRot(currentAngle);

                    Vec3 ppos = this.position().add(currentOffset);

                    ParticleOptions particleOptions;

                    float rnd = random.nextFloat();

                    float speedX = random.nextFloat() * 0.5f - 0.25f;
                    float speedY = random.nextFloat() * 0.5f + 0.5f;
                    float speedZ = random.nextFloat() * 0.5f - 0.25f;

                    if (rnd > 0.75){
                        particleOptions = ParticleTypes.LAVA;
                    }else if (rnd > 0.5f){
                        particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(),20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,
                                (float) Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                    }else if (rnd > 0.25f){
                        particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),20 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,
                                (float)Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                    }else{

                        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.getRandom(random));

                        FDColor colorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
                        FDColor colorEnd = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

                        speedX = 0;
                        speedY = 0;
                        speedZ = 0;

                        List<Vec3> offsets = List.of(
                                new Vec3(0,0,0.01),
                                new Vec3(random.nextFloat() * 4 - 2,2 + random.nextInt(2),random.nextFloat() * 4 - 2),
                                new Vec3(random.nextFloat() * 4 - 2,4 + random.nextInt(2),random.nextFloat() * 4 - 2)
                        );

                        particleOptions = StripeParticleOptions.builder()
                                .startColor(colorStart)
                                .endColor(colorEnd)
                                .lifetime(5 + random.nextInt(15))
                                .lod(20)
                                .scale(0.05f)
                                .stripePercentLength(0.5f)
                                .startInPercent(0.5f)
                                .endOutPercent(0.5f)
                                .offsets(offsets)
                                .build();
                    }

                    level().addParticle(particleOptions, true, ppos.x + random.nextFloat() * segmentSize - segmentSize/2, ppos.y, ppos.z + random.nextFloat() * segmentSize - segmentSize/2,
                            speedX,
                            speedY,
                            speedZ
                    );

                }

            }
        }
    }

    private void particles(){
        if (level().getGameTime() % 5 == 0) {
            float segmentSize = 3f;

            float radius = 25;

            for (float currentRad = segmentSize; currentRad <= radius; currentRad += segmentSize) {

                float currentArcLength = FDMathUtil.FPI * currentRad;

                for (float i = 0; i <= currentArcLength; i += segmentSize) {

                    float p = i / currentArcLength;

                    float currentAngle = FDMathUtil.FPI * p;

                    Vec3 currentOffset = new Vec3(currentRad, 0, 0)
                            .yRot(currentAngle);

                    Vec3 ppos = this.position().add(currentOffset);

                    ParticleOptions particleOptions;

                    if (random.nextBoolean()) {
                        particleOptions = ParticleTypes.LAVA;
                    } else {
                        float col = random.nextFloat() * 0.2f + 0.3f;
                        particleOptions = BigSmokeParticleOptions.builder()
                                .lifetime(0,0,40)
                                .size(2f + random.nextFloat())
                                .color(col, col, col)
                                .build();
                    }

                    level().addParticle(particleOptions, true, ppos.x + random.nextFloat() * segmentSize - segmentSize/2, ppos.y, ppos.z + random.nextFloat() * segmentSize - segmentSize/2, 0, 0, 0);

                }

            }
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

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
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }
}
