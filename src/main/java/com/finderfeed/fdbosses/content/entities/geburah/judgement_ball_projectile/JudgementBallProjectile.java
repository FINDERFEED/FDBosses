package com.finderfeed.fdbosses.content.entities.geburah.judgement_ball_projectile;

import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.trails.FDTrailDataGenerator;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class JudgementBallProjectile extends FDEntity implements AutoSerializable {

    @SerializableField
    public ProjectileMovementPath projectileMovementPath;

    @SerializableField
    public float damage = 2;

    public FDTrailDataGenerator<JudgementBallProjectile> trail;

    public static JudgementBallProjectile summon(Level level, ProjectileMovementPath projectileMovementPath){
        JudgementBallProjectile judgementBallProjectile = new JudgementBallProjectile(BossEntities.GEBURAH_JUDGEMENT_BALL.get(), level);

        judgementBallProjectile.setPos(projectileMovementPath.getPositions().getFirst());

        judgementBallProjectile.projectileMovementPath = projectileMovementPath;

        projectileMovementPath.tick(judgementBallProjectile);

        level.addFreshEntity(judgementBallProjectile);

        return judgementBallProjectile;
    }

    public JudgementBallProjectile(EntityType<?> type, Level level) {
        super(type, level);
        if (level.isClientSide){
            trail = new FDTrailDataGenerator<>((p,v) -> this.getPosition(v), 10,0.01f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.tickMovementPath();
            this.detectTargetsAndExplode();
        }else{
            this.trail.tick(this);
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        level().addParticle(new JudgementBallExplosionParticleOptions(this.getDeltaMovement(),10,2f),this.getX(),this.getY() + this.getBbHeight()/2,this.getZ(),0,0,0);

    }

    private void tickMovementPath(){
        if (projectileMovementPath == null){
            this.setRemoved(RemovalReason.DISCARDED);
            return;
        }
        if (!this.projectileMovementPath.isFinished()) {
            this.projectileMovementPath.tick(this);
        }else{
            this.setRemoved(RemovalReason.DISCARDED);
        }
    }

    private void detectTargetsAndExplode(){

        AABB box = this.getBoundingBox();
        var targets = level().getEntitiesOfClass(LivingEntity.class, box);

        if (!targets.isEmpty()){

            for (var target : targets){
                target.hurt(level().damageSources().generic(), damage);
            }
            this.setRemoved(RemovalReason.DISCARDED);

        }


    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

}
