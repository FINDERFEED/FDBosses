package com.finderfeed.fdbosses.content.entities.geburah.chain_trap;

import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDTargetFinder;
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

public class GeburahChainTrapEntity extends FDEntity implements AutoSerializable {

    public static final int CATCH_TIME = 10;
    public static final int PULL_TIME = 10;

    public static final EntityDataAccessor<Integer> ENTITY_ABOUT_TO_TRAP = SynchedEntityData.defineId(GeburahChainTrapEntity.class, EntityDataSerializers.INT);

    @SerializableField
    private UUID entityAboutToTrap;

    private int catchingTime = -1;

    private int pullingTime = -1;

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

                    if (entityAboutToTrap.isDeadOrDying()) {
                        this.setRemoved(RemovalReason.DISCARDED);
                        return;
                    }

                    if (catchingTime == -1){
                        catchingTime = CATCH_TIME;
                    }else if (catchingTime == 0){
                        entityAboutToTrap.startRiding(this, true);
                    }
                    catchingTime = Mth.clamp(catchingTime - 1, 0, CATCH_TIME);
                    this.getEntityData().set(ENTITY_ABOUT_TO_TRAP, entityAboutToTrap.getId());
                } else {
                    for (var player : FDTargetFinder.getEntitiesInCylinder(LivingEntity.class, level(), this.position().add(0,-0.1,0),10,10)){
                        if (!(player instanceof Player player1) || player1.isCrouching()){
                            this.trapEntity(player);
                            break;
                        }
                    }
                }
            }else{
                if (this.getPassengers().getFirst() instanceof LivingEntity livingEntity){
                    if (livingEntity.isDeadOrDying()){
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
            }
        }else{

        }

        if (pullingTime != -1){
            pullingTime = Mth.clamp(pullingTime - 1,0,PULL_TIME);
        }

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor == ENTITY_ABOUT_TO_TRAP && this.getPassengers().isEmpty()){
            catchingTime = CATCH_TIME;
        }
    }

    public void trapEntity(LivingEntity entity){
        if (entity.distanceTo(this) > 10) return;
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
    protected void defineSynchedData(SynchedEntityData.Builder b) {
        b.define(ENTITY_ABOUT_TO_TRAP, -1);
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
