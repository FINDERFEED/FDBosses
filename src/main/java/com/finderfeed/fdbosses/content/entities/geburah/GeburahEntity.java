package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttackController;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances.GeburahLasersAttack;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahRotatingWeaponsHandler;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class GeburahEntity extends FDLivingEntity implements AutoSerializable {

    public static final float MAX_LASERS_RADIUS = 50;

    public static FDModel CLIENT_MODEL;

    public static EntityDataAccessor<Boolean> LASERS_ACTIVE = SynchedEntityData.defineId(GeburahEntity.class, EntityDataSerializers.BOOLEAN);

    public static final String GEBURAH_STOMPING_LAYER = "stomping";

    protected GeburahRotatingWeaponsHandler rotatingWeaponsHandler;

    private GeburahRayController rayController;

    private GeburahWeaponAttackController attackController;

    @SerializableField
    private GeburahStompingController stompingController;

    public GeburahEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.rotatingWeaponsHandler = new GeburahRotatingWeaponsHandler(this);
        this.rayController = new GeburahRayController(this);
        this.stompingController = new GeburahStompingController(this, 30);
        this.attackController = new GeburahWeaponAttackController(this);
    }



    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            this.particles();
        }else{
            this.getRayController().tick();
            this.getStompingController().tick();
            this.getAttackController().tick();

            if (tickCount % 100 == 0){
                this.getRotatingWeaponsHandler().startConstantRotation(1 + random.nextFloat() * 10);
            }

        }

        if (this.getRotatingWeaponsHandler().finishedRotation()){
//            this.getRotatingWeaponsHandler().rotateWeaponsBy(10,30);
        }

        this.getRotatingWeaponsHandler().tick();

    }

    public List<Pair<Vec3, Vec3>> getCannonsPositionAndDirection(){
        return this.getCannonsPositionAndDirection(1f);
    }

    public List<Pair<Vec3, Vec3>> getCannonsPositionAndDirection(float pticks){

        float offsetFromCenter = 6.5f;
        float verticalOffset = 1.5f;

        float currentRotation = this.getRotatingWeaponsHandler().getLerpedRotation(pticks);

        int cannonCount = 8;
        float angle = 360f / cannonCount;

        List<Pair<Vec3, Vec3>> pairs = new ArrayList<>();

        for (int i = 0; i < cannonCount; i++) {

            Vec3 dir = new Vec3(1, 0, 0).yRot((float) Math.toRadians(currentRotation + angle * i));

            Vec3 pos = this.position().add(dir.x * offsetFromCenter, verticalOffset, dir.z * offsetFromCenter);

            pairs.add(new Pair<>(pos, dir));

        }

        return pairs;
    }

    public void setLaserVisualsState(boolean state){
        this.getEntityData().set(LASERS_ACTIVE, state);
    }

    public boolean isLaserVisualActive(){
        return this.getEntityData().get(LASERS_ACTIVE);
    }

    public Vec3 getCorePosition(){
        return this.position().add(0,21.5f,0);
    }

    public GeburahRayController getRayController() {
        return rayController;
    }

    public GeburahStompingController getStompingController() {
        return stompingController;
    }

    public GeburahWeaponAttackController getAttackController() {
        return attackController;
    }

    private void particles(){

        this.coreParticles();
        this.laserParticles();

    }

    private void laserParticles(){

        if (!this.isLaserVisualActive()) return;

        float r = 0.3f;
        float g = 0.7f;
        float b = 1f;

        for (var pair : this.getCannonsPositionAndDirection()){

            Vec3 pos = pair.first;
            Vec3 dir = pair.second;
            Matrix4f mat = new Matrix4f();
            FDRenderUtil.applyMovementMatrixRotations(mat, dir);

            Vec3 ppos = pos.add(dir.scale(0.25f));

            for (var dir2 : new HorizontalCircleRandomDirections(random, 6,1f)) {

                Vec3 baseSpeed = dir2.scale(0.25f - random.nextFloat() * 0.05f).add(0,-.1f - random.nextFloat() * 0.05,0);

                Vec3 speed = BossUtil.matTransformDirectionVec3(mat, baseSpeed);

                Vec3 spawnOffset = BossUtil.matTransformDirectionVec3(mat, dir2.scale(0.1f + random.nextFloat() * 0.1f));


                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .brightness(2)
                        .color(r,g,b)
                        .size(0.2f + random.nextFloat() * 0.1f)
                        .friction(0.7f)
                        .scalingOptions(0,0,5)
                        .build();

                level().addParticle(ballParticleOptions,
                        ppos.x + spawnOffset.x,
                        ppos.y + spawnOffset.y,
                        ppos.z + spawnOffset.z,
                        speed.x,speed.y,speed.z);

            }



        }


    }

    private void coreParticles(){
        if (level().getGameTime() % 3 == 0) {
            BallParticleOptions ballParticle = BallParticleOptions.builder()
                    .color(1f,0.8f, 0.3f)
                    .scalingOptions(0, 0, 20)
                    .brightness(1)
                    .size(2f)
                    .build();

            Vec3 corePos = this.getCorePosition();

            level().addParticle(ballParticle, true, corePos.x, corePos.y, corePos.z, 0, 0, 0);

        }
    }

    public GeburahRotatingWeaponsHandler getRotatingWeaponsHandler() {
        return rotatingWeaponsHandler;
    }


    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.getRotatingWeaponsHandler().onStartSeeingGeburah(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_20174_) {
        super.stopSeenByPlayer(p_20174_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LASERS_ACTIVE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

}
