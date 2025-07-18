package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
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
            this.tickDamage();
            if (this.tickCount > this.getEarthquakeTime() + 20){
                this.remove(RemovalReason.DISCARDED);
            }
        }


    }

    private void tickDamage(){

        var radius = this.getCurrentDamageRadius();

        Vec3 dirAndLength = this.getDirectionAndLength();

        float length = (float) dirAndLength.length();

        Vec2 dir = new Vec2((float) dirAndLength.x,(float) dirAndLength.z);

        var entities = BossTargetFinder.getEntitiesInArc(LivingEntity.class, level(), this.position().add(0,-1,0), dir, this.getAngle(),  2, length);

        for (var entity : entities){

            Vec3 position = entity.position();
            Vec3 b = position.subtract(this.position()).multiply(1,0,1);

            double k = b.length();

            if (k < radius.first || k > radius.second){
                continue;
            }

            entity.hurt(new MalkuthDamageSource(level().damageSources().generic(),this.getEarthquakeType(), 51),2);



            Vec3 speed = new Vec3(dir.x ,0,dir.y).normalize().add(0,1,0);

            if (entity instanceof ServerPlayer serverPlayer){
                FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
            }else{
                entity.setDeltaMovement(speed);
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

        float sizeOfSegment = 1.25f;

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

                int segmentTime = 10;

                MalkuthEarthquakeSegment malkuthEarthquakeSegment = new MalkuthEarthquakeSegment(segmentType, offset, segmentAngle, segmentTime);

                this.segments.add(malkuthEarthquakeSegment);

                if (this.getEarthquakeType().isIce() && random.nextFloat() > 0.6f){

                    float additionalAngle = (reverse ? -1 : 1) * random.nextFloat() * sizeInAngle;

                    offset = ndir.yRot(rot - angle / 2 + additionalAngle).multiply(i,i,i);

                    MalkuthEarthquakeSegment s = new MalkuthEarthquakeSegment(MalkuthEarthquakeSegment.Type.ICE_SPIKE, offset, random.nextFloat() * FDMathUtil.FPI / 32,segmentTime);

                    this.segments.add(s);

                }

                this.segments.sort(Comparator.comparingInt(v->v.getType().getId()));

                for (int k = 0; k < 1; k++){

                    float hspeed = 0.4f;
                    ParticleOptions options;

                    if (this.getEarthquakeType().isFire()){
                        float rnd = random.nextFloat();
                        if (rnd > 0.66){
                            options = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(),20 + random.nextInt(4),0.15f + random.nextFloat() * 0.2f,
                                    (float)Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                        } else if (rnd > 0.33){

                            float rc = 0.9f + random.nextFloat() * 0.1f;
                            float gc = 0.2f + random.nextFloat() * 0.2f;
                            float bc = random.nextFloat() * 0.2f;
                            options = BallParticleOptions.builder()
                                    .color(rc,gc,bc)
                                    .size(0.2f + random.nextFloat() * 0.2f)
                                    .scalingOptions(0,0,20)
                                    .friction(0.7f)
                                    .build();

                            hspeed = 0.75f;

                        } else{
                            options = ParticleTypes.LAVA;
                        }
                    }else{
                        if (random.nextFloat() > 0.5) {
                            options = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                    (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                        }else{

                            float rc = random.nextFloat() * 0.2f;
                            float gc = 0.7f + random.nextFloat() * 0.1f;
                            float bc = 0.9f + random.nextFloat() * 0.1f;

                            options = BallParticleOptions.builder()
                                    .color(rc,gc,bc)
                                    .size(0.2f + random.nextFloat() * 0.2f)
                                    .scalingOptions(0,0,20)
                                    .friction(0.7f)
                                    .build();
                            hspeed = 0.75f;
                        }
                    }

                    Vec3 pos = offset.add(this.position()).add(
                            random.nextFloat() * 0.8 - 0.4,
                            random.nextFloat() * 0.8 - 0.4,
                            random.nextFloat() * 0.8 - 0.4
                    );

                    level().addParticle(options,true, pos.x,pos.y,pos.z,
                            ndir.x * hspeed,
                            random.nextFloat() * 0.75 + 0.1,
                            ndir.z * hspeed
                            );

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
