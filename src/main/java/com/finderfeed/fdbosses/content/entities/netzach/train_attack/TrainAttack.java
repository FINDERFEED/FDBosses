package com.finderfeed.fdbosses.content.entities.netzach.train_attack;

import com.finderfeed.fdbosses.content.entities.OwnableEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class TrainAttack extends OwnableEntity implements AutoSerializable {

    private static final EntityDataAccessor<String> PATTERN_ID = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TRAIN_SPEED = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RAILS_APPEAR_TIME = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> READY_TIME = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DISAPPEARING = SynchedEntityData.defineId(TrainAttack.class, EntityDataSerializers.BOOLEAN);

    public static final int RAILS_DISAPPEAR_TIME = 40;
    public static final int DEFAULT_RAILS_APPEAR_TIME = 60;
    public static final int DEFAULT_READY_TIME = 60;
    public static final int DEFAULT_WAGON_COUNT = 3;
    public static final float DEFAULT_TRAIN_SPEED = 0.5f;
    public static final double CAR_SPACING = 3.75;

    private static FDModel tunnelModel;
    private static FDModel straightRailModel;
    private static FDModel turnRailModel;

    @SerializableField
    private UUID lastCarUuid;

    @SerializableField
    private int wagonCount = DEFAULT_WAGON_COUNT;

    private int disappearTicksLeft;

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
        return summon(level, position, patternId, trainSpeed, railsAppearTime, readyTime, DEFAULT_WAGON_COUNT);
    }

    public static TrainAttack summon(Level level, Vec3 position, String patternId, float trainSpeed, int railsAppearTime, int readyTime, int wagonCount){
        validate(patternId, trainSpeed, railsAppearTime, readyTime);
        TrainAttack attack = new TrainAttack(BossEntities.TRAIN_ATTACK.get(), level);
        attack.setPos(position);
        attack.entityData.set(PATTERN_ID, patternId);
        attack.entityData.set(TRAIN_SPEED, trainSpeed);
        attack.entityData.set(RAILS_APPEAR_TIME, railsAppearTime);
        attack.entityData.set(READY_TIME, readyTime);
        attack.wagonCount = Math.max(0, wagonCount);
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
        if (this.isDisappearing()){
            this.disappearTicksLeft--;
        }
        if (!this.level().isClientSide){

            if (this.getPattern() == null){
                this.discard();
                return;
            }

            if (this.isDisappearing()){
                if (this.disappearTicksLeft <= 0){
                    this.discard();
                }
            }else if (this.lastCarUuid == null){
                int delay = Math.max(0, this.getRailsAppearTime() + this.getReadyTime() - this.tickCount);
                DoomTrain train = DoomTrain.summon(this.level(), BossEntities.DOOM_TRAIN.get(), this.position(), this.getPatternId(), this.getTrainSpeed(), delay);
                this.lastCarUuid = train.getUUID();
                for (int i = 1; i <= this.wagonCount; i++){
                    int wagonDelay = delay + (int)Math.ceil(i * CAR_SPACING / this.getTrainSpeed());
                    DoomTrain wagon = DoomTrain.summon(this.level(), BossEntities.DOOM_TRAIN_WAGON.get(), this.position(), this.getPatternId(), this.getTrainSpeed(), wagonDelay);
                    this.lastCarUuid = wagon.getUUID();
                }
            }else{
                Entity lastCar = ((ServerLevel)this.level()).getEntity(this.lastCarUuid);
                if (lastCar == null || !lastCar.isAlive()){
                    this.entityData.set(DISAPPEARING, true);
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor){
        super.onSyncedDataUpdated(accessor);
        if (accessor == DISAPPEARING){
            if (this.isDisappearing()){
                this.disappearTicksLeft = RAILS_DISAPPEAR_TIME + this.getTunnelAnimationTime();
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

    public int getWagonCount(){
        return wagonCount;
    }

    public int getTunnelAnimationTime(){
        return BossAnims.DOOM_TRAIN_TUNNEL_APPEAR.get().getAnimTime();
    }

    public boolean isDisappearing(){
        return this.entityData.get(DISAPPEARING);
    }

    public int getDisappearTicksLeft(){
        return disappearTicksLeft;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){
        super.defineSynchedData(builder);
        builder.define(PATTERN_ID, TrainAttackPatterns.STRAIGHT);
        builder.define(TRAIN_SPEED, DEFAULT_TRAIN_SPEED);
        builder.define(RAILS_APPEAR_TIME, DEFAULT_RAILS_APPEAR_TIME);
        builder.define(READY_TIME, DEFAULT_READY_TIME);
        builder.define(DISAPPEARING, false);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        tag.putString("pattern_id", this.getPatternId());
        tag.putFloat("train_speed", this.getTrainSpeed());
        tag.putInt("rails_appear_time", this.getRailsAppearTime());
        tag.putInt("ready_time", this.getReadyTime());
        tag.putBoolean("disappearing", this.isDisappearing());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.wagonCount = Math.max(0, this.wagonCount);
        String id = tag.getString("pattern_id");
        float speed = tag.getFloat("train_speed");
        int appearTime = tag.getInt("rails_appear_time");
        int readyTime = tag.getInt("ready_time");

        if (!TrainAttackPatterns.PATTERNS.containsKey(id)){
            id = TrainAttackPatterns.STRAIGHT;
        }

        entityData.set(PATTERN_ID, id);
        entityData.set(TRAIN_SPEED, speed);
        entityData.set(RAILS_APPEAR_TIME, appearTime);
        entityData.set(READY_TIME, readyTime);
        entityData.set(DISAPPEARING, tag.getBoolean("disappearing"));
    }

}
