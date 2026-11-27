package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.HashSet;
import java.util.Set;

public class DoomTrain extends FDEntity implements AutoSerializable, IEntityWithComplexSpawn {

    private static final EntityDataAccessor<Boolean> WAITING = SynchedEntityData.defineId(DoomTrain.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PATH_DISTANCE = SynchedEntityData.defineId(DoomTrain.class, EntityDataSerializers.FLOAT);

    @SerializableField
    private String patternId = TrainAttackPatterns.DIRECT_ROWS_A_PATH_1;

    @SerializableField
    private Vec3 pathOrigin = Vec3.ZERO;

    @SerializableField
    private float trainSpeed = TrainAttack.DEFAULT_TRAIN_SPEED;

    @SerializableField
    private int departureDelay = 0;

    @SerializableField
    private double traveledDistance = 0;

    private boolean oneShotSync = false;

    private float previousRoll;
    private float roll;
    private float rollForce;

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
            this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.DOOM_TRAIN_RIDE)
                    .build());
            if (this.departureDelay > 0){
                this.departureDelay--;
                this.setDeltaMovement(Vec3.ZERO);
                return;
            }
            this.entityData.set(WAITING, false);
        }

        if (!this.isWaiting()){
            TrainAttackPattern pattern = this.getPattern();
            double pathLength = pattern.getPathLength();
            double previousDistance = this.traveledDistance;
            this.traveledDistance = Math.min(previousDistance + this.trainSpeed, pathLength);

            Vec3 pathPosition = pattern.getPosition(this.traveledDistance / pathLength);
            Vec3 nextPosition = this.pathOrigin.add(pathPosition);
            Vec3 movement = nextPosition.subtract(this.position());
            this.setDeltaMovement(movement);
            if (movement.x != 0 || movement.z != 0){
                float targetYaw = FDMathUtil.yRotFromVector(movement);
                this.setYRot(this.yRotO + FDMathUtil.convertMCYRotationToNormal(targetYaw - this.yRotO));
            }

            if (this.level().isClientSide){
                double earlierDistance = Math.max(previousDistance - this.trainSpeed, 0);

                Vec3 earlierPathPosition = pattern.getPosition(earlierDistance / pathLength);
                Vec3 previousPathPosition = pattern.getPosition(previousDistance / pathLength);

                this.tickRoll(previousPathPosition.subtract(earlierPathPosition), pathPosition.subtract(previousPathPosition));
            }else{
                this.damageEntities(previousDistance);
            }
            this.setPos(nextPosition);
        }

        if (!this.level().isClientSide){
            if (!oneShotSync){
                this.entityData.set(PATH_DISTANCE, (float)this.traveledDistance);
                oneShotSync = true;
            }
            if (this.traveledDistance >= this.getPattern().getPathLength()){
                this.discard();
            }
        }

    }

    private void tickRoll(Vec3 previousMovement, Vec3 movement){
        float maxRoll = 15f;
        float terminalRollForce = 5f;

        float rollGravity = 2f / this.trainSpeed;
        float turnRollForce = 80f * this.trainSpeed;

        this.previousRoll = this.roll;

        float turnDot = (float)(previousMovement.z * movement.x - previousMovement.x * movement.z);

        this.rollForce += turnDot * turnRollForce;
        if (this.roll != 0){
            this.rollForce += this.roll > 0 ? -rollGravity : rollGravity;
        }

        this.rollForce = Mth.clamp(this.rollForce, -maxRoll, maxRoll);

        this.roll = Mth.clamp(this.roll + this.rollForce, -maxRoll, maxRoll);

        if ((this.previousRoll > 0 && this.roll <= 0) || (this.previousRoll < 0 && this.roll >= 0)){
            this.roll = 0;
            this.rollForce = Math.abs(this.rollForce) > terminalRollForce ? this.rollForce * 0.25f : 0;
        }else if (Math.abs(this.roll) == maxRoll){
            this.rollForce = 0;
        }
    }

    public float getRoll(float partialTicks){
        return Mth.lerp(partialTicks, this.previousRoll, this.roll);
    }

    private void damageEntities(double previousDistance){
        Set<LivingEntity> targets = new HashSet<>();
        Vec3 from = this.position();
        TrainAttackPattern pattern = this.getPattern();

        for (double distance = previousDistance; distance < this.traveledDistance;){
            double nextDistance = Math.min(distance + 1, this.traveledDistance);

            Vec3 to = this.pathOrigin.add(pattern.getPosition(nextDistance / pattern.getPathLength()));
            Vec3 movement = to.subtract(from);
            double length = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
            if (length > 0){
                float width = this.getBbWidth();
                Vec3 start = from.subtract(movement.scale(width / (2 * length)));
                targets.addAll(FDTargetFinder.getEntitiesInHorizontalBox(LivingEntity.class, this.level(), start,
                        new Vec2((float)movement.x, (float)movement.z), (float)length + width, width, this.getBbHeight(), LivingEntity::isAlive));
            }

            distance = nextDistance;
            from = to;
        }

        for (Entity entity : targets){
            entity.hurt(this.damageSources().magic(), 1);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor){
        super.onSyncedDataUpdated(accessor);
        if (this.level().isClientSide){
            if (accessor == PATH_DISTANCE){
                this.traveledDistance = this.entityData.get(PATH_DISTANCE);
                this.setPos(this.pathOrigin.add(this.getPattern().getPosition(this.getProgress())));
            }
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps){

    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer){
        buffer.writeUtf(this.patternId);
        buffer.writeDouble(this.pathOrigin.x);
        buffer.writeDouble(this.pathOrigin.y);
        buffer.writeDouble(this.pathOrigin.z);
        buffer.writeFloat(this.trainSpeed);
        buffer.writeDouble(this.traveledDistance);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buffer){
        this.patternId = buffer.readUtf();
        this.pathOrigin = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.trainSpeed = buffer.readFloat();
        this.traveledDistance = buffer.readDouble();
    }

    public TrainAttackPattern getPattern(){
        return TrainAttackPatterns.PATTERNS.get(this.patternId);
    }

    public double getProgress(){
        return this.traveledDistance / this.getPattern().getPathLength();
    }

    public double getProgress(float distance){
        return distance / this.getPattern().getPathLength();
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
        builder.define(PATH_DISTANCE, 0.0f);
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
            this.patternId = TrainAttackPatterns.DIRECT_ROWS_A_PATH_1;
        }
        this.entityData.set(WAITING, this.departureDelay > 0);
    }

}
