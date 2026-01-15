package com.finderfeed.fdbosses.content.entities.geburah.chain_trap;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class ChainTrapSummonProjectile extends FDEntity {

    public FDTrailDataGenerator<ChainTrapSummonProjectile> trail;

    public static final double GRAVITY = Player.DEFAULT_BASE_GRAVITY;

    public static ChainTrapSummonProjectile summon(Level level, Vec3 pos, Vec3 targetPos){

        ChainTrapSummonProjectile projectile = new ChainTrapSummonProjectile(BossEntities.GEBURAH_CHAIN_TRAP_SUMMON_PROJECTILE.get(), level);

        Vec3 velocity = BossUtil.calculateMortarProjectileVelocity(pos,targetPos,-GRAVITY,20);
        projectile.setPos(pos);
        projectile.setDeltaMovement(velocity);

        level.addFreshEntity(projectile);

        return projectile;
    }

    public ChainTrapSummonProjectile(EntityType<?> type, Level level) {
        super(type, level);
        trail = new FDTrailDataGenerator<>(ChainTrapSummonProjectile::getPosition, 3, 0.01f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (tickCount > 2000){
                this.setRemoved(RemovalReason.DISCARDED);
            }
            this.tickAndSummonTrap();
        }else{
            trail.tick(this);
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
        BossUtil.applyGravity(this, this.getDefaultGravity());
    }

    private void tickAndSummonTrap(){
        ClipContext clipContext = new ClipContext(this.position(),this.position().add(this.getDeltaMovement()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
        var result = level().clip(clipContext);

        if (result.getType() != HitResult.Type.MISS){

            var location = result.getLocation();
            GeburahChainTrapEntity trapEntity = new GeburahChainTrapEntity(BossEntities.GEBURAH_CHAIN_TRAP.get(), level());
            float fraction = (float) (location.y - (int)location.y);

            Vec3 pos = location.add(0,0.01f,0);

            if (fraction > 0.25){
                pos = new Vec3(pos.x,Math.ceil(pos.y),pos.z);
            }

            trapEntity.setPos(pos);
            level().addFreshEntity(trapEntity);

            this.setRemoved(RemovalReason.DISCARDED);
        }
    }

    protected double getDefaultGravity() {
        return GRAVITY;
    }


    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

}
