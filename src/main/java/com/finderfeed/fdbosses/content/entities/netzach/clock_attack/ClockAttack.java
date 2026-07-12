package com.finderfeed.fdbosses.content.entities.netzach.clock_attack;

import com.finderfeed.fdbosses.content.util.AttackTimings;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class ClockAttack extends FDEntity implements AutoSerializable {

    private static FDModel model;

    public static final EntityDataAccessor<AttackTimings> ATTACK_TIMINGS = SynchedEntityData.defineId(ClockAttack.class, BossEntityDataSerializers.ATTACK_TIMINGS.get());
    public static final EntityDataAccessor<Float> ROTATION_ANGLE = SynchedEntityData.defineId(ClockAttack.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> FINISHED_ROTATION = SynchedEntityData.defineId(ClockAttack.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity cachedTarget;
    private UUID target;

    public float previousRotationAngle = -1;

    @SerializableField
    public float rotationAngle;

    @SerializableField
    public int afterRotatedTicks = -1;

    private int passDelay = 5;

    @SerializableField
    private boolean hasRotatedThroughPlayerOnce = false;

    private Vec3 previousTargetPos;

    @SerializableField
    private AttackTimings attackTimings = new AttackTimings()
            .addAttackTiming(40)
            .addAttackTiming(100);

    public static FDModel getCLientModel(){
        if (model == null){
            model = new FDModel(BossModels.CLOCK_ATTACK_ARROW.get());
        }
        return model;
    }

    public static ClockAttack summon(Level level, Vec3 pos, int prepareTime, int rotateTime, LivingEntity target){
        ClockAttack clockAttack = new ClockAttack(BossEntities.CLOCK_ATTACK.get(), level);
        clockAttack.setPos(pos);
        clockAttack.previousTargetPos = target.position();
        clockAttack.cachedTarget = target;
        clockAttack.target = target.getUUID();

        Vec3 b = target.position().subtract(pos);
        var angle = Math.atan2(b.z, b.x) + FDMathUtil.FPI * 2 + FDMathUtil.FPI / 16;
        clockAttack.rotationAngle = (float) angle;
        clockAttack.getEntityData().set(ROTATION_ANGLE, (float) angle);

        clockAttack.setAttackTimings(new AttackTimings()
                .addAttackTiming(prepareTime)
                .addAttackTiming(rotateTime));
        level.addFreshEntity(clockAttack);
        return clockAttack;
    }

    public ClockAttack(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()){

            if (target == null){
                this.setRemoved(RemovalReason.DISCARDED);
                return;
            }

            if (cachedTarget == null){
                cachedTarget = (LivingEntity) ((ServerLevel)level()).getEntity(target);
                if (cachedTarget == null){
                    this.setRemoved(RemovalReason.DISCARDED);
                    return;
                }
                previousTargetPos = cachedTarget.position();
            }


            this.previousRotationAngle = rotationAngle;

            if (this.afterRotatedTicks != -1){
                this.entityData.set(FINISHED_ROTATION, true);
                float rotationPercent = this.attackTimings.getAttackTimingPercent(1, afterRotatedTicks);

                float rotationSpeed = FDEasings.squareHill(rotationPercent) * FDMathUtil.FPI / 14;
                this.rotationAngle += rotationSpeed;
                this.getEntityData().set(ROTATION_ANGLE, this.rotationAngle);

                if (rotationSpeed > 0){
                    float halfAngle = (this.rotationAngle - this.previousRotationAngle) / 2;

                    Vec3 dir = new Vec3(1,0,0).yRot((this.rotationAngle - halfAngle));
                    var targets = FDTargetFinder.getEntitiesInArc(LivingEntity.class, level(), this.position().add(0,-1,0),
                            new Vec2((float) dir.x, (float) dir.z),
                            this.rotationAngle - this.previousRotationAngle,
                            1.5f,34
                            );

                    for (var target : targets){
                        target.hurt(level().damageSources().magic(), 1);
                    }

                }

                if (rotationPercent >= 1){
                    this.remove(RemovalReason.DISCARDED);
                }



                afterRotatedTicks++;
            }else{
                this.rotateToTarget(FDMathUtil.FPI / 12);
            }


            previousTargetPos = cachedTarget.position();
        }else{

            previousRotationAngle = rotationAngle;
            rotationAngle = this.getEntityData().get(ROTATION_ANGLE);

            if (this.getEntityData().get(FINISHED_ROTATION)){
                this.afterRotatedTicks++;
            }

        }
    }

    private Vec3 cachedPos = null;

    private void rotateToTarget(float rotationSpeed){

        Vec3 pos = this.getApproximatedTargetPos();

        if (cachedPos != null){
            pos = cachedPos;
        }

        Vec3 b = pos.subtract(this.position());

        var angle = (float) -Math.atan2(b.z, b.x);
        var transformedAngle = this.rotationAngle - angle;
        var next = transformedAngle + rotationSpeed;
        Vec3 prevVec = new Vec3(1,0,0).yRot(-transformedAngle);
        Vec3 nextVec = new Vec3(1,0,0).yRot(-next);

        if (this.hasRotatedThroughPlayerOnce){
            if (passDelay <= 0){

                var modded = transformedAngle % (FDMathUtil.FPI * 2);
                var diff = FDMathUtil.FPI * 2 - modded;

                float speedCoefficient = (float) (1 - Math.exp(-2 * diff));

                this.rotationAngle += rotationSpeed * speedCoefficient;

                if (speedCoefficient < 0.001){
                    this.afterRotatedTicks = 0;
                    this.entityData.set(FINISHED_ROTATION, true);
                }else if (speedCoefficient < 0.9 && cachedPos == null){
                    cachedPos = pos;
                }

            }else{
                passDelay--;
                this.rotationAngle = next + angle;
            }
        }else{
            if (prevVec.z <= 0 && nextVec.z >= 0){
                this.hasRotatedThroughPlayerOnce = true;
                passDelay = 5;
            }
            this.rotationAngle = next + angle;
        }

        this.entityData.set(ROTATION_ANGLE, this.rotationAngle);

    }

    public Vec3 getApproximatedTargetPos(){

        Vec3 currentPos = this.cachedTarget.position();
        Vec3 oldPos = this.previousTargetPos;

        Vec3 b = currentPos.subtract(oldPos);

        var scale = this.attackTimings.getAttackLength(0) * 1.9f;

        return currentPos.add(b.scale(scale));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ATTACK_TIMINGS, new AttackTimings()
                .addAttackTiming(40)
                .addAttackTiming(100));
        builder.define(FINISHED_ROTATION, false);
        builder.define(ROTATION_ANGLE, 0f);
    }

    private void setAttackTimings(AttackTimings attackTimings){
        this.attackTimings = attackTimings;
        this.entityData.set(ATTACK_TIMINGS, attackTimings);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_20059_) {
        super.onSyncedDataUpdated(p_20059_);
        if (p_20059_ == ATTACK_TIMINGS) {
            this.attackTimings = this.entityData.get(ATTACK_TIMINGS);
        }else if (p_20059_ == ROTATION_ANGLE){
            if (previousRotationAngle == -1){
                rotationAngle = this.getEntityData().get(ROTATION_ANGLE);
                previousRotationAngle = this.getEntityData().get(ROTATION_ANGLE);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad("edata", tag);
        if (tag.contains("target")){
            this.target = tag.getUUID("target");
        }
        this.getEntityData().set(ATTACK_TIMINGS, attackTimings);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave("edata", tag);
        if (this.target != null) {
            tag.putUUID("target", this.target);
        }
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return AABB.INFINITE;
    }

}
