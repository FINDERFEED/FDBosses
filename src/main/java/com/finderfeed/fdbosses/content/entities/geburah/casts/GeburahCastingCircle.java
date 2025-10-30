package com.finderfeed.fdbosses.content.entities.geburah.casts;

import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.nbt.FDTagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class GeburahCastingCircle extends Entity {

    public static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(GeburahCastingCircle.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(GeburahCastingCircle.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vec3> DIRECTION = SynchedEntityData.defineId(GeburahCastingCircle.class, FDEDataSerializers.VEC3.get());

    public static final int FADE_IN = 10;
    public static final int FADE_OUT = 10;

    public boolean wasCast = false;

    public GeburahCastingCircle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

            int castTick = this.getDuration() / 2;
            if (tickCount == castTick){
                if (!wasCast) {
                    this.cast();
                    this.wasCast = true;
                }
            }else if (tickCount > this.getDuration()){
                this.discard();
            }
        }
    }

    public abstract void cast();

    public Vec3 getCastDirection(){
        return this.getEntityData().get(DIRECTION);
    }

    public void setDirection(Vec3 direction){
        this.getEntityData().set(DIRECTION, direction);
    }

    public int getDuration(){
        return this.getEntityData().get(DURATION);
    }

    public void setCastDuration(int duration){
        this.getEntityData().set(DURATION, FADE_IN + FADE_OUT + duration);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DURATION, 20);
        builder.define(COLOR, 0xffffffff);
        builder.define(DIRECTION, new Vec3(0,1,0));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("color")) {
            this.getEntityData().set(COLOR, tag.getInt("color"));
        }
        if (tag.contains("duration")) {
            this.getEntityData().set(DURATION, tag.getInt("duration"));
        }
        if (tag.contains("dir_x")) {
            this.getEntityData().set(DIRECTION, new Vec3(
                    tag.getDouble("dir_x"),
                    tag.getDouble("dir_y"),
                    tag.getDouble("dir_z")
            ));
        }

        this.wasCast = tag.getBoolean("was_cast");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("color",this.getEntityData().get(COLOR));
        tag.putInt("duration", this.getEntityData().get(DURATION));
        Vec3 dir = this.getEntityData().get(DIRECTION);
        tag.putDouble("dir_x",dir.x);
        tag.putDouble("dir_y",dir.y);
        tag.putDouble("dir_z",dir.z);
        tag.putBoolean("was_cast", this.wasCast);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

}
