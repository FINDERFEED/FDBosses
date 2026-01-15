package com.finderfeed.fdbosses.content.entities.geburah.chain_trap;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.util.Undismountable;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class GeburahChainTrapEntity extends Entity implements AutoSerializable, Undismountable {

    public static int CATCH_TIME = 10;
    public static int PULL_TIME = 4;

    public static final EntityDataAccessor<Integer> ENTITY_ABOUT_TO_TRAP = SynchedEntityData.defineId(GeburahChainTrapEntity.class, EntityDataSerializers.INT);

    @SerializableField
    private UUID entityAboutToTrap;

    @SerializableField
    private int catchTicks = 0;

    protected int catchingTime = -1;

    protected int pullingTime = -1;

    private Vec3 lastKnownTargetPos;


    public GeburahChainTrapEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (this.getPassengers().isEmpty()) {
                LivingEntity entityAboutToTrap = this.getEntityAboutToTrap();

                if (entityAboutToTrap != null) {

                    if (entityAboutToTrap.isDeadOrDying() || (entityAboutToTrap.getVehicle() instanceof GeburahChainTrapEntity e && e != this) ) {
                        this.setRemoved(RemovalReason.DISCARDED);
                        return;
                    }

                    if (catchingTime == -1){
                        catchingTime = CATCH_TIME;
                    }else if (catchingTime == 0){
                        entityAboutToTrap.startRiding(this, true);
                    }

                    this.getEntityData().set(ENTITY_ABOUT_TO_TRAP, entityAboutToTrap.getId());
                } else {
                    for (var entity : FDTargetFinder.getEntitiesInCylinder(LivingEntity.class, level(), this.position().add(0,-0.1,0),5,1.5f)){

                        if (entity instanceof Player player){
                            if (!BossUtil.isPlayerInSurvival(player)){
                                continue;
                            }
                        }

                        if (!(entity.getVehicle() instanceof GeburahChainTrapEntity)){
                            this.trapEntity(entity);
                            break;
                        }
                    }
                }
            }else{
                if (this.getPassengers().get(0) instanceof LivingEntity livingEntity){
                    if (livingEntity.isDeadOrDying()){
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
                this.tickCaughtEntity();
            }
        }else{

            if (level().getGameTime() % 2 == 0) {
                BallParticleOptions ballParticle = BallParticleOptions.builder()
                        .color(0.3f, 0.8f + random.nextFloat() * 0.2f, 1f)
                        .size(0.15f)
                        .brightness(3)
                        .scalingOptions(0, 0, 20)
                        .build();

                Vec3 rndOffset = new Vec3(random.nextFloat() * 0.2f + 0.3, 0, 0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);

                level().addParticle(ballParticle, this.getX() + rndOffset.x, this.getY(), this.getZ() + rndOffset.z, 0, 0.05f, 0);

            }
        }

        if (catchingTime != -1){
            catchingTime = Mth.clamp(catchingTime - 1, 0, CATCH_TIME);
        }

        if (pullingTime != -1){
            pullingTime = Mth.clamp(pullingTime - 1,0,PULL_TIME);
        }

    }

    private void tickCaughtEntity(){
        if (!this.getPassengers().isEmpty()) {
            var entity = this.getPassengers().get(0);
            int duration = 600;
            if (entity instanceof Player player) {
                duration = 60;
            }
            if (catchTicks > duration) {
                this.remove(RemovalReason.DISCARDED);
            }
            catchTicks++;
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor == ENTITY_ABOUT_TO_TRAP && this.getPassengers().isEmpty()){
            catchingTime = CATCH_TIME;
        }
    }

    public void trapEntity(LivingEntity entity){
        if (entity.distanceTo(this) > 20) return;
        this.entityAboutToTrap = entity.getUUID();
        this.getEntityData().set(ENTITY_ABOUT_TO_TRAP, entity.getId());
    }

    public LivingEntity getEntityAboutToTrap(){
        if (level() instanceof ServerLevel serverLevel){
            if (serverLevel.getEntity(entityAboutToTrap) instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }else{
            if (level().getEntity(this.entityData.get(ENTITY_ABOUT_TO_TRAP)) instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }
        return null;
    }



    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {

        Vec3 height = entity.getVehicleAttachmentPoint(this);

        if (this.lastKnownTargetPos == null){
            this.lastKnownTargetPos = entity.position().add(height);
        }

        if (pullingTime == -1){
            pullingTime = PULL_TIME;
        }

        float p = 1 - (pullingTime / (float) PULL_TIME);

        Vec3 target = this.position().add(height);

        Vec3 pos = FDMathUtil.interpolateVectors(lastKnownTargetPos, target, p);

        if (entity instanceof JudgementBirdEntity){
            pos = pos.add(0,1f,0);
        }

        return pos;
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity p_316322_) {
        return Vec3.ZERO;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public int getCatchingTime() {
        return catchingTime;
    }



    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ENTITY_ABOUT_TO_TRAP, -1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
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
