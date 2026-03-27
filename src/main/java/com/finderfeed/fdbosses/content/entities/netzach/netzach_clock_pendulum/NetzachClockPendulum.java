package com.finderfeed.fdbosses.content.entities.netzach.netzach_clock_pendulum;

import com.finderfeed.fdbosses.content.util.AttackTimings;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class NetzachClockPendulum extends FDEntity {

    public static final int PENDULUM_APPEAR = 0;
    public static final int PENDULUM_WAIT_1 = 1;
    public static final int PENDULUM_ATTACK = 2;
    public static final int PENDULUM_WAIT_2 = 3;
    public static final int PENDULUM_DISAPPEAR = 4;

    public static final EntityDataAccessor<Float> PENDULUM_ATTACK_LENGTH = SynchedEntityData.defineId(NetzachClockPendulum.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<AttackTimings> PENDULUM_ATTACK_TIMINGS = SynchedEntityData.defineId(NetzachClockPendulum.class, BossEntityDataSerializers.ATTACK_TIMINGS.get());

    public static void summon(Level level, Vec3 startPos, Vec3 direction, float attackLength, int attackDuration){
        NetzachClockPendulum pendulum = new NetzachClockPendulum(BossEntities.NETZACH_CLOCK_PENDULUM.get(), level);
        pendulum.setAttackLength(attackLength);
        pendulum.setPos(startPos);
        pendulum.lookAt(EntityAnchorArgument.Anchor.FEET, startPos.add(direction));

        AttackTimings attackTimings = new AttackTimings(20)
                .addAttackTiming(10)
                .addAttackTiming(attackDuration)
                .addAttackTiming(10)
                .addAttackTiming(20);

        pendulum.getEntityData().set(PENDULUM_ATTACK_TIMINGS, attackTimings);

        level.addFreshEntity(pendulum);
    }

    public NetzachClockPendulum(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            var timings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);
            if (tickCount > timings.getFullTiming()){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private AttackTimings defaultTimings(){
        return new AttackTimings(20)
                .addAttackTiming(10)
                .addAttackTiming(100)
                .addAttackTiming(10)
                .addAttackTiming(20);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PENDULUM_ATTACK_LENGTH, 10f);
        builder.define(PENDULUM_ATTACK_TIMINGS,
                this.defaultTimings()
        );
    }

    public void setAttackLength(float attackLength){
        this.getEntityData().set(PENDULUM_ATTACK_LENGTH, attackLength);
    }

    public float getAttackLength(){
        return this.getEntityData().get(PENDULUM_ATTACK_LENGTH);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("attackLength", this.getAttackLength());

        AttackTimings attackTimings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);
        CompoundTag compoundTag = new CompoundTag();
        attackTimings.autoSave(compoundTag);
        tag.put("attackTimings", compoundTag);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAttackLength(tag.getFloat("attackLength"));

        if (tag.contains("attackTimings")) {
            AttackTimings attackTimings = new AttackTimings();
            attackTimings.autoLoad(tag.getCompound("attackTimings"));
            this.getEntityData().set(PENDULUM_ATTACK_TIMINGS, attackTimings);
        }else{
            this.getEntityData().set(PENDULUM_ATTACK_TIMINGS, this.defaultTimings());
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

}
