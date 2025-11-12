package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahCastingCircleJudgementBird;
import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahChainTrapCastCircle;
import com.finderfeed.fdbosses.content.entities.geburah.casts.GeburahSinCrystalCastCircle;
import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.ChainTrapSummonProjectile;
import com.finderfeed.fdbosses.content.entities.geburah.chain_trap.GeburahChainTrapEntity;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.StartGeburahDistortionEffectPacket;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.GeburahWeaponAttackController;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances.GeburahAttackFireDefaultProjectiles;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances.GeburahLasersAttack;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_weapons.instances.GeburahRoundAndRoundLaserAttack;
import com.finderfeed.fdbosses.content.entities.geburah.judgement_bird.JudgementBirdEntity;
import com.finderfeed.fdbosses.content.entities.geburah.justice_hammer.JusticeHammerAttack;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdbosses.content.entities.geburah.sins.PlayerSinsHandler;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.ActivePlayerSinInstance;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSin;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.util.CylinderPlayerPositionsCollector;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.content.util.WorldBox;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdlib.data_structures.Pair;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.TransitionAnimation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFramesPacket;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeburahEntity extends FDLivingEntity implements AutoSerializable, GeburahBossBuddy {

    public static final int SIN_PUNISHMENT_ATTACK_DURATION = 40;

    public static final int ATTACK_START_DELAY = 40;

    public static final int CANNONS_AMOUNT = 8;
    public static final float RAY_PREPARATION_PARTICLES_OFFSET = 0.05f;
    public static final float STOMP_PREPARATION_PARTICLES_OFFSET = 0.01f;
    public static final float LASERS_PREPARATION_OFFSET = 0.03f;
    public static final float RAY_DECAL_OFFSET = 0.03f;

    public static final String SIMPLE_NO_SIN_RUN_AROUND = "simple_no_sin_run_around";
    public static final String RUN_CLOCKWISE_HAMMERS_RAY_PROJECTILES = "run_clockwise_hammers_ray_projectiles";
    public static final String LIMITED_BUTTONS_LASERS_AND_EARTHQUAKES = "limited_buttons_lasers_and_earthquakes";
    public static final String NO_JUMP_RAYS_EARTHQUAKES_PROJECTILES = "no_jump_rays_earthquakes_projectiles";
    public static final String SIN_CRYSTALS_LASERS_AND_CANNONS = "sin_crystals_lasers_and_cannons";
    public static final String NO_KILL_ENTITIES_ATTACK = "no_kill_entities_attack";
    public static final String SIN_PUNISHMENT_ATTACK = "sin_punishment_attack";
    public static final String EMPTY_SINS_AND_DELAY = "empty_sins_and_delay";

    public static final String GEBURAH_STOMPING_LAYER = "stomping";
    public static final String GEBURAH_CANNONS_LAYER = "cannons";
    public static final String GEBURAH_TILT_LAYER = "tilt";

    public static final int ARENA_HEIGHT = 30;
    public static final int ARENA_RADIUS = 32;
    public static final float MAX_LASERS_RADIUS = ARENA_RADIUS;



    private static FDModel CLIENT_MODEL;

    public static EntityDataAccessor<List<PlayerSin>> ACTIVE_SINS = SynchedEntityData.defineId(GeburahEntity.class, BossEntityDataSerializers.SINS.get());
    public static EntityDataAccessor<Boolean> LASERS_ACTIVE = SynchedEntityData.defineId(GeburahEntity.class, EntityDataSerializers.BOOLEAN);

    @SerializableField
    private int judgementBirdSpawnTicker = 0;

    @SerializableField
    private GeburahStompingController stompingController;
    protected GeburahWeaponRotationController rotatingWeaponsHandler;
    private GeburahRayController rayController;
    private GeburahWeaponAttackController attackController;

    private AttackChain mainAttackChain;

    private CylinderPlayerPositionsCollector playerPositionsCollector;

    public static final int MAX_SIN_APPEAR_TICK = 20;
    public int sinsAppearTick = 0;
    public int sinsAppearTickO = 0;
    public static final int MAX_LASER_VISUAL_DISAPPEAR_TIME = 5;
    public int laserVisualDisappearTicker = 0;

    public GeburahLaserAttackPreparator laserAttackPreparator;

    public int sinPunishmentAttackTicker = -1;

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


        AttackOptions<?> noJumpRaysEarthquakesProjectiles = AttackOptions.chainOptionsBuilder()
                .addAttack(NO_JUMP_RAYS_EARTHQUAKES_PROJECTILES)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        AttackOptions<?> runClockwiseHammersRayProjectiles = AttackOptions.chainOptionsBuilder()
                .addAttack(RUN_CLOCKWISE_HAMMERS_RAY_PROJECTILES)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        AttackOptions<?> limitedButtonsLasersAndEarthquakes = AttackOptions.chainOptionsBuilder()
                .addAttack(LIMITED_BUTTONS_LASERS_AND_EARTHQUAKES)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        AttackOptions<?> sinCrystalsLasersAndCannons = AttackOptions.chainOptionsBuilder()
                .addAttack(SIN_CRYSTALS_LASERS_AND_CANNONS)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        AttackOptions<?> noKillEntitiesAttack = AttackOptions.chainOptionsBuilder()
                .addAttack(NO_KILL_ENTITIES_ATTACK)
                .addAttack(EMPTY_SINS_AND_DELAY)
                .build();

        this.mainAttackChain = new AttackChain(level.random)
                .registerAttack(EMPTY_SINS_AND_DELAY, this::emptySinsAndDelay)
                .registerAttack(SIMPLE_NO_SIN_RUN_AROUND, this::simpleNoSinRunAroundAttack)
                .registerAttack(RUN_CLOCKWISE_HAMMERS_RAY_PROJECTILES, this::runClockwiseProjectilesHammersRay)
                .registerAttack(LIMITED_BUTTONS_LASERS_AND_EARTHQUAKES, this::limitedButtonsRotatingLasers)
                .registerAttack(NO_JUMP_RAYS_EARTHQUAKES_PROJECTILES, this::noJumpEarthquakesProjectilesRays)
                .registerAttack(SIN_CRYSTALS_LASERS_AND_CANNONS, this::sinCrystalsLasersAndCannons)
                .registerAttack(NO_KILL_ENTITIES_ATTACK, this::noKillEntitiesAttack)
                .registerAttack(SIN_PUNISHMENT_ATTACK, this::sinPunishmentAttack)
                .attackListener(this::attackListener)
                .addAttack(0, SIN_PUNISHMENT_ATTACK)
//                .addAttack(0, noKillEntitiesAttack)
//                .addAttack(0, simpleRunAroundNoSins)
//                .addAttack(0, noJumpRaysEarthquakesProjectiles)
//                .addAttack(0, runClockwiseHammersRayProjectiles)
//                .addAttack(0, limitedButtonsLasersAndEarthquakes)
//                .addAttack(0, sinCrystalsLasersAndCannons)
        ;

        this.laserAttackPreparator = new GeburahLaserAttackPreparator(this);

        this.getAnimationSystem().startAnimation(GEBURAH_TILT_LAYER, AnimationTicker.builder(BossAnims.GEBURAH_TILT_LEFT).build());

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
            this.tickSinPunishmentEffect();
            this.particles();
            this.tickSinsAppearTick();
            this.laserAttackPreparator.tick();
            this.tickLaserVisualDisappearance();
        }else{
            this.mainAttackChain.tick();
            this.tickClockwiseSin();
            this.getRayController().tick();
            this.getStompingController().tick();
            this.getWeaponAttackController().tick();
            this.throwSinCrystals();
            this.tickTrapEntitiesSpawn();
            this.tickJudgementBirdSpawn();
        }

        this.getWeaponRotationController().tick();

    }

    private void tickSinPunishmentEffect(){
        this.sinPunishmentAttackTicker = Mth.clamp(sinPunishmentAttackTicker - 1,-1, GeburahEntity.SIN_PUNISHMENT_ATTACK_DURATION);
    }

    public void tickTrapEntitiesSpawn(){
        if (tickCount % 20 != 0) return;

        int angles = 4;
        float angle = FDMathUtil.FPI * 2 / angles;

        for (int i = 0; i < angles; i++){

            float currentAngle = angle * i + FDMathUtil.FPI / 4 + (level().random.nextFloat() * 2 - 1) * FDMathUtil.FPI / 8;
            Vec3 direction = new Vec3(1,0,0).yRot(currentAngle);

            var entities = FDTargetFinder.getEntitiesInArc(Entity.class, level(), this.position().add(0,-5f,0),
                    new Vec2((float) direction.x,(float) direction.z), FDMathUtil.FPI / 2, 35, ARENA_RADIUS, (entity) -> {
                return entity instanceof GeburahChainTrapEntity || entity instanceof ChainTrapSummonProjectile || entity instanceof GeburahChainTrapCastCircle;
            });

            if (entities.isEmpty()){

                Vec3 offsetPos = direction.scale(MAX_LASERS_RADIUS / 2 + level().random.nextFloat() * 3 - 1.5 + 2);
                Vec3 targetPos = this.position().add(offsetPos);
                Vec3 castCirclePos = this.position().add(direction.scale(3)).add(0,5,0);

                GeburahChainTrapCastCircle.summon(level(), castCirclePos, direction.add(0,1,0), targetPos);

            }

        }

    }

    public void tickJudgementBirdSpawn(){
        if (this.judgementBirdSpawnTicker != 0) {
            this.judgementBirdSpawnTicker = Mth.clamp(judgementBirdSpawnTicker - 1,0,Integer.MAX_VALUE);
            return;
        }

        this.judgementBirdSpawnTicker = 400;

        int angles = 4;
        float angle = FDMathUtil.FPI * 2 / angles;
        float halfRadius = ARENA_RADIUS / 2f;
        float roostingBoxCenterHeight = 5;
        double centerDirectionOffset = Math.sqrt(halfRadius * halfRadius + halfRadius * halfRadius);

        for (int i = 0; i < angles; i++){

            float currentAngle = angle * i + FDMathUtil.FPI / 4;
            Vec3 direction = new Vec3(1,0,0).yRot(currentAngle);


            Vec3 roostingBoxCenter = this.position().add(direction.scale(centerDirectionOffset)).add(0,roostingBoxCenterHeight,0);

            AABB roostingBox = new AABB(
                    roostingBoxCenter.x - halfRadius,
                    roostingBoxCenter.y - 0.5,
                    roostingBoxCenter.z - halfRadius,
                    roostingBoxCenter.x + halfRadius,
                    roostingBoxCenter.y + 0.5,
                    roostingBoxCenter.z + halfRadius
            );
            AABB findEntityBox = new AABB(
                    roostingBoxCenter.x - halfRadius,
                    roostingBoxCenter.y - roostingBoxCenterHeight,
                    roostingBoxCenter.z - halfRadius,
                    roostingBoxCenter.x + halfRadius,
                    roostingBoxCenter.y + 5,
                    roostingBoxCenter.z + halfRadius
            );

            var entities = level().getEntitiesOfClass(Entity.class, findEntityBox,(entity -> {
                return entity instanceof GeburahCastingCircleJudgementBird || entity instanceof JudgementBirdEntity;
            }));

            if (entities.isEmpty()){

                Vec3 castCirclePos = this.position().add(direction.scale(3)).add(0,roostingBoxCenterHeight,0);

                Vec3 flyTo = castCirclePos.add(direction.scale(7));

                GeburahCastingCircleJudgementBird.summon(level(), castCirclePos, direction, flyTo, roostingBox);

            }

        }

    }

    public void tickLaserVisualDisappearance(){
        if (this.isLaserVisualActive()){
            this.laserVisualDisappearTicker = MAX_LASER_VISUAL_DISAPPEAR_TIME;
        }else{
            this.laserVisualDisappearTicker = Mth.clamp(laserVisualDisappearTicker - 1,0,MAX_LASER_VISUAL_DISAPPEAR_TIME);
        }
    }

    public float getLaserVisualAlpha(float pticks){
        if (this.isLaserVisualActive()){
            if (laserVisualDisappearTicker == MAX_LASER_VISUAL_DISAPPEAR_TIME){
                return 1;
            }
        }
        return Mth.clamp(laserVisualDisappearTicker - pticks,0,MAX_LASER_VISUAL_DISAPPEAR_TIME) / MAX_LASER_VISUAL_DISAPPEAR_TIME;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor == LASERS_ACTIVE && this.getEntityData().get(LASERS_ACTIVE)){
            laserVisualDisappearTicker = MAX_LASER_VISUAL_DISAPPEAR_TIME;
        }
    }

    public CylinderPlayerPositionsCollector getPlayerPositionsCollector() {
        return playerPositionsCollector;
    }

    public Player pickRandomCombatant(){
        var positionsCollector = this.getPlayerPositionsCollector();

        List<Player> players = new ArrayList<>(positionsCollector.getPlayers());

        if (players.isEmpty()){
            return null;
        }

        return players.get(random.nextInt(players.size()));
    }

    private AttackAction attackListener(String s) {

//        if (this.getPlayerPositionsCollector().getPlayers().isEmpty()){
//            return AttackAction.WAIT;
//        }

        return AttackAction.PROCEED;
    }



