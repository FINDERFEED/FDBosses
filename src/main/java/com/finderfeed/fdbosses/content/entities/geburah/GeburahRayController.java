package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdlib.FDLibCalls;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;

public class GeburahRayController {

    public int currentShotCharge = -1;

    private List<Vec3> targets = new ArrayList<>();

    private GeburahEntity geburah;

    public GeburahRayController(GeburahEntity geburah){
        this.geburah = geburah;
    }

    public void tick(){
        if (geburah.level().isClientSide) return;

        Level level = geburah.level();

        if (currentShotCharge > 0){
            currentShotCharge = Mth.clamp(currentShotCharge - 1,0,Integer.MAX_VALUE);
        }else if (currentShotCharge == 0){

            Vec3 start = geburah.getCorePosition();

            for (var target : this.targets){

                Vec3 between = target.subtract(start).normalize();

                Vec3 end = target.add(between.multiply(10,10,10));

                ClipContext clipContext = new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

                BlockHitResult result = level.clip(clipContext);

                Vec3 location = result.getLocation();

                var options = GeburahRayOptions.builder()
                        .end(location)
                        .color(1f,0.8f,0.2f)
                        .time(0,2,7)
                        .width(1f)
                        .build();

                FDLibCalls.sendParticles((ServerLevel) level, options, start, 200);
                BossUtil.createOnEarthBlockExplosionEffect(level, result.getLocation(), between);
                BossUtil.geburahRayParticles((ServerLevel) level, result.getLocation(), 200, between.reverse());

            }

            this.currentShotCharge = -1;
            this.targets.clear();
        }

    }

    public void shoot(int shootTime, List<Vec3> targets){
        this.currentShotCharge = shootTime;
        this.targets.addAll(targets);
    }

}
