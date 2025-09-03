package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boss_spawner.MalkuthBossSpawner;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder.MalkuthBoulderEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonProjectile;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquake;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireball;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor.MalkuthFloorEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword.MalkuthGiantSwordSlash;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform.MalkuthPlatform;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal.MalkuthRepairCrystal;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.packets.MalkuthChargeSwordPacket;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.network.lib_packets.PlaySoundInEarsPacket;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.HeadControllerContainer;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.head.IHasHead;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.ModelSystem;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.BaseModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.model_system.attachments.instances.fdmodel.FDModelAttachmentData;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.systems.hud.bossbars.FDServerBossBar;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.music.data.FDMusicData;
import com.finderfeed.fdlib.systems.music.data.FDMusicPartData;
import com.finderfeed.fdlib.systems.music.music_areas.FDMusicArea;
import com.finderfeed.fdlib.systems.music.music_areas.FDMusicAreasHandler;
import com.finderfeed.fdlib.systems.music.music_areas.shapes.FDMusicAreaCylinder;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class MalkuthEntity extends FDMob implements IHasHead<MalkuthEntity>, MalkuthBossBuddy, AutoSerializable, BossSpawnerContextAssignable {

    public static final UUID BOSS_MUSIC_UUID = UUID.fromString("5c6cd8c0-7e3e-44a3-9c2e-2459a61377f3");

    public static float CHANCE_TO_CHOOSE_WEAK_TYPE = 0.75f;

    public static final Vec3 FIRE_PLAYER_CANNON_OFFSET = new Vec3(-6.5,-1,-25);
    public static final Vec3 ICE_PLAYER_CANNON_OFFSET = new Vec3(6.5,-1,-25);

    public UUID bossSpawnerUUID;

    public static final Vec3 WALL_OFFSET = new Vec3(0, 13.0, 27.85);

    public static final String MAIN_LAYER = "MAIN";

    public static final float ENRAGE_HEIGHT = 11;
    public static final float ENRAGE_RADIUS = 29;

    public static final String SLASH_ATTACK = "slash";
    public static final String JUMP_CRUSH = "jump_crush";
    public static final String PULL_AND_PUNCH = "pull_and_punch";
    public static final String JUMP_ON_WALL_COMMAND_CANNONS = "jump_on_wall_command_cannons";
    public static final String CAROUSEL_SLASHES = "carousel_slashes";
    public static final String JUMP_BACK_ON_SPAWN = "jump_back_on_spawn";
    public static final String JUMP_BACK_ON_SPAWN_WITH_CRUSH = "jump_back_on_spawn_with_crush";
    public static final String GIANT_SWORDS_ULTIMATE = "giant_swords_attack";
    public static final String SUMMON_AND_THROW_SIDE_ROCKS = "summon_and_throw_side_rocks";
    public static final String ATTACH_SWORDS = "attach_swords";
    public static final String DEATTACH_SWORDS = "deattach_swords";
    public static final String DELAY_20 = "nothing_20_ticks";
    public static final String DELAY_10 = "nothing_10_ticks";
    public static final String DELAY_5 = "nothing_5_ticks";
    public static final String SUMMON_EARTHQUAKE = "summon_earthquake";
    public static final String SUMMON_EARTHQUAKE_LOWER = "summon_earthquake_lower";
    public static final String PLATFORMS_N_FIREBALLS = "platforms_n_fireballs";

    private static FDModel SERVER_MODEL;
    private static FDModel CLIENT_MODEL;

    public static final UUID FIRE_SWORD_UUID = UUID.fromString("7a6d1a24-599a-4717-baa3-42d9e3293896");
    public static final UUID FIRE_SWORD_EMISSIVE_UUID = UUID.fromString("cd95b81b-4a3f-4ef0-9b46-f0b3503ed7fb");
    public static final UUID ICE_SWORD_UUID = UUID.fromString("a46c06e3-f6af-4295-a296-20fba19ac613");
    public static final UUID ICE_SWORD_EMISSIVE_UUID = UUID.fromString("930a0a3b-5a47-4ebd-b614-7e42e5d0cd92");

    public static final ResourceLocation MALKUTH_SWORD_SOLID = FDBosses.location("textures/item/malkuth_sword_solid.png");
    public static final ResourceLocation MALKUTH_ICE_SWORD = FDBosses.location("textures/item/malkuth_sword_ice_emissive.png");
    public static final ResourceLocation MALKUTH_FIRE_SWORD = FDBosses.location("textures/item/malkuth_sword_fire_emissive.png");

    private FDServerBossBar bossbar = new FDServerBossBar(BossBars.MALKUTH_BOSS_BAR, this);

    private HeadControllerContainer<MalkuthEntity> headControllerContainer;

    private AttackChain attackChain;

    @SerializableField
    protected Vec3 spawnPosition;

    @SerializableField
    private MalkuthBossInitializer malkuthBossInitializer;

    @SerializableField
    private MalkuthSecondPhaseInitializer malkuthSecondPhaseInitializer;

    @SerializableField
    protected boolean lookAtTarget = true;

    @SerializableField
    private int hits = 10;

    @SerializableField
    private boolean allowedToBeDamaged = false;

    private int maxHits = 10;

    private boolean dropLoot = true;

    public MalkuthEntity(EntityType<? extends FDMob> type, Level level) {
        super(type, level);
        if (level.isClientSide){
            if (CLIENT_MODEL == null) {
                CLIENT_MODEL = new FDModel(BossModels.MALKUTH.get());
            }
        }else{
            if (SERVER_MODEL == null) {
                SERVER_MODEL = new FDModel(BossModels.MALKUTH.get());
            }
        }

        this.maxHits = BossConfigs.BOSS_CONFIG.get().malkuthConfig.malkuthMaxHits;

        malkuthBossInitializer = new MalkuthBossInitializer(this);
        malkuthSecondPhaseInitializer = new MalkuthSecondPhaseInitializer(this);

        this.headControllerContainer = new HeadControllerContainer<>(this)
                .addHeadController(CLIENT_MODEL, "head");
        this.lookControl = this.headControllerContainer;


        AttackOptions<?> cannons = AttackOptions.chainOptionsBuilder()
                .addAttack(JUMP_ON_WALL_COMMAND_CANNONS)
                .addAttack(DELAY_5)
                .addAttack(JUMP_BACK_ON_SPAWN)
                .build();

        AttackOptions<?> cannonsNoJumpBack = AttackOptions.chainOptionsBuilder()
                .addAttack(JUMP_ON_WALL_COMMAND_CANNONS)
                .addAttack(DELAY_5)
                .build();

        AttackOptions<?> jumpCrushEarthquake = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_BACK_ON_SPAWN).build();
        AttackOptions<?> jumpCrushEarthquakeNoJumpBack = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).build();

        AttackOptions<?> jumpCrushEarthquakeJumpCrushEarthquake = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_BACK_ON_SPAWN).build();
        AttackOptions<?> jumpCrushEarthquakeJumpCrushEarthquakeNoJumpBack = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).build();

        AttackOptions<?> jumpCrushSlashNEarthquake = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SLASH_ATTACK).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SLASH_ATTACK).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).addAttack(JUMP_BACK_ON_SPAWN).build();
        AttackOptions<?> jumpCrushSlashNEarthquakeNoJumpBack = AttackOptions.chainOptionsBuilder().addAttack(JUMP_CRUSH).addAttack(DELAY_10).addAttack(SLASH_ATTACK).addAttack(DELAY_5).addAttack(SUMMON_EARTHQUAKE).addAttack(DELAY_5).addAttack(SLASH_ATTACK).addAttack(DELAY_5).addAttack(JUMP_CRUSH).addAttack(DELAY_5).build();


        AttackOptions<?> randomJumpCrush = AttackOptions.builder()
                .addAttack(jumpCrushEarthquake)
                .addAttack(jumpCrushEarthquakeJumpCrushEarthquake)
                .addAttack(jumpCrushSlashNEarthquake)
                .build();


        AttackOptions<?> randomJumpCrushNoJumpBack = AttackOptions.builder()
                .addAttack(jumpCrushEarthquakeNoJumpBack)
                .addAttack(jumpCrushEarthquakeJumpCrushEarthquakeNoJumpBack)
                .addAttack(jumpCrushSlashNEarthquakeNoJumpBack)
                .build();


        AttackOptions<?> slashOptions = AttackOptions.chainOptionsBuilder()
                .addAttack(SLASH_ATTACK)
                .addAttack(SLASH_ATTACK)
                .addAttack(SLASH_ATTACK)
                .addAttack(SLASH_ATTACK)
                .addAttack(SLASH_ATTACK)
                .build();

        AttackOptions<?> carouselEarthquakes = AttackOptions.chainOptionsBuilder()
                .addAttack(CAROUSEL_SLASHES)
                .addAttack(SUMMON_EARTHQUAKE_LOWER)
                .addAttack(CAROUSEL_SLASHES)
                .addAttack(SUMMON_EARTHQUAKE_LOWER)
                .addAttack(CAROUSEL_SLASHES)
                .build();

        AttackOptions<?> boulders = AttackOptions.chainOptionsBuilder()
                .addAttack(DELAY_10)
                .addAttack(DEATTACH_SWORDS)
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .addAttack(ATTACH_SWORDS)
                .addAttack(DELAY_20)
                .build();

        AttackOptions<?> bouldersNCarousel = AttackOptions.chainOptionsBuilder()
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .addAttack(CAROUSEL_SLASHES)
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .addAttack(CAROUSEL_SLASHES)
                .build();

        int id = 0;

        this.attackChain = new AttackChain(this.getRandom())
                .registerAttack(DELAY_5,v->this.doNothingNTicks(v,5))//not an attack
                .registerAttack(DELAY_10,v->this.doNothingNTicks(v,10))//not an attack
                .registerAttack(DELAY_20,v->this.doNothingNTicks(v,20))//not an attack
                .registerAttack(DEATTACH_SWORDS, v->this.attachSwordsAttack(v,false)) //not an attack
                .registerAttack(ATTACH_SWORDS, v->this.attachSwordsAttack(v,true)) //not an attack

                .registerAttack(SLASH_ATTACK,this::aerialSlashAttack)
                .registerAttack(PULL_AND_PUNCH,this::pullAndPunch) //combos and anti flight
                .registerAttack(JUMP_CRUSH,this::jumpCrushAttack)
                .registerAttack(CAROUSEL_SLASHES,this::carouselSlashesAttack)
                .registerAttack(JUMP_BACK_ON_SPAWN,v->this.jumpBackOnSpawn(v,this.isBelowHalfHP()))
                .registerAttack(JUMP_ON_WALL_COMMAND_CANNONS,this::jumpAndCommandCannons)
                .registerAttack(GIANT_SWORDS_ULTIMATE,this::giantSwordUltimate)
                .registerAttack(SUMMON_AND_THROW_SIDE_ROCKS,this::summonAndThrowSideRocks)
                .registerAttack(SUMMON_EARTHQUAKE,v->this.summonEarthquake(v, 0))
                .registerAttack(SUMMON_EARTHQUAKE_LOWER,v->this.summonEarthquake(v, -1))
                .registerAttack(PLATFORMS_N_FIREBALLS, this::fireballsNPlatforms)

                .addAlwaysTryCastAttack(this::checkCanPunch, PULL_AND_PUNCH)

