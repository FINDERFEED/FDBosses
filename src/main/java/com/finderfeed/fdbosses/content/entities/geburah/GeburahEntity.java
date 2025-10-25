package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttackController;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances.GeburahAttackFireDefaultProjectiles;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdbosses.content.entities.geburah.sins.PlayerSinsHandler;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.ActivePlayerSinInstance;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.util.CylinderPlayerPositionsCollector;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.content.util.WorldBox;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdbosses.init.GeburahSins;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class GeburahEntity extends FDLivingEntity implements AutoSerializable {

    public static final String SIMPLE_NO_SIN_RUN_AROUND = "simple_no_sin_run_around";
    public static final String EMPTY_SINS_AND_DELAY = "empty_sins_and_delay";

    public static final int ARENA_HEIGHT = 30;

    public static final int ARENA_RADIUS = 30;

    public static final float MAX_LASERS_RADIUS = ARENA_RADIUS;

    private static FDModel CLIENT_MODEL;

    public static EntityDataAccessor<Boolean> LASERS_ACTIVE = SynchedEntityData.defineId(GeburahEntity.class, EntityDataSerializers.BOOLEAN);

    public static final String GEBURAH_CANNONS_LAYER = "cannons";

    public static final String GEBURAH_STOMPING_LAYER = "stomping";

    protected GeburahWeaponRotationController rotatingWeaponsHandler;

    private GeburahRayController rayController;

    private GeburahWeaponAttackController attackController;

    @SerializableField
    private GeburahStompingController stompingController;

    private AttackChain mainAttackChain;

    private CylinderPlayerPositionsCollector playerPositionsCollector;

    public GeburahEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.playerPositionsCollector = new CylinderPlayerPositionsCollector(level, ARENA_RADIUS, ARENA_HEIGHT, BossUtil::isPlayerInSurvival);
        this.rotatingWeaponsHandler = new GeburahWeaponRotationController(this);
        this.rayController = new GeburahRayController(this);
        this.stompingController = new GeburahStompingController(this, ARENA_RADIUS);
        this.attackController = new GeburahWeaponAttackController(this);

        AttackOptions<?> simpleRunAroundNoSins = AttackOptions.chainOptionsBuilder()
                .addAttack(SIMPLE_NO_SIN_RUN_AROUND)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        this.mainAttackChain = new AttackChain(level.random)
                .registerAttack(EMPTY_SINS_AND_DELAY, this::emptySinsAndDelay)
                .registerAttack(SIMPLE_NO_SIN_RUN_AROUND, this::simpleNoSinRunAroundAttack)

                .addAttack(0, simpleRunAroundNoSins)
        ;
    }


    public static FDModel getClientModel(){
        if (CLIENT_MODEL == null){
            CLIENT_MODEL = new FDModel(BossModels.GEBURAH.get());
        }
        return CLIENT_MODEL;
    }


    @Override
    public void tick() {

        playerPositionsCollector.tick(this.position().add(0,-0.1,0));

        super.tick();

        if (level().isClientSide) {
            this.particles();
        }else{
            this.mainAttackChain.tick();

            this.tickClockwiseSin();
            this.getRayController().tick();
            this.getStompingController().tick();
            this.getWeaponAttackController().tick();
        }

        this.getWeaponRotationController().tick();

    }



    //-------------------------------------------------------------------ATTACKS--------------------------------------------------------------------------

    public boolean emptySinsAndDelay(AttackInstance attackInstance){
        return attackInstance.tick > 100;
    }

    public boolean simpleNoSinRunAroundAttack(AttackInstance attackInstance){


        this.attackWithRay(attackInstance.tick, 20, 10, 60);

        this.simpleCannonAttacks(attackInstance.tick, 5,20);

        return false;
    }

    private boolean rotationBool = false;

    private void simpleCannonAttacks(int currentTick, int timeBetweenShots, int frequency){

        var attackController = this.getWeaponAttackController();
        var weaponRotationController = this.getWeaponRotationController();

        int startShootTime = frequency + timeBetweenShots;

        int localtick = currentTick % startShootTime;

        if (localtick == 0){

            float rotation = (random.nextFloat() * 100 + 40) * (rotationBool ? -1 : 1);

            rotationBool = !rotationBool;

            weaponRotationController.rotateWeaponsBy(rotation, frequency);
        }else if (localtick == frequency - BossAnims.GEBURAH_FIRE_CANNONS.get().getAnimTime() + 5){
            attackController.setCurrentAttack(new GeburahAttackFireDefaultProjectiles(this, ARENA_RADIUS, 100,1f),false);
        }

    }

    private void attackWithRay(int currentTick, int rayChargeDuration, int attackRedirectTick, int frequency){

        if (rayChargeDuration >= frequency){
            throw new RuntimeException("Cannot have ray charge duration higher than frequency");
        }

        var rayController = this.getRayController();

        if (currentTick % frequency == 0){
            var list = this.playerPositionsCollector.getCurrentPlayerPositions();
            rayController.shoot(rayChargeDuration, list);
        }

        if (rayController.getCurrentShotCharge() == attackRedirectTick){
            var list = this.playerPositionsCollector.getCurrentPlayerPositions();
            rayController.shoot(rayChargeDuration, list.stream().map(v->v.add(0,0.5,0)).toList());
        }

    }


    //------------------------------------------------------------------ATTACKS-END-----------------------------------------------------------------------


    public void tickClockwiseSin(){

        for (var player : this.playerPositionsCollector.getPlayers()){

            PlayerSins playerSins = PlayerSins.getPlayerSins(player);

            if (playerSins.hasSinActive(GeburahSins.MOVE_CLOCKWISE_SIN.get()) && !playerSins.isGainingSinsOnCooldown()){

                var pair = this.playerPositionsCollector.getOldAndCurrentPlayerPosition(player);
                Vec3 oldPos = pair.first.multiply(1,0,1);
                Vec3 newPos = pair.second.multiply(1,0,1);

                Vec3 between = newPos.subtract(oldPos);

                if (between.length() < 0.1) continue;


                Vec3 betweenThisAndThat = newPos.subtract(this.position().multiply(1,0,1));


                Vec3 rotated = betweenThisAndThat.yRot(FDMathUtil.FPI / 2);

                double dot = between.normalize().dot(rotated.normalize());

                if (dot < 0){
                    PlayerSinsHandler.sin((ServerPlayer) player, 40);
                }

            }

        }

    }

    public List<Pair<Vec3, Vec3>> getCannonsPositionAndDirection(){
        return this.getCannonsPositionAndDirection(1f);
    }

    public List<Pair<Vec3, Vec3>> getCannonsPositionAndDirection(float pticks){

        float offsetFromCenter = 6.5f;
        float verticalOffset = 1.5f;

        float currentRotation = this.getWeaponRotationController().getLerpedRotation(pticks);

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

    public GeburahWeaponAttackController getWeaponAttackController() {
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

    public void trySetAllPlayersSins(List<PlayerSin> sins, int sinCooldown){

        AABB sinBox = this.constructSinBox();

        for (var player : this.playerPositionsCollector.getPlayers()){

            PlayerSins playerSins = PlayerSins.getPlayerSins(player);

            if (!playerSins.hasExactlyThisSins(sins)){

                List<ActivePlayerSinInstance> instances = sins.stream().map(sin -> {
                    return new ActivePlayerSinInstance(sin, new WorldBox(level().dimension(), sinBox),0);
                }).toList();

                playerSins.setSinGainCooldown(sinCooldown);

                playerSins.setActiveSins(instances);

            }

        }

    }

    private AABB constructSinBox(){
        return new AABB(
                -ARENA_RADIUS,-2,-ARENA_RADIUS,
                ARENA_RADIUS, ARENA_HEIGHT, ARENA_RADIUS
        ).move(this.position());
    }


    public GeburahWeaponRotationController getWeaponRotationController() {
        return rotatingWeaponsHandler;
    }


    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.getWeaponRotationController().onStartSeeingGeburah(player);
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

        CompoundTag mainAttackChain = new CompoundTag();
        this.mainAttackChain.save(mainAttackChain);
        tag.put("mainAttackChain",mainAttackChain);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.mainAttackChain.load(tag.getCompound("mainAttackChain"));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

//    @EventBusSubscriber(modid = FDBosses.MOD_ID)
//    public static class Events {
//
//
//    }

    @EventBusSubscriber(modid = FDBosses.MOD_ID)
    public static class Events {

        @SubscribeEvent
        public static void killEvent(LivingDeathEvent event){

            var entity = event.getEntity();
            var source = event.getSource();
            Level level = entity.level();

            if (source.getEntity() instanceof ServerPlayer serverPlayer){
                PlayerSins playerSins = PlayerSins.getPlayerSins(serverPlayer);

                if (playerSins.hasSinActive(GeburahSins.KILL_ENTITY_SIN.get())){
                    PlayerSinsHandler.sin(serverPlayer, 100);
                }


            }

        }

    }

}
