package com.finderfeed.fdbosses.content.entities.chesed_boss;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.client.particles.chesed_attack_ray.ChesedRayOptions;
import com.finderfeed.fdbosses.client.particles.particle_processors.ChesedRayCircleParticleProcessor;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.sonic_particle.SonicParticleOptions;
import com.finderfeed.fdbosses.content.entities.BossDespawner;
import com.finderfeed.fdbosses.content.entities.BossInitializer;
import com.finderfeed.fdbosses.content.entities.FDDespawnable;
import com.finderfeed.fdbosses.content.entities.IEffectImmune;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal.ChesedCrystalEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_monolith.ChesedMonolith;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_one_shot_vertical_ray.ChesedOneShotVerticalRayEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_vertical_ray.ChesedMovingVerticalRay;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdbosses.content.entities.chesed_boss.electric_sphere.ChesedElectricSphereEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlock;
import com.finderfeed.fdbosses.content.entities.chesed_boss.kinetic_field.ChesedKineticFieldEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake.RadialEarthquakeEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ray_reflector.ChesedRayReflector;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor.MalkuthFloorEntity;
import com.finderfeed.fdbosses.content.util.DelayedSound;
import com.finderfeed.fdbosses.content.util.RepeatedSound;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdbosses.packets.ChesedRayReflectPacket;
import com.finderfeed.fdbosses.content.projectiles.ChesedBlockProjectile;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.network.lib_packets.PlaySoundInEarsPacket;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.cutscenes.packets.StartCutscenePacket;
import com.finderfeed.fdlib.systems.cutscenes.packets.StopCutscenePacket;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.systems.hud.bossbars.FDServerBossBar;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.systems.particle.CompositeParticleProcessor;
import com.finderfeed.fdlib.systems.particle.SetParticleSpeedProcessor;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterData;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.BoundToEntityProcessor;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.CircleSpawnProcessor;
import com.finderfeed.fdlib.systems.screen.screen_effect.SendScreenEffectPacket;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.finderfeed.fdbosses.init.BossAnims.CHESED_ATTACK;
import static com.finderfeed.fdbosses.init.BossAnims.*;

public class ChesedEntity extends FDMob implements ChesedBossBuddy, BossSpawnerContextAssignable, IEffectImmune, FDDespawnable {

    public static final String ROCKFALL_TICKER = "ROCKFALL";

    public static final String RAY_ATTACK_LAYER = "ATTACK";

    public static final String FINAL_ATTACK_LAYER = "BOOM";

    public static final String EARTHQUAKE_ATTACK_LAYER = "EARTHQUAKE";

    public static int ARENA_HEIGHT = 40;
    public static int ARENA_RADIUS = 39;

    private UUID bossSpawnerUUID;

    public static final String FINAL_ATTACK = "final";
    public static final String CRYSTALS_ATTACK = "crystals";
    public static final String RAY_ATTACK = "ray";
    public static final String BLOCKS_ATTACK = "blocks";
    public static final String ROLL_ATTACK = "roll";
    public static final String EARTHQUAKE_ATTACK = "equake";
    public static final String ROCKFALL_ATTACK = "rockfall";
    public static final String ELECTRIC_SPHERE_ATTACK = "esphere";
    public static final String RAY_EVASION_ATTACK = "ray_evasion";

    private static final Vec3[] MONOLITH_SPAWN_OFFSETS = {
            new Vec3(10,0,10),
            new Vec3(-10,0,10),
            new Vec3(10,0,-10),
            new Vec3(-10,0,-10)
    };

