package com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class RadialEarthquakeEntity extends Entity implements AutoSerializable {

    public static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(RadialEarthquakeEntity.class, EntityDataSerializers.INT);

    protected HashMap<BlockPos, EarthShatterInstance> clientEarthShatters = new HashMap<>();

    @SerializableField
    public float damage;

    @SerializableField
    public int endRadius = 10;

    @SerializableField
    public float speed;

    @SerializableField
    public float current = 0;

    @SerializableField
    private int previousRadius = -1;

    @SerializableField
    private int removalTick = 20;

    public RadialEarthquakeEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static RadialEarthquakeEntity summon(Level level,BlockPos pos,int startRadius,int endRadius,float speed,float damage){
        RadialEarthquakeEntity radialEarthquakeEntity = new RadialEarthquakeEntity(BossEntities.RADIAL_EARTHQUAKE.get(),level);
        radialEarthquakeEntity.speed = speed;
        radialEarthquakeEntity.current = startRadius;
        radialEarthquakeEntity.endRadius = endRadius;
        radialEarthquakeEntity.damage = damage;
        radialEarthquakeEntity.entityData.set(RADIUS, endRadius);
        radialEarthquakeEntity.setPos(pos.getCenter());
        level.addFreshEntity(radialEarthquakeEntity);
        return radialEarthquakeEntity;
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (current > endRadius){
                if (removalTick-- < 0) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }else {
                int currentRad = (int) current;
                if (currentRad != previousRadius) {
                    for (int i = previousRadius + 1; i <= currentRad; i++) {
                        this.spawnAndDamageWithRadius(i);
                    }
                }
                previousRadius = currentRad;
                current += speed;
            }

        }else{
            this.tickEarthShatters();
        }
    }


    private void tickEarthShatters(){

        var iterator = this.clientEarthShatters.entrySet().iterator();

        while (iterator.hasNext()){

            var pair = iterator.next();

            var instance = pair.getValue();

            if (instance.shouldBeRemoved()){
                iterator.remove();
                continue;
            }

            instance.tick();

        }

    }




    public void spawnAndDamageWithRadius(int rad){

        AABB box = new AABB(-rad,-0.1,-rad,rad,1,rad).move(this.position());

        Predicate<LivingEntity> predicate = (entity)->{

            double dist = entity.position().multiply(1,0,1)
                    .distanceTo(this.position().multiply(1,0,1));


            return !(entity instanceof ChesedBossBuddy) && Math.abs(dist - rad) <= 1.5;
        };

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, box, predicate);

        for (LivingEntity entity : entities){
            entity.hurt(BossDamageSources.CHESED_EARTHQUAKE_SOURCE,this.damage);
        }

        FDPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(
                this.getX(),this.getY(),this.getZ(), Math.max(endRadius * 2,120), level().dimension()
        )), new SpawnRadialEarthquakeOnRadiusPacket(this,rad));


        Vec3 tpos = this.position();
        BossUtil.posEvent((ServerLevel) level(),tpos, BossUtil.RADIAL_EARTHQUAKE_PARTICLES,rad,60);

    }

    public void spawnEarthShattersOnRadius(int rad){

        Vec3 b = new Vec3(rad,0,0);
        float angle;
        if (rad != 0){
            angle = 0.5f / rad;
        }else{
            angle = FDMathUtil.FPI * 2;
        }

        BlockPos prevPos = null;
        Vec3 tpos = this.position();

        for (float i = 0; i < FDMathUtil.FPI * 2;i += angle){
            Vec3 pos = tpos.add(b.yRot(i));
            BlockPos ppos = FDMathUtil.vec3ToBlockPos(pos);
            if (!ppos.equals(prevPos) && !this.clientEarthShatters.containsKey(ppos)){
                Vec3 c = ppos.getCenter();
                Vec3 dir = tpos.subtract(c).multiply(1,0,1).normalize().add(0,1,0);

                BlockState state = level().getBlockState(ppos);
                if (state.isAir()) continue;


                EarthShatterSettings settings = EarthShatterSettings.builder()
                                .direction(dir)
                                .stayTime(random.nextInt(4))
                                .upTime(4 - random.nextInt(4))
                                .downTime(4 - random.nextInt(4))
                        .build();

                EarthShatterInstance earthShatterInstance = new EarthShatterInstance(settings,state);
                this.clientEarthShatters.put(ppos, earthShatterInstance);

            }
            prevPos = ppos;
        }

    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 10);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
        this.entityData.set(RADIUS, this.endRadius);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }

    public static class EarthShatterInstance {

        private EarthShatterSettings settings;

        private BlockState blockState;

        private int tickCount;

        public EarthShatterInstance(EarthShatterSettings earthShatterSettings, BlockState blockState){
            this.settings = earthShatterSettings;
            this.blockState = blockState;
        }

        public BlockState getBlockState() {
            return blockState;
        }

        public void tick(){
            tickCount++;
        }

        public boolean shouldBeRemoved(){
            return this.tickCount > this.settings.getLifetime();
        }

        public int getTickCount() {
            return tickCount;
        }

        public EarthShatterSettings getSettings() {
            return settings;
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        int rad = this.entityData.get(RADIUS) + 2;
        return new AABB(
                -rad,-rad,-rad,rad,rad,rad
        ).move(this.position());
    }

}