//-------------------------------------------------------------------ATTACKS--------------------------------------------------------------------------

    public boolean sinPunishmentAttack(AttackInstance instance){

        int stage = instance.stage;
        int tick = instance.tick;

        if (stage == 0){
            BossUtil.geburahTriggerSinPunishmentAttack((ServerLevel) level(), this.position(), 120, this);
            instance.nextStage();
        }else if (stage == 1){

            if (tick == SIN_PUNISHMENT_ATTACK_DURATION - 2){
                for (var entity : this.playerPositionsCollector.getPlayers()) {

                    PacketDistributor.sendToPlayer((ServerPlayer) entity, new ImpactFramesPacket(
                            List.of(
                                    new ImpactFrame().setDuration(2),
                                    new ImpactFrame().setDuration(1).setInverted(true),
                                    new ImpactFrame().setDuration(1),
                                    new ImpactFrame().setDuration(1).setInverted(true)
                            )
                    ));
                }
            }else if (tick == SIN_PUNISHMENT_ATTACK_DURATION){
                BossUtil.geburahTriggerSinPunishmentAttackImpactEffect((ServerLevel) level(), this.position(), 120, GeburahEntity.ARENA_RADIUS / 2);
                BossUtil.geburahSinPunishmentAttackServerEffect(level(), this.position(), ARENA_RADIUS / 2f, BossBlocks.JUSTICESTONE_BRICKS.get().defaultBlockState());
                for (var entity : this.playerPositionsCollector.getPlayers()) {


                    PacketDistributor.sendToPlayer((ServerPlayer) entity, new DefaultShakePacket(FDShakeData.builder()
                            .amplitude(0.3f)
                            .outTime(80)
                            .build()));
                }


            }else if (tick >= SIN_PUNISHMENT_ATTACK_DURATION + 50){
                return true;
            }
        }

        return false;
    }

    public boolean emptySinsAndDelay(AttackInstance attackInstance){
        this.propagateSins(20);

        if (attackInstance.tick == 0) {
            var currentAnim = this.getAnimationSystem().getTickerAnimation(GEBURAH_TILT_LAYER);

            if (currentAnim == null || currentAnim.isToNullTransition()) {
                currentAnim = BossAnims.GEBURAH_TILT_RIGHT.get();
            }else if (currentAnim instanceof TransitionAnimation transitionAnimation){
                currentAnim = transitionAnimation.getTransitionTo();
            }

            if (currentAnim == BossAnims.GEBURAH_TILT_RIGHT.get()){
                this.getAnimationSystem().startAnimation(GEBURAH_TILT_LAYER, AnimationTicker.builder(BossAnims.GEBURAH_TILT_LEFT)
                                .setSpeed(1.25f)
                        .build());
            }else{
                this.getAnimationSystem().startAnimation(GEBURAH_TILT_LAYER, AnimationTicker.builder(BossAnims.GEBURAH_TILT_RIGHT)
                        .setSpeed(1.25f)
                        .build());
            }

        }

        if (attackInstance.tick == 60){
            for (var serverPlayer : FDTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), this.position().add(0,-0.1,0), 40, ARENA_RADIUS)){
                PacketDistributor.sendToPlayer(serverPlayer, new StartGeburahDistortionEffectPacket(this));
            }
            level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.GEBURAH_SIN_CHANGE.get(), SoundSource.HOSTILE, 5f, 1f);
        }

        return attackInstance.tick > 80;
    }

    public boolean noKillEntitiesAttack(AttackInstance inst){

        this.propagateSins(0, GeburahSins.KILL_ENTITY_SIN.get());
        int localStagesCount = 3;
        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0 && tick < ATTACK_START_DELAY){
            return false;
        }

        int localStage = stage % localStagesCount;
        var attackController = this.getWeaponAttackController();

        int maxStages = 8 * localStagesCount;

        if (this.getPlayerPositionsCollector().getPlayers().isEmpty()) return false;

        if (stage < maxStages) {
            if (localStage == 0) {
                this.getStompingController().stompFullCircle(30, true, 1f, 1f);
                attackController.setCurrentAttack(new GeburahRoundAndRoundLaserAttack(this, this.sideSwitch), false);
                this.sideSwitch = !sideSwitch;
                inst.nextStage();
            } else if (localStage == 1) {
                if (!attackController.isAttacking()) {
                    inst.nextStage();
                }
            } else if (localStage == 2) {
                if (tick >= 30) {
                    inst.nextStage();
                }
            }
        }

        return stage >= maxStages;
    }


    @SerializableField
    private float sinCrystalsThrowTick = 0;

    public void throwSinCrystals(){

        var activeSins = this.getEntityData().get(ACTIVE_SINS);

        if (activeSins.contains(GeburahSins.CRYSTAL_OF_SIN.get())) {
            if (sinCrystalsThrowTick % 200 == 0) {

                var players = new ArrayList<>(this.playerPositionsCollector.getPlayers());

                if (!players.isEmpty()) {
                    for (int i = 0; i < 4; i++) {

                        Vec3 direction = new Vec3(1, 0, 0).yRot(FDMathUtil.FPI / 4 + FDMathUtil.FPI / 2 * i);
                        Vec3 pos = this.position().add(0, 4, 0).add(direction.scale(5));
                        Player player = players.get(random.nextInt(players.size()));
                        GeburahSinCrystalCastCircle.summon(level(), pos, direction.add(0, 1, 0), player);

                    }
                }

            }
            sinCrystalsThrowTick++;
        }else{
            sinCrystalsThrowTick = 0;
        }

    }

    private boolean sideSwitch = true;

    public boolean sinCrystalsLasersAndCannons(AttackInstance inst){

        this.propagateSins(0, GeburahSins.CRYSTAL_OF_SIN.get());

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            if (tick < ATTACK_START_DELAY) {
                return false;
            }else{
                inst.nextStage();
            }
        }else{
            stage = stage - 1;
        }

        int localStagesAmount = 6;
        int localStage = stage % localStagesAmount;


        int stagesAmount = 10 * localStagesAmount;

        if (stage < stagesAmount){
            if (localStage < 3){


                this.simpleCannonAttacks(inst.tick, 20, 10,175);

                if (tick > 11){
                    inst.nextStage();
                }

            }else{

                GeburahWeaponAttackController controller = this.getWeaponAttackController();
                if (localStage == 3) {
                    if (!this.getPlayerPositionsCollector().getPlayers().isEmpty()) {
                        controller.setCurrentAttack(new GeburahRoundAndRoundLaserAttack(this, sideSwitch = !sideSwitch), true);
                        inst.nextStage();
                    }
                } else if (localStage == 4){
                    if (controller.getCurrentAttack() == null){
                        inst.nextStage();
                    }
                }else{
                    if (tick > 2) inst.nextStage();
                }

            }
        }

        return stage >= stagesAmount && tick > 40;
    }


    public boolean noJumpEarthquakesProjectilesRays(AttackInstance inst){

        this.propagateSins(30, GeburahSins.JUMPING_SIN.get());

        int tick = inst.tick;

        if (tick < ATTACK_START_DELAY){
            return false;
        }else{
            tick = tick - ATTACK_START_DELAY;
        }

        this.simpleCannonAttacks(tick, 10, 15);
        this.randomStompsAndRays(tick, 70, random.nextInt(2) + 4);

        return tick > 400;
    }

    private int stompingSwitcher = 0;

    public void randomStompsAndRays(int currentTick, int frequency, int count){

        if (currentTick % frequency == 0) {

            float rayShotRadius = 5;

            var stompingController = this.getStompingController();

            List<Vec3> playerPositions = this.playerPositionsCollector.getCurrentPlayerPositions();

            if (playerPositions.isEmpty()) return;


            List<GeburahStompingController.StompInstance> stompInstances = new ArrayList<>();

            float angle = FDMathUtil.FPI / count;
            Vec3 pos = playerPositions.get(random.nextInt(playerPositions.size()));

            Vec3 direction = pos.subtract(this.position()).multiply(1, 0, 1);

            if (stompingSwitcher % 2 == 0){
                direction = direction.yRot(angle);
            }

            double distToPlayer = direction.length();
            direction = direction.normalize();

            stompingSwitcher++;


            List<Vec3> rayPositions = new ArrayList<>();
            float startRadius = 10;

            int id = random.nextBoolean() ? 0 : count - 1;



            for (int i = 0; i < count; i++) {


                Vec3 stompDirection = direction.yRot(i * angle * 2);

                stompInstances.add(new GeburahStompingController.StompInstance(new Vec2((float) stompDirection.x, (float) stompDirection.z), angle/2));

                float randomRadius = startRadius + random.nextFloat() * (ARENA_RADIUS - startRadius - rayShotRadius);

                if (i == id){
                    randomRadius = Math.max(startRadius, (float) distToPlayer);
                }

                float arcLength = FDMathUtil.FPI * 2 * randomRadius * (angle / FDMathUtil.FPI / 2);

                float angleRandomCoefficient = arcLength / (rayShotRadius * 2) / 2;
                float additionAngle = Math.max(0, angle / 2 * (angleRandomCoefficient - 1));



                Vec3 rayDirection = direction.yRot(i * angle * 2 + angle + additionAngle * BossUtil.randomPlusMinus());
                Vec3 rayPos = this.position().add(rayDirection.scale(randomRadius));

                rayPositions.add(rayPos);

            }

            this.getRayController().shoot(frequency - 20, rayShotRadius, true, rayPositions);

            stompingController.stomp(frequency - 30, true, 1f, 1f, stompInstances);
        }

    }


    public boolean limitedButtonsRotatingLasers(AttackInstance attackInstance){

        this.propagateSins(60, GeburahSins.PRESSED_TOO_MANY_BUTTONS_SIN.get());

        int tick = attackInstance.tick;

        if (tick < ATTACK_START_DELAY){
            return false;
        }else{
            tick = tick - ATTACK_START_DELAY;
        }

        int attackDuration = 400;


        if (tick < attackDuration){

            this.constantRotatingLaser(0.5f);

            if ((tick + 50) % 100 == 0){
                this.getStompingController().stompFullCircle(20,true,1f,1f);
            }

        }else{
            this.stopLaser();
        }

        return tick > attackDuration;
    }

    private void constantRotatingLaser(float rotationSpeed){
        var rotationController = this.getWeaponRotationController();
        var attackController = this.getWeaponAttackController();

        rotationController.startConstantRotation(rotationSpeed);

        if (!(attackController.getCurrentAttack() instanceof GeburahLasersAttack)) {
            attackController.setCurrentAttack(new GeburahLasersAttack(this), true);
        }
    }

    private void stopLaser(){
        var rotationController = this.getWeaponRotationController();
        var attackController = this.getWeaponAttackController();

        rotationController.stopRotation();

        if (attackController.getCurrentAttack() instanceof GeburahLasersAttack){
            attackController.stopAttack();
        }

    }


    @SerializableField
    private boolean clockwise = true;

    public boolean runClockwiseProjectilesHammersRay(AttackInstance attackInstance){

        if (clockwise) {
            this.propagateSins(20, GeburahSins.MOVE_CLOCKWISE_SIN.get());
        }else{
            this.propagateSins(20, GeburahSins.MOVE_COUNTERCLOCKWISE_SIN.get());
        }
        int tick = attackInstance.tick;

        if (tick < ATTACK_START_DELAY){
            return false;
        }else{
            tick = tick - ATTACK_START_DELAY;
        }

        int attackDuration = 400;

        if (tick <= attackDuration) {
            this.attackPlayersWithRay(tick, 20, 10, 60);
            this.simpleCannonAttacks(tick, 5, 15);
            this.smackWithHammersAround(tick + 1, 40);
        }

        if (tick > attackDuration + 40){
            this.clockwise = !clockwise;
            return true;
        }

        return false;
    }

    public boolean simpleNoSinRunAroundAttack(AttackInstance attackInstance){

        this.propagateSins(20);

        int tick = attackInstance.tick;

        if (tick < ATTACK_START_DELAY){
            return false;
        }else{
            tick = tick - ATTACK_START_DELAY;
        }

        this.attackPlayersWithRay(tick, 20, 10, 60);

        this.simpleCannonAttacks(tick, 5,20);

        return tick > 400;
    }

    private boolean cannonRotationSwap = false;

    private void simpleCannonAttacks(int currentTick, int timeBetweenShots, int frequency){
        this.simpleCannonAttacks(currentTick,timeBetweenShots,frequency,80, -1);
    }

    private void simpleCannonAttacks(int currentTick, int timeBetweenShots, int frequency, int projectileFlyTime){
        this.simpleCannonAttacks(currentTick,timeBetweenShots,frequency,projectileFlyTime, -1);
    }

    private void simpleCannonAttacks(int currentTick, int timeBetweenShots, int frequency, int projectileFlyTime, float fixedAngle){

        var attackController = this.getWeaponAttackController();
        var weaponRotationController = this.getWeaponRotationController();

        int startShootTime = frequency + timeBetweenShots;

        int localtick = currentTick % startShootTime;

        if (localtick == 0){

            float rotation;
            if (fixedAngle == -1) {
                rotation = (random.nextFloat() * 100 + 40) * (cannonRotationSwap ? -1 : 1);
                cannonRotationSwap = !cannonRotationSwap;
            }else{
                rotation = fixedAngle  * (cannonRotationSwap ? -1 : 1);
            }

            weaponRotationController.rotateWeaponsBy(rotation, frequency);
        }else if (localtick == frequency - BossAnims.GEBURAH_FIRE_CANNONS.get().getAnimTime() + 5){
            attackController.setCurrentAttack(new GeburahAttackFireDefaultProjectiles(this, ARENA_RADIUS, projectileFlyTime,1f),false);
        }

    }

    private void attackPlayersWithRay(int currentTick, int rayChargeDuration, int attackRedirectTick, int frequency){

        if (rayChargeDuration >= frequency){
            throw new RuntimeException("Cannot have ray charge duration higher than frequency");
        }

        var rayController = this.getRayController();

        if (currentTick % frequency == 0){
            var list = this.playerPositionsCollector.getCurrentPlayerPositions();
            rayController.shoot(rayChargeDuration, 2, false, list);
        }

        if (rayController.getCurrentShotCharge() == attackRedirectTick){
            var list = this.playerPositionsCollector.getCurrentPlayerPositions();
            rayController.shoot(rayChargeDuration, 2, false, list.stream().map(v->v.add(0,0.5,0)).toList());
        }

    }

    private int hammerDirectionSwap = 1;

    private void smackWithHammersAround(int currentTick, int frequency){

        if (currentTick % frequency == 0) {

            float centerOffset = 20;
            float offsetFromCenter = 8;


            int localDirectionSwap = hammerDirectionSwap > 1 ? -1 : 1;

            for (var dir : new HorizontalCircleRandomDirections(random, 8, 0)) {


                Vec3 pos = this.position().add(dir.scale(centerOffset));

                Vec3 hammerOffset = dir.scale(localDirectionSwap).scale(offsetFromCenter);

                JusticeHammerAttack.summon(level(), pos.add(hammerOffset), hammerOffset.reverse());

                localDirectionSwap = -localDirectionSwap;
            }

            hammerDirectionSwap = (hammerDirectionSwap + 1) % 4;

        }



    }




    //------------------------------------------------------------------ATTACKS-END-----------------------------------------------------------------------

    private void tickSinsAppearTick(){
        var list = this.getEntityData().get(ACTIVE_SINS);
        sinsAppearTickO = sinsAppearTick;
        if (list.isEmpty()){
            sinsAppearTick = Mth.clamp(sinsAppearTick - 1,0, MAX_SIN_APPEAR_TICK);
        }else{
            sinsAppearTick = Mth.clamp(sinsAppearTick + 1,0, MAX_SIN_APPEAR_TICK);
        }
    }

    public void tickClockwiseSin(){

        for (var player : this.playerPositionsCollector.getPlayers()){

            PlayerSins playerSins = PlayerSins.getPlayerSins(player);

            if ((playerSins.hasSinActive(GeburahSins.MOVE_CLOCKWISE_SIN.get()) || playerSins.hasSinActive(GeburahSins.MOVE_COUNTERCLOCKWISE_SIN.get())) && !playerSins.isGainingSinsOnCooldown()){

                var pair = this.playerPositionsCollector.getOldAndCurrentPlayerPosition(player);
                Vec3 oldPos = pair.first.multiply(1,0,1);
                Vec3 newPos = pair.second.multiply(1,0,1);

                Vec3 between = newPos.subtract(oldPos);

                if (between.length() < 0.2) continue;


                Vec3 betweenThisAndThat = newPos.subtract(this.position().multiply(1,0,1));


                Vec3 rotated = betweenThisAndThat.yRot(FDMathUtil.FPI / 2);

                double dot = between.normalize().dot(rotated.normalize());

                if (playerSins.hasSinActive(GeburahSins.MOVE_CLOCKWISE_SIN.get())) {
                    if (dot < -0.1) {
                        PlayerSinsHandler.sin((ServerPlayer) player, 40);
                    }
                }else{
                    if (dot > 0.1) {
                        PlayerSinsHandler.sin((ServerPlayer) player, 40);
                    }
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

        float angle = 360f / CANNONS_AMOUNT;

        List<Pair<Vec3, Vec3>> pairs = new ArrayList<>();

        for (int i = 0; i < CANNONS_AMOUNT; i++) {

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

    public void propagateSins(int cooldown, PlayerSin... sins){
        var list = new ArrayList<>(Arrays.stream(sins).toList());
        this.getEntityData().set(ACTIVE_SINS, list);
        this.trySetAllPlayersSins(list, cooldown, true);
    }

    private void particles(){

        this.coreParticles();
        this.laserParticles();

    }

    @Override
    public boolean hurt(DamageSource src, float p_21017_) {

        return (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)) && super.hurt(src, p_21017_);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }


    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Entity p_21294_) {

    }

    @Override
    protected void pushEntities() {

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

    public void trySetAllPlayersSins(List<PlayerSin> sins, int sinCooldown, boolean sendUpdate){

        AABB sinBox = this.constructSinBox();

        for (var player : this.playerPositionsCollector.getPlayers()){

            PlayerSins playerSins = PlayerSins.getPlayerSins(player);

            if (!playerSins.hasExactlyThisSins(sins)) {

                List<ActivePlayerSinInstance> instances = sins.stream().map(sin -> {
                    return new ActivePlayerSinInstance(sin, new WorldBox(level().dimension(), sinBox), 0);
                }).toList();

                playerSins.setSinGainCooldown(sinCooldown);

                playerSins.setActiveSins(instances);

                if (sendUpdate) {
                    PlayerSins.setPlayerSins(player, playerSins);
                }
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
        builder.define(ACTIVE_SINS, new ArrayList<>());
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

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_21197_) {
        return false;
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {

    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {

    }


    @EventBusSubscriber(modid = FDBosses.MOD_ID)
    public static class Events {

        @SubscribeEvent
        public static void killEvent(LivingDeathEvent event){

            var entity = event.getEntity();
            var source = event.getSource();
            Level level = entity.level();

            if (!(entity instanceof ServerPlayer player)) {
                if (source.getEntity() instanceof ServerPlayer serverPlayer) {
                    PlayerSins playerSins = PlayerSins.getPlayerSins(serverPlayer);

                    if (playerSins.hasSinActive(GeburahSins.KILL_ENTITY_SIN.get())) {
                        PlayerSinsHandler.sin(serverPlayer, 100);
                    }

                }
            }else{
                var inventory = player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++){

                    var item = inventory.getItem(i);
                    if (item.is(BossItems.GEBURAH_EXPLOSIVE_CRYSTAL.get())){
                        inventory.setItem(i, ItemStack.EMPTY);
                    }

                }
            }

        }

    }

}
