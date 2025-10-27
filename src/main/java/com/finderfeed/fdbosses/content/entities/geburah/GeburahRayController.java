package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.DecalParticleOptions;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.options.AlphaOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;

public class GeburahRayController {

    private List<Vec3> targets = new ArrayList<>();
    private int currentShotCharge = -1;
    private float damageRadius = 1;

    private GeburahEntity geburah;

    public GeburahRayController(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        if (geburah.level().isClientSide) return;

        Level level = geburah.level();

        if (currentShotCharge > 0){


            if (currentShotCharge >= 10) {

                if (currentShotCharge % 2 == 0) {
                    Vec3 pos = geburah.getCorePosition();

                    StripeParticleOptions stripeParticleOptions = StripeParticleOptions.createHorizontalCircling(
                            new FDColor(1f, 0.3f, 0.2f, 1f),new FDColor(1f, 0.6f, 0.2f, 1f),
                            new Vec3(0, 1, 0), (currentShotCharge / 2) * (FDMathUtil.FPI / 2), 0.3f, 10, 100, 0, 10, 0.5f,
                            0.5f, true, true
                    );

                    FDLibCalls.sendParticles((ServerLevel) level, stripeParticleOptions, pos, 200);
                }

                BossUtil.geburahRayChargeParticles((ServerLevel) level, geburah.getCorePosition(), 100, geburah);
            }


            currentShotCharge = Mth.clamp(currentShotCharge - 1,0,Integer.MAX_VALUE);
        }else if (currentShotCharge == 0){

            for (var target : this.targets){
               this.fireRayAtPos(target,1, damageRadius);
            }

            this.currentShotCharge = -1;
            this.targets.clear();
        }

    }

    public void fireRayAtPos(Vec3 target, float damage, float damageRadius){
        Vec3 start = geburah.getCorePosition();

        Level level = geburah.level();

        Vec3 between = target.subtract(start).normalize();

        Vec3 end = target.add(between.multiply(10,10,10));

        ClipContext clipContext = new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        BlockHitResult result = level.clip(clipContext);
        Vec3 location = result.getLocation();

        List<LivingEntity> targets = new ArrayList<>();
        targets.addAll(FDTargetFinder.getEntitiesInSphere(LivingEntity.class, level, target, damageRadius));
        targets.addAll(FDHelpers.traceEntities(level, start, location, 0.1f,(r)->r instanceof LivingEntity).stream().map(v->(LivingEntity)v).toList());

        for (var living : targets){
            living.hurt(level.damageSources().generic(),damage);
        }

        var options = GeburahRayOptions.builder()
                .end(location)
                .color(1f,0.8f,0.2f)
                .time(0,2,7)
                .width(0.5f)
                .build();

        FDLibCalls.sendParticles((ServerLevel) level, options, start, 200);

        if (result.getType() != HitResult.Type.MISS) {
            BossUtil.createOnEarthBlockExplosionEffect(level, result.getLocation(), between, 10, Blocks.STONE.defaultBlockState());

            Direction direction = result.getDirection();

            var decal = new DecalParticleOptions(BossParticles.GEBURAH_RAY_DECAL.get(),new Vec3(direction.getStepX(),direction.getStepY(),direction.getStepZ()),
                    AlphaOptions.builder()
                            .stay(20)
                            .out(10)
                            .build(),
                    3);

            FDLibCalls.sendParticles((ServerLevel) geburah.level(), decal, target.add(
                    direction.getStepX() * GeburahEntity.RAY_DECAL_OFFSET,
                    direction.getStepY() * GeburahEntity.RAY_DECAL_OFFSET,
                    direction.getStepZ() * GeburahEntity.RAY_DECAL_OFFSET),
                    120);

        }

        BossUtil.geburahRayParticles((ServerLevel) level, result.getLocation(), 200, between.reverse());

        PositionedScreenShakePacket.send((ServerLevel) level, FDShakeData.builder()
                .amplitude(1f)
                .outTime(5)
                .frequency(20)
                .build(), result.getLocation(), 60);




    }

    public void shoot(int shootTime, float damageRadius, boolean preparationParticles, List<Vec3> targets){
        if (targets.isEmpty()){
            return;
        }

        if (this.currentShotCharge == -1) {
            this.currentShotCharge = shootTime;
        }

        this.damageRadius = damageRadius;
        this.targets.clear();
        this.targets.addAll(targets);

        if (preparationParticles){
            for (var pos : targets){
                for (var dir : new HorizontalCircleRandomDirections(geburah.getRandom(), 4, 0)) {
                    ArcAttackPreparationParticleOptions options =
                            new ArcAttackPreparationParticleOptions(dir,damageRadius,FDMathUtil.FPI / 4, shootTime, Math.round(shootTime * 0.25f), 10,1f,0.8f,0.2f,0.3f);
                    FDLibCalls.sendParticles((ServerLevel) geburah.level(), options, pos.add(0,GeburahEntity.RAY_PREPARATION_PARTICLES_OFFSET,0), 120);
                }
            }
        }

    }

    public int getCurrentShotCharge() {
        return currentShotCharge;
    }
}
