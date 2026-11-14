package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DoomTrain extends FDEntity implements AutoSerializable {

    private static final EntityDataAccessor<Boolean> WAITING = SynchedEntityData.defineId(DoomTrain.class, EntityDataSerializers.BOOLEAN);

    @SerializableField
    private String patternId = TrainAttackPatterns.STRAIGHT;

    @SerializableField
    private Vec3 pathOrigin = Vec3.ZERO;

    @SerializableField
    private float trainSpeed = TrainAttack.DEFAULT_TRAIN_SPEED;

    @SerializableField
    private int departureDelay = 0;

    @SerializableField
    private double traveledDistance = 0;

    public DoomTrain(EntityType<?> type, Level level){
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static DoomTrain summon(Level level, EntityType<DoomTrain> type, Vec3 origin, String patternId, float speed, int delay){
        DoomTrain train = new DoomTrain(type, level);
        train.patternId = patternId;
        train.pathOrigin = origin;
        train.trainSpeed = speed;
        train.departureDelay = Math.max(0, delay);
        train.entityData.set(WAITING, train.departureDelay > 0);
        TrainAttackPattern pattern = train.getPattern();
        train.setPos(origin.add(pattern.getPosition(0)));
        train.lookAt(EntityAnchorArgument.Anchor.FEET, origin.add(pattern.getPosition(Math.min(speed / pattern.getPathLength(), 1))));
        level.addFreshEntity(train);
        return train;
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.level().isClientSide){
            TrainAttackPattern pattern = this.getPattern();
            if (this.departureDelay > 0){
                this.departureDelay--;
                this.setDeltaMovement(Vec3.ZERO);
                return;
            }
            this.entityData.set(WAITING, false);
            this.traveledDistance = Math.min(this.traveledDistance + this.trainSpeed, pattern.getPathLength());
            Vec3 nextPosition = this.pathOrigin.add(pattern.getPosition(this.getProgress()));
            this.setDeltaMovement(nextPosition.subtract(this.position()));

            if (this.traveledDistance >= this.getPattern().getPathLength()){
                this.discard();
            }
        }

        if (!this.isWaiting()){
            Vec3 nextPosition = this.position().add(this.getDeltaMovement());
            float targetYaw = FDMathUtil.yRotFromVector(this.getDeltaMovement());
            this.setYRot(this.yRotO + FDMathUtil.convertMCYRotationToNormal(targetYaw - this.yRotO));
            this.setPos(nextPosition);
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps){
        super.lerpTo(x, y, z, this.getYRot(), pitch, steps);
    }

    public TrainAttackPattern getPattern(){
        return TrainAttackPatterns.PATTERNS.get(this.patternId);
    }

    public double getProgress(){
        return this.traveledDistance / this.getPattern().getPathLength();
    }

    public double getTraveledDistance(){
        return traveledDistance;
    }

    public boolean isWaiting(){
        return this.entityData.get(WAITING);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){
        builder.define(WAITING, true);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (!TrainAttackPatterns.PATTERNS.containsKey(this.patternId)){
            this.patternId = TrainAttackPatterns.STRAIGHT;
        }
        this.entityData.set(WAITING, this.departureDelay > 0);
    }
}
