package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MalkuthEarthquake extends Entity implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthEarthquake.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());
    public static final EntityDataAccessor<Float> ANGLE = SynchedEntityData.defineId(MalkuthEarthquake.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Vec3> DIRECTION = SynchedEntityData.defineId(MalkuthEarthquake.class, FDEDataSerializers.VEC3.get());
    public static final EntityDataAccessor<Integer> TIME = SynchedEntityData.defineId(MalkuthEarthquake.class, EntityDataSerializers.INT);

    @SerializableField
    private float damage;

    private List<MalkuthEarthquakeSegment> segments = new ArrayList<>();

    public static MalkuthEarthquake summon(Level level, MalkuthAttackType malkuthAttackType,Vec3 pos, Vec3 direction, int time, float arcAngle, float damage){
        MalkuthEarthquake malkuthEarthquake = new MalkuthEarthquake(BossEntities.MALKUTH_EARTHQUAKE.get(), level);

        malkuthEarthquake.setEarthquakeTime(time);
        malkuthEarthquake.setDirectionAndLength(direction);
        malkuthEarthquake.setAngle(arcAngle);
        malkuthEarthquake.setMalkuthAttackType(malkuthAttackType);
        malkuthEarthquake.setPos(pos);
        malkuthEarthquake.damage = damage;

        level.addFreshEntity(malkuthEarthquake);
        return malkuthEarthquake;
    }

    public MalkuthEarthquake(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide){
            this.summonSegments();
            this.manageSegments();
        }else{
            if (this.tickCount > this.getEarthquakeTime() + 20){
                this.remove(RemovalReason.DISCARDED);
            }
        }


    }

    public List<MalkuthEarthquakeSegment> getSegments() {
        return segments;
    }

    private Pair<Float,Float> getCurrentDamageRadius(){

        float fullLength = (float) this.getDirectionAndLength().length();

        float currentCompletionPercent = this.tickCount / (float) this.getEarthquakeTime();

        float currentLength = fullLength * currentCompletionPercent;

        float sizeOfDamageArea = this.getSizeOfDamageArea();

        return new Pair<>(
                currentLength,
                currentLength + sizeOfDamageArea
        );
    }

    private float getSizeOfDamageArea(){
        float fullLength = (float) this.getDirectionAndLength().length();
        return fullLength / this.getEarthquakeTime();
    }

    private void summonSegments(){

        if (this.tickCount > this.getEarthquakeTime()) return;

        var pair = this.getCurrentDamageRadius();

        Vec3 direction = this.getDirectionAndLength();

        float angle = this.getAngle();

        float start = pair.first;
        float end = pair.second;

        float sizeOfSegment = 0.75f;

        Vec3 ndir = direction.normalize();

        boolean reverse = false;


        for (float i = start + 0.01f; i <= end;i += sizeOfSegment){

            float lengthOfCircle = FDMathUtil.FPI * 2 * i;

            float anglePercent = sizeOfSegment / lengthOfCircle;

            float sizeInAngle = anglePercent * FDMathUtil.FPI * 2;

            for (float b = random.nextFloat() * sizeInAngle/2; b <= angle; b += sizeInAngle){

                float rot = reverse ? (angle - b) : b;

                float f = i + sizeOfSegment * random.nextFloat() / 2;

                Vec3 offset = ndir.multiply(f,f,f).yRot(rot - angle / 2);

                MalkuthEarthquakeSegment.Type segmentType = this.getRandomSegmentType(random.nextInt(2));

                float segmentAngle = FDMathUtil.FPI / 4
                        - random.nextFloat() * FDMathUtil.FPI / 6

                        ;

                MalkuthEarthquakeSegment malkuthEarthquakeSegment = new MalkuthEarthquakeSegment(segmentType, offset, segmentAngle, 20);

                this.segments.add(malkuthEarthquakeSegment);

                if (this.getEarthquakeType().isIce() && random.nextFloat() > 0.6f){

                    float additionalAngle = (reverse ? -1 : 1) * random.nextFloat() * sizeInAngle;

                    offset = ndir.yRot(rot - angle / 2 + additionalAngle).multiply(i,i,i);

                    MalkuthEarthquakeSegment s = new MalkuthEarthquakeSegment(MalkuthEarthquakeSegment.Type.ICE_SPIKE, offset, random.nextFloat() * FDMathUtil.FPI / 32,20);

                    this.segments.add(s);

                }

            }

            reverse = true;
        }

    }



    private MalkuthEarthquakeSegment.Type getRandomSegmentType(int segmentId){
        if (this.getEarthquakeType().isFire()){
            if (segmentId == 0){
                return MalkuthEarthquakeSegment.Type.FIRE_1;
            }else{
                return MalkuthEarthquakeSegment.Type.FIRE_2;
            }
        }else{
            if (segmentId == 0){
                return MalkuthEarthquakeSegment.Type.ICE_1;
            }else{
                return MalkuthEarthquakeSegment.Type.ICE_2;
            }
        }
    }

    private void manageSegments(){
        var iter = this.segments.iterator();
        while (iter.hasNext()){
            var segment = iter.next();
            if (segment.hasFinished()){
                iter.remove();
            }else{
                segment.tick();
            }
        }
    }

    public int getEarthquakeTime(){
        return this.entityData.get(TIME);
    }

    public void setEarthquakeTime(int time){
        this.entityData.set(TIME,time);
    }

    public void setMalkuthAttackType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(MALKUTH_ATTACK_TYPE,malkuthAttackType);
    }

    public void setDirectionAndLength(Vec3 direction){
        this.entityData.set(DIRECTION,direction);
    }

    public Vec3 getDirectionAndLength(){
        return this.entityData.get(DIRECTION);
    }

    public MalkuthAttackType getEarthquakeType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    public void setAngle(float angle){
        this.entityData.set(ANGLE,angle);
    }

    public float getAngle(){
        return this.entityData.get(ANGLE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MALKUTH_ATTACK_TYPE,MalkuthAttackType.FIRE);
        builder.define(DIRECTION, new Vec3(1,0,0));
        builder.define(TIME, 20);
        builder.define(ANGLE, FDMathUtil.FPI/6);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
        this.entityData.set(ANGLE,tag.getFloat("angle"));
        this.entityData.set(DIRECTION,new Vec3(tag.getDouble("xdir"),0,tag.getDouble("zdir")));
        this.entityData.set(MALKUTH_ATTACK_TYPE, MalkuthAttackType.valueOf(tag.getString("earthquake_type")));
        this.entityData.set(TIME, tag.getInt("time"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
        tag.putFloat("angle",this.entityData.get(ANGLE));
        tag.putDouble("xdir",this.entityData.get(DIRECTION).x);
        tag.putDouble("zdir",this.entityData.get(DIRECTION).z);
        tag.putString("earthquake_type",this.entityData.get(MALKUTH_ATTACK_TYPE).name());
        tag.putInt("time",this.entityData.get(TIME));
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
