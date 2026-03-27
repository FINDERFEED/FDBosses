package com.finderfeed.fdbosses.content.entities.netzach.netzach_clock_pendulum;

import com.finderfeed.fdbosses.init.BossEntities;
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

    public static final int PREPARE_TIME = 20;

    public static final EntityDataAccessor<Float> PENDULUM_ATTACK_LENGTH = SynchedEntityData.defineId(NetzachClockPendulum.class, EntityDataSerializers.FLOAT);

    public static void summon(Level level, Vec3 startPos, Vec3 direction, float attackLength){
        NetzachClockPendulum pendulum = new NetzachClockPendulum(BossEntities.NETZACH_CLOCK_PENDULUM.get(), level);
        pendulum.setAttackLength(attackLength);
        pendulum.setPos(startPos);
        pendulum.lookAt(EntityAnchorArgument.Anchor.FEET, startPos.add(direction));
        level.addFreshEntity(pendulum);
    }

    public NetzachClockPendulum(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        p_326003_.define(PENDULUM_ATTACK_LENGTH, 10f);
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
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAttackLength(tag.getFloat("attackLength"));
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