//                .addAttack(id++, slashOptions)
//                .addAttack(id++, randomJumpCrushNoJumpBack)
//                .addAttack(id++, cannonsNoJumpBack)
//                .addAttack(id++, randomJumpCrush)
//                .addAttack(id++, carouselEarthquakes)
//                .addAttack(id++, randomJumpCrushNoJumpBack)
//                .addAttack(id++, cannons)
//                .addAttack(id++, slashOptions)
                .addAttack(id++, GIANT_SWORDS_ULTIMATE)
//                .addAttack(id++, randomJumpCrushNoJumpBack)
//                .addAttack(id++, cannons)
//                .addAttack(id++, slashOptions)
//                .addAttack(id++, randomJumpCrush)
//                .addAttack(id++, boulders)
//                .addAttack(id++, AttackOptions.chainOptionsBuilder()
//                        .addAttack(JUMP_CRUSH)
//                        .addAttack(DELAY_10)
//                        .addAttack(PLATFORMS_N_FIREBALLS)
//                        .build())


                .attackListener(this::attackListener)
        ;

    }

    private boolean doNothingNTicks(AttackInstance instance, int tick){
        if (instance.tick >= tick){
            return true;
        }
        return false;
    }

    public static FDModel getClientModel(){
        if (CLIENT_MODEL == null){
            CLIENT_MODEL = new FDModel(BossModels.MALKUTH.get());
        }
        return CLIENT_MODEL;
    }

    @Override
    public void tick() {
        if (!level().isClientSide && firstTick){
            this.attachSwords();
        }

        super.tick();
        if (level().isClientSide){
            this.headControllerContainer.clientTick();
        }else{


            this.preventEnteringLavaField();

//            BossUtil.malkuthSwordsInsertParticles((ServerLevel) level(), this.position(), 30, this.getId());

            this.bossbar.setPercentage(this.hits / (float) this.getMaxHits());

            AnimationSystem animationSystem = this.getAnimationSystem();
            if (malkuthBossInitializer.isFinished() && !this.isDeadOrDying()) {

                if (animationSystem.getTicker(MAIN_LAYER) == null) {
                    animationSystem.startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                }

                if (!FDMusicAreasHandler.hasMusicArea(this.getUUID())){
                    FDMusicAreasHandler.addArea(this.getUUID(), this.constructMusicArea());
                }

            }

            if (!this.isDeadOrDying()) {
                if (malkuthBossInitializer.isFinished() && (!this.isBelowHalfHP() || malkuthSecondPhaseInitializer.isFinished())) {
                    this.attackChain.tick();


                    if (this.getTarget() != null) {

                        var target = this.getTarget();

                        this.checkTarget(target);

                        if (lookAtTarget) {
                            this.getLookControl().setLookAt(target);
                        }

                    } else {

                        this.changeTarget();

                        if (this.getTarget() == null) {
                            this.getLookControl().setLookAt(
                                    this.position().add(this.getForward().multiply(100, 0, 100))
                            );
                        }

                    }

                } else {
                    if (!malkuthBossInitializer.isFinished()) {
                        malkuthBossInitializer.tick();
                    } else if (!malkuthSecondPhaseInitializer.isFinished()) {
                        malkuthSecondPhaseInitializer.tick();
                    }
                }
            }






            this.setYRot(this.yBodyRot);

        }
    }

    private FDMusicArea constructMusicArea(){
        return new FDMusicArea(this.level().dimension(), this.spawnPosition.add(0,-2,0), new FDMusicAreaCylinder(ENRAGE_RADIUS, ENRAGE_HEIGHT + 8), this.constructMusicData());
    }

    private FDMusicData constructMusicData(){
        return new FDMusicData(BOSS_MUSIC_UUID, new FDMusicPartData(BossSounds.MALKUTH_THEME_INTRO.get(),14.75f))
                .addMusicPart(new FDMusicPartData(BossSounds.MALKUTH_THEME_MAIN.get(), 103.375f).setLooping(true))
                .fadeInTime(80)
                .inactiveDeleteTime(600);
    }

    private void preventEnteringLavaField(){

        for (var entity : BossTargetFinder.getEntitiesInArc(LivingEntity.class, level(), this.spawnPosition.add(0,-1.5,-0.7), new Vec2(0,1), FDMathUtil.FPI, 13, ENRAGE_RADIUS)){

            if (entity instanceof MalkuthBossBuddy) continue;

            Vec3 speed = new Vec3(0,0.025f,-1f);

            if (entity instanceof ServerPlayer serverPlayer){
                FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
            }else{
                entity.setDeltaMovement(speed);
            }

            Vector3f col = getAndRandomizeColor(MalkuthAttackType.FIRE, random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .color(col.x,col.y,col.z)
                    .scalingOptions(0,0,20)
                    .brightness(2)
                    .friction(0.7f)
                    .build();

            ((ServerLevel)level()).sendParticles(ballParticleOptions, entity.getX(),entity.getY() + entity.getBbHeight()/2,entity.getZ(),25,
                    entity.getBbWidth()/2,entity.getBbHeight()/2,entity.getBbWidth()/2,0.5f);


        }

    }

    public void hurtBoss(int amount){
        if (allowedToBeDamaged) {
            this.hits = Math.clamp(this.hits - amount, 0, maxHits);
            if (hits == 0) {
                this.kill();
            }
        }
    }

    @Override
    public void die(DamageSource src) {
        this.dropLoot = false;

        for (var repairCrystal : BossTargetFinder.getEntitiesInCylinder(MalkuthRepairCrystal.class, level(), this.spawnPosition.add(0,-3,0),30,40)){
            repairCrystal.remove(RemovalReason.DISCARDED);
        }

        this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_FLOAT)
                .setToNullTransitionTime(0).build());
        this.getAnimationSystem().stopAnimation(MAIN_LAYER);

        Vec3 teleportPos = this.spawnPosition.add(WALL_OFFSET);

        this.teleportTo(teleportPos.x,teleportPos.y,teleportPos.z);

        this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

        this.lookAt(EntityAnchorArgument.Anchor.EYES, this.position().add(0,0,-100));

        super.die(src);

        CutsceneData data = this.deathCutscene();

        for (var player : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), this.spawnPosition.subtract(0,2,0),40,40)){
            FDLibCalls.startCutsceneForPlayer(player, data);
        }

    }

    private CutsceneData deathCutscene(){

        Vec3 base = this.spawnPosition;
        CameraPos last = new CameraPos(base.add(0,16.119,23.010), new Vec3(0,-0.270,0.963));

        CutsceneData data1 = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1),0,6,30)
                .timeEasing(EasingType.EASE_OUT)
                .time(80)
                .addCameraPos(new CameraPos(base.add(0,16.119,19.863), new Vec3(0,-0.182,0.983)))
                .addCameraPos(last)

                ;

        CutsceneData data2 = CutsceneData.create()
                .addScreenEffect(219, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0,0,0,1),0,30,30)
                .addCameraPos(last)
                .time(220)
                ;
        data1.nextCutscene(data2);

        return data1;
    }

    @Override
    protected void tickDeath() {

        this.deathTime++;

        int animStartTime = 5;
        int animEndtime = animStartTime + BossAnims.MALKUTH_DEATH.get().getAnimTime();

        if (!level().isClientSide) {

            FDMusicAreasHandler.removeArea(((ServerLevel)level()).getServer(), this.getUUID(), 40);

            if (this.deathTime == animStartTime) {

                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_DEATH).build());

            } else if (deathTime == animEndtime - 38){
                DefaultShakePacket.send((ServerLevel) level(), spawnPosition, 40, FDShakeData.builder()
                        .amplitude(0.3f)
                        .outTime(30)
                        .build());
                for (var player : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), this.spawnPosition.subtract(0,2,0),40,40)){
                    PacketDistributor.sendToPlayer(player, new PlaySoundInEarsPacket(BossSounds.MALKUTH_HIT.get(),0.75f,1f));
                    PacketDistributor.sendToPlayer(player, new PlaySoundInEarsPacket(BossSounds.MALKUTH_VOLCANO_ERRUPTION.get(),1.75f,1f));
                }
            }else if (this.deathTime == animEndtime - 37) {
                BossUtil.malkuthSwordsInsertParticles((ServerLevel) level(), this.spawnPosition, 100, this.getId());

            } else if (this.deathTime == animEndtime + 15) {

                this.dropLoot = true;

                Vector3f col1 = getAndRandomizeColor(MalkuthAttackType.FIRE, random);
                Vector3f col2 = getAndRandomizeColor(MalkuthAttackType.ICE, random);

                BallParticleOptions options = BallParticleOptions.builder()
                        .color(col1.x,col1.y,col1.z)
                        .scalingOptions(0,0,25)
                        .size(0.25f)
                        .brightness(2)
                        .friction(0.8f)
                        .build();
                BallParticleOptions options2 = BallParticleOptions.builder()
                        .color(col2.x,col2.y,col2.z)
                        .scalingOptions(0,0,25)
                        .size(0.25f)
                        .brightness(2)
                        .friction(0.8f)
                        .build();
                for (var player : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), this.spawnPosition.subtract(0,2,0),40,40)){
                    player.connection.send(new ClientboundLevelParticlesPacket(options, true, this.getX(), this.getY(), this.getZ(), 1f, 1f, 1f,0.75f,400));
                    player.connection.send(new ClientboundLevelParticlesPacket(options2, true, this.getX(), this.getY(), this.getZ(), 1f, 1f, 1f,0.75f,400));
                }

                BossSpawnerEntity spawnerEntity = this.getSpawner();

                if (spawnerEntity != null){
                    spawnerEntity.setActive(true);
                }

                this.teleportTo(this.spawnPosition.x,this.spawnPosition.y,this.spawnPosition.z);
                this.dropAllDeathLoot((ServerLevel) level(), level().damageSources().generic());

                this.setRemoved(RemovalReason.KILLED);

            }
        }



    }

    @Override
    protected boolean shouldDropLoot() {
        return dropLoot;
    }

    public int getCurrentHits(){
        return this.hits;
    }

    public int getMaxHits(){
        return maxHits;
    }

    public void removeThingsForSecondPhase(){

        for (var entity : this.getMalkuthCannons()){
            entity.cancelShot();
        }

        for (var entity : this.getPlayerCannons(false)){
            entity.cancelShot();
        }

        for (var entity : BossTargetFinder.getEntitiesInCylinder(MalkuthCannonProjectile.class, level(), this.spawnPosition.add(0,-2,0), 30, 40)){
            entity.remove(RemovalReason.DISCARDED);
        }

        this.getAnimationSystem().stopAnimation(MAIN_LAYER);

        this.attackChain.reset();

    }

    private AttackAction attackListener(String attack){
        if (this.getTarget() == null){
            return AttackAction.WAIT;
        }
        return AttackAction.PROCEED;
    }

    //============================================================================ATTACKS==============================================================================================

    /*
    1  3  5  7  9
    0  2  4  6  8
     */
    private static final Vec3[] PLATFORM_SPAWN_OFFSETS = new Vec3[]{
            new Vec3(16,5,-14 - 1),
            new Vec3(16,5,-6 - 1),

            new Vec3(8,5,-16 - 1),
            new Vec3(8,5,-8 - 1),


            new Vec3(0,5,-18 - 1),
            new Vec3(0,5,-10 - 1),


            new Vec3(-8,5,-16 - 1),
            new Vec3(-8,5,-8 - 1),

            new Vec3(-16,5,-14 - 1),
            new Vec3(-16,5,-6 - 1),


    };

    //0 fire 1 ice

    private static final int[][] PLATFORM_ATTACK_PATTERN = {
            {0,1,1,0,0,1,1,0,0,1},

            {1,1,0,0,1,1,0,0,1,1},

            {0,0,0,1,1,1,0,1,0,0},

            {1,0,0,1,1,0,0,1,1,0},

            {0,0,1,1,0,0,1,1,0,0},

            {1,1,1,0,0,0,1,0,1,1}

    };

    private Vec3[] FLY_TO_OFFSETS = new Vec3[]{
            new Vec3(0,7,15),
            new Vec3(-15,7,12),
            new Vec3(15,7,12)
    };

    @SerializableField
    private int currentlyFlyingTo = -1;

    @SerializableField
    private ProjectileMovementPath startSummonPlatformsPath;

    private boolean fireballsNPlatforms(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_CRUSH)
                    .setSpeed(1.25f).build());

            Vec3 between = this.spawnPosition.subtract(this.position());
            Vec3 midpos = this.position().add(between.multiply(0.4,0,0.4)).add(0,15,0);

            if(startSummonPlatformsPath == null){
                startSummonPlatformsPath = new ProjectileMovementPath(15, false)
                        .addPos(this.position())
                        .addPos(midpos)
                        .addPos(this.spawnPosition);
            }

            if (tick > 5) {
                startSummonPlatformsPath.tick(this);
                if (tick == 6){
                    this.doJumpStartParticles(0);
                }
            }

            if (tick == 25){
                startSummonPlatformsPath = null;
                this.setDeltaMovement(Vec3.ZERO);
                this.teleportTo(spawnPosition.x,spawnPosition.y,spawnPosition.z);
                this.lookAt(EntityAnchorArgument.Anchor.FEET,this.position().add(0,0,-100));
                inst.nextStage();
            }

        }else if (stage == 1){
            if (tick == 1) {

                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 2f,1f);

                for (int i = 0; i < 20; i++){
                    this.malkuthEarthStrikeStripe(MalkuthAttackType.getRandom(random));
                }

                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(2.5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),this.position(),60);

                MalkuthFloorEntity malkuthFloorEntity = new MalkuthFloorEntity(BossEntities.MALKUTH_FLOOR.get(), level());

                malkuthFloorEntity.setPos(this.spawnPosition.add(0,-1,0));

                level().addFreshEntity(malkuthFloorEntity);

                var combatants = this.getCombatants(true);
                for (var player : combatants) {
                    if (Math.abs(player.getY() - this.spawnPosition.y) <= 3) {
                        FDLibCalls.setServerPlayerSpeed((ServerPlayer) player, new Vec3(0, 2, 0));
                    }
                }
                for (Vec3 offset : PLATFORM_SPAWN_OFFSETS) {
                    Vec3 pos = this.spawnPosition.add(offset);
                    MalkuthPlatform malkuthPlatform = new MalkuthPlatform(BossEntities.MALKUTH_PLATFORM.get(), level());
                    malkuthPlatform.setPos(pos);
                    level().addFreshEntity(malkuthPlatform);
                }
                inst.nextStage();
            }
        }else if (stage == 2){

            if (tick == 0) {
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_TO_FLOAT)
                        .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_FLOAT).build())
                        .build());
            }

            if (tick > 4) {
                BossUtil.malkuthFloatParticles((ServerLevel) level(), this);
                currentlyFlyingTo = 0;
                this.noPhysics = true;
                this.setNoGravity(true);
                inst.nextStage();
            }
        }else if (stage == 3){

            BossUtil.malkuthFloatParticles((ServerLevel) level(), this);

            if (this.getTarget() != null) {
                this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getTarget().position());
            }

            this.noPhysics = true;
            this.setNoGravity(true);

            int fireballTickStart = 30;
            int fireballTickEnd = fireballTickStart + PLATFORM_SPAWN_OFFSETS.length;

            int fireballLaunchTick = fireballTickEnd + 20;

            int cycleTime = fireballLaunchTick + 5;

            int currentCycle = tick / cycleTime;

            int localTick = tick % cycleTime;

            //------------------------------------------------------------------CONTROL AMOUNT----------------------------------------------------------
            if (currentCycle > 6){
                inst.nextStage();
                return false;
            }

            if (currentlyFlyingTo == -1){
                currentlyFlyingTo = random.nextInt(FLY_TO_OFFSETS.length);
            }



            if (localTick == 0){
                int old = currentlyFlyingTo;
                while (currentlyFlyingTo == old){
                    currentlyFlyingTo = random.nextInt(FLY_TO_OFFSETS.length);
                }

                if (currentCycle == 0){
                    currentlyFlyingTo = 0;
                }

            }else if (localTick < fireballTickStart){
                Vec3 target = this.spawnPosition.add(FLY_TO_OFFSETS[currentlyFlyingTo]);
                this.moveToPos(target);

                int ticksToEnd = fireballTickStart - localTick;

                if (ticksToEnd == 7){
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SUMMON_AND_FIRE_FIREBALLS)
                            .important()
                            .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_FLOAT).build())
                            .build());
                }

            }else if (localTick < fireballTickEnd){

                int[] pattern = PLATFORM_ATTACK_PATTERN[currentCycle % PLATFORM_ATTACK_PATTERN.length];

                if (localTick == fireballTickStart){


                    for (int i = 0; i < PLATFORM_SPAWN_OFFSETS.length;i++){

                        int id = pattern[i];

                        MalkuthAttackType type = id == 0 ? MalkuthAttackType.FIRE : MalkuthAttackType.ICE;

                        Vec3 offset = PLATFORM_SPAWN_OFFSETS[i];

                        Vec3 centerpos = this.spawnPosition.add(offset);

                        Vector3f color = getMalkuthAttackPreparationParticleColor(type);

                        RectanglePreparationParticleOptions options = new RectanglePreparationParticleOptions(
                                new Vec3(0,0,-1), 5, 1.5f, 30, 5, 10, color.x,color.y,color.z,0.2f
                        );

                        int dir = offset.dot(new Vec3(1,0,0)) < 0 ? 1 : -1;

                        Vec3 direction = new Vec3(dir,0,0);

                        RectanglePreparationParticleOptions options2 = new RectanglePreparationParticleOptions(
                                direction, 5, 1.5f, 30, 5, 10, color.x,color.y,color.z,0.2f
                        );

                        FDLibCalls.sendParticles((ServerLevel) level(), options, centerpos.add(0,1.06f,2.5),60);
                        FDLibCalls.sendParticles((ServerLevel) level(), options2, centerpos.add(0,1.061f,0).add(direction.multiply(-2.5,0,0)),60);

                    }

                }

                this.setDeltaMovement(Vec3.ZERO);
                int currentFireball = localTick - fireballTickStart;

                int id = pattern[currentFireball];

                MalkuthAttackType type = id == 0 ? MalkuthAttackType.FIRE : MalkuthAttackType.ICE;


                Vec3 forward = this.getForward().multiply(1,0,1).normalize();

                Vec3 left = forward.yRot(FDMathUtil.FPI / 2);

                Vec3 platformPos = this.spawnPosition.add(PLATFORM_SPAWN_OFFSETS[currentFireball]);
                float p = (float) currentFireball / (PLATFORM_SPAWN_OFFSETS.length - 1);
                float angle = FDMathUtil.FPI * p;
                Vector3f spawnOffset = new Quaternionf(new AxisAngle4d(angle, forward.x,forward.y,forward.z)).transform((float)left.x,(float)left.y,(float)left.z,new Vector3f()).mul(5);
                Vec3 spawnPos = this.position().add(spawnOffset.x * 0.3f,spawnOffset.y * 0.3f + 2,spawnOffset.z * 0.3f);
                Vec3 gotoPos = spawnPos.add(spawnOffset.x,spawnOffset.y,spawnOffset.z);

                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.fireballsDamage);

                MalkuthFireball malkuthFireball = MalkuthFireball.summon(type, level(), spawnPos, gotoPos, platformPos.add(0,1.5,0), damage);

            }else if (localTick == fireballLaunchTick){
                level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_FIREBALL_LAUNCH.get(), SoundSource.HOSTILE, 3f, 1f);
                for (var fireball : this.level().getEntitiesOfClass(MalkuthFireball.class, new AABB(-20,-20,-20,20,20,20).move(this.position()))){
                    fireball.setMoveToTarget(12);
                }
            }


        } else if (stage == 4){

            if (tick == 0){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE)
                                .important()
                        .build());
            }

            if (this.moveToPos(spawnPosition)){
                this.noPhysics = false;
                this.setNoGravity(false);
                this.setDeltaMovement(Vec3.ZERO);
                this.teleportTo(spawnPosition.x,spawnPosition.y,spawnPosition.z);
                inst.nextStage();
            }else{
                BossUtil.malkuthFloatParticles((ServerLevel) level(), this);
            }

        } else if (stage == 5){
            if (tick == 5){
                for (var platform : level().getEntitiesOfClass(MalkuthPlatform.class,new AABB(-ENRAGE_RADIUS,-ENRAGE_RADIUS,-ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS).move(spawnPosition))){
                    platform.kill();
                }

                for (var platform : level().getEntitiesOfClass(MalkuthFloorEntity.class,new AABB(-ENRAGE_RADIUS,-ENRAGE_RADIUS,-ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS).move(spawnPosition))){
                    platform.kill();
                }

            } else if (tick >= 20) {
                return true;
            }
        }

        return false;
    }

    private void malkuthEarthStrikeStripe(MalkuthAttackType type){

        float rndRadius = 4 + FDEasings.easeOut(random.nextFloat()) * 3f;
        Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

        Vec3 dir = rnd.normalize();

        float startOffsetRand = 0.25f;
        Vec3 startOffset = dir.multiply(startOffsetRand,startOffsetRand,startOffsetRand);

        Vec3 stripePos = this.position().add(this.getForward().multiply(1,0,1).normalize()).add(startOffset);

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(type);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstPointOffset = 1 + random.nextFloat() * 1;
        float secondPointOffset = 3 + random.nextFloat() * 1;

        StripeParticleOptions stripeParticleOptions = StripeParticleOptions.builder()
                .startColor(fireColorStart)
                .endColor(fireColor)
                .lifetime(10)
                .lod(50)
                .scale(0.1f)
                .stripePercentLength(0.5f)
                .endOutPercent(0.2f)
                .startInPercent(0.2f)
                .offsets(new Vec3(0.01f,0,0),
                        dir.multiply(firstPointOffset,0,firstPointOffset).add(0,1.5,0),
                        dir.multiply(secondPointOffset,0,secondPointOffset).add(0,3.5,0),
                        rnd.add(0,4 + random.nextFloat() * 2,0))
                .build();

        ((ServerLevel)level()).sendParticles(stripeParticleOptions, stripePos.x,stripePos.y,stripePos.z,0,0,0,0,0);

    }

    private boolean moveToPos(Vec3 target){
        Vec3 thisPos = this.position();
        Vec3 between = target.subtract(thisPos);
        double len = between.length();
        if (len > 0.025){

            float p = (float) Math.clamp(len / 15f, 0, 1);

            float speed = (float) FDMathUtil.lerp(0.025, 2, p);

            this.setDeltaMovement(between.normalize().multiply(speed,speed,speed));

            return false;
        }else{
            this.setDeltaMovement(Vec3.ZERO);
            return true;
        }
    }

    private MalkuthAttackType chooseAttackTypeForTarget(LivingEntity target, float chanceToChooseWeakToType){
        if (target instanceof Player player){
            MalkuthAttackType weakTo = MalkuthWeaknessHandler.getWeakTo(player);
            if (random.nextFloat() < chanceToChooseWeakToType){
                return weakTo;
            }else{
                return MalkuthAttackType.getOpposite(weakTo);
            }
        }
        return MalkuthAttackType.getRandom(random);
    }

    private MalkuthAttackType earthquakeToSummon = MalkuthAttackType.FIRE;

    private boolean summonEarthquake(AttackInstance inst, int spawnYOffset){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            lookAtTarget = true;

            var target = this.getTarget();

            this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

            earthquakeToSummon = this.chooseAttackTypeForTarget(this.getTarget(), CHANCE_TO_CHOOSE_WEAK_TYPE);

            if (earthquakeToSummon.isFire()){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SINGLE_EARTHQUAKE_FIRE)
                                .important()
                                .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                        .build());
            }else{
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SINGLE_EARTHQUAKE_ICE)
                                .important()
                                .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                        .build());
            }
            inst.nextStage();
        }else if (stage == 1){

            if (tick == 8){


                Vec3 targetPos;

                if (this.getTarget() != null){
                    targetPos = this.getTarget().position();
                }else{
                    targetPos = this.position().add(this.getForward().multiply(1,0,1).normalize().multiply(10,10,10));
                }

                Vec3 direction = targetPos.subtract(this.position()).multiply(1.5f,0,1.5f);


                SlamParticlesPacket packet = new SlamParticlesPacket(
                        new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,0.5f,0),direction.normalize())
                                .maxAngle(FDMathUtil.FPI)
                                .maxSpeed(0.5f)
                                .collectRadius(2)
                                .maxParticleLifetime(30)
                                .count(40)
                                .maxVerticalSpeedEdges(0.15f)
                                .maxVerticalSpeedCenter(0.4f)
                );
                PacketDistributor.sendToPlayersTrackingEntity(this,packet);

                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 3f ,1f + random.nextFloat() * 0.2f);


                double dist = direction.length();

                int time = (int) Math.ceil(dist);

                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.impalingDoomDamage);

                MalkuthEarthquake malkuthEarthquake = MalkuthEarthquake.summon(level(),earthquakeToSummon, this.position().add(0, spawnYOffset, 0), direction, time, FDMathUtil.FPI / 8, damage);
            }else if (tick == 9){

                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(2.5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),this.position(),60);
            }else if (tick >= 20){
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
                return true;
            }
        }
        return false;
    }

    private boolean attachSwordsAttack(AttackInstance instance, boolean attach){
        if (attach){
            this.attachSwords();
            this.causeSwordChargeParticles(MalkuthAttackType.FIRE);
            this.causeSwordChargeParticles(MalkuthAttackType.ICE);
        }else{
            this.deattachSwords();
            this.causeSwordChargeParticles(MalkuthAttackType.FIRE);
            this.causeSwordChargeParticles(MalkuthAttackType.ICE);
        }
        return true;
    }

    private MalkuthAttackType sideRocksCurrentType = MalkuthAttackType.FIRE;

    private boolean summonAndThrowSideRocks(AttackInstance inst){


        int stage = inst.stage;
        int lstage = stage % 3;
        int tick = inst.tick;

        if (lstage == 0){
            this.deattachSwords();
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_SUMMON_THROW_SIDE_STONES)
                            .important()
                            .setSpeed(0.8f)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
            inst.nextStage();
        }else if (lstage == 1){

            if (tick == 10){

                int count = 8;
                float distForOne = 3.4f;

                Vec3 startPos = this.position().add(0,0.1,-1);

                int h = random.nextInt(2);

                float damage = BossUtil.transformDamage(level(),BossConfigs.BOSS_CONFIG.get().malkuthConfig.sideRocksDamage);

                MalkuthAttackType currentType = this.sideRocksCurrentType;

                for (int i = 0; i < count; i++){

                    int k = h % 2 == 0 ? 1 : -1;

                    Vec3 clipdir = new Vec3(k * 30, 0,0);
                    Vec3 cliplastpos = startPos.add(clipdir);

                    ClipContext clipContext = new ClipContext(startPos, cliplastpos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

                    var ctx = level().clip(clipContext);

                    Vec3 location = ctx.getLocation();

                    double x = location.x - k;
                    double r = startPos.x - x;
                    double y = this.getY() - 1;
                    double z = startPos.z;

                    Vec3 summonPos = new Vec3(x,y,z);
                    Vec3 moveToPos = new Vec3(x + r * 2,y,z);

                    ProjectileMovementPath movementPath = new ProjectileMovementPath(summonPos, 10, false)
                            .addPos(moveToPos);

                    MalkuthBoulderEntity malkuthBoulderEntity = MalkuthBoulderEntity.summon(level(), summonPos, 10,3, movementPath, currentType, damage);


                    Vector3f col = getMalkuthAttackPreparationParticleColor(currentType);

                    RectanglePreparationParticleOptions rectanglePreparationParticleOptions = new RectanglePreparationParticleOptions(new Vec3(-k,0,0), 56, distForOne/2, 30, 10,10,col.x,col.y,col.z,0.25f);
                    Vec3 ppos = new Vec3(this.getX() + k * 28,y + 0.01f,z);

                    FDLibCalls.sendParticles(((ServerLevel) level()),rectanglePreparationParticleOptions, ppos, 60);


                    if (currentType.isFire()){
                        currentType = MalkuthAttackType.ICE;
                    }else{
                        currentType = MalkuthAttackType.FIRE;
                    }



                    startPos = startPos.add(0,0,-distForOne);
                    h++;
                }

                if (this.sideRocksCurrentType.isFire()){
                    this.sideRocksCurrentType = MalkuthAttackType.ICE;
                }else{
                    this.sideRocksCurrentType = MalkuthAttackType.FIRE;
                }

            }else if (tick > 40){

                var l = this.level().getEntitiesOfClass(MalkuthBoulderEntity.class, new AABB(-30,-30,-30,30,30,30).move(this.position()));

                for (var b : l){
                    b.setShouldMoveToTarget(true);
                }

                inst.nextStage();
            }

        }else if (lstage == 2){
            return true;
        }
        return false;
    }


    private ProjectileMovementPath jumpBackOnSpawnPath;

    private MalkuthAttackType jumpBackOnSpawnCrushType = MalkuthAttackType.FIRE;

    private boolean jumpBackOnSpawn(AttackInstance attackInstance, boolean crush){

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        ProjectileMovementPath path = jumpBackOnSpawnPath;



        if (jumpBackOnSpawnPath == null){
            Vec3 thisPos = this.position();
            Vec3 target = this.spawnPosition;

            Vec3 between = target.subtract(thisPos);

            Vec3 pos1 = thisPos.add(between.multiply(0.25,0.25,0.25).add(0,5,0));
            Vec3 pos2 = thisPos.add(between.multiply(0.5,0.5,0.5).add(0,8,0));
            Vec3 pos3 = thisPos.add(between.multiply(0.75,0.75,0.75).add(0,5,0));

            path = new ProjectileMovementPath(20, false)
                    .addPos(thisPos)
                    .addPos(pos1)
                    .addPos(pos2)
                    .addPos(pos3)
                    .addPos(target);

            jumpBackOnSpawnPath = path;

            attackInstance.stage = 0;
            attackInstance.nextStage();
        }

        int earthquakesCount = 5;

        float angle = FDMathUtil.FPI / earthquakesCount;

        float radius = 28;

        if (stage == 1) {
            Vec3 lastpos = this.jumpBackOnSpawnPath.getPositions().getLast();
            if (crush){
                if (tick == 10){
                    this.jumpEndEarthquakePrepareParticles(lastpos.add(0,-0.99,0),earthquakesCount,angle,radius);
                }
            }
            if (tick == 0){
                jumpBackOnSpawnCrushType = MalkuthAttackType.getRandom(random);
                if (!crush) {
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_LAND)
                            .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
                }else{
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_CRUSH)
                            .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
                }
//                this.lookAt(EntityAnchorArgument.Anchor.FEET, lastpos);
            }else if (tick > 5) {

                this.noPhysics = true;
                this.setNoGravity(true);
                this.getLookControl().setLookAt(lastpos);
                lookAtTarget = false;

                if (path.isFinished()) {
                    this.setDeltaMovement(Vec3.ZERO);
                    this.teleportTo(lastpos.x, lastpos.y, lastpos.z);
                    this.noPhysics = false;
                    this.setNoGravity(false);
                    lookAtTarget = true;
                    attackInstance.nextStage();
                } else {
                    path.tick(this);
                }
            }
        }else if (stage == 2){

            if (tick == 3 && crush){
                this.jumpEarthquake(this.position().add(0,-0.99,0), earthquakesCount, angle, radius);
                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 2f, 0.8f);
                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.ROCK_IMPACT.get(), SoundSource.HOSTILE, 2f, 0.8f);
            }

            this.noPhysics = false;
            this.setNoGravity(false);
            lookAtTarget = true;
            int waitTime = crush ? 30 : 10;


            if (tick == 0 && this.getTarget() != null){
                this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getTarget().position());
            }

            if (tick >= waitTime){
                this.jumpBackOnSpawnPath = null;
                return true;
            }
        }

        return false;
    }

    private void jumpEndEarthquakePrepareParticles(Vec3 lastpos, int earthquakesCount, float angle, float radius){

        MalkuthAttackType localType = jumpBackOnSpawnCrushType;

        for (int i = 0; i < earthquakesCount; i++) {

            float currentAngle = i * angle + angle / 2;

            Vector3f col = this.getMalkuthAttackPreparationParticleColor(localType);

            Vec3 v = new Vec3(1,0,0).yRot(currentAngle);

            ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(v, radius, angle/2, 20,5,10,col.x,col.y,col.z,0.25f);
            ((ServerLevel)level()).sendParticles(options, lastpos.x,lastpos.y,lastpos.z,1,0,0,0,0);

            if (localType.isFire()){
                localType = MalkuthAttackType.ICE;
            }else{
                localType = MalkuthAttackType.FIRE;
            }

        }

    }

    private void jumpEarthquake(Vec3 lastpos, int earthquakesCount, float angle, float radius){

        MalkuthAttackType localType = jumpBackOnSpawnCrushType;

        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.jumpBackOnSpawnCrushDamage);

        for (int i = 0; i < earthquakesCount; i++) {

            float currentAngle = i * angle + angle / 2;

            Vec3 v = new Vec3(radius,0,0).yRot(currentAngle);

            MalkuthEarthquake earthquake = MalkuthEarthquake.summon(level(), localType, lastpos, v, 40, angle, damage);
            if (localType.isFire()){
                localType = MalkuthAttackType.ICE;
            }else{
                localType = MalkuthAttackType.FIRE;
            }
        }

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(5)
                .amplitude(5f)
                .inTime(0)
                .stayTime(0)
                .outTime(5)
                .build(),lastpos,120);

    }



    private MalkuthAttackType slashAttackType = MalkuthAttackType.FIRE;

    private Animation getSlashAttackAnimation(MalkuthAttackType malkuthAttackType){
        if (malkuthAttackType.isFire()){
            return BossAnims.MALKUTH_SLASH_FIRE.get();
        }else{
            return BossAnims.MALKUTH_SLASH_ICE.get();
        }
    }

    private boolean aerialSlashAttack(AttackInstance inst){


        int stage = inst.stage;

        int tick = inst.tick;

        int localStage = stage % 3;


        if (localStage == 0){
            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);

            this.slashAttackType = this.chooseAttackTypeForTarget(this.getTarget(), CHANCE_TO_CHOOSE_WEAK_TYPE);

            Animation animation = this.getSlashAttackAnimation(this.slashAttackType);

            AnimationTicker ticker = AnimationTicker.builder(animation)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                    .important()
                    .build();

            this.getAnimationSystem().startAnimation(MAIN_LAYER, ticker);
            inst.nextStage();
        }else if (localStage == 1){

            if (tick == 24){

                Vec3 spawnPos = this.position().add(0,1.5,0);
                Vec3 targetPos;
                float speedMod = 1;
                if (this.getTarget() != null){
                    LivingEntity t = this.getTarget();
                    if (t.hasEffect(MobEffects.MOVEMENT_SPEED)){
                        speedMod = t.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;
                    }
                    targetPos = t.position().add(0,t.getBbHeight()/2,0);
                }else{
                    targetPos = this.position().add(this.getForward().multiply(100,0,100));
                }


                float spawnForwardOffset = -speedMod;

                spawnPos = spawnPos.add(this.getForward().multiply(1,0,1).multiply(spawnForwardOffset,spawnForwardOffset,spawnForwardOffset));


                Vec3 speedv = targetPos.subtract(spawnPos);
                double speed = Math.min(Math.max(2,speedv.length() * 0.15f) * speedMod,5);
                speedv = speedv.normalize().multiply(speed,speed,speed);

                float rotation = this.slashAttackType.isFire() ? 25 : -25;

                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.slashAttackDamage);


                MalkuthSlashProjectile malkuthSlashProjectile = MalkuthSlashProjectile.summon(level(),spawnPos,speedv,this.slashAttackType, damage, 2.2f, rotation,0);

            }else if (tick == 20){
                this.playSlashSound();
            } else if (tick >= 28){
                inst.nextStage();
            }else if (tick == 8) {
                this.causeSwordChargeParticles(slashAttackType);
                for (int i = 0; i < 6; i++) {
                    this.doSwordChargeStripe(slashAttackType, 3f, 0.5f, 0.25f, false);
                }
            }

        }else if (localStage == 2){
            if (this.getTarget() == null){
                return false;
            }
//            if (stage >= 23){
//                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
            return true;
//            }else{
//                inst.nextStage();
//            }
        }


        return false;
    }

    private ProjectileMovementPath jumpCrushAttackMovementPath;

    private boolean jumpCrushAttack(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_CRUSH_ATTACK_FULL)
                            .setLoopMode(Animation.LoopMode.ONCE)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                    .build());
            if (tick == 5){
                this.jumpCrushAttackMovementPath = this.createJumpCrushAttackMovementPath(15);
                this.lookAt(EntityAnchorArgument.Anchor.FEET, this.jumpCrushAttackMovementPath.getPositions().getLast());

                inst.nextStage();
            }
        }else if (stage == 1){

            if (tick == 1){
                this.doJumpStartParticles(-2f);
            }

            this.setNoGravity(true);
            this.noPhysics = true;
            this.jumpCrushAttackMovementPath.tick(this);
            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            if (this.jumpCrushAttackMovementPath.isFinished()){

                Vec3 lastPos = this.jumpCrushAttackMovementPath.getPositions().getLast();

                Vec3 between = lastPos.subtract(this.position());

                double dist = between.length();

                if (dist < 0.5) {

                    inst.nextStage();
                }else{
                    this.setDeltaMovement(between.multiply(0.5,0.5,0.5));
                }
            }else{
                if (tick < 20 && this.getTarget() != null){

                    this.jumpCrushAttackMovementPath.getPositions().set(this.jumpCrushAttackMovementPath.getPositions().size() - 1, this.getTarget().position());

                }
            }
        }else if (stage == 2){

            this.setNoGravity(false);
            this.noPhysics = false;

            if (tick == 5){

                Vec3 basePos = this.position().add(this.getForward().multiply(1,0,1).normalize());
                Vec3 actualPos = this.findGroundPosForCrush(basePos);

                if (this.isBelowHalfHP()){
                    this.summonRepairCrystal(actualPos);
                }

                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.earthshatterDamage);

                MalkuthCrushAttack malkuthCrushAttack = MalkuthCrushAttack.summon(level(), actualPos, damage);
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),malkuthCrushAttack.position(),120);
                level().playSound(null, malkuthCrushAttack.getX(),malkuthCrushAttack.getY(),malkuthCrushAttack.getZ(), BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.HOSTILE, 2f, 0.8f);
                level().playSound(null, malkuthCrushAttack.getX(),malkuthCrushAttack.getY(),malkuthCrushAttack.getZ(), BossSounds.ROCK_IMPACT.get(), SoundSource.HOSTILE, 2f, 0.8f);
            }else if (tick >= 10){
                return true;
            }

        }

        return false;
    }

    private void summonRepairCrystal(Vec3 pos){

        var crystals = BossTargetFinder.getEntitiesInCylinder(MalkuthRepairCrystal.class, level(), this.spawnPosition.add(0,-2,0), 20, 30);

        var playerCannons = this.getPlayerCannons(true);

        int spawnedCrystals = crystals.size();

        int cannonsAmount = playerCannons.size();

        if (crystals.size() == playerCannons.size()) return;

        int fireCrystals = (int) crystals.stream().filter(c->c.getCrystalType().isFire()).count();
        int iceCrystals = spawnedCrystals - fireCrystals;

        int fireCannons = (int) playerCannons.stream().filter(cannon->cannon.getCannonType().isFire()).count();
        int iceCannons = cannonsAmount - fireCannons;

        MalkuthAttackType finalType;

        if (fireCrystals >= fireCannons && iceCrystals >= iceCannons) return;

        if (fireCrystals >= fireCannons){
            finalType = MalkuthAttackType.ICE;
        }else if (iceCrystals >= iceCannons){
            finalType = MalkuthAttackType.FIRE;
        }else{
            finalType = MalkuthAttackType.getRandom(random);
        }

        MalkuthRepairCrystal malkuthRepairCrystal = new MalkuthRepairCrystal(BossEntities.MALKUTH_REPAIR_CRYSTAL.get(), level());

        malkuthRepairCrystal.getEntityData().set(MalkuthRepairCrystal.CRYSTAL_TYPE, finalType);

        malkuthRepairCrystal.setPos(pos);

        level().addFreshEntity(malkuthRepairCrystal);
    }

    private Vec3 findGroundPosForCrush(Vec3 crushBasePos){

        Vec3 end = crushBasePos.add(0,-5,0);

        ClipContext clipContext = new ClipContext(crushBasePos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());

        var res = level().clip(clipContext);

        if (res.getType() == HitResult.Type.BLOCK){
            return res.getLocation();
        }
        return crushBasePos;
    }

    private ProjectileMovementPath createJumpCrushAttackMovementPath(int flyTime){

        Vec3 target;

        if (this.getTarget() != null){
            target = this.getTarget().position();
        }else{
            target = this.position().add(this.getForward().multiply(1,0,1).normalize().multiply(10,0,10));
        }

        Vec3 begin = this.position();

        Vec3 between = target.subtract(begin);

        Vec3 pos1 = this.position().add(between.multiply(0.25f,0.25f,0.25f)).add(0,9,0);
        Vec3 pos2 = this.position().add(between.multiply(0.33f,0.33f,0.33f)).add(0,13,0);
        Vec3 pos3 = this.position().add(between.multiply(0.5f,0.5f,0.5f)).add(0,9,0);

        ProjectileMovementPath path = new ProjectileMovementPath(begin, flyTime, false)
                .addPos(pos1)
                .addPos(pos2)
                .addPos(pos3)
                .addPos(target)
                ;

        return path;
    }



    private boolean checkCanPunch(){
        AABB box = new AABB(-ENRAGE_RADIUS,4,-ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS).move(this.position());

        List<Player> players = level().getEntitiesOfClass(Player.class, box);

        return !players.isEmpty();
    }

    private boolean pullAndPunch(AttackInstance inst){


        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){

            if (this.getTarget() != null){
                this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getTarget().position());
            }


            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_PULL_AND_PUNCH)
                            .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build())
                    .build());
            inst.nextStage();
        }else if (stage == 1){

            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
            if (tick == 6){
                this.causeSwordChargeParticles(MalkuthAttackType.ICE);
                this.deattachIceSword();
            }else if (tick == 10){

                if (this.getTarget() == null){
                    this.causeSwordChargeParticles(MalkuthAttackType.ICE);
                    this.attachIceSword();
                    this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                    return true;
                }

                AABB box = new AABB(-ENRAGE_RADIUS,-2,-ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS).move(this.position());

                for (Player target : level().getEntitiesOfClass(Player.class, box)) {
                    Vec3 pullToPos = this.position().add(0, 2.5, 0).add(this.getForward().multiply(1, 0, 1).normalize().multiply(2, 2, 2));
                    MalkuthChainEntity malkuthChainEntity = MalkuthChainEntity.summon(level(), this, MalkuthAttackType.ICE, pullToPos, target, 10, 10);
                }
                inst.nextStage();
            }
        }else if (stage == 2){

            if (tick > 0 && tick < 11){
                level().playSound(null,  this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 2.5f, 0.85f);
            } else if (tick < 21){
                level().playSound(null,  this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_CHAIN_PULL.get(), SoundSource.HOSTILE, 2.5f, 0.9f);
            }

            if (tick == 10){
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            }else if (tick == 21){
                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_PUNCH.get(), SoundSource.HOSTILE, 2f, 1f);
            } else if (tick == 25){
                level().playSound(null, this.getX(),this.getY(),this.getZ(), BossSounds.CHESED_CRYSTAL_HIT.get(), SoundSource.HOSTILE, 2f, 0.5f);

                Vector3f pos = this.getModelPartPosition(this, getMalkuthSwordPlaceBone(MalkuthAttackType.ICE),SERVER_MODEL);

                Vector3f col = getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);

                FDLibCalls.sendParticles((ServerLevel) level(), BallParticleOptions.builder()
                        .size(5f)
                        .scalingOptions(0, 0, 2)
                        .color(col.x,col.y,col.z)
                                .brightness(2)
                        .build(), this.position().add(pos.x,pos.y,pos.z), 60);

                float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.pullAndPunchDamage);


                for (var chain : this.level().getEntitiesOfClass(MalkuthChainEntity.class, this.getBoundingBox().inflate(20,20,20))){
                    var passengers = new ArrayList<>(chain.getPassengers());
                    chain.ejectPassengers();
                    chain.setRemoved(RemovalReason.DISCARDED);

                    Vec3 forward = this.getForward().multiply(1,0,1).normalize();

                    for (var e : passengers){
                        if (e instanceof LivingEntity livingEntity){
                            livingEntity.hurt(BossDamageSources.MALKUTH_CHAINPUNCH_SOURCE,damage);

                            Vec3 speed = forward.multiply(6,6,6).add(0,-1,0);

                            if (livingEntity instanceof ServerPlayer player){
                                FDLibCalls.setServerPlayerSpeed(player, speed);

                                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,40,0,true,false));
                                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40,0,true,false));
                                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,0,true,false));

                                PacketDistributor.sendToPlayer(player, new DefaultShakePacket(FDShakeData.builder()
                                        .stayTime(0)
                                        .inTime(0)
                                        .outTime(35)
                                        .amplitude(0.2f)
                                        .build()));


                            }else{
                                livingEntity.setDeltaMovement(speed);
                            }

                        }
                    }

                }
            }else if (tick == 35){
                this.causeSwordChargeParticles(MalkuthAttackType.ICE);
                this.attachIceSword();
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
            } else if (tick >= 40){
                return true;
            }
        }


        return false;
    }

    private ProjectileMovementPath jumpOnWallPath = null;

    private boolean jumpAndCommandCannons(AttackInstance attackInstance){

        this.allowedToBeDamaged = true;
        this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

        this.lookAtTarget = false;


        this.getLookControl().setLookAt(this.position().add(0,0,-100));

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        if (stage == 0){

            for (var cannon : BossTargetFinder.getEntitiesInCylinder(MalkuthCannonEntity.class, level(), this.spawnPosition.add(0,-5,0),20, 35)){
                cannon.setBroken(false);
            }

            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_LAND)
                            .important()
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
            this.jumpOnWallPath = this.makeJumpOnWallPath(23,false);
            attackInstance.nextStage();
        }else if (stage == 1){

            if (this.jumpOnWallPath == null){
                this.jumpOnWallPath = this.makeJumpOnWallPath(23,false);
            }

            if (tick >= 6) {
                if (tick == 6){
                    this.doJumpStartParticles(0);
                }
                this.jumpOnWallPath.tick(this);
                this.noPhysics = true;
                this.setNoGravity(true);
            }

            if (this.jumpOnWallPath.isFinished()){
                this.lookAt(EntityAnchorArgument.Anchor.EYES, this.position().add(0,0,-100));
                Vec3 last = this.jumpOnWallPath.getPositions().getLast();
                this.setNoGravity(false);
                this.noPhysics = false;
                this.teleportTo(last.x,last.y,last.z);
                attackInstance.nextStage();
                this.getAnimationSystem().startAnimation(MAIN_LAYER,AnimationTicker.builder(BossAnims.MALKUTH_SWORD_FORWARD)
                                .setToNullTransitionTime(0)
                        .build());
            }
        }else if (stage == 2){
            int start = 6;
            int t = tick - start;

            if (t >= 160) {
                attackInstance.nextStage();
            }

            if (t % 40 == 0){
                this.shootCannons();
            }

        }

        else if (stage == 3){
            if (tick > 60){
                allowedToBeDamaged = true;
                lookAtTarget = true;
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
                return true;
            }
            return false;
        }


        return false;
    }

    private void shootCannons(){
        this.shootCannons(5);
    }

    private void shootCannons(int amountPerPoint){
        var cannons = this.getMalkuthCannons();
        if (cannons.isEmpty()){
            //summon them

            return;
        }
        var combatants = this.getCombatants(true);

        List<Vec3> positions = new ArrayList<>();

        for (var combatant : combatants){
            positions.add(combatant.position());
        }


        Vec3 start = this.spawnPosition.add(0,-1,0);

        Vec3 v = new Vec3(0,0,-ENRAGE_RADIUS / 2 - 2);



        float rndRadius = ENRAGE_RADIUS / 2;
        for (int i = 0; i < 3; i++){
            Vec3 p1 = start.add(v.yRot(i * FDMathUtil.FPI / 8));
            Vec3 p2 = start.add(v.yRot(-i * FDMathUtil.FPI / 8));
            for (int k = 0; k < amountPerPoint;k++){
                Vec3 rndPos = p1.add(
                        random.nextFloat() * 2 * rndRadius - rndRadius,
                        0,
                        random.nextFloat() * 2 * rndRadius - rndRadius
                );

                Vec3 rndPos2 = p2.add(
                        random.nextFloat() * 2 * rndRadius - rndRadius,
                        0,
                        random.nextFloat() * 2 * rndRadius - rndRadius
                );
                positions.add(rndPos);
                if (i != 0){
                    positions.add(rndPos2);
                }
            }
        }


        int baseAmountPerCannon = positions.size() / cannons.size();
        int spreadAmount = baseAmountPerCannon * cannons.size();


        HashMap<MalkuthCannonEntity, List<Vec3>> cannonTargets = new HashMap<>();

        int currentCannon = 0;
        while (!positions.isEmpty()){
            Vec3 pos = positions.removeFirst();
            MalkuthCannonEntity cannon = cannons.get(currentCannon);
            cannonTargets.computeIfAbsent(cannon, c->new ArrayList<>()).add(pos);
            currentCannon = (currentCannon + 1) % cannons.size();
            spreadAmount--;
            if (spreadAmount <= 0){
                currentCannon = random.nextInt(cannons.size());
            }
        }

        float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.cannonDamage);

        for (var entry : cannonTargets.entrySet()){
            var cannon = entry.getKey();
            cannon.shoot(entry.getValue(), damage);
        }
    }

    private ProjectileMovementPath makeJumpOnWallPath(int time, boolean reversed){

        Vec3 start = this.position();

        Vec3 offset = WALL_OFFSET;

        Vec3 end = this.spawnPosition.add(offset);

        double yDiff = end.y - start.y;

        Vec3 hoffset = end.subtract(start).multiply(1,0,1);
        Vec3 v1 = start.add(hoffset.multiply(0.25,0.25,0.25).add(0,yDiff + 5,0));
        Vec3 v2 = start.add(hoffset.multiply(0.5,0.5,0.5).add(0,yDiff + 10,0));
        Vec3 v3 = start.add(hoffset.multiply(0.75,0.75,0.75).add(0,yDiff + 5,0));

        ProjectileMovementPath path = new ProjectileMovementPath(time,false);
        if (!reversed){
            path.addPos(start);
            path.addPos(v1);
            path.addPos(v2);
            path.addPos(v3);
            path.addPos(end);
        }else{
            path.addPos(end);
            path.addPos(v3);
            path.addPos(v2);
            path.addPos(v1);
            path.addPos(start);
        }

        return path;
    }

    protected void doJumpStartParticles(float verticalOffset){
        SlamParticlesPacket packet = new SlamParticlesPacket(
                new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,verticalOffset,0),new Vec3(1,0,0))
                        .maxAngle(FDMathUtil.FPI * 2)
                        .maxSpeed(0.3f)
                        .collectRadius(2)
                        .maxParticleLifetime(30)
                        .count(20)
                        .maxVerticalSpeedEdges(0.15f)
                        .maxVerticalSpeedCenter(0.15f)
        );
        PacketDistributor.sendToPlayersTrackingEntity(this,packet);
    }

    private void hideRepairCrystals(boolean hide){
        for (var crystal : BossTargetFinder.getEntitiesInCylinder(MalkuthRepairCrystal.class, level(), this.spawnPosition.add(0,-2,0),10,40)){
            crystal.setHidden(hide);
        }
    }

    private MalkuthAttackType currentStartCarouselSlash = MalkuthAttackType.FIRE;

    private boolean carouselSlashesAttack(AttackInstance attackInstance){

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        int preparationTime = 20;

        float radius = 30;

        Vec3 startVec = new Vec3(1,0,0);

        int slashesAmount = 10;

        this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

        if (tick == 0){

            this.hideRepairCrystals(true);

            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_CAROUSEL_SLASH_1)
                            .important()
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());

            float angle = FDMathUtil.FPI / slashesAmount;

            MalkuthAttackType localCarouselSlash = this.currentStartCarouselSlash;

            for (int i = 0; i < slashesAmount;i++){

                Vec3 direction = startVec.yRot(i * angle + angle / 2);


                Vector3f col = this.getMalkuthAttackPreparationParticleColor(localCarouselSlash);

                ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(direction, radius, angle/2, preparationTime, 10, 10, col.x,col.y,col.z,0.25f);

                ((ServerLevel)level()).sendParticles(options, this.getX(),this.getY() - 0.99, this.getZ(),1, 0, 0,0,0);

                if (localCarouselSlash.isFire()){
                    localCarouselSlash = MalkuthAttackType.ICE;
                }else{
                    localCarouselSlash = MalkuthAttackType.FIRE;
                }
            }


        } else if (tick == preparationTime){


            float angle = FDMathUtil.FPI / slashesAmount;

            float maxSlashSize = (float) Math.sqrt(2 * radius * radius * (1 - (float) Math.cos(angle))) / 2;

            float slashSpeed = 2.5f;

            int reachDestinationTime = Math.round(radius / slashSpeed);

            float verticalSpeed = -1f / reachDestinationTime;

            MalkuthAttackType localCarouselSlash = this.currentStartCarouselSlash;

            float damage = BossUtil.transformDamage(level(), BossConfigs.BOSS_CONFIG.get().malkuthConfig.arcSlashesDamage);

            this.playSlashSound();

            for (int i = 0; i < slashesAmount;i++){

                Vec3 direction = startVec.yRot(i * angle + angle / 2);

                Vec3 speed = direction.multiply(slashSpeed,slashSpeed,slashSpeed);
                speed = speed.add(0,verticalSpeed * 0.15f,0);


                MalkuthSlashProjectile.summon(level(),this.position().add(0,0.25,0), speed, localCarouselSlash, damage, maxSlashSize + 2f, 0, reachDestinationTime);

                if (localCarouselSlash.isFire()){
                    localCarouselSlash = MalkuthAttackType.ICE;
                }else{
                    localCarouselSlash = MalkuthAttackType.FIRE;
                }

            }

            for (var entity : BossTargetFinder.getEntitiesInCylinder(LivingEntity.class, level(), this.position().add(0,-2,0),5,2)){

                if (entity instanceof MalkuthBossBuddy) continue;

                MalkuthAttackType malkuthAttackType;
                if (entity instanceof Player player){
                    malkuthAttackType = MalkuthWeaknessHandler.getWeakTo(player);
                }else{
                    malkuthAttackType = MalkuthAttackType.getRandom(random);
                }

                entity.hurt(new MalkuthDamageSource(BossDamageSources.MALKUTH_SLASHES_SOURCE, malkuthAttackType, MalkuthWeaknessHandler.MAX / 3), damage);

            }

            if (currentStartCarouselSlash.isFire()){
                currentStartCarouselSlash = MalkuthAttackType.ICE;
            }else{
                currentStartCarouselSlash = MalkuthAttackType.FIRE;
            }

        }else if (tick >= preparationTime + 20){
            this.hideRepairCrystals(false);
            return true;
        }

        return false;
    }

    private void playSlashSound(){
        level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.MALKUTH_SLASH.get(), SoundSource.HOSTILE, 2f, 1f + random.nextFloat() * 0.5f);
    }

    public static Vector3f getAndRandomizeColor(MalkuthAttackType malkuthAttackType, RandomSource random){

        Vector3f color = getMalkuthAttackPreparationParticleColor(malkuthAttackType);

        color.x = Math.clamp(color.x + random.nextFloat() * 0.2f, 0, 1);
        color.y = Math.clamp(color.y + random.nextFloat() * 0.2f, 0, 1);
        color.z = Math.clamp(color.z + random.nextFloat() * 0.2f, 0, 1);

        return color;
    }

    public static Vector3f getMalkuthAttackPreparationParticleColor(MalkuthAttackType attackType){
        float r;
        float g;
        float b;

        if (attackType.isFire()){
            r = 1f;
            g = 0.4f;
            b = 0.1f;
        }else{
            r = 0.1f;
            g = 0.8f;
            b = 1f;
        }

        return new Vector3f(r,g,b);
    }

    @SerializableField
    private MalkuthAttackType giantSwordUltimateStartAttackType = MalkuthAttackType.FIRE;

    public boolean giantSwordUltimate(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0) {

            AABB firstBox = new AABB(0,-2,-29,29,10,0).move(this.spawnPosition);
            AABB secondBox = new AABB(-29,-2,-29,0,10,0).move(this.spawnPosition);

            var players = level().getEntitiesOfClass(Player.class, firstBox, player -> {
                return true;
            });
            if (players.isEmpty()){
                players = level().getEntitiesOfClass(Player.class, secondBox, player -> {
                    return true;
                });
                if (!players.isEmpty()){
                    Player firstPlayer = players.getFirst();
                    MalkuthAttackType weakTo = MalkuthWeaknessHandler.getWeakTo(firstPlayer);
                    giantSwordUltimateStartAttackType = MalkuthAttackType.getOpposite(weakTo);
                }
            }else{
                Player firstPlayer = players.getFirst();
                MalkuthAttackType weakTo = MalkuthWeaknessHandler.getWeakTo(firstPlayer);
                giantSwordUltimateStartAttackType = weakTo;
            }




            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_GIANT_SWORD_ATTACK)
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .build());

            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

            this.lookAtTarget = false;

            this.getLookControl().setLookAt(new Vec3(0,0,-100).add(this.position()));

            inst.nextStage();

        }else if (stage == 1){


            int swordSpawnTick = 12;

            int particlesStart = swordSpawnTick - 3;
            int particlesEnd = swordSpawnTick + 40;

            if (tick >= particlesStart && tick < particlesEnd && tick % 3 == 0){
                BossUtil.malkuthSwordChargeParticles((ServerLevel) level(), MalkuthAttackType.FIRE, this, 60);
                BossUtil.malkuthSwordChargeParticles((ServerLevel) level(), MalkuthAttackType.ICE, this, 60);
                float sizeModifier = (float) (tick - particlesStart) / (particlesEnd - particlesStart);
                this.doSwordChargeStripe(MalkuthAttackType.FIRE,1.75f, sizeModifier);
                this.doSwordChargeStripe(MalkuthAttackType.ICE,1.75f, sizeModifier);
            }

            if (tick < swordSpawnTick + MalkuthGiantSwordSlash.TIME_TO_RISE && tick % 25 == 0){
                this.shootCannons(3);
            }

            if (tick == swordSpawnTick - 5){

                this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(0,0,-100).add(this.position()));

                Vector3f red = MalkuthEntity.getMalkuthAttackPreparationParticleColor(giantSwordUltimateStartAttackType);
                Vector3f blue = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.getOpposite(giantSwordUltimateStartAttackType));

                RectanglePreparationParticleOptions rectanglePreparationParticleOptions1 =
                        new RectanglePreparationParticleOptions(new Vec3(0,0,-1),30, 14,60,20,10, red.x,red.y,red.z, 0.25f);

                RectanglePreparationParticleOptions rectanglePreparationParticleOptions2 =
                        new RectanglePreparationParticleOptions(new Vec3(0,0,-1),30, 14,60,20,10, blue.x,blue.y,blue.z, 0.25f);

                Vec3 pos1 = new Vec3(14,-0.99,0.5).add(this.position());
                Vec3 pos2 = new Vec3(-14,-0.99,0.5).add(this.position());

                FDLibCalls.sendParticles((ServerLevel) level(), rectanglePreparationParticleOptions1, pos1,100);
                FDLibCalls.sendParticles((ServerLevel) level(), rectanglePreparationParticleOptions2, pos2,100);


            } else if (tick == swordSpawnTick){



                Vec3 offs1 = new Vec3(10, -1, 11);
                Vec3 offs2 = new Vec3(-10, -1, 11);

                Vec3 pos1 = this.position().add(offs1);
                Vec3 pos2 = this.position().add(offs2);

                var slash1 = MalkuthGiantSwordSlash.summon(level(), pos1, new Vec3(0,0,-1), giantSwordUltimateStartAttackType, 0);
                slash1.setDoDamage(false);


                var slash2 = MalkuthGiantSwordSlash.summon(level(), pos2, new Vec3(0,0,-1), MalkuthAttackType.getOpposite(giantSwordUltimateStartAttackType), 0);
                slash2.setDoDamage(false);

            }else if (tick == MalkuthGiantSwordSlash.TIME_TO_HIT + MalkuthGiantSwordSlash.TIME_TO_RISE + swordSpawnTick - 2){
                ImpactFrame base = new ImpactFrame(0.5f, 0.1f, 1, false);
                FDLibCalls.sendImpactFrames((ServerLevel) level(), this.position(), 30,
                        base,
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true)
                );


            }else if (tick == MalkuthGiantSwordSlash.TIME_TO_HIT + MalkuthGiantSwordSlash.TIME_TO_RISE + swordSpawnTick){


                for (var entity : BossTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), this.spawnPosition.add(0,-5,0), 50,50)){
                    PacketDistributor.sendToPlayer(entity, new PlaySoundInEarsPacket(BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(),1f,1));
                    PacketDistributor.sendToPlayer(entity, new PlaySoundInEarsPacket(BossSounds.MALKUTH_SWORD_ULTIMATE_IMPACT.get(),1f,1));
                }
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                                .amplitude(4)
                                .outTime(60)
                                .frequency(100)
                        .build(), this.spawnPosition, 50);

                AABB firstBox = new AABB(0,-2,-29,29,10,0).move(this.spawnPosition);
                AABB secondBox = new AABB(-29,-2,-29,0,10,0).move(this.spawnPosition);

                DamageSource damageSource = BossDamageSources.MALKUTH_TSARS_WRATH_SOURCE;

                for (var entity : this.level().getEntitiesOfClass(LivingEntity.class, firstBox, entity -> !(entity instanceof MalkuthBossBuddy))){
                    entity.invulnerableTime = 0;
                    entity.hurt(new MalkuthDamageSource(damageSource, giantSwordUltimateStartAttackType, 100), Integer.MAX_VALUE);
                }

                for (var entity : this.level().getEntitiesOfClass(LivingEntity.class, secondBox, entity -> !(entity instanceof MalkuthBossBuddy))){
                    entity.invulnerableTime = 0;
                    entity.hurt(new MalkuthDamageSource(damageSource, MalkuthAttackType.getOpposite(giantSwordUltimateStartAttackType), 100), Integer.MAX_VALUE);
                }
                inst.nextStage();
            }

        }else if (stage == 2){

            if (tick == 0){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
                lookAtTarget = true;
            }
            if (tick > 80){
                return true;
            }
        }

        return false;

    }

    private void doSwordChargeStripe(MalkuthAttackType sword, float radius, float sizeModifier){
        this.doSwordChargeStripe(sword,radius,sizeModifier,1 + random.nextFloat(), true);

    }

    private void doSwordChargeStripe(MalkuthAttackType sword, float radius, float sizeModifier,float circleAmount, boolean in){



        Matrix4f swordTransform = this.getModelPartTransformation(this,getMalkuthSwordPlaceBone(sword) , SERVER_MODEL);

        Vector3f fireSwordDirection = swordTransform.transformDirection(0,1,0,new Vector3f());

        Vector3f fireSwordPosition = swordTransform.transformPosition(0,0,0,new Vector3f());


        float rndHeightFire = 0.5f + 2 * random.nextFloat();

        Vector3f colFire = getMalkuthAttackPreparationParticleColor(sword);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        StripeParticleOptions fireOptions = StripeParticleOptions.createHorizontalCircling(fireColorStart, fireColor,
                new Vec3(fireSwordDirection.x,fireSwordDirection.y,fireSwordDirection.z), FDMathUtil.FPI * 2 * random.nextFloat(), 0.015f + 0.06f * FDEasings.easeOut(sizeModifier),10,50,random.nextFloat() * 4  - 2, radius, circleAmount, 0.5f,
                true, in);



        Vector3f fireStripeLocation = fireSwordPosition.add(fireSwordDirection.mul(rndHeightFire, new Vector3f()));


        FDLibCalls.sendParticles(((ServerLevel) level()), fireOptions, new Vec3(fireStripeLocation.x + this.getX(),fireStripeLocation.y + this.getY(),fireStripeLocation.z + this.getZ()), 60);


    }

    //=============================================================================OTHER================================================================================================


    private boolean isBelowHalfHP(){
        return this.hits <= maxHits / 2;
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {

        if (src.getEntity() instanceof ServerPlayer serverPlayer){

            Vec3 position = this.position();

            double dist = serverPlayer.position().distanceTo(position);

            if (dist < 5){

                level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.MALKUTH_HIT.get(), SoundSource.HOSTILE,1f,0.5f);

                PacketDistributor.sendToPlayer(serverPlayer, new DefaultShakePacket(FDShakeData.builder()
                        .outTime(5)
                        .amplitude(0.15f)
                        .build()));

                serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));

            }

        }

        if (!src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

        return super.hurt(src, damage);
    }

    private void causeSwordChargeParticles(MalkuthAttackType attackType){
        PacketDistributor.sendToPlayersTrackingEntity(this, new MalkuthChargeSwordPacket(this,attackType));
    }


    @Nullable
    @Override
    public LivingEntity getTarget() {
        return super.getTarget();
    }

    @Override
    public void setTarget(@Nullable LivingEntity living) {
        super.setTarget(living);
    }

    private void changeTarget(){
        List<Player> combatants = this.getCombatants(false);
        if (combatants.isEmpty()){
            this.setTarget(null);
        }else{
            this.setTarget(combatants.get(random.nextInt(combatants.size())));
        }
    }


    private void checkTarget(LivingEntity target){

        if (target.isDeadOrDying()){
            this.changeTarget();
            return;
        }else if (target.position().distanceTo(this.position()) > ENRAGE_RADIUS){
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

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (this.spawnPosition == null && x != 0 && y != 0 && z != 0){
            this.spawnPosition = new Vec3(x,y,z);
        }
    }

    public List<Player> getCombatants(boolean includeCreativeAndSpectator){
        return this.level().getEntitiesOfClass(Player.class, this.createEnrageRadiusAABB(this.spawnPosition),player->{
            return includeCreativeAndSpectator || (!player.isCreative() && !player.isSpectator());
        });
    }

    private AABB createEnrageRadiusAABB(Vec3 offset){
        return new AABB(
                -ENRAGE_RADIUS,
                -ENRAGE_RADIUS,
                -ENRAGE_RADIUS,
                ENRAGE_RADIUS,
                ENRAGE_RADIUS,
                ENRAGE_RADIUS
        ).move(offset);
    }

    protected List<MalkuthCannonEntity> getMalkuthCannons(){
        return BossTargetFinder.getEntitiesInCylinder(MalkuthCannonEntity.class, level(), this.spawnPosition, 30, 35, (cannon) -> {
            return !cannon.isPlayerControlled();
        });
    }


    protected List<MalkuthCannonEntity> getPlayerCannons(boolean onlyCannonsThatNeedRepair){
        return BossTargetFinder.getEntitiesInCylinder(MalkuthCannonEntity.class, level(), this.spawnPosition.add(0,-2,0), 30, 30, (cannon) -> {
            return cannon.isPlayerControlled() && (!onlyCannonsThatNeedRepair || cannon.requiresRepair());
        });
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.attackChain.save(tag);
        if (this.jumpCrushAttackMovementPath != null){
            this.jumpCrushAttackMovementPath.autoSave("jumpCrushAttackMovementPath",tag);
        }
        if (this.jumpOnWallPath != null){
            this.jumpOnWallPath.autoSave("jumpOnWallPath",tag);
        }
        if (this.bossSpawnerUUID != null){
            tag.putUUID("boss_spawner",this.bossSpawnerUUID);
        }
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        if (tag.contains("boss_spawner")){
            this.bossSpawnerUUID = tag.getUUID("boss_spawner");
        }
        this.attackChain.load(tag);
        if (tag.contains("jumpCrushAttackMovementPath")){
            this.jumpCrushAttackMovementPath = new ProjectileMovementPath();
            this.jumpCrushAttackMovementPath.autoLoad("jumpCrushAttackMovementPath",tag);
        }
        if (tag.contains("jumpOnWallPath")){
            this.jumpOnWallPath = new ProjectileMovementPath();
            this.jumpOnWallPath.autoLoad("jumpOnWallPath",tag);
        }
    }

    private void attachSwords(){
        this.attachFireSword();
        this.attachIceSword();
    }

    private void attachIceSword(){
        ModelSystem modelSystem = this.getModelSystem();
        BaseModelAttachmentData iceSword = new BaseModelAttachmentData();
        modelSystem.attachToLayer(0, "ice_sword_place", ICE_SWORD_UUID, FDModelAttachmentData.create(iceSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_SWORD_SOLID)
                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
        );
        modelSystem.attachToLayer(0, "ice_sword_place", ICE_SWORD_EMISSIVE_UUID, FDModelAttachmentData.create(iceSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_ICE_SWORD)
                .renderType(FDRenderTypes.EYES.get())
        );
    }

    private void attachFireSword(){
        ModelSystem modelSystem = this.getModelSystem();
        BaseModelAttachmentData fireSword = new BaseModelAttachmentData();
        modelSystem.attachToLayer(0, "fire_sword_place", FIRE_SWORD_UUID, FDModelAttachmentData.create(fireSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_SWORD_SOLID)
                .renderType(FDRenderTypes.ENTITY_CUTOUT_NO_CULL.get())
        );
        modelSystem.attachToLayer(0, "fire_sword_place", FIRE_SWORD_EMISSIVE_UUID, FDModelAttachmentData.create(fireSword, BossModels.MALKUTH_SWORD.get())
                .texture(MALKUTH_FIRE_SWORD)
                .renderType(FDRenderTypes.EYES.get())
        );
    }

    @Override
    protected void playBlockFallSound() {

    }

    private void deattachFireSword(){
        ModelSystem modelSystem = this.getModelSystem();
        modelSystem.removeAttachment(FIRE_SWORD_UUID);
        modelSystem.removeAttachment(FIRE_SWORD_EMISSIVE_UUID);
    }

    private void deattachIceSword(){
        ModelSystem modelSystem = this.getModelSystem();
        modelSystem.removeAttachment(ICE_SWORD_UUID);
        modelSystem.removeAttachment(ICE_SWORD_EMISSIVE_UUID);
    }

    private void deattachSwords(){
        this.deattachIceSword();
        this.deattachFireSword();
    }

    @Override
    public int getHeadRotSpeed() {
        return 10;
    }

    @Override
    public int getMaxHeadXRot() {
        return super.getMaxHeadXRot();
    }

    @Override
    public int getMaxHeadYRot() {
        return 80;
    }

    @Override
    public HeadControllerContainer<MalkuthEntity> getHeadControllerContainer() {
        return headControllerContainer;
    }

    @Override
    public void push(double p_20286_, double p_20287_, double p_20288_) {

    }

    @Override
    public void push(Entity p_21294_) {

    }

    @Override
    public void push(Vec3 p_347665_) {

    }

    @Override
    protected void pushEntities() {

    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void setSpawnedBy(BossSpawnerEntity bossSpawnerEntity) {
        this.bossSpawnerUUID = bossSpawnerEntity.getUUID();
    }

    @Override
    public void setSpawnPosition(Vec3 spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    @Override
    public BossSpawnerEntity getSpawner() {
        if (this.bossSpawnerUUID != null && level() instanceof ServerLevel level){
            return (BossSpawnerEntity) level.getEntity(bossSpawnerUUID);
        }
        return null;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossbar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossbar.removePlayer(player);
    }

    public static String getMalkuthSwordPlaceBone(MalkuthAttackType type){
        if (type.isFire()){
            return "fire_sword_place";
        }else{
            return "ice_sword_place";
        }
    }

    @EventBusSubscriber(modid = FDBosses.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class Events {

        @SubscribeEvent
        public static void ignoreTotems(LivingUseTotemEvent event){

            LivingEntity livingEntity = event.getEntity();

            if (!livingEntity.level().isClientSide && event.getSource() == BossDamageSources.MALKUTH_COWARDICE_SOURCE){
                event.setCanceled(true);
            }

        }

        @SubscribeEvent
        public static void onCowardice(MobEffectEvent.Expired event){

            MobEffectInstance instance = event.getEffectInstance();

            var effect = instance.getEffect();

            LivingEntity entity = event.getEntity();

            if (!entity.level().isClientSide && effect.is(BossEffects.MARK_OF_A_COWARD)){
                entity.hurt(BossDamageSources.MALKUTH_COWARDICE_SOURCE, Float.MAX_VALUE);
            }


        }

        public static final int MARK_OF_A_COWARD_DURATION = 60;

        @SubscribeEvent
        public static void livingTick(PlayerTickEvent.Post event){

            var entity = event.getEntity();

            Level level = entity.level();

            if (!level.isClientSide){

                if (entity.hasEffect(BossEffects.MARK_OF_A_KNIGHT)) {
                    Vec3 cylinderStart = entity.position().add(0, -MalkuthEntity.ENRAGE_HEIGHT, 0);

                    var spawners = BossTargetFinder.getEntitiesInArc(MalkuthBossSpawner.class, level, cylinderStart, new Vec2(0, 1), FDMathUtil.FPI,
                            MalkuthEntity.ENRAGE_HEIGHT + 2, MalkuthEntity.ENRAGE_RADIUS);


                    if (spawners.isEmpty()) {
                        entity.removeEffect(BossEffects.MARK_OF_A_KNIGHT);
                        if (!entity.hasEffect(BossEffects.MARK_OF_A_COWARD)) {
                            entity.addEffect(new MobEffectInstance(BossEffects.MARK_OF_A_COWARD, MARK_OF_A_COWARD_DURATION, 0, true, false));
                        }
                    } else {
                        entity.removeEffect(BossEffects.MARK_OF_A_COWARD);
                    }
                }

            }


        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent.Pre event){

            LivingEntity living = event.getEntity();
            Level level = living.level();

            if (!level.isClientSide && living instanceof Player player && event.getSource() instanceof MalkuthDamageSource damageSource) {

                int malkuthDamageAmount = damageSource.getMalkuthAttackAmount();
                MalkuthAttackType malkuthAttackType = damageSource.getMalkuthAttackType();

                if (!MalkuthWeaknessHandler.isWeakTo(player, malkuthAttackType)) {
                    float damage = event.getOriginalDamage();
                    float reduction = 1 - BossConfigs.BOSS_CONFIG.get().malkuthConfig.nonWeakToDamageReduction / 100f;
                    event.setNewDamage(damage * reduction);
                }

                MalkuthWeaknessHandler.damageWeakness(malkuthAttackType, player, malkuthDamageAmount);

            }

        }

    }

}
