package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon.clock_o_ball;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.BossSimpleProjectile;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ClockWyvernClockOBall extends BossSimpleProjectile {

    public static void summon(Entity owner, Vec3 pos, Vec3 movement){
        ClockWyvernClockOBall clockWyvernClockOBall = new ClockWyvernClockOBall(BossEntities.CLOCK_WYVERN_CLOCK_O_BALL.get(), owner.level());
        clockWyvernClockOBall.setOwner(owner);
        clockWyvernClockOBall.setPos(pos);
        clockWyvernClockOBall.setDeltaMovement(movement);
        owner.level().addFreshEntity(clockWyvernClockOBall);
    }

    protected FDTrailDataGenerator<ClockWyvernClockOBall> trail;

    public ClockWyvernClockOBall(EntityType<?> type, Level level) {
        super(type, level);
        trail = new FDTrailDataGenerator<>(Entity::getPosition, 4, 0.01f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

        }else{
            trail.tick(this);
        }
    }

    @Override
    public void onEntityHit(EntityHitResult entity) {
        this.explode(entity.getEntity(), entity.getLocation());
    }

    @Override
    public void onBlockHit(BlockHitResult result) {
        this.explode(null, result.getLocation());
    }

    private void explode(Entity entityHit, Vec3 hitPos) {
        if (entityHit == this.getOwner()) {
            return;
        }

        if (!level().isClientSide) {
            this.setRemoved(RemovalReason.DISCARDED);
            BossUtil.clockOBallExplosion(level(), hitPos);
        }
    }

}
