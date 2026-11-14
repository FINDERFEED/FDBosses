package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdbosses.content.entities.OwnableEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrainAttack extends OwnableEntity {

    private static final EntityDataAccessor<String> PATTERN_ID = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TRAIN_SPEED = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RAILS_APPEAR_TIME = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> READY_TIME = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.INT);

    public static final int RAILS_DISAPPEAR_TIME = 40;
    public static final int DEFAULT_RAILS_APPEAR_TIME = 60;
    public static final int DEFAULT_READY_TIME = 60;
    public static final float DEFAULT_TRAIN_SPEED = 0.5f;

    private static FDModel tunnelModel;
    private static FDModel straightRailModel;
    private static FDModel turnRailModel;

    public TrainAttack(EntityType<?> type, Level level){
        super(type, level);
        this.noPhysics = true;
    }

    public static FDModel getTunnelModel(){
        if (tunnelModel == null){
            tunnelModel = new FDModel(BossModels.DOOM_TRAIN_TUNNEL.get());
        }
        return tunnelModel;
    }

    public static FDModel getStraightRailModel(){
        if (straightRailModel == null){
            straightRailModel = new FDModel(BossModels.DOOM_TRAIN_RAILS.get());
        }
        return straightRailModel;
    }

    public static FDModel getTurnRailModel(){
        if (turnRailModel == null){
            turnRailModel = new FDModel(BossModels.DOOM_TRAIN_RAILS_TURN.get());
        }
        return turnRailModel;
    }

    public static TrainAttack summon(Level level, Vec3 position, String patternId, float trainSpeed, int railsAppearTime){
        return summon(level, position, patternId, trainSpeed, railsAppearTime, DEFAULT_READY_TIME);
    }

    public static TrainAttack summon(Level level, Vec3 position, String patternId, float trainSpeed, int railsAppearTime, int readyTime){
        validate(patternId, trainSpeed, railsAppearTime, readyTime);
        TrainAttack attack = new TrainAttack(BossEntities.TRAIN_ATTACK.get(), level);
        attack.setPos(position);
        attack.entityData.set(PATTERN_ID, patternId);
        attack.entityData.set(TRAIN_SPEED, trainSpeed);
        attack.entityData.set(RAILS_APPEAR_TIME, railsAppearTime);
        attack.entityData.set(READY_TIME, readyTime);
        level.addFreshEntity(attack);
        return attack;
    }

    private static void validate(String patternId, float speed, int appearTime, int readyTime){
        if (!TrainAttackPatterns.PATTERNS.containsKey(patternId)) throw new IllegalArgumentException("Unknown train pattern: " + patternId);
        if (!Float.isFinite(speed) || speed <= 0 || appearTime <= 0 || readyTime < 0){
            throw new IllegalArgumentException("Invalid train attack timings or speed");
        }
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.level().isClientSide){
            if (this.getPattern() == null || this.tickCount >= this.getTunnelDisappearStart() + this.getTunnelAnimationTime()){
                this.discard();
            }
        }
    }

    public String getPatternId(){
        return entityData.get(PATTERN_ID);
    }

    public TrainAttackPattern getPattern(){
        return TrainAttackPatterns.PATTERNS.get(this.getPatternId());
    }

    public float getTrainSpeed(){
        return entityData.get(TRAIN_SPEED);
    }

    public int getRailsAppearTime(){
        return entityData.get(RAILS_APPEAR_TIME);
    }

    public int getReadyTime(){
        return entityData.get(READY_TIME);
    }

    public int getTunnelAnimationTime(){
        return BossAnims.DOOM_TRAIN_TUNNEL_APPEAR.get().getAnimTime();
    }

    public int getRailsDisappearStart(){
        return this.getRailsAppearTime() + this.getReadyTime();
    }

    public int getTunnelDisappearStart(){
        return this.getRailsDisappearStart() + RAILS_DISAPPEAR_TIME;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){
        super.defineSynchedData(builder);
        builder.define(PATTERN_ID, TrainAttackPatterns.STRAIGHT);
        builder.define(TRAIN_SPEED, DEFAULT_TRAIN_SPEED);
        builder.define(RAILS_APPEAR_TIME, DEFAULT_RAILS_APPEAR_TIME);
        builder.define(READY_TIME, DEFAULT_READY_TIME);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.putString("pattern_id", this.getPatternId());
        tag.putFloat("train_speed", this.getTrainSpeed());
        tag.putInt("rails_appear_time", this.getRailsAppearTime());
        tag.putInt("ready_time", this.getReadyTime());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        String id = tag.getString("pattern_id");
        float speed = tag.getFloat("train_speed");
        int appearTime = tag.getInt("rails_appear_time");
        int readyTime = tag.getInt("ready_time");

        if (!TrainAttackPatterns.PATTERNS.containsKey(id)){
            id = TrainAttackPatterns.STRAIGHT;
        }
        if (!Float.isFinite(speed) || speed <= 0){
            speed = DEFAULT_TRAIN_SPEED;
        }
        if (appearTime <= 0){
            appearTime = DEFAULT_RAILS_APPEAR_TIME;
        }
        if (readyTime < 0){
            readyTime = DEFAULT_READY_TIME;
        }

        entityData.set(PATTERN_ID, id);
        entityData.set(TRAIN_SPEED, speed);
        entityData.set(RAILS_APPEAR_TIME, appearTime);
        entityData.set(READY_TIME, readyTime);
    }

}