    public static final EntityDataAccessor<Boolean> IS_ROLLING = SynchedEntityData.defineId(ChesedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_LAUNCHING_ORBS = SynchedEntityData.defineId(ChesedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_DRAINING_MONOLITHS = SynchedEntityData.defineId(ChesedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DRAIN_PERCENT = SynchedEntityData.defineId(ChesedEntity.class,EntityDataSerializers.FLOAT);

    public FDServerBossBar bossBar = new FDServerBossBar(BossBars.CHESED_BOSS_BAR,this);

    public AttackChain chain;
    protected static FDModel serverModel;
    protected static FDModel clientModel;
    private Vec3 oldRollPos;
    private boolean playIdle = true;
    private boolean lookingAtTarget = true;

    public float drainPercentOld;

    private LivingEntity target;
    private Vec3 previousTargetPos;

    private boolean doBlinding = true;

    private int remainingHits = 10;

    private boolean alreadySpawned = false;

    private BossInitializer<ChesedEntity> bossInitializer = new ChesedBossInitializer(this);

    private List<DelayedSound> delayedSounds = new ArrayList<>();

    private RepeatedSound repeatedSound = null;

    private boolean dropLoot = true;

    private int secondPhaseTicker = 0;

    private boolean secondPhaseAnimPlayed = false;

    private int skipAttackTimes = 0;

    private BossDespawner<ChesedEntity> bossDespawner;

    public ChesedEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        if (serverModel == null) {
            serverModel = new FDModel(BossModels.CHESED.get());
        }
        if (clientModel == null){
            clientModel = new FDModel(BossModels.CHESED.get());
        }

        remainingHits = BossConfigs.BOSS_CONFIG.get().chesedConfig.chesedMaxHits;

        if (!level.isClientSide) {

            AttackOptions ray = AttackOptions.builder()
                    .setPreAttack(CRYSTALS_ATTACK)
                    .addAttack(RAY_ATTACK)
                    .build();

            AttackOptions rayOrBlocks = AttackOptions.builder()
                    .addAttack(3,ray)
                    .addAttack(2,BLOCKS_ATTACK)
                    .build();

            chain = new AttackChain(level.random)
                    .registerAttack("nothing",this::doNothing)
                    .registerAttack(FINAL_ATTACK,this::finalBOOMAttack)
                    .registerAttack(CRYSTALS_ATTACK,this::summonCrystals)
                    .registerAttack(RAY_ATTACK,this::rayAttack) //0 and in the middle
                    .registerAttack(BLOCKS_ATTACK,this::blockAttack) //in the middle
                    .registerAttack(ROLL_ATTACK,this::roll) //after all
                    .registerAttack(EARTHQUAKE_ATTACK,this::earthquakeAttack) // 1
                    .registerAttack(ROCKFALL_ATTACK,this::rockfallAttack) // 1
                    .registerAttack(ELECTRIC_SPHERE_ATTACK,this::electricSphereAttack) // 1
                    .registerAttack(RAY_EVASION_ATTACK,this::rayEvasionAttack)
                    .attackListener(this::attackListener)
                    .addAttack(0, ray)
                    .addAttack(1,AttackOptions.builder()
                            .addAttack(ELECTRIC_SPHERE_ATTACK)
                            .setNextAttack(rayOrBlocks)
                            .build())
                    .addAttack(1,AttackOptions.builder()
                            .addAttack(ROCKFALL_ATTACK)
                            .setNextAttack(rayOrBlocks)
                            .build())
                    .addAttack(1,AttackOptions.builder()
                            .addAttack(EARTHQUAKE_ATTACK)
                            .setNextAttack(rayOrBlocks)
                            .build())
                    .addAttack(4,AttackOptions.builder()
                            .addAttack(ROLL_ATTACK)
                            .build())
                    .addAttack(5, RAY_EVASION_ATTACK)
                    .addAttack(6, FINAL_ATTACK)
            ;

            this.bossDespawner = new BossDespawner<>(
                    this,
                    new AABB(-ARENA_RADIUS, -5,-ARENA_RADIUS,ARENA_RADIUS,ARENA_HEIGHT,ARENA_RADIUS),
                    30,
                    ChesedCrystalEntity.class,
                    ChesedMonolith.class,
                    ChesedBlockProjectile.class,
                    ChesedFallingBlock.class,
                    ChesedKineticFieldEntity.class,
                    ChesedElectricSphereEntity.class
            );

        }
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {

        if (!level().isClientSide && src.getEntity() instanceof LivingEntity livingEntity && !(livingEntity instanceof ChesedEntity)){
            if (!this.isRolling()) {
                Vec3 between = livingEntity.position().subtract(this.position());
                double distance = between.length();
                if (distance < 10) {
                    between = between.normalize();

                    Vec3 speed = new Vec3(between.x * 10, between.y + 0.5, between.z * 10);
                    if (livingEntity instanceof ServerPlayer serverPlayer) {
                        FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
                    } else {
                        livingEntity.setDeltaMovement(between.x * 10, between.y + 0.5, between.z * 10);
                        livingEntity.hasImpulse = true;
                    }

                    livingEntity.hurt(BossDamageSources.chesedAttack(this),2f);

                    level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.FAST_LIGHTNING_STRIKE.get(), SoundSource.HOSTILE, 2f, 1f);
                    FDLibCalls.sendParticles((ServerLevel) level(), BallParticleOptions.builder()
                            .size(25f)
                            .scalingOptions(0, 0, 2)
                            .color(150, 230, 255)
                            .build(), this.position().add(between.multiply(2, 2, 2)), 100);

                }
            }
        }

        if (!src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;



        return super.hurt(src,damage);
    }

    @Override
    public void tick() {
        if (!this.isRolling() && !level().isClientSide) {
            this.setYRot(yHeadRot);
        }
        drainPercentOld = this.getMonolithDrainPercent();
        super.tick();
        AnimationSystem system = this.getAnimationSystem();
        system.setVariable("variable.radius",580);
        system.setVariable("variable.appear_height",400);
        system.setVariable("variable.angle",360);
        if (!this.level().isClientSide){

            this.passiveEntityPullInArena();

            this.bossBar.setPercentage(this.getRemainingHits() / (float) this.getBossMaxHits());

            bossInitializer.tick();

            this.tickDelayedSounds();

            if (tickCount == 5 && !alreadySpawned){
                alreadySpawned = true;
                this.summonOrReviveMonoliths();
            }

            if (playIdle) {
                system.startAnimation("IDLE", AnimationTicker.builder(CHESED_IDLE).build());
            } else {
                system.stopAnimation("IDLE");
            }

            if (level().getGameTime() % 5 == 0) {
                this.blindCombatants();
            }

            if (bossInitializer.isFinished() && !this.isDeadOrDying() && secondPhaseTicker <= 0) {


                this.chain.tick();

                this.electrifiedAir();

                if (repeatedSound != null && repeatedSound.tick(level())){

                    if (repeatedSound.sound.soundEvent == BossSounds.ELECTRIC_SPHERES_STORM.get()){
                        level().playSound(null,this.getX(),this.getY(),this.getZ(),BossSounds.ELECTRIC_SPHERES_STORM_END.get(),SoundSource.HOSTILE, repeatedSound.sound.volume,repeatedSound.sound.pitch);
                    }

                    repeatedSound = null;
                }

                if (this.getTarget() != null) {
                    this.checkTarget(this.getTarget());
                    if (this.getTarget() != null) {
                        if (this.lookingAtTarget) {
                            this.lookAtTarget(this.getTarget());
                        } else {
                            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getEyePosition().add(this.getLookAngle()));
                        }
                        previousTargetPos = target.position();
                    }
                } else {
                    if (level().getGameTime() % 20 == 0) {
                        this.changeTarget();
                    }
                }


                this.bossDespawner.tick();

            }else{
                this.setRolling(false);
                this.setDrainingMonoliths(false);
            }

            this.tickSecondPhaseStart();

        }else{


            this.clientBoomAttackRotatingRay();

            if (this.isDrainingFromMonoliths()){
                this.monolithEnergyDrainParticles();
            }

            this.playIdleSound();

            if (this.isRolling()){
                this.handleClientRolling();
            }else{
                AnimationTicker ticker = this.getAnimationSystem().getTicker("APPEAR");
                if (ticker != null) {
                    if (this.tickCount > 60) {
                        this.idleParticles();
                    }
                }else{
                    this.idleParticles();
                }
            }
        }
    }

    private void playIdleSound(){
        if (level().getGameTime() % 28 == 0){
            level().playSound(FDClientHelpers.getClientPlayer(), this.getX(),this.getY(),this.getZ(),BossSounds.CHESED_IDLE.get(),SoundSource.HOSTILE,5f,1f);
        }
    }

    private void addMonolithHP(){
        var monoliths = this.getMonoliths();
        float monolithHPGain = BossConfigs.BOSS_CONFIG.get().chesedConfig.additionalSecondPhaseMonolithHP;
        for (var m : monoliths){
            AttributeModifier hpModifier = new AttributeModifier(FDBosses.location("monolith_hp"), monolithHPGain, AttributeModifier.Operation.ADD_VALUE);
            var attr = m.getAttribute(Attributes.MAX_HEALTH);
            if (attr != null) {
                attr.addPermanentModifier(
                        hpModifier
                );
            }
            m.setHealth(m.getMaxHealth());
            m.setDeactivated(false);
        }
    }

    private void tickSecondPhaseStart(){

        if (!bossInitializer.isFinished() || tickCount < 250) return;

        int tickerTime = 210;

        if (!secondPhaseAnimPlayed && this.isBelowHalfHP()){
            this.secondPhaseTicker = tickerTime;
            secondPhaseAnimPlayed = true;
            lookingAtTarget = false;
            this.killCrystals();
            this.setRolling(false);
            this.entityData.set(IS_LAUNCHING_ORBS,false);
            this.chain.reset();

            this.addMonolithHP();
        }

        if (this.secondPhaseTicker <= 0) return;

        Vec3 hlook = this.getLookAngle().multiply(1,0,1).normalize();


        int fullOffset = 20;
        int firstRayOffset = 39;
        int perRayOffset = 11;
        float distmult = 37;
        float heighton = 23;

        if (this.secondPhaseTicker == tickerTime){

            this.stopAllNonIdleAnimations();

            this.getAnimationSystem().startAnimation("PREPARE_SECOND_PHASE", AnimationTicker.builder(CHESED_SECOND_PHASE_PREPARE)
                            .setToNullTransitionTime(0)
                            .startTime(2)
                            .setSpeed(0.75f)
                    .build());



            this.sendPacketToCombatants(new SendScreenEffectPacket<>(new ScreenColorData(0f,0f,0f,1f),FDScreenEffects.SCREEN_COLOR.get(),0,fullOffset,20));




        }else if (this.secondPhaseTicker <= tickerTime - fullOffset && this.secondPhaseTicker >= tickerTime - fullOffset - 10){

            int count = 3;

            float angle = FDMathUtil.FPI * 2 / count;

            Vec3 center = this.position();

            for (int i = 0; i < count; i++) {
                float a = angle * i;
                Vec3 v = new Vec3(3,0,0).yRot(a);
                Vec3 ppos = this.position().add(v);
                FDLibCalls.sendParticles(((ServerLevel) level()),BallParticleOptions.builder()
                        .size(0.5f)
                        .color(100 + random.nextInt(50), 255, 255)
                        .particleProcessor(new CompositeParticleProcessor(
                                new CircleParticleProcessor(center,true,true,1),
                                new SetParticleSpeedProcessor(new Vec3(0,0.075,0))
                        ))
                        .scalingOptions(10,10,0)
                        .build(),ppos,ARENA_RADIUS * 2);

            }
            if (this.secondPhaseTicker == tickerTime - fullOffset) {
                Vec3 pos = this.position()
                        .add(0, 3, 0)
                        .add(hlook.multiply(4, 0, 4));

                this.sendPacketToCombatants(new DefaultShakePacket(FDShakeData.builder()
                        .stayTime(30)
                        .inTime(2)
                        .outTime(0)
                        .amplitude(0.2f)
                        .build()));

                this.getAnimationSystem().startAnimation("PREPARE_SECOND_PHASE", AnimationTicker.builder(CHESED_SECOND_PHASE_PREPARE)
                        .setToNullTransitionTime(0)
                        .build());

                CutsceneData cutsceneData = new CutsceneData()
                        .time(60)
                        .timeEasing(EasingType.EASE_IN_OUT)
                        .stopMode(CutsceneData.StopMode.UNSTOPPABLE)
                        .addCameraPos(new CameraPos(pos, hlook.reverse().add(0, -0.3, 0)))
                        .addCameraPos(new CameraPos(pos.add(hlook.multiply(20, 0, 20)), hlook.reverse().add(0, 0.1, 0)));

                this.sendPacketToCombatants(new StartCutscenePacket(cutsceneData));
            }
        } else if (this.secondPhaseTicker == tickerTime - 30 - fullOffset){
            this.getAnimationSystem().startAnimation("SECOND_PHASE_START", AnimationTicker.builder(CHESED_SECOND_PHASE_STRIKE_WALLS)
                    .setToNullTransitionTime(0)
                    .build());

        }else if (this.secondPhaseTicker == tickerTime - firstRayOffset - fullOffset){

            Vec3 v = hlook.yRot((FDMathUtil.FPI / 4 + FDMathUtil.FPI / 2))
                    .multiply(distmult,0,distmult)
                    .add(0,heighton,0);
            this.decorativeRay(v.add(this.position()));

            this.sendPacketToCombatants(new DefaultShakePacket(FDShakeData.builder()
                    .stayTime(perRayOffset - 2)
                    .inTime(2)
                    .outTime(0)
                    .amplitude(0.2f)
                    .build()));

        }else if (this.secondPhaseTicker == tickerTime - firstRayOffset - perRayOffset - fullOffset){

            Vec3 v = hlook.yRot((FDMathUtil.FPI / 4 + FDMathUtil.FPI))
                    .multiply(distmult,0,distmult)
                    .add(0,heighton,0);
            this.decorativeRay(v.add(this.position()));


            this.sendPacketToCombatants(new DefaultShakePacket(FDShakeData.builder()
                    .stayTime(perRayOffset - 2)
                    .inTime(0)
                    .outTime(0)
                    .amplitude(0.5f)
                    .build()));

        }else if (this.secondPhaseTicker == tickerTime - firstRayOffset - perRayOffset * 2 - fullOffset){

            Vec3 v = hlook.yRot((FDMathUtil.FPI / 4 + FDMathUtil.FPI * 3 / 2))
                    .multiply(distmult,0,distmult)
                    .add(0,heighton,0);
            this.decorativeRay(v.add(this.position()));


            this.sendPacketToCombatants(new DefaultShakePacket(FDShakeData.builder()
                    .stayTime(perRayOffset - 2)
                    .inTime(0)
                    .outTime(0)
                    .amplitude(0.75f)
                    .build()));

        }else if (this.secondPhaseTicker == tickerTime - firstRayOffset - perRayOffset * 3 - fullOffset){

            Vec3 v = hlook.yRot(FDMathUtil.FPI / 4)
                    .multiply(distmult,0,distmult)
                    .add(0,heighton,0);
            this.decorativeRay(v.add(this.position()));

            this.sendPacketToCombatants(new DefaultShakePacket(FDShakeData.builder()
                    .stayTime(40)
                    .inTime(0)
                    .outTime(0)
                    .amplitude(0.9f)
                    .build()));

            this.sendPacketToCombatants(new SendScreenEffectPacket<>(new ScreenColorData(0f,0f,0f,1f),FDScreenEffects.SCREEN_COLOR.get(),5,80,20));


        }else if (this.secondPhaseTicker == 40){

            lookingAtTarget = true;
            this.sendPacketToCombatants(new StopCutscenePacket());

            float rh = BossConfigs.BOSS_CONFIG.get().chesedConfig.healthAfterSecondPhaseTransition;

            for (Player player : this.getCombatants(false)){
                float health = player.getHealth();
                if (health > rh){
                    player.setHealth(rh);
                }
            }

            skipAttackTimes = 2;

            this.chain.reset();
        }

        secondPhaseTicker = Mth.clamp(secondPhaseTicker - 1,0, Integer.MAX_VALUE);
    }

    private void decorativeRay(Vec3 end){

        Vec3 start = this.position().add(0,1.5,0);

        Vec3 look = start.subtract(end).normalize();

        ChesedRayOptions options = ChesedRayOptions.builder()
                .time(2,2,5)
                .lightningColor(90, 180, 255)
                .color(100, 255, 255)
                .end(end)
                .width(0.8f)
                .build();
        FDLibCalls.sendParticles((ServerLevel) level(),options,start,120);

        FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                .size(25f)
                .scalingOptions(1,0,2)
                .color(150,230,255)
                .build(),end,200);

        BossUtil.chesedRaySmoke((ServerLevel) level(),end,look,120);

        PositionedScreenShakePacket.send((ServerLevel) level(),FDShakeData.builder()
                .frequency(5)
                .amplitude(5f)
                .inTime(0)
                .stayTime(0)
                .outTime(5)
                .build(),end,120);

        BossUtil.chesedRayExplosion((ServerLevel) level(),end,look,100,15,0.75f);

        this.summonStonesAfterRayAttack(30, look.multiply(1,0,1).normalize(),end);

        ((ServerLevel)level()).playSound(null,end.x,end.y,end.z, BossSounds.CHESED_RAY.get(), SoundSource.HOSTILE,5f,1f);

    }

    private void electrifiedAir(){
        for (LivingEntity entity : this.getAffectedEntities(false)){

            double y = this.getY();
            double ey = entity.getY();
            if (ey - y > 3){
                if (entity.tickCount % 30 == 0) {


                    float damagePercent = BossConfigs.BOSS_CONFIG.get().chesedConfig.electrifiedAirCurrentHealthDamagePercent;

                    float instadeathHP = BossConfigs.BOSS_CONFIG.get().chesedConfig.electrifiedAirInstadeathHP;

                    float health = entity.getHealth();

                    float damage;
                    if (health <= instadeathHP){
                        damage = Integer.MAX_VALUE;
                    }else{
                        damage = health * (damagePercent / 100f);
                    }

                    entity.hurt(BossDamageSources.chesedAttack(this), damage);
                }

                if (tickCount % 5 == 0){
                    ((ServerLevel)level()).sendParticles(LightningParticleOptions.builder()
                            .color(50 + random.nextInt(40), 183 + random.nextInt(60), 200 + random.nextInt(50))
                            .quadSize(0.1f)
                            .lifetime(10)
                            .randomRoll(true)
                            .build(),
                            entity.getX(),
                            entity.getY() + entity.getBbHeight() / 2,
                            entity.getZ(),
                            4, entity.getBbWidth()/2,0.5,entity.getBbWidth()/2,0);
                }

            }
        }
    }

    private void tickDelayedSounds(){
        if (!level().isClientSide) {
            var iterator = this.delayedSounds.listIterator();
            while (iterator.hasNext()) {
                var s = iterator.next();
                if (s.tick()){
                    level().playSound(null, s.pos.x,s.pos.y,s.pos.z,s.sound.soundEvent, s.sound.soundSource,s.sound.volume,s.sound.pitch);
                    iterator.remove();
                }
            }
        }
    }

    public AttackAction attackListener(String attackName){
        if (this.getTarget() == null){
            return AttackAction.WAIT;
        }

        if (skipAttackTimes > 0){
            skipAttackTimes--;
            return AttackAction.SKIP;
        }

        if (!this.isBelowHalfHP()){
            if (attackName.equals(RAY_EVASION_ATTACK)){
                return AttackAction.SKIP;
            }
        }

        this.doBlinding = true;
        return AttackAction.PROCEED;
    }


    public List<ChesedMonolith> getMonoliths(){
        AABB box = new AABB(-30,-10,-30,30,10,30).move(this.position());
        var list = level().getEntitiesOfClass(ChesedMonolith.class,box);
        return list;
    }

    public void setMonolithsImmunity(boolean immune){
        List<ChesedMonolith> monoliths = this.getMonoliths();
        for (ChesedMonolith monolith : monoliths){
            monolith.setImmuneToAttacks(immune);
        }
    }

    public void summonOrReviveMonoliths(){
        List<ChesedMonolith> list = this.getMonoliths();
        if (list.size() != 4){
            for (ChesedMonolith monolith : list){
                monolith.remove(RemovalReason.DISCARDED);
            }
            this.summonMonoliths();
        }else{

            for (ChesedMonolith monolith : list) {
                monolith.setDeactivated(false);
            }

        }
    }

    private void removeMonoliths(){
        AABB box = new AABB(-30,-10,-30,30,10,30).move(this.position());
        var list = level().getEntitiesOfClass(ChesedMonolith.class,box);
        for (ChesedMonolith monolith : list){
            monolith.remove(RemovalReason.DISCARDED);
        }
    }

    private void summonMonoliths(){
        for (Vec3 pos : MONOLITH_SPAWN_OFFSETS){
            ChesedMonolith monolith = new ChesedMonolith(BossEntities.CHESED_MONOLITH.get(),level());
            monolith.setPos(pos.add(this.position()));
            level().addFreshEntity(monolith);
        }
    }

    public int getAliveMonolithsCount(){
        var monoliths = this.getMonoliths();
        if (monoliths.isEmpty()) return 0;
        int count = 0;
        for (ChesedMonolith monolith : monoliths){
            if (!monolith.isDeactivated()){
                count++;
            }
        }
        return count;
    }

    public List<LivingEntity> getAffectedEntities(boolean includeCreativeAndSpectator){
        float radius = this.targetDetectionRadius();
        List<LivingEntity> combatants = level().getEntitiesOfClass(LivingEntity.class,new AABB(-radius,-2,-radius,radius,ARENA_HEIGHT,radius).move(this.position()),(entity)->{

            if (entity instanceof ChesedBossBuddy) return false;

            boolean result = entity.position().multiply(1,0,1).distanceTo(this.position().multiply(1,0,1)) <= radius;

            if (entity instanceof Player player){
                result = result && (includeCreativeAndSpectator || (!player.isCreative() && !player.isSpectator()));
            }

            return result;
        });
        return combatants;
    }

    public List<Player> getCombatants(boolean includeCreativeAndSpectator){
        return this.getCombatants(includeCreativeAndSpectator, 0);
    }

    public List<Player> getCombatants(boolean includeCreativeAndSpectator,float additionalRadius){
        float radius = this.targetDetectionRadius() + additionalRadius;
        List<Player> combatants = level().getEntitiesOfClass(Player.class,new AABB(-radius,-1,-radius,radius,ARENA_HEIGHT,radius).move(this.position()),(player)->{
            return player.position().multiply(1,0,1).distanceTo(this.position().multiply(1,0,1)) <= radius
                    && (includeCreativeAndSpectator || !(player.isCreative() || player.isSpectator())) && player.isAlive();
        });
        return combatants;
    }

    private void checkTarget(LivingEntity target){

        if (target.isDeadOrDying()){
            this.changeTarget();
            return;
        }else if (target.position().distanceTo(this.position()) > this.targetDetectionRadius()){
            this.changeTarget();
            return;
        }


        if (target instanceof Player player){
            if (player.isCreative() || player.isSpectator()){
                this.changeTarget();
                return;
            }
        }


    }

    private void changeTarget(){
        List<Player> combatants = this.getCombatants(false);
        if (combatants.isEmpty()){
            this.setTarget(null);
        }else{
            this.setTarget(combatants.get(random.nextInt(combatants.size())));
        }
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        if (target != null) {
            this.previousTargetPos = target.position();
        }
    }

    private void blindCombatants(){
        var combatants = this.getCombatants(true);

        for (Player player : combatants){

            Vec3 lookAngle = player.getLookAngle();
            Vec3 b = player.position().subtract(this.position()).normalize();
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,400,0,true,false));
            if (!(player.isCreative() || player.isSpectator())) {
                if (doBlinding) {
                    if (lookAngle.dot(b) > 0.05 && !player.hasEffect(BossEffects.CHESED_ENERGIZED)) {
                        player.addEffect(new MobEffectInstance(BossEffects.CHESED_GAZE, 200, 0, true, true));
                    } else {
                        player.removeEffect(BossEffects.CHESED_GAZE);
                    }
                }else{
                    player.removeEffect(BossEffects.CHESED_GAZE);
                }
            }else{
                player.removeEffect(BossEffects.CHESED_GAZE);
            }

        }

    }

    private void idleParticles(){
        if (deathTime > CHESED_DEATH.get().getAnimTime() - 10) return;

        if (this.level().getGameTime() % 2 == 0){
            float rad = 2;
            if (this.entityData.get(IS_LAUNCHING_ORBS)){
                rad = 5;
            }
            var bonePos = this.getModelPartPosition(this,"core", clientModel);
            Vec3 basePos = new Vec3(bonePos.x,bonePos.y,bonePos.z).add(this.getX(),this.getY(),this.getZ());
            float baseAngle = -(float)Math.toRadians(this.yBodyRot) + FDMathUtil.FPI / 4;
            float randomRange = FDMathUtil.FPI * 2 - FDMathUtil.FPI / 2;
            for (int i = 0; i < 2;i++) {
                var endOffset = new Vector3f(0, 0, rad).rotateY(baseAngle + randomRange * random.nextFloat()).add(0, -bonePos.y, 0);
                Vec3 end = basePos.add(endOffset.x,endOffset.y,endOffset.z);

                level().addParticle(ArcLightningOptions.builder(BossParticles.ARC_LIGHTNING.get())
                                .end(end.x, end.y, end.z)
                                .lifetime(2)
                                .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                                .lightningSpread(0.25f)
                                .width(0.1f)
                                .segments(6)
                                .circleOffset(random.nextFloat() * 2 - 2)
                                .build(),
                        true, basePos.x, basePos.y, basePos.z, 0, 0, 0
                );
            }
        }


        if (this.entityData.get(IS_LAUNCHING_ORBS)){

            for (int i = 0; i <= 30;i++){
                this.summonOrbLaunchingParticle();
            }

        }

    }

    private void summonOrbLaunchingParticle(){


        Vec3 direction = new Vec3(1,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);

        float rndHeight = random.nextFloat();
        float heightMd = FDEasings.easeOut(1 - Math.abs(rndHeight * 2 - 1)) * 0.5f + 0.5f;

        rndHeight *= 3;

        Vec3 basePos = this.position().add(
                0,
                rndHeight,
                0
        );

        Vec3 rotateFromPos = this.position().add(
                direction.x * heightMd * 3,
                rndHeight,
                direction.z * heightMd * 3
        );

        ParticleOptions options;

        if (random.nextFloat() < 0.9) {
            options = BallParticleOptions.builder()
                    .particleProcessor(new CircleParticleProcessor(
                            basePos, true, true, 2
                    ))
                    .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                    .size(0.4f)
                    .scalingOptions(2, 20, 10)
                    .build();
        }else{
            options = LightningParticleOptions.builder()
                    .particleProcessor(new CircleParticleProcessor(
                            basePos, true, true, 2
                    ))
                    .color(50 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                    .quadSize(0.4f)
                    .lifetime(10)
                    .randomRoll(true)
                    .build();
        }

        level().addParticle(options,true,rotateFromPos.x,rotateFromPos.y,rotateFromPos.z,0,0,0);

    }

    private void monolithEnergyDrainParticles(){
        for (Vec3 pos : MONOLITH_SPAWN_OFFSETS){

            Vec3 ppos = this.position().add(pos.add(0,2.5,0));
            Vec3 center = this.position().add(0,2.5,0);

            BallParticleOptions options = BallParticleOptions.builder()
                    .particleProcessor(new CircleParticleProcessor(
                            center,false,true,0.5f
                    ))
                    .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                    .size(1f)
                    .scalingOptions(2,15,0)
                    .build();

            float p = this.getMonolithDrainPercent();

            level().addParticle(options,ppos.x,ppos.y,ppos.z,0,FDMathUtil.lerp(0,-0.08,p),0);



        }
    }

    public boolean rayEvasionAttack(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            if (tick == 0) {
                this.trapPlayers(true);
                doBlinding = false;
            }else if (tick > 20) {
                inst.nextStage();
            }
        }else if (stage == 1){

            int rate = 40;

            if (tick % rate == 0){
                for (ChesedKineticFieldEntity kineticFieldEntity : this.findCages()){
                    this.summonRayPattern(kineticFieldEntity, kineticFieldEntity.getSquareRadius() - 0.5, (tick / rate) % 4);
                }
            }else if (tick > rate * 8){
                inst.nextStage();
            }
        }else{
            if (tick > 20) {
                this.trapPlayers(false);
                doBlinding = true;
                return true;
            }
        }

        return false;
    }

    private List<ChesedKineticFieldEntity> findCages(){
        return this.level().getEntitiesOfClass(ChesedKineticFieldEntity.class, new AABB(-ARENA_RADIUS,-1,-ARENA_RADIUS,ARENA_RADIUS,3,ARENA_RADIUS).move(this.position()));
    }

    //0 - center and corners
    //1 - thunder cross split attack
    //2 - square
    //3 - circle
    private void summonRayPattern(ChesedKineticFieldEntity kineticFieldEntity, double maxRad, int pattern){

        Vec3 pos = kineticFieldEntity.position();

        if (pattern == 0) {

            this.summonOneShotAtPos(pos.add(-maxRad, 0, -maxRad));
            this.summonOneShotAtPos(pos.add(maxRad, 0, -maxRad));
            this.summonOneShotAtPos(pos.add(maxRad, 0, maxRad));
            this.summonOneShotAtPos(pos.add(-maxRad, 0, maxRad));

            this.summonOneShotAtPos(pos.add(maxRad, 0, 0));
            this.summonOneShotAtPos(pos.add(0, 0, maxRad));
            this.summonOneShotAtPos(pos.add(-maxRad, 0, 0));
            this.summonOneShotAtPos(pos.add(0, 0, -maxRad));

            this.summonOneShotAtPos(pos);
        }else if (pattern == 1){

            this.summonOneShotAtPos(pos.add(-maxRad/2,0,-maxRad/2));
            this.summonOneShotAtPos(pos.add(maxRad/2,0,-maxRad/2));
            this.summonOneShotAtPos(pos.add(maxRad/2,0,maxRad/2));
            this.summonOneShotAtPos(pos.add(-maxRad/2,0,maxRad/2));

            this.summonOneShotAtPos(pos.add(-maxRad, 0, -maxRad));
            this.summonOneShotAtPos(pos.add(maxRad, 0, -maxRad));
            this.summonOneShotAtPos(pos.add(maxRad, 0, maxRad));
            this.summonOneShotAtPos(pos.add(-maxRad, 0, maxRad));

            this.summonOneShotAtPos(pos);

        }else if (pattern == 2){

            this.summonOneShotAtPos(pos.add(-maxRad/2,0,-maxRad/2));
            this.summonOneShotAtPos(pos.add(maxRad/2,0,-maxRad/2));
            this.summonOneShotAtPos(pos.add(maxRad/2,0,maxRad/2));
            this.summonOneShotAtPos(pos.add(-maxRad/2,0,maxRad/2));


            this.summonOneShotAtPos(pos.add(maxRad/2, 0, 0));
            this.summonOneShotAtPos(pos.add(0, 0, maxRad/2));
            this.summonOneShotAtPos(pos.add(-maxRad/2, 0, 0));
            this.summonOneShotAtPos(pos.add(0, 0, -maxRad/2));

        }else if (pattern == 3){


            double l = 2 * Math.PI * maxRad;
            double angleB = 4 / l * Math.PI;

            for (double a = 0; a < Math.PI * 2 - angleB;a+=angleB){
                Vec3 b = new Vec3(maxRad,0,0).yRot((float)a);
                this.summonOneShotAtPos(pos.add(b));
            }

            this.summonOneShotAtPos(pos);

        }
    }

    private void summonOneShotAtPos(Vec3 pos){
        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.kineticFieldRayDamage);
        ChesedOneShotVerticalRayEntity entity = ChesedOneShotVerticalRayEntity.summon(level(), pos, damage, 40, 30);
        entity.setDamageRadius(1.75f);
        entity.softerSound = true;
    }

    public boolean finalBOOMAttack(AttackInstance instance){

        if (instance.stage == 0) {
            lookingAtTarget = false;
            int chargeEndTime = 100;
            int chargeStartTime = 20;
            int afterChargeIdle = 30;
            int chargeTime = chargeEndTime - chargeStartTime;

            if (instance.tick == 0) {
                this.setMonolithsImmunity(true);
                this.setMonolithDrainPercent(0);
                this.getAnimationSystem().startAnimation(FINAL_ATTACK_LAYER, AnimationTicker.builder(CHESED_BOOM_ATTACK)
                        .setToNullTransitionTime(0)
                        .build());
            }else if (instance.tick == chargeStartTime - 5){
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.CHESED_FINAL_ATTACK_CHARGE.get(), SoundSource.HOSTILE, 5f, 1f);
            }else if (instance.tick == chargeStartTime){
                this.setMonolithDrainPercent(0);
                this.setDrainingMonoliths(true);
            }else if (instance.tick < chargeEndTime){
                int currentChargeTime = instance.tick - chargeStartTime;
                this.setMonolithDrainPercent(currentChargeTime / (float) chargeTime);
            }else if (instance.tick < chargeEndTime + afterChargeIdle){
                this.setDrainingMonoliths(false);
            }else{
                instance.nextStage();
            }
        }else if (instance.stage == 1) {

            lookingAtTarget = false;
            int rayStartTick = 15;
            int rayDuration = FINAL_ATTACK_RAY_ROTATION_DURATION;
            int buildupSoundStart = 54;
            int impactFramesStart = 58;
            int damageAndEffectsStart = 60;

            if (instance.tick == 0) {
                this.setMonolithsImmunity(true);
                this.killCrystals();
                this.darkenCombatants(80, false);

            } else if (instance.tick == rayStartTick) {

                this.boomAttackRotatingRay(rayDuration);
                ((ServerLevel) level()).playSound(null, this.position().x, this.position().y, this.position().z, BossSounds.CHESED_FINAL_ATTACK_RAY.get(), SoundSource.HOSTILE, 6f, 0.8f);
            }else if (instance.tick > rayStartTick && instance.tick <= rayStartTick + rayDuration) {

                float p = 1 - (instance.tick - rayStartTick) / (float) rayDuration;
                this.setMonolithDrainPercent(p);

            } else if (instance.tick == buildupSoundStart) {
                this.setMonolithDrainPercent(0);
                this.playInEarsSound(BossSounds.CHESED_FINAL_ATTACK_EXPLOSION_PREPARE.get(), 1f, 1);
            } else if (instance.tick == impactFramesStart) {
                this.setMonolithDrainPercent(0);
                ImpactFrame base = new ImpactFrame(0.5f, 0.1f, 4, false);
                FDLibCalls.sendImpactFrames((ServerLevel) level(), this.position(), 60,
                        base,
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true)
                );
            } else if (instance.tick == damageAndEffectsStart) {

                lookingAtTarget = false;
                this.setMonolithDrainPercent(0);

                this.boomAttackAfterBlackout();
                this.playInEarsSound(BossSounds.CHESED_FINAL_ATTACK_EXPLOSION_BIGGER.get(), 1f, 1);

                DefaultShakePacket.send((ServerLevel) level(), this.position(), 60, FDShakeData.builder()
                        .amplitude(0.25f)
                        .inTime(0)
                        .stayTime(30)
                        .outTime(100)
                        .build());
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(50)
                        .amplitude(4f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(50)
                        .build(), this.position(), 60);


                int aliveMonoliths = this.getAliveMonolithsCount();
                if (aliveMonoliths != 0){

                    float damagePercent = (BossConfigs.BOSS_CONFIG.get().chesedConfig.finalAttackDamagePercentPerMonolith / 100f)
                            * aliveMonoliths;

                    var affectedEntities = this.getAffectedEntities(false);

                    DamageSource source = BossDamageSources.chesedAttack(this);

                    for (LivingEntity livingEntity : affectedEntities){
                        livingEntity.invulnerableTime = 0;
                        livingEntity.hurt(source, livingEntity.getMaxHealth() * damagePercent);
                    }

                }
            } else if (instance.tick == damageAndEffectsStart + 1) {
                this.setMonolithDrainPercent(0);
                this.darkenCombatants(0, true);
                this.summonOrReviveMonoliths();
            }else if (instance.tick >= damageAndEffectsStart + 2){
                this.setMonolithDrainPercent(0);
                instance.nextStage();
            }
        }else if (instance.stage == 2){
            if (instance.tick > 100){

                lookingAtTarget = true;
                this.summonOrReviveMonoliths();
                this.setMonolithsImmunity(false);
                return true;
            }
            return false;
        }


        return false;
    }


    private void darkenCombatants(int duration,boolean clear){

        for (Player player : this.getCombatants(true)){

            if (clear){
                player.removeEffect(BossEffects.CHESED_DARKEN);
            }else{
                player.addEffect(new MobEffectInstance(BossEffects.CHESED_DARKEN,duration,0,true,false));
            }

        }

    }

    public static final int FINAL_ATTACK_RAY_ROTATION_DURATION = 15;


    private void clientBoomAttackRotatingRay(){

        var system = this.getAnimationSystem();

        var ticker = system.getTicker(ChesedEntity.FINAL_ATTACK_LAYER);

        if (ticker == null) return;

        float elapsedTime = ticker.getElapsedTime();

        float offset = 155;

        if (elapsedTime < offset || elapsedTime > 157) return;

        float radius = 35;

        float angle = FDMathUtil.FPI / 12;

        Vector3f v1 = new Vector3f(radius,0,0);
        Vector3f v2 = new Vector3f(radius,0,0).rotateY(angle);



        for (float i = 0; i < FDMathUtil.FPI * 2; i += angle){

            float dist = v1.distance(v2);

            for (float v = 0; v < dist; v += 2f){

                Vector3f b = v1.lerp(v2,Math.clamp(random.nextFloat(),0,1),new Vector3f());


                for (int k = 0; k < 3;k++) {

                    float rndHeight = random.nextFloat() * 0.8f - 0.4f;

                    float hp = 1 - Math.abs(rndHeight) / 0.4f;

                    Vector3f n = b.normalize(new Vector3f()).mul(0.025f * k / 2f * hp);

                    ParticleOptions options;

                    if (random.nextFloat() > 0.5) {
                        options = BallParticleOptions.builder()
                                .size(1f - random.nextFloat() * 0.1f - (1 - hp) * 0.2f)
                                .scalingOptions(5 + random.nextInt(3), 0, 40)
                                .color(150 + random.nextInt(40), 230, 255)
                                .build();
                    }else{
                        options = LightningParticleOptions.builder()
                                .color(50 + random.nextInt(40), 183 + random.nextInt(60), 200 + random.nextInt(50))
                                .quadSize(0.25f)
                                .lifetime(40)
                                .randomRoll(true)
                                .build();
                    }

                    level().addParticle(options,
                            true,
                            this.getX() + b.x + random.nextFloat() * 0.5 - 0.25f,
                            this.getY() + 1.4f + b.y + rndHeight,
                            this.getZ() + b.z + random.nextFloat() * 0.5 - 0.25f,
                            -n.x + random.nextFloat() * 0.01f - 0.005f,
                            -n.y + random.nextFloat() * 0.01f - 0.005f,
                            -n.z + random.nextFloat() * 0.01f - 0.005f
                    );
                }

            }


            v1 = v1.rotateY(angle);
            v2 = v2.rotateY(angle);
        }

    }

    private void boomAttackRotatingRay(int rotateDuration){

        Vec3 look = this.getLookAngle();
        Vec3 p = this.getCenter();
        Vec3 end = p.add(look.multiply(1,0,1).normalize().multiply(38,38,38));

        ChesedRayOptions options = ChesedRayOptions.builder()
                .processor(new ChesedRayCircleParticleProcessor(p,1.15f,true))
                .time(5,rotateDuration - 10,5)
                .lightningColor(90, 180, 255)
                .color(100, 255, 255)
                .end(p)
                .width(0.8f)
                .build();
        FDLibCalls.sendParticles((ServerLevel) level(),options,end,120);

    }

    private void boomAttackAfterBlackout(){

        BossUtil.chesedBoomParticles((ServerLevel) level(),this.position().add(0,1,0),38,120);


        int amount = 60;
        int amountPerAmount = 3;
        float angle = FDMathUtil.FPI * 2 / amount;
        for (int i = 0; i < amount;i++){
            for (int c = 0; c < amountPerAmount;c++) {

                float p = c / (float)(amountPerAmount - 1);

                Vec3 v = new Vec3(36, 0, 0).yRot(angle * i + (random.nextFloat() * 2 - 1) * angle / 2);
                Vec3 direction = v.reverse().normalize();
                Vec3 pos = this.position().add(v.x, 1 + random.nextFloat() * 2, v.z);

                BlockState state = level().random.nextFloat() > 0.5 ? Blocks.SCULK.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState();

                float horizontalSpeed = 0.2f + random.nextFloat() * 0.05f + p * 0.4f;
                float verticalSpeed = random.nextFloat() * 0.4f + 0.05f + 0.3f * p;
                float randomRot = (random.nextFloat() * 2 - 1) * FDMathUtil.FPI / 4;

                Vec3 speed = direction.multiply(horizontalSpeed, horizontalSpeed, horizontalSpeed)
                        .yRot(randomRot)
                        .add(0, verticalSpeed, 0);

                ChesedFallingBlock block = ChesedFallingBlock.summon(level(), state, pos,0);
                block.setDeltaMovement(speed);


                float rnd = random.nextFloat() * 0.05f;
                FDLibCalls.addParticleEmitter(level(), 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                                .color(0.35f - rnd, 0.35f - rnd, 0.35f - rnd)
                                .lifetime(0, 0, 25)
                                .size(1.5f)
                                .build())
                        .lifetime(200)
                        .processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                        .position(this.position())
                        .build());
            }
        }


    }


    public boolean doNothing(AttackInstance instance){
        if (instance.tick > 100){
            return true;
        }
        return false;
    }

    private void killCrystals(){
        AABB box = new AABB(-40, -5, -40, 40, 5, 40).move(this.position());
        var list = level().getEntitiesOfClass(ChesedCrystalEntity.class, box);
        for (ChesedCrystalEntity chesedCrystal : list) {
            chesedCrystal.kill();
        }
        for (Player player : this.getCombatants(true)){
            player.removeEffect(BossEffects.CHESED_ENERGIZED);
        }
    }

    public boolean summonCrystals(AttackInstance instance){

        if (this.getTarget() == null){
            if (instance.tick % 10 == 0){
                this.changeTarget();
            }
            return false;
        }

        this.killCrystals();

        for (int i = 0; i < 4; i++) {

            ChesedCrystalEntity chesedCrystal = new ChesedCrystalEntity(BossEntities.CHESED_CRYSTAL.get(), level());
            Vector3f pos = new Vector3f(
                    (float) this.position().x,
                    (float) this.position().y,
                    (float) this.position().z
            );



            Vector3f between = new Vector3f(36, 0, 0).rotateY(FDMathUtil.FPI / 4 + FDMathUtil.FPI / 2 * i /*+ (random.nextFloat() * 2 - 1) * FDMathUtil.FPI / 5f*/);
            Vector3f summonPos = pos.add(between, new Vector3f());
            Vector3f normalized = between.normalize(new Vector3f());

            Matrix4f mt = new Matrix4f();
            FDRenderUtil.applyMovementMatrixRotations(mt, new Vec3(-normalized.x, 0, -normalized.z));
            mt.rotateX(-FDMathUtil.FPI / 3);

            Vector3f direction = mt.transformDirection(new Vector3f(0, 1, 0));
            chesedCrystal.setCrystalFacingDirection(new Vec3(direction.x, direction.y, direction.z));
            chesedCrystal.setPos(summonPos.x, summonPos.y, summonPos.z);
            level().addFreshEntity(chesedCrystal);

        }


        return true;
    }

    public boolean rayAttack(AttackInstance instance){

        this.doBlinding = false;

        int tick = instance.tick;
        int stage = instance.stage;

        if (stage == 0 && tick == 0){
            this.changeTarget();
        }

        int localStage = stage % 3;

        if (localStage == 1) {



            float animationSpeed = 1;
            int rayAttackTick = 35;
            int rayAttackReloadTime = 30;

            SoundEvent soundEvent = BossSounds.CHESED_RAY_CHARGE.get();

            if (this.isBelowHalfHP()){
                soundEvent = BossSounds.CHESED_RAY_CHARGE_FAST.get();
                rayAttackTick = 25;
                animationSpeed = 1.5f;
                rayAttackReloadTime = 20;
            }

            if (tick == 0) {

                level().playSound(null,this.getX(),this.getY(),this.getZ(), soundEvent, SoundSource.HOSTILE, 5f ,1f);

                this.getAnimationSystem().startAnimation(RAY_ATTACK_LAYER, AnimationTicker.builder(CHESED_ATTACK)
                                .setToNullTransitionTime(0)
                                .setSpeed(animationSpeed)
                        .build());

            }else if (tick > 10 && tick < rayAttackTick) {

                int d = this.isBelowHalfHP() ? 12 : 10;

                if (tick == rayAttackTick - d){
                    this.playInEarsSound(BossSounds.ATTACK_DING.get(),1f,1f);
                }

                if (tick < rayAttackTick - 5){
                    lookingAtTarget = true;
                }else{
                    lookingAtTarget = false;
                }

                var c = this.getModelPartPosition(this,"core",serverModel);
                Vec3 center = new Vec3(c.x,c.y,c.z).add(this.position());
                for (int i = 0; i < 10;i++) {
                    Vec3 pos = center.add(new Vec3(
                                    random.nextFloat() * 2 - 1,
                                    random.nextFloat() * 2 - 1,
                                    random.nextFloat() * 2 - 1
                            ).normalize().multiply(1.2,1.2,1.2));
                    Vec3 speed = center.subtract(pos).normalize().multiply(0.05,0.05,0.05);
                    BallParticleOptions options = BallParticleOptions.builder()
                            .friction(1.8f)
                            .size(0.3f)
                            .scalingOptions(0, 1, 4)
                            .color(100 + random.nextInt(50), 255, 255)
                            .particleProcessor(new SetParticleSpeedProcessor(speed))
                            .build();
                    FDLibCalls.sendParticles((ServerLevel) level(),options,pos,60);
                }
            } else if (tick == rayAttackTick) {
                lookingAtTarget = false;
                Vec3 look = this.getLookAngle();
                Vec3 p = this.getCenter().add(look.reverse());
                Vec3 end = p.add(look.multiply(60,60,60));


                ClipContext clipContext = new ClipContext(p,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                var result = level().clip(clipContext);
                end = result.getLocation();

                ChesedRayOptions options = ChesedRayOptions.builder()
                        .time(0,15,5)
                        .lightningColor(90, 180, 255)
                        .color(100, 255, 255)
                        .end(end)
                        .width(0.8f)
                        .build();
                FDLibCalls.sendParticles((ServerLevel) level(),options,p,60);

                Vec3 reversedLook = look.reverse();


                FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                        .size(25f)
                        .scalingOptions(1,0,2)
                        .color(150,230,255)
                        .build(),p,200);


                BossUtil.chesedRaySmoke((ServerLevel) level(),end,reversedLook,120);
                ((ServerLevel)level()).playSound(null,end.x,end.y,end.z, BossSounds.CHESED_LIGHTNING_RAY.get(), SoundSource.HOSTILE,5f,0.8f);


                PositionedScreenShakePacket.send((ServerLevel) level(),FDShakeData.builder()
                        .frequency(5)
                        .amplitude(5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),end,120);


                FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                        .size(50f)
                        .scalingOptions(2,0,3)
                        .color(100,230,255)
                        .build(),end,120);



                this.summonStonesAfterRayAttack(30,reversedLook,end);
                BossUtil.chesedRayExplosion((ServerLevel) level(),end,reversedLook,100,15,0.75f);
                this.damageOnRay(p,end);
            }else if (tick > rayAttackTick){
                if (tick > rayAttackTick + rayAttackReloadTime){
                    instance.nextStage();
                    return false;
                }
            }
        }else if (localStage == 0) {
            lookingAtTarget = true;
            if (this.getTarget() != null) {
                if (isLookingStraightAtEntity(this.getTarget(), 0.01)) {
                    instance.nextStage();
                    lookingAtTarget = false;
                }
            }
        }else if (localStage == 2){
            if (instance.stage / 3 > 6) { //at least four guaranteed attacks
                float rnd = random.nextFloat();
                float p = stage / (6f * 3);
                float ch = FDMathUtil.normalDistribution(p, 0, 0.43f);
                if (rnd > ch) {
                    lookingAtTarget = true;
                    this.killCrystals();
                    this.doBlinding = true;
                    return true;
                }
            }
            instance.nextStage();
        }

        return false;
    }

    private void summonStonesAfterRayAttack(int count,Vec3 direction,Vec3 pos){

        Vector3f v = new Vector3f(0,1,0).cross((float)direction.x,(float)direction.y,(float)direction.z);

        for (int i = 0; i < count;i++){

            BlockState state = random.nextFloat() > 0.5 ? Blocks.BLACKSTONE.defaultBlockState() : Blocks.SCULK.defaultBlockState();

            Vector3f add = v.rotateAxis(FDMathUtil.FPI * 2 * random.nextFloat(),(float)direction.x,(float)direction.y,(float)direction.z,new Vector3f());
            float rd = random.nextFloat() * 0.5f;
            ChesedFallingBlock block = ChesedFallingBlock.summon(level(),state,pos,direction.add(
                    add.x * rd * 2,
                    add.y * rd,
                    add.z * rd * 2
            ).normalize().multiply(0.5,2.4 - rd,0.5), 0);

            float rnd = random.nextFloat() * 0.05f;

            FDLibCalls.addParticleEmitter(level(), 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                            .color(0.35f - rnd, 0.35f - rnd, 0.35f - rnd)
                            .lifetime(0, 0, 25)
                            .size(1.5f)
                            .build())
                    .lifetime(200)
                    .processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                    .position(this.position())
                    .build());

        }

    }


    private void damageOnRay(Vec3 begin, Vec3 end){

        var list = FDHelpers.traceEntities(level(),begin,end,1,(entity)->{
            return !(entity instanceof ChesedBossBuddy);
        });






        float damage = BossUtil.transformDamage(level(), BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.rayDamage));

        boolean chesedRayReflectorHit = false;
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity entity : list){
            if (entity instanceof LivingEntity living){
                targets.add(living);
            }else if (entity instanceof ChesedRayReflector reflector){
                chesedRayReflectorHit = true;
            }
        }

        boolean targetHadEnergized = false;

        for (LivingEntity target : targets){
            if (target.hasEffect(BossEffects.CHESED_ENERGIZED)){

                if (target instanceof ServerPlayer serverPlayer){
                    PacketDistributor.sendToPlayer(serverPlayer, new ChesedRayReflectPacket());
                }

                MobEffectInstance instance = target.getEffect(BossEffects.CHESED_ENERGIZED);
                int amplifier = instance.getAmplifier();
                target.removeEffect(BossEffects.CHESED_ENERGIZED);
                if (amplifier > 0 && !chesedRayReflectorHit){
                    target.addEffect(new MobEffectInstance(BossEffects.CHESED_ENERGIZED,400,amplifier - 1,false,true));
                }
                targetHadEnergized = true;
            }else{

                DamageSource source = BossDamageSources.chesedAttack(this);

                if (level().random.nextFloat() < 0.05){
                    if (level().random.nextFloat() > 0.5){
                        source = BossDamageSources.chesedBaAttack(this);
                    }else {
                        source = BossDamageSources.chesedLorAttack(this);
                    }
                }

                target.hurt(source,damage);
            }
        }

        if (chesedRayReflectorHit && targetHadEnergized){
            this.decreaseHitCount(1);
        }

    }

    public boolean electricSphereAttack(AttackInstance instance){
        lookingAtTarget = true;
//        if (true) return true;
        var tick = instance.tick;
        var stage = instance.stage;
        if (stage == 0) {

            if (tick == 0) {
                this.getAnimationSystem().startAnimation("CAST", AnimationTicker.builder(CHESED_CAST)
                        .setToNullTransitionTime(0)
                        .build());

            }else if (tick > 15) {
                level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.ELECTRIC_SPHERES_CAST.get(), SoundSource.HOSTILE, 5f, 1f);
                this.repeatedSound = new RepeatedSound(BossSounds.ELECTRIC_SPHERES_STORM.get(), SoundSource.HOSTILE, this.position(),5f, 1f, 60, 11);
                this.entityData.set(IS_LAUNCHING_ORBS, true);
                instance.nextStage();
            }
        }else if (stage == 1) {

            if (tick % 10 == 0) {
                this.summonLessSpheresAround(this.position(),tick);
            }


            if (tick > 200){
                instance.nextStage();
            }

        }else if (stage == 2) {

            if (tick % 15 == 0){

                int amountOnArc = 5;

                float rndOffs = random.nextFloat();

                for (int i = -amountOnArc;i <= amountOnArc;i++){

                    Vec3 v = new Vec3(1,0,0)
                            .yRot(i / (float) amountOnArc * FDMathUtil.FPI / 8 + rndOffs * FDMathUtil.FPI);

                    this.shootSphere(this.position(),v,80);
                    this.shootSphere(this.position(),v.reverse(),80);

                }


            }


            if (tick > 200){
                instance.nextStage();
            }
        }else{

            if (tick < 200) {
                if (tick % 10 == 0) {
                    this.summonSpheresAround(25, tick / 10f * FDMathUtil.FPI / 9);
                }


            }else if (tick < 350){
                this.entityData.set(IS_LAUNCHING_ORBS,false);
            }else{
                return true;
            }
            return false;
        }

        return false;
    }

    private void summonLessSpheresAround(Vec3 pos,int tick){
        float rndAngle = random.nextFloat() * FDMathUtil.FPI / 8 - FDMathUtil.FPI / 16;
        for (int i = 0; i < 4; i++) {
            Vec3 v = new Vec3(1, 0, 0).yRot(i * FDMathUtil.FPI / 2 + tick / 2f * FDMathUtil.FPI / 8 + rndAngle);
            this.shootSphere(pos,v);
        }
    }


    private void summonSpheresAround(int count,float angleOffset){

        float angle = FDMathUtil.FPI * 2 / count;

        for (int i = 0; i < count;i++){

            Vec3 dir = new Vec3(1,0,0).yRot(i * angle + angleOffset);

            this.shootSphere(this.position(),dir,150);
        }
    }

    private void shootSphere(Vec3 pos,Vec3 direction){
        this.shootSphere(pos,direction,100);
    }

    private void shootSphere(Vec3 pos,Vec3 direction,int time){
        direction = direction.normalize();
        Vec3 sppos = pos.add(0,1,0).add(direction);
        Vec3 endPos = sppos.add(direction.multiply(37,37,37));
        ProjectileMovementPath path = new ProjectileMovementPath(time,false)
                .addPos(sppos)
                .addPos(endPos);

        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.electricSphereDamage);

        ChesedElectricSphereEntity sphereEntity = ChesedElectricSphereEntity.summon(level(),damage,path);
    }


    public boolean rockfallAttack(AttackInstance instance){
        lookingAtTarget = true;
        int stage = instance.stage;
        int tick = instance.tick;
        int height = 32;
        int rad = 36;
        if (stage == 0){
            this.getAnimationSystem().startAnimation(ROCKFALL_TICKER, AnimationTicker.builder(CHESED_ROCKFALL_CAST)
                            .setToNullTransitionTime(0)
                    .build());

            if (tick > 10 && tick < 30) {
                int count = 3;

                float angle = FDMathUtil.FPI * 2 / count;

                Vec3 center = this.position();

                for (int i = 0; i < count; i++) {
                    float a = angle * i;
                    Vec3 v = new Vec3(2,0,0).yRot(a);
                    Vec3 ppos = this.position().add(v);
                    FDLibCalls.sendParticles(((ServerLevel) level()),BallParticleOptions.builder()
                            .size(0.5f)
                            .color(100 + random.nextInt(50), 255, 255)
                            .particleProcessor(new CompositeParticleProcessor(
                                    new CircleParticleProcessor(center,true,true,2),
                                    new SetParticleSpeedProcessor(new Vec3(0,0.2,0))
                            ))
                            .scalingOptions(10,10,0)
                            .build(),ppos,height * 1.25f);


                }
            }else if (tick == 45){
                Vec3 pos = this.position();

                FDLibCalls.sendParticles((ServerLevel) level(),ChesedRayOptions.builder()
                                .width(1f)
                                .end(pos.add(0,height,0))
                                .lightningColor(90, 180, 255)
                                .color(100, 255, 255)
                                .stay(8)
                                .in(2)
                                .out(10)
                        .build(), pos.add(0,1,0),60);
                PositionedScreenShakePacket.send((ServerLevel) level(),FDShakeData.builder()
                        .frequency(10)
                        .amplitude(10f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(10)
                        .build(),this.position().add(0,height,0),height * 1.25f);
                DefaultShakePacket.send((ServerLevel) level(),this.position(),60,FDShakeData.builder()
                                .amplitude(0.15f)
                                .inTime(0)
                                .stayTime(50)
                                .outTime(150)
                        .build());





                BossUtil.chesedRayExplosion((ServerLevel) level(),this.position().add(0,height,0),new Vec3(0,-1,0),120,10,1);
                ((ServerLevel)level()).playSound(null,this.getX(),this.getY() + height,this.getZ(), BossSounds.CHESED_RAY.get(), SoundSource.HOSTILE,100f,1f);


                this.playInEarsSound(BossSounds.ROCKFALL.get(),1,1);
                this.playInEarsSound(BossSounds.RUMBLING.get(),1,1);

                int count = 10 + random.nextInt(10);
                float angle = FDMathUtil.FPI * 2 / count;


                this.rockfallCastStones(count, height, angle);

                FDLibCalls.addParticleEmitter(level(), height * 1.25f, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                                .color(0.25f, 0.25f, 0.25f)
                                .lifetime(0, 0, 100)
                                .size(1f)
                                .build())
                        .lifetime(400)
                        .particlesPerTick(20)
                        .processor(new CircleSpawnProcessor(new Vec3(0,-1,0),0.05f,0.1f,36))
                        .position(this.position().add(0,height + 2,0))
                        .build());


                FDLibCalls.sendParticles((ServerLevel) level(),BallParticleOptions.builder()
                        .size(75f)
                        .scalingOptions(1,0,2)
                        .color(150,230,255)
                        .build(),this.position().add(0,height,0),200);


            }else if (tick >= 46){

                instance.nextStage();
            }
        }else if (stage == 1){

            float duration = 400;


            if (tick % (this.isBelowHalfHP() ? 25 : 40) == 0){
                this.summonDelayedVerticalRayOnFieldNearPlayers(5,this.isBelowHalfHP() ? 3 : 2);
            }

            this.summonStonesAround(4,rad, this.position().add(0,height,0),true,false,FDEasings::easeOut);

            int stonesAroundPlayersFrequency = this.isBelowHalfHP() ? 6 : 5;

            if (tick % stonesAroundPlayersFrequency == 0) {
                AABB box = new AABB(-rad, -2, -rad, rad, height, rad).move(this.position());
                for (Player player : this.level().getNearbyPlayers(BossUtil.ALL, this, box)) {

                    Vec3 p = player.position();

                    if (p.subtract(this.position()).multiply(1, 0, 1).length() > rad) continue;

                    this.summonStonesAround(1, 5, p.multiply(1, 0, 1).add(0, this.position().y + height, 0), true, false, FDEasings::easeOut);

                }
            }

            if (tick >= duration) {
                instance.nextStage();
            }
        }else if (stage == 2){
            return instance.tick > 80;
        }


        return false;
    }

    public void rockfallCastStones(int count, float height, float angle){
        for (int i = 0; i < count;i++) {


            BlockState state = random.nextFloat() > 0.5f ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.SCULK.defaultBlockState();

            ChesedFallingBlock block = ChesedFallingBlock.summon(level(), state, this.position().add(0, height, 0),
                    BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.rockfallRockDamage));

            Vec3 v = new Vec3(random.nextFloat() * 0.025 + 0.2,0,0).yRot(angle * i + (random.nextFloat() * 2 - 1) * angle);

            block.setDeltaMovement(v.add(0,-random.nextFloat() * 0.5 - 0.25,0));

            float rnd = random.nextFloat() * 0.05f;

            FDLibCalls.addParticleEmitter(level(), height * 2, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                            .color(0.35f - rnd, 0.35f - rnd, 0.35f - rnd)
                            .lifetime(0, 0, 50)
                            .size(2f)
                            .build())
                    .lifetime(200)
                    .processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                    .position(this.position())
                    .build());


        }
    }

    private void summonStonesAround(int count, int rad, Vec3 center, boolean useEasing, boolean reverseEasing, Function<Float,Float> func){

        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.rockfallRockDamage);

        for (int i = 0; i < count;i++){

            float rnd = random.nextFloat();

            if (useEasing){
                if (!reverseEasing) {
                    rnd = func.apply(rnd);
                }else{
                    rnd = 1 - func.apply(rnd);
                }
            }

            Vec3 v = new Vec3(rad * rnd,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);

            Vec3 p = center.add(v);

            BlockState state;
            if (level().random.nextFloat() > 0.5){
                state = Blocks.SCULK.defaultBlockState();
            }else{
                state = Blocks.BLACKSTONE.defaultBlockState();
            }

            ChesedFallingBlock fallingBlock = ChesedFallingBlock.summon(level(), state,p,damage);
            fallingBlock.softerSound = true;
        }
    }


    public boolean earthquakeAttack(AttackInstance instance){
        lookingAtTarget = false;
        int t = instance.tick;
        int radius = 38;
        if (instance.stage <= 4) {

            if (t < 6) {

                if (t > 3) {
                    this.summonCirclingParticlesServerside(4, 0.55f, 2, 0.5f, 6, 9);
                }

                if (t == 0) {
                    this.getAnimationSystem().startAnimation(EARTHQUAKE_ATTACK_LAYER, AnimationTicker.builder(CHESED_EARTHQUAKE_CAST)
                            .setToNullTransitionTime(0)
                            .build());
                }
//                if (t % 2 == 0) {
//                    SonicParticleOptions options = SonicParticleOptions.builder().facing(0, 1, 0).color(0.3f, 1f, 1f).startSize(radius).endSize(0).resizeSpeed(-0.3f).resizeAcceleration(-1f).build();
//                    for (Player player : this.level().getNearbyPlayers(BossUtil.ALL, this, this.getBoundingBox().inflate(100))) {
//                        ((ServerLevel) level()).sendParticles((ServerPlayer) player, options, true, this.position().x, this.position().y + 0.01, this.position().z, 1, 0, 0, 0, 0);
//                    }
//                }
            } else {
                if (t < 10) {
                    this.summonCirclingParticlesServerside(4, 0.55f, 2, 0.5f, 6, 9);
                }

                if (t == 17){
                    this.level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.CHESED_FLOOR_SMASH.get(), SoundSource.HOSTILE, 10f, 1f);
                } else if (t == 18) {
                    this.summonRadialEarthquake(radius, true);

                } else if (t == 21) {
                    PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                            .frequency(10f)
                            .stayTime(0)
                            .inTime(5)
                            .outTime(5)
                            .amplitude(3.f)
                            .build(), this.position(), 50);

                    FDLibCalls.sendParticles((ServerLevel) level(), BallParticleOptions.builder()
                            .size(25f)
                            .scalingOptions(0, 0, 2)
                            .color(150 - random.nextInt(50), 230, 255)
                            .build(), this.position(), 60);

                    int count = this.isBelowHalfHP() ? 12 : 8;


                    this.summonVerticalRayAttacksRadial(random.nextFloat() * FDMathUtil.FPI * 2,
                            FDMathUtil.FPI * (random.nextFloat() > 0.5 ? 1 : -1),
                            radius + 2,
                            BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.earthquakeRayDamage),
                            90 - (this.isBelowHalfHP() ? 10 : 0),
                            count);

                } else if (t > 50) {
                    instance.nextStage();
                }
            }
        }else{
            if (instance.tick > 40) {
                return true;
            }
        }
        return false;
    }

    private void trapPlayers(boolean trap){
        if (trap){

            var players = this.getCombatants(false);
            for (Player player : players){

                ChesedKineticFieldEntity fieldEntity = new ChesedKineticFieldEntity(BossEntities.CHESED_KINETIC_FIELD.get(),level());
                fieldEntity.setPos(
                        player.getX(),
                        this.getY() - 0.01,
                        player.getZ()
                );
                level().addFreshEntity(fieldEntity);

            }

        }else{

            float r = 80;
            var list = level().getEntitiesOfClass(ChesedKineticFieldEntity.class,new AABB(-r,-r,-r,r,r,r).move(this.position()));
            for (ChesedKineticFieldEntity entity : list){
                entity.kill();
            }

        }
    }

    private void summonRadialEarthquake(int radius,boolean doSonicParticles){
        SlamParticlesPacket packet = new SlamParticlesPacket(
                new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,0.5,0),new Vec3(1,0,0))
                        .maxAngle(FDMathUtil.FPI * 2)
                        .maxSpeed(0.15f)
                        .collectRadius(2)
                        .maxParticleLifetime(30)
                        .count(200)
                        .maxVerticalSpeedEdges(0.2f)
                        .maxVerticalSpeedCenter(0.2f)
        );
        PacketDistributor.sendToPlayersTrackingEntity(this,packet);

        RadialEarthquakeEntity radialEarthquakeEntity = RadialEarthquakeEntity.summon(level(),this.getOnPos(),1,radius,1f,
                BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.eartquakeDamage)
        );
        if (doSonicParticles) {
            for (int i = 0; i < 6; i++) {
                SonicParticleOptions options = SonicParticleOptions.builder()
                        .facing(0, 1, 0)
                        .color(0.3f, 1f, 1f)

                        .startSize(2)
                        .endSize(radius)
                        .resizeSpeed(-i * 2)
                        .resizeAcceleration(0.75f + i)
                        .lifetime(60)
                        .build();
                for (Player player : this.level().getNearbyPlayers(BossUtil.ALL, this, this.getBoundingBox().inflate(100))) {
                    ((ServerLevel) level()).sendParticles((ServerPlayer) player, options, true, this.position().x, this.position().y + 0.01, this.position().z, 1, 0, 0, 0, 0);
                }
            }
        }
    }



    private void summonVerticalRayAttacksRadial(float offsetAngle,float finalRotationAngle,float radius,float damage,int time,int count){

        float angleBetween = FDMathUtil.FPI * 2 / count;

        for (int i = 0; i < count;i++){

            ProjectileMovementPath path = new ProjectileMovementPath(time,false);

            for (float x = 1; x <= radius;x++){
                float p = x / radius;
                float angle = angleBetween * i + offsetAngle + p * finalRotationAngle;
                Vec3 offs = new Vec3(x,0,0).yRot(angle);
                path.addPos(
                        this.position().add(offs)
                );
            }

            ChesedMovingVerticalRay.summon(level(),this.position(),path,damage,40);
        }

    }

    private List<ChesedBlockProjectile> blockAttackProjectiles = new ArrayList<>();

    public boolean blockAttack(AttackInstance instance){
        lookingAtTarget = true;
        float height = 8;
        int blocksUpTime = 30;
        int blocksCycleTime = 30;
        int timeTillAttack = 40;

        int stage = instance.stage;
        int tick = instance.tick;

        int count = 10;

        if (stage == 0){
            if (blockAttackProjectiles.isEmpty()){
                if (!this.trySearchProjectiles()) {
                    this.getAnimationSystem().startAnimation("blockAttack", AnimationTicker.builder(CHESED_CAST)
                            .setToNullTransitionTime(0)
                            .setSpeed(1.2f)
                            .build());

                    this.initiateBlockProjectiles(blocksUpTime,blocksCycleTime,height,count);
                    this.changeTarget();
                    instance.nextStage();
                }
            }else{
                this.changeTarget();
                instance.nextStage();
            }
        }else if (stage == 1){
            if (tick == 13){
                if (blockAttackProjectiles.size() == count) {
                    BossUtil.posEvent((ServerLevel) level(), this.position().add(0, 0.05, 0), BossUtil.CHESED_GET_BLOCKS_FROM_EARTH_EVENT, 0, 60);
                    level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.CHESED_FLOOR_SMASH.get(), SoundSource.HOSTILE, 5f, 1f);
                }
            }else if (tick >= timeTillAttack){
                instance.nextStage();
            }
        }else if (stage == 2){
            if (!this.blockAttackProjectiles.isEmpty()) {
                ;
                if (instance.tick % 8 == 0) {
                    LivingEntity player = this.getTarget();
                    if (player != null) {
                        this.throwBlock(player, height);
                    }
                }
            }else{
                instance.nextStage();
            }
        }else{
            if (instance.tick > 20) {
                return true;
            }
        }
        return false;

    }


    private void throwBlock(LivingEntity player,float height){
        ChesedBlockProjectile next = this.blockAttackProjectiles.removeLast();
        next.noPhysics = false;
        next.movementPath = null;
        Vec3 tpos = this.targetGroundPosition(player);
        Vec3 b = tpos.subtract(next.position());
        Vec3 h = b.multiply(1,0,1);
        Vec3 targetPos = tpos.add(h.normalize().reverse().multiply(2.5,0,2.5));

        next.setRotationSpeed(10f);

        Vec3 flyTo = this.position().add(0,height + 1.5,0);
        ProjectileMovementPath path = new ProjectileMovementPath(8,false);
        path.addPos(next.position());
        path.addPos(flyTo);
        path.addPos(flyTo.add(0,1,0));
        path.setSpeedOnEnd(targetPos.subtract(flyTo).multiply(0.25,0.25,0.25));
        next.movementPath = path;

        this.delayedSounds.add(new DelayedSound(BossSounds.THROW_STUFF.get(), SoundSource.HOSTILE, flyTo,10f,2f,7));
    }

    private Vec3 targetGroundPosition(LivingEntity target){


        Vec3 last = this.previousTargetPos != null ? this.previousTargetPos : target.position();
        Vec3 current = target.position();

        Vec3 b = current.subtract(last);

        Vec3 toReturn = target.position().add(b.multiply(3,0,3));




        BlockPos pos = new BlockPos(
                (int)Math.floor(toReturn.x),
                (int)Math.floor(toReturn.y),
                (int)Math.floor(toReturn.z)
        );
        for (int i = 0; i < 5;i++){
            if (level().getBlockState(pos.offset(0,-i,0)).isAir()){
                toReturn = toReturn.subtract(0,1,0);
            }else{
                return toReturn;
            }

        }
        return toReturn;
    }


    private void initiateBlockProjectiles(int blocksUpTime,int blocksCycleTime,float height,int count){
        for (int i = 0; i < count; i++) {
            float angle = this.getInitProjectileRotation(i, count);
            ChesedBlockProjectile projectile = new ChesedBlockProjectile(BossEntities.BLOCK_PROJECTILE.get(), level());

            projectile.setBlockState(random.nextFloat() > 0.5 ? Blocks.SCULK.defaultBlockState() : Blocks.DEEPSLATE.defaultBlockState());
            projectile.setDropParticlesTime(blocksUpTime);
            var path = this.createRotationPath(angle, -2,height, 30, blocksUpTime, false);
            var next = this.createRotationPath(angle, height,height, 30, blocksCycleTime, true);

            path.setNext(next);
            projectile.setDamage(BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.blockAttackDamage));
            projectile.noPhysics = true;
            projectile.setPos(path.getPositions().getFirst());
            projectile.movementPath = path;
            blockAttackProjectiles.add(projectile);
            level().addFreshEntity(projectile);
        }
    }




    private boolean trySearchProjectiles(){
        List<ChesedBlockProjectile> projectiles = level().getEntitiesOfClass(ChesedBlockProjectile.class,this.getBoundingBox().inflate(10,20,10),projectile->{
            return true;
        });
        if (!projectiles.isEmpty()){
            this.blockAttackProjectiles = projectiles;
            return true;
        }
        return false;
    }

    private ProjectileMovementPath createRotationPath(float angle,float yStart, float yEnd, int pathDetalization, int time, boolean cycle){
        ProjectileMovementPath path = new ProjectileMovementPath(time,cycle);
        float a1 = FDMathUtil.FPI * 2 / pathDetalization;
        for (int i = 0; i <= pathDetalization;i++){
            float p = i / (float) pathDetalization;
            float a = angle + i * a1;
            Vec3 v = new Vec3(5,0,0).yRot(a);
            Vec3 pos = this.position().add(v.x,FDMathUtil.lerp(yStart,yEnd, FDEasings.easeInOutBack(p)),v.z);
            path.addPos(pos);
        }
        return path;
    }

    private float getInitProjectileRotation(int id,int count){
        return 2 * FDMathUtil.FPI * (id / (float) count);
    }



    public boolean roll(AttackInstance instance){
        lookingAtTarget = false;
        doBlinding = false;
        int tick = instance.tick;
        var stage = instance.stage;
        if (tick == 0 && stage == 0){
            this.oldRollPos = this.position();
        }
        Vector3f p = this.getModelPartPosition(this,"base", serverModel);
        Vec3 pos = this.position().add(p.x,p.y,p.z);
        var system = this.getAnimationSystem();

        if (stage == 0){
            this.playIdle = false;
            if (system.getTickerAnimation("ROLL_UP") != CHESED_ROLL_UP.get()) {
                system.startAnimation("ROLL_UP", AnimationTicker.builder(CHESED_ROLL_UP)
                        .startTime(tick)
                        .build());
            }
            if (tick >= CHESED_ROLL_UP.get().getAnimTime()){
                instance.nextStage();
                return false;
            }
        }else if (stage == 1){
            this.playIdle = false;

            PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                    .frequency(5f)
                    .stayTime(0)
                    .inTime(2)
                    .outTime(5)
                    .amplitude(3.5f)
                    .build(),pos,10);

            if (tickCount % 2 == 0) {
                this.level().playSound(null, pos.x, pos.y, pos.z, BossSounds.RUMBLING_SHORT.get(), SoundSource.HOSTILE, 1f, 1f);
            }
            if (system.getTickerAnimation("ROLL_AROUND") != CHESED_ROLL.get()){
                system.startAnimation("ROLL_AROUND",AnimationTicker.builder(CHESED_ROLL)
                        .startTime(tick)
                        .setToNullTransitionTime(0)
                        .build());
            }
            if (system.getTickerAnimation("ROLLING") != CHESED_ROLL_ROLL.get()){
                system.startAnimation("ROLLING",AnimationTicker.builder(CHESED_ROLL_ROLL)
                        .setToNullTransitionTime(0)
                        .startTime(tick)
                        .build());
            }
            if (tick >= CHESED_ROLL.get().getAnimTime() - 20){
                instance.nextStage();
                return false;
            }else{
                if (this.isBelowHalfHP()){
                    if (oldRollPos == null){
                        oldRollPos = pos;
                    }
                    Vec3 b = oldRollPos.subtract(pos);
                    int amount = (int) Math.ceil(b.length());
                    for (int i = 0; i < amount;i++) {
                        ChesedFireTrailEntity fireTrailEntity =
                                ChesedFireTrailEntity.summon(level(), pos.add(0, -0.5, 0).add(b.normalize().multiply(i,i,i)),
                                        BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.fireTrailDamage), 80);
                    }
                }
                this.handleRoll(tick, pos);
            }
        }else if (stage == 2){
            doBlinding = true;
            this.setRolling(false);
            system.stopAnimation("ROLL_UP");
            system.startAnimation("ROLL_UP_END",AnimationTicker.builder(CHESED_ROLL_UP_JUST_END)
                    .setToNullTransitionTime(0)
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .build());
            if (tick >= CHESED_ROLL_UP_JUST_END.get().getAnimTime() + 20){
                instance.nextStage();
                this.playIdle = true;
                return false;
            }
        }else{
            this.playIdle = true;
            this.setRolling(false);
            system.stopAnimation("ROLL_UP");
            system.stopAnimation("ROLL_UP_END");
            system.stopAnimation("ROLL_AROUND");
            system.stopAnimation("ROLLING");
            doBlinding = true;
            return true;
        }
        return false;
    }

    private void handleRoll(int attackTime, Vec3 pos){
        if (attackTime == 0){
            this.oldRollPos = this.position();
        }

        if (oldRollPos == null){
            oldRollPos = pos;
        }
        this.summonRollEarthShatters(oldRollPos.add(0,-1,0),pos.add(0,-1,0));
        this.rollDamageEntities(oldRollPos,pos);
        oldRollPos = pos;


        int rollTime = BossAnims.CHESED_ROLL.get().getAnimTime() + BossAnims.CHESED_ROLL_UP.get().getAnimTime();

        if (attackTime >= rollTime){
            this.setRolling(false);
        }else{
            this.setRolling(true);
        }
    }

    private void rollDamageEntities(Vec3 oldPos,Vec3 pos){
        var entities = FDHelpers.traceEntities(level(),oldPos,pos,2.1,entity->{
            return entity instanceof LivingEntity living && !(entity instanceof ChesedBossBuddy);
        });

        float damage = BossUtil.transformDamage(level(), BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.rollAttackDamage));

        for (Entity entity : entities){
            LivingEntity livingEntity = (LivingEntity) entity;
            Vec3 pushDirection = FDMathUtil.getNormalVectorFromLineToPoint(oldPos,pos,entity.position()).multiply(1,0,1)
                    .normalize()
                    .multiply(3,0,3)
                    .add(0,0.5,0);
            livingEntity.hurt(BossDamageSources.CHESED_ROLL_SOURCE,damage);
            if (livingEntity instanceof Player player){
                FDLibCalls.setServerPlayerSpeed((ServerPlayer) player,pushDirection);
            }else{
                livingEntity.setDeltaMovement(pushDirection);
            }
        }

    }


    private void summonRollEarthShatters(Vec3 oldPos, Vec3 pos){

        Vec3 b = pos.subtract(oldPos);
        Vec3 nb = b.normalize();
        Vec3 r = nb.yRot((float)Math.PI / 2);
        Vec3 l = nb.yRot(-(float)Math.PI / 2);


        Matrix4fStack mat = new Matrix4fStack(3);
        float angle = (float) Math.atan2(nb.x,nb.z);
        mat.rotateY(angle);

        mat.pushMatrix();
        mat.rotateX((float)Math.toRadians(-65));
        Vector4f direction = mat.transform(0,0,1,1,new Vector4f());
        mat.popMatrix();

        mat.pushMatrix();
        mat.rotateY((float)Math.toRadians(45));
        mat.rotateX((float)Math.toRadians(-45));
        Vector4f directionLeft = mat.transform(0,0,1,1,new Vector4f());
        mat.popMatrix();

        mat.pushMatrix();
        mat.rotateY((float)Math.toRadians(-45));
        mat.rotateX((float)Math.toRadians(-45));
        Vector4f directionRight = mat.transform(0,0,1,1,new Vector4f());
        mat.popMatrix();

        EarthShatterSettings settingsMain = EarthShatterSettings.builder()
                .direction(
                        direction.x,
                        direction.y,
                        direction.z
                )
                .upTime(4)
                .upDistance(0.25f)
                .downTime(8)
                .stayTime(2)
                .build();

        EarthShatterSettings settingsLeft = EarthShatterSettings.builder()
                .direction(
                        directionLeft.x,
                        directionLeft.y,
                        directionLeft.z
                )
                .upTime(4)
                .upDistance(0.5f)
                .downTime(8)
                .stayTime(2)
                .build();

        EarthShatterSettings settingsRight = EarthShatterSettings.builder()
                .direction(
                        directionRight.x,
                        directionRight.y,
                        directionRight.z
                )
                .upTime(4)
                .upDistance(0.5f)
                .downTime(8)
                .stayTime(2)
                .build();

        for (float i = 0; i < b.length();i++){
            Vec3 v = pos.add(nb.multiply(i,i,i).reverse());

            Vec3 r1 = v.add(r);
            Vec3 l1 = v.add(l);

            BlockPos mainpos = new BlockPos(
                    (int)v.x,
                    (int)v.y,
                    (int)v.z
            );

            BlockPos rpos = new BlockPos(
                    (int)r1.x,
                    (int)r1.y,
                    (int)r1.z
            );

            BlockPos lpos = new BlockPos(
                    (int)l1.x,
                    (int)l1.y,
                    (int)l1.z
            );

            int tries = 0;

            while (EarthShatterEntity.summon(level(),mainpos.below(tries), settingsMain) == null && tries++ < 5);

            tries = 0;

            while (EarthShatterEntity.summon(level(),lpos.below(tries), settingsLeft) == null && tries++ < 5);

            tries = 0;

            while (EarthShatterEntity.summon(level(),rpos.below(tries), settingsRight) == null && tries++ < 5);


        }

    }

    private void handleClientRolling(){
        Vector3f p = this.getModelPartPosition(this,"base", clientModel);
        Vec3 pos = this.position().add(
                p.x,p.y - 1,p.z
        );
        if (oldRollPos == null){
            oldRollPos = pos;
        }

        Vec3 b = pos.subtract(oldRollPos);
        Vec3 nb = b.normalize();
        Vec3 r = nb.yRot((float)Math.PI / 2);
        Vec3 l = nb.yRot(-(float)Math.PI / 2);


        Vec3 ppos = pos.add(
                random.nextFloat() * 2 - 1,
                0,
                random.nextFloat() * 2 - 1
        );

        float md = random.nextFloat() + 1;

        level().addParticle(
                ArcLightningOptions.builder(BossParticles.ARC_LIGHTNING.get())
                        .end(ppos.add(-nb.x * md,1 + 0.1f,-nb.z * md))
                        .lifetime(4)
                        .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                        .lightningSpread(0.25f)
                        .width(0.2f)
                        .segments(5)
                        .circleOffset(-3)
                        .build(),
                true,ppos.x,ppos.y + 0.5 + 0.1f,ppos.z,0,0,0
        );


        for (float i = 0; i < b.length();i++){
            Vec3 v = pos.add(nb.multiply(i,i,i).reverse());

            Vec3 r1 = v.add(r);
            Vec3 l1 = v.add(l);

            BlockPos mainpos = new BlockPos(
                    (int)v.x,
                    (int)v.y,
                    (int)v.z
            );

            BlockPos rpos = new BlockPos(
                    (int)r1.x,
                    (int)r1.y,
                    (int)r1.z
            );

            BlockPos lpos = new BlockPos(
                    (int)l1.x,
                    (int)l1.y,
                    (int)l1.z
            );
            BlockState mainState = level().getBlockState(mainpos);
            BlockState leftState = level().getBlockState(lpos);
            BlockState rightState = level().getBlockState(rpos);

            float randomRadius = 1;

            if (!mainState.isAir()){
                for (int g = 0; g < 5;g++) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, mainState),true,
                            v.x + level().random.nextFloat() * randomRadius - randomRadius/2,
                            v.y + 0.75,
                            v.z + level().random.nextFloat() * randomRadius - randomRadius/2,
                            0, 0, 0
                    );
                }
            }
            if (!leftState.isAir()){
                for (int g = 0; g < 5;g++) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, leftState),true,
                            l1.x + level().random.nextFloat() * randomRadius - randomRadius / 2,
                            l1.y + 0.75,
                            l1.z + level().random.nextFloat() * randomRadius - randomRadius / 2,
                            0, 0, 0
                    );
                }
            }
            if (!rightState.isAir()){
                for (int g = 0; g < 5;g++) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, rightState),true,
                            r1.x + level().random.nextFloat() * randomRadius - randomRadius / 2,
                            r1.y + 0.75,
                            r1.z + level().random.nextFloat() * randomRadius - randomRadius / 2,
                            0, 0, 0
                    );
                }
            }


        }
        oldRollPos = pos;
    }

    private void lookAtTarget(LivingEntity target){
        Vec3 pos = this.getLookAtPos(target);
        this.getLookControl().setLookAt(pos);
    }

    private Vec3 getLookAtPos(LivingEntity target){


        double dist = target.distanceTo(this);

        double distMod = Mth.clamp(dist / 5,0,1);

        double baseLookAtDist = this.isBelowHalfHP() ? 8 : 10;

        double lookAtPosMod = baseLookAtDist * FDEasings.easeIn((float)(distMod * distMod));

        Vec3 v = target.position().subtract(previousTargetPos);




        Vec3 lookAtMod = v.multiply(lookAtPosMod,lookAtPosMod,lookAtPosMod);

        Vec3 pos = target.position().add(0,target.getBbHeight() / 2,0)
                .add(lookAtMod);


        return pos;
    }

    private boolean isLookingStraightAtEntity(LivingEntity entity,double accuracy){
        Vec3 look = this.getLookAngle();
        Vec3 lookAt = this.getLookAtPos(entity);
        Vec3 center = this.getEyePosition();
        Vec3 b = lookAt.subtract(center).normalize();
        double v = look.dot(b);
        return v >= (1 - accuracy);
    }

    private void summonDelayedVerticalRayOnFieldNearPlayers(float radius, int count){


        float angle = FDMathUtil.FPI * 2 / count;

        for (Player player : this.getCombatants(false)) {

            float r = random.nextFloat();

            Vec3 pos = new Vec3(radius - r,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

            for (int i = 0; i < count;i++){

                Vec3 resultingPos = pos.yRot(angle * i).add(player.position());

                ChesedOneShotVerticalRayEntity entity = ChesedOneShotVerticalRayEntity.summon(level(), resultingPos,
                        BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().chesedConfig.rockfallRayDamage), 40, 30);
                entity.setDamageRadius(2f);

            }

        }
    }

    private void summonCirclingParticlesServerside(int count,float verticalSpeed,float offset,float size,int in,int stay){

        float angle = FDMathUtil.FPI * 2 / count;

        Vec3 center = this.position();

        for (int i = 0; i < count; i++) {
            float a = angle * i;
            Vec3 v = new Vec3(offset,0,0).yRot(a);
            Vec3 ppos = this.position().add(v);
            FDLibCalls.sendParticles(((ServerLevel) level()),BallParticleOptions.builder()
                    .size(size)
                    .color(100 + random.nextInt(50), 255, 255)
                    .particleProcessor(new CompositeParticleProcessor(
                            new CircleParticleProcessor(center,true,true,2),
                            new SetParticleSpeedProcessor(new Vec3(0,verticalSpeed,0))
                    ))
                    .scalingOptions(in,stay,0)
                    .build(),ppos,80);


        }
    }

    @Override
    public void die(DamageSource p_21014_) {
        this.dropLoot = false;
        super.die(p_21014_);
        if (!level().isClientSide){

            this.trySearchProjectiles();
            for (var projectile : this.blockAttackProjectiles){
                projectile.remove(RemovalReason.DISCARDED);
            }

            this.removeMonoliths();
            this.killCrystals();

            this.lookingAtTarget = false;

            this.stopAllNonIdleAnimations();


            this.getAnimationSystem().startAnimation("DEATH", AnimationTicker.builder(CHESED_DEATH)
                    .build());

            Vec3 forward = this.getForward();

            Vec3 pos = this.position().add(forward.multiply(4,0,4)).add(0,1.75,0);

            CutsceneData cutsceneData = new CutsceneData()
                    .stopMode(CutsceneData.StopMode.AUTOMATIC)
                    .time(CHESED_DEATH.get().getAnimTime() + 40)
                    .addCameraPos(new CameraPos(pos,forward.reverse()));

            for (Player player : this.getCombatants(true)){
                if (player instanceof ServerPlayer serverPlayer){
                    FDLibCalls.startCutsceneForPlayer(serverPlayer, cutsceneData);
                }
            }

//            level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.CHESED_DEATH.get(), SoundSource.HOSTILE, 5f, 1f);


        }
    }

    private void passiveEntityPullInArena(){

        int pullRadius = 5;
        int arenaRadius = ARENA_RADIUS - 2;

        var entities = this.level().getEntitiesOfClass(Entity.class,new AABB(-arenaRadius - pullRadius,-2,-arenaRadius - pullRadius,arenaRadius + pullRadius,20,arenaRadius + pullRadius).move(this.position()),(entity)->!(entity instanceof ChesedBossBuddy));

        for (var entity : entities){

            if (entity instanceof ItemEntity) continue;

            Vec3 b = this.position().subtract(entity.position());
            Vec3 h = b.multiply(1,0,1);
            double dist = h.length();

            if (dist >= arenaRadius && dist <= arenaRadius + pullRadius){

                ClipContext clipContext = new ClipContext(entity.position().add(0,entity.getBbHeight()/2,0),this.position().add(0,2,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

                BlockHitResult result = level().clip(clipContext);

                if (result.getType() != HitResult.Type.MISS) continue;

                Vec3 speed = b.normalize();

                boolean doParticles = false;

                if (entity instanceof ServerPlayer serverPlayer){
                    if ((!serverPlayer.isCreative() && !serverPlayer.isSpectator())) {
                        FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
                        doParticles = true;
                    }
                }else{
                    doParticles = true;
                    entity.setDeltaMovement(speed);
                }


                if (doParticles) {
                    ParticleOptions rush = new RushParticleOptions(speed.reverse(),
                            new FDColor(0.8f, 1f, 1f, 1f),
                            random.nextFloat() * 0.5f + 0.25f,
                            0.05f + random.nextFloat() * 0.025f,
                            2 + random.nextInt(2));

                    BallParticleOptions ball = BallParticleOptions.builder()
                            .color(40, 200 + random.nextInt(30), 255)
                            .scalingOptions(0, 20, 20)
                            .size(0.25f)
                            .scalingOptions(2, 0, 4)
                            .build();

                    ((ServerLevel) level()).sendParticles(rush,
                            entity.getX(),
                            entity.getY() + entity.getBbHeight() / 2,
                            entity.getZ(), 10,
                            entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2, 0f);


                    ((ServerLevel) level()).sendParticles(ball,
                            entity.getX(),
                            entity.getY() + entity.getBbHeight() / 2,
                            entity.getZ(), 10,
                            entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2, 0f);
                }

            }
        }

    }

    public void playInEarsSound(SoundEvent soundEvent, float volume, float pitch){
        for (var player : this.getCombatants(true, 10)){
            PacketDistributor.sendToPlayer((ServerPlayer) player, new PlaySoundInEarsPacket(soundEvent,pitch,volume));
        }
    }

    private void stopAllNonIdleAnimations(){
        for (var ticker : new ArrayList<>(this.getAnimationSystem().getTickers().keySet())) {

            if (ticker.equals("IDLE")) continue;

            this.getAnimationSystem().stopAnimation(ticker);

        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        this.lookingAtTarget = false;

        if (!this.isRemoved()) {
            if (!this.level().isClientSide()) {
                if (this.deathTime >= CHESED_DEATH.get().getAnimTime() + 50) {
                    BossSpawnerEntity spawnerEntity = this.getSpawner();
                    if (spawnerEntity != null) {
                        spawnerEntity.setActive(true);
                    }
                    this.remove(Entity.RemovalReason.KILLED);
                } else if (this.deathTime == CHESED_DEATH.get().getAnimTime() + 30) {
                    for (Player player : this.getCombatants(true)) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            FDLibCalls.sendScreenEffect(serverPlayer, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0f, 0f, 0f, 1f), 5, 20, 10);
                        }
                    }
                }else if (this.deathTime == CHESED_DEATH.get().getAnimTime()){

                    dropLoot = true;
                    this.dropAllDeathLoot((ServerLevel) level(),level().damageSources().generic());

                    for (var combatant : this.getCombatants(true)){
                        if (!combatant.isSpectator()){
                            BossCriteriaTriggers.BOSS_KILLED.get().trigger((ServerPlayer) combatant,this);
                        }
                    }

                    if (this.captureDrops() != null) {
                        for (ItemEntity entity : this.captureDrops()) {

                            Vec3 rnd = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                            entity.setDeltaMovement(rnd.add(0,0.25,0));

                        }
                    }

                }
            }else{
                if (this.deathTime < 30) {
                    this.spawnDyingParticles();
                }else if (this.deathTime == CHESED_DEATH.get().getAnimTime() - 3){
                    Vec3 pos = this.position().add(0,1.1f,0);
                    level().addParticle(BallParticleOptions.builder()
                            .size(5f)
                            .scalingOptions(0,0,3)
                            .color(100,230,255)
                            .build(),pos.x,pos.y,pos.z,0,0,0);
                } else if (this.deathTime == CHESED_DEATH.get().getAnimTime()) {
                    this.deathExplosionParticles();
                }
            }
        }
    }

    @Override
    protected boolean shouldDropLoot() {
        return this.dropLoot;
    }

    private void deathExplosionParticles(){
        Vec3 pos = this.position().add(0,1.1f,0);

        level().addParticle(BallParticleOptions.builder()
                        .size(5f)
                        .scalingOptions(3,0,3)
                        .color(100,230,255)
                .build(),pos.x,pos.y,pos.z,0,0,0);

        float speed = 1f;
        for (int i = 0; i < 300;i++){

            if (i % 2 == 0) {
                level().addParticle(LightningParticleOptions.builder()
                                .color(20, 150 + random.nextInt(50), 255)
                                .lifetime(30)
                                .maxLightningSegments(3)
                                .randomRoll(true)
                                .physics(true)
                                .build(), pos.x, pos.y, pos.z,
                        random.nextFloat() * speed - speed / 2,
                        random.nextFloat() * speed - speed / 2,
                        random.nextFloat() * speed - speed / 2
                );
            }


            level().addParticle(BallParticleOptions.builder()
                            .color(40, 200 + random.nextInt(30), 255)
                            .scalingOptions(0,20,20)
                            .physics(true)
                            .size(0.25f)
                            .build(), pos.x, pos.y, pos.z,
                    random.nextFloat() * speed - speed/2,
                    random.nextFloat() * speed - speed/2,
                    random.nextFloat() * speed - speed/2
            );

        }
    }

    private void spawnDyingParticles(){
        for (int i = 0; i < 5;i++) {
            Vec3 pos = new Vec3(
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1
                    )
                    .normalize()
                    .multiply(1.5, 1.5, 1.5)
                    .add(
                            this.getX() + random.nextFloat() * 0.2 - 0.1,
                            this.getY() + random.nextFloat() * 0.2 + 1.1f,
                            this.getZ() + random.nextFloat() * 0.2 - 0.1
                    );

            level().addParticle(LightningParticleOptions.builder()
                            .color(20, 150 + random.nextInt(50), 255)
                            .lifetime(10)
                            .maxLightningSegments(3)
                            .randomRoll(true)
                            .build(), pos.x, pos.y, pos.z,
                    random.nextFloat() * 0.05 - 0.025,
                    random.nextFloat() * 0.05 - 0.025,
                    random.nextFloat() * 0.05 - 0.025
            );

            level().addParticle(BallParticleOptions.builder()
                            .color(40, 200 + random.nextInt(30), 255)
                            .scalingOptions(0,10,5)
                            .size(0.25f)
                            .build(), pos.x, pos.y, pos.z,
                    random.nextFloat() * 0.05 - 0.025,
                    random.nextFloat() * 0.05 - 0.025,
                    random.nextFloat() * 0.05 - 0.025
            );

        }

    }


    private Vec3 getCenter(){
        return this.position().add(0,1.3,0);
    }

    public boolean isBelowHalfHP(){
        return this.getRemainingHits() < this.getBossMaxHits() / 2f;
    }

    private float targetDetectionRadius(){
        return 39;
    }

    public int getBossMaxHits(){
        return BossConfigs.BOSS_CONFIG.get().chesedConfig.chesedMaxHits;
    }

    public int getRemainingHits(){
        return remainingHits;
    }

    public void decreaseHitCount(int amount){
        this.remainingHits = Mth.clamp(this.remainingHits - amount,0,this.getBossMaxHits());
        this.bossBar.broadcastEvent(ChesedBossBar.HIT_EVENT,amount * 10);
        if (remainingHits <= 0){
            this.kill();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ROLLING,false);
        builder.define(IS_LAUNCHING_ORBS,false);
        builder.define(IS_DRAINING_MONOLITHS,false);
        builder.define(DRAIN_PERCENT,0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (chain != null){
            CompoundTag t = new CompoundTag();
            chain.save(t);
            tag.put("chain",t);
        }

        if (bossSpawnerUUID != null){
            tag.putUUID("spawner",this.bossSpawnerUUID);
        }

        tag.putInt("bossHealth",this.remainingHits);
        this.bossInitializer.autoSave("initializer",tag);
        tag.putBoolean("alreadySpawned",alreadySpawned);
        tag.putInt("secondPhaseTicker",this.secondPhaseTicker);
        tag.putBoolean("secondPhaseBegun",this.secondPhaseAnimPlayed);
        super.addAdditionalSaveData(tag);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {

        this.secondPhaseTicker = tag.getInt("secondPhaseTicker");
        this.secondPhaseAnimPlayed = tag.getBoolean("secondPhaseBegun");

        this.alreadySpawned = tag.getBoolean("alreadySpawned");

        if (tag.contains("spawner")){
            this.bossSpawnerUUID = tag.getUUID("spawner");
        }

        this.bossInitializer.autoLoad("initializer",tag);
        if (chain != null){
            this.chain.load(tag.getCompound("chain"));
        }
        if (tag.contains("bossHealth")) {
            this.remainingHits = tag.getInt("bossHealth");
        }else{
            this.remainingHits = this.getBossMaxHits();
        }
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public void setRolling(boolean state){
        this.entityData.set(IS_ROLLING,state);
    }

    public boolean isRolling(){
        return this.entityData.get(IS_ROLLING);
    }

    public boolean isDrainingFromMonoliths(){
        return this.entityData.get(IS_DRAINING_MONOLITHS);
    }

    public void setDrainingMonoliths(boolean state){
        this.entityData.set(IS_DRAINING_MONOLITHS,state);
    }

    public float getMonolithDrainPercent(){
        return this.entityData.get(DRAIN_PERCENT);
    }

    public void setMonolithDrainPercent(float percent){
        this.entityData.set(DRAIN_PERCENT,Mth.clamp(percent,0,1));
    }

    public void sendPacketToCombatants(CustomPacketPayload customPacketPayload){
        for (Player player : this.getCombatants(true)){
            PacketDistributor.sendToPlayer((ServerPlayer) player,customPacketPayload);
        }
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public void setDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        super.setDeltaMovement(0,0,0);
    }

    @Override
    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public int getHeadRotSpeed() {
        return 5;
    }

    @Override
    public void setSpawnedBy(BossSpawnerEntity bossSpawnerEntity) {
        this.bossSpawnerUUID = bossSpawnerEntity.getUUID();
    }

    @Override
    public void setSpawnPosition(Vec3 spawnPosition) {

    }

    @Override
    public BossSpawnerEntity getSpawner() {
        if (level() instanceof ServerLevel serverLevel){
            return (BossSpawnerEntity) serverLevel.getEntity(this.bossSpawnerUUID);
        }
        return null;
    }


    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
        super.pushEntities();
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Entity entity) {
        if (!this.isRolling() && !(entity instanceof ChesedBossBuddy)) {
            Vec3 b = entity.position().subtract(this.position()).normalize().add(0, 0.3, 0);
            entity.setDeltaMovement(b);
            entity.hasImpulse = true;
        }
    }


    @Override
    public void push(Vec3 p_347665_) {

    }


    @Override
    protected void doPush(Entity entity) {
        if (!this.isRolling() && !(entity instanceof ChesedBossBuddy)) {
            Vec3 b = entity.position().subtract(this.position()).multiply(1, 0, 1).normalize().add(0, 0.5, 0);
            entity.setDeltaMovement(b);
            entity.hasImpulse = true;
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean onFDDespawn() {
        BossSpawnerEntity spawnerEntity = this.getSpawner();
        if (spawnerEntity != null) {
            spawnerEntity.setActive(true);
            return true;
        }
        return false;
    }

}
