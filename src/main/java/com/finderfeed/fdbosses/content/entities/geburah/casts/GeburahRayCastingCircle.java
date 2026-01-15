package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahBossBuddy;
import com.finderfeed.fdbosses.content.entities.geburah.particles.geburah_ray.GeburahRayOptions;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class GeburahRayCastingCircle extends GeburahCastingCircle {


    public static GeburahRayCastingCircle summon(Level level, Vec3 pos, Vec3 dir){
        GeburahRayCastingCircle castCircle = new GeburahRayCastingCircle(BossEntities.GEBURAH_CASTING_CIRCLE_RAY.get(), level);
        castCircle.setPos(pos);
        castCircle.setDirection(dir);
        castCircle.setCastDuration(0);
        castCircle.getEntityData().set(COLOR, 0xff22ffff);
        level.addFreshEntity(castCircle);

        return castCircle;
    }

    public GeburahRayCastingCircle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void cast() {


        Vec3 end = this.position().add(this.getCastDirection().scale(20));

        ClipContext clipContext = new ClipContext(this.position(), end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        var res = level().clip(clipContext);
        end = res.getLocation();
        level().playSound(null, end.x,end.y,end.z, BossSounds.GEBURAH_RAY_SHOT.get(), SoundSource.HOSTILE, 2f, 1f);

        var entities = FDHelpers.traceEntities(level(), this.position(),end, 0.0f, (e)->e instanceof LivingEntity livingEntity && !(e instanceof GeburahBossBuddy));


        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().geburahConfig.judgementBirdDamage);

        for (var entity : entities){
            entity.hurt(BossDamageSources.GEBURAH_LASER_STRIKE_SOURCE, damage);
        }

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .amplitude(1f)
                        .outTime(5)
                        .frequency(20)
                .build(),end,40);

        GeburahRayOptions options = GeburahRayOptions.builder()
                .width(0.25f)
                .color(0.3f,0.8f,1f,1f)
                .end(end)
                .stay(2)
                .out(3)
                .build();

        FDLibCalls.sendParticles((ServerLevel) level(), options, this.position(), 120);

        if (res.getType() != HitResult.Type.MISS) {
            BossUtil.judgementBirdRayParticles((ServerLevel) level(), end, 100, this.position().subtract(end).normalize());
        }
    }

}
