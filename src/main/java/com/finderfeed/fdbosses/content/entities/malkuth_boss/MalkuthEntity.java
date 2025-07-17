package com.finderfeed.fdbosses.content.entities.malkuth_boss;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.square_preparation_particle.RectanglePreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_boulder.MalkuthBoulderEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_chain.MalkuthChainEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_earthquake.MalkuthEarthquake;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireball;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_floor.MalkuthFloorEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword.MalkuthGiantSwordSlash;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_platform.MalkuthPlatform;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash.MalkuthSlashProjectile;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.packets.MalkuthChargeSwordPacket;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.init.FDRenderTypes;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
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
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class MalkuthEntity extends FDMob implements IHasHead<MalkuthEntity>, MalkuthBossBuddy, AutoSerializable, BossSpawnerContextAssignable {

    public static final String MAIN_LAYER = "MAIN";

    public static final float ENRAGE_RADIUS = 30;

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
    public static final String NOTHING_20_TICKS = "nothing_20_ticks";
    public static final String SUMMON_EARTHQUAKE = "summon_earthquake";
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

    private HeadControllerContainer<MalkuthEntity> headControllerContainer;

    private AttackChain attackChain;

    @SerializableField
    private Vec3 spawnPosition;

    @SerializableField
    private MalkuthBossInitializer malkuthBossInitializer;

    @SerializableField
    private boolean lookAtTarget = true;

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

        malkuthBossInitializer = new MalkuthBossInitializer(this);

        this.headControllerContainer = new HeadControllerContainer<>(this)
                .addHeadController(CLIENT_MODEL, "head");
        this.lookControl = this.headControllerContainer;

        AttackOptions sideRocks = AttackOptions.builder()
                .setPreAttack(DEATTACH_SWORDS)
                .addAttack(SUMMON_AND_THROW_SIDE_ROCKS)
                .setNextAttack(ATTACH_SWORDS)
                .build();

        this.attackChain = new AttackChain(this.getRandom())
                .registerAttack(NOTHING_20_TICKS,this::doNothing20Ticks)//not an attack
                .registerAttack(DEATTACH_SWORDS, v->this.attachSwordsAttack(v,false)) //not an attack
                .registerAttack(ATTACH_SWORDS, v->this.attachSwordsAttack(v,true)) //not an attack

                .registerAttack(SLASH_ATTACK,this::aerialSlashAttack)
                .registerAttack(PULL_AND_PUNCH,this::pullAndPunch) //combos and anti flight
                .registerAttack(JUMP_CRUSH,this::jumpCrushAttack)
                .registerAttack(CAROUSEL_SLASHES,this::carouselSlashesAttack)
                .registerAttack(JUMP_BACK_ON_SPAWN,v->this.jumpBackOnSpawn(v,false))
                .registerAttack(JUMP_BACK_ON_SPAWN_WITH_CRUSH,v->this.jumpBackOnSpawn(v,true))
                .registerAttack(JUMP_ON_WALL_COMMAND_CANNONS,this::jumpAndCommandCannons)
                .registerAttack(GIANT_SWORDS_ULTIMATE,this::giantSwordUltimate)
                .registerAttack(SUMMON_AND_THROW_SIDE_ROCKS,this::summonAndThrowSideRocks)
                .registerAttack(SUMMON_EARTHQUAKE,this::summonEartquake)
                .registerAttack(PLATFORMS_N_FIREBALLS, this::fireballsNPlatforms)

//                .addAttack(-1, sideRocks)
//                .addAttack(0, NOTHING_20_TICKS)
                .addAttack(0, PLATFORMS_N_FIREBALLS)
//                .addAttack(0, SLASH_ATTACK)
//                .addAttack(1, JUMP_CRUSH)
//                .addAttack(2, SUMMON_EARTHQUAKE)
//                .addAttack(3, JUMP_BACK_ON_SPAWN_WITH_CRUSH)
//                .addAttack(2, PULL_AND_PUNCH)
//                .addAttack(3, JUMP_ON_WALL_COMMAND_CANNONS)
//                .addAttack(4, CAROUSEL_SLASHES)
                .attackListener(this::attackListener)
        ;

    }

    private boolean doNothing20Ticks(AttackInstance instance){
        return true;
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

            AnimationSystem animationSystem = this.getAnimationSystem();
            if (animationSystem.getTicker(MAIN_LAYER) == null){
                animationSystem.startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
            }

            if (malkuthBossInitializer.isFinished()) {
                this.attackChain.tick();
            }else{
                malkuthBossInitializer.tick();
            }

            if (this.getTarget() != null) {

                var target = this.getTarget();

                this.checkTarget(target);

                if (lookAtTarget) {
                    this.getLookControl().setLookAt(target);
                }

            }else{

                this.changeTarget();

                if (this.getTarget() == null){
                    this.getLookControl().setLookAt(
                            this.position().add(this.getForward().multiply(100,0,100))
                    );
                }

            }



            this.setYRot(this.yBodyRot);

        }
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
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_CRUSH).build());

            if(startSummonPlatformsPath == null){
                startSummonPlatformsPath = new ProjectileMovementPath(20, false)
                        .addPos(this.position())
                        .addPos(this.position().add(0,20,0))
                        .addPos(this.position());
            }

            if (tick > 5) {
                startSummonPlatformsPath.tick(this);
                if (tick == 6){
                    this.doJumpStartParticles(0);
                }
            }

            if (tick == 30){
                startSummonPlatformsPath = null;
                this.setDeltaMovement(Vec3.ZERO);
                this.teleportTo(spawnPosition.x,spawnPosition.y,spawnPosition.z);

                inst.nextStage();
            }

        }else if (stage == 1){
            if (tick == 1) {

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
                MalkuthFireball malkuthFireball = MalkuthFireball.summon(type, level(), spawnPos, gotoPos, platformPos.add(0,1.5,0));

            }else if (localTick == fireballLaunchTick){
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

    private MalkuthAttackType earthquakeToSummon = MalkuthAttackType.FIRE;

    private boolean summonEartquake(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){
            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            lookAtTarget = true;
            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.getTarget().position());
            earthquakeToSummon = MalkuthAttackType.getRandom(random);
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

                Vec3 targetPos = this.getTarget().position();

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


                double dist = direction.length();

                int time = (int) Math.ceil(dist);

                MalkuthEarthquake malkuthEarthquake = MalkuthEarthquake.summon(level(),earthquakeToSummon, this.position(), direction, time, FDMathUtil.FPI / 9, 1);
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
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
            inst.nextStage();
        }else if (lstage == 1){

            if (tick == 10){

                int count = 8;
                float distForOne = 3.4f;

                Vec3 startPos = this.position().add(0,0.1,-1);

                int h = random.nextInt(2);

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

                    MalkuthBoulderEntity malkuthBoulderEntity = MalkuthBoulderEntity.summon(level(), summonPos, 10,3, movementPath, currentType);


                    Vector3f col = getMalkuthAttackPreparationParticleColor(currentType);

                    RectanglePreparationParticleOptions rectanglePreparationParticleOptions = new RectanglePreparationParticleOptions(new Vec3(-k,0,0), 56, distForOne/2, 20, 10,10,col.x,col.y,col.z,0.25f);
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

            }else if (tick > 30){

                var l = this.level().getEntitiesOfClass(MalkuthBoulderEntity.class, new AABB(-30,-30,-30,30,30,30).move(this.position()));

                for (var b : l){
                    b.setShouldMoveToTarget(true);
                }

                inst.nextStage();
            }

        }else if (lstage == 2){
            if (tick >= 15){
                if (stage >= 12) {
                    return true;
                }else{
                    inst.nextStage();
                    return false;
                }
            }
        }
        return false;
    }


    private ProjectileMovementPath jumpBackOnSpawnPath;

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
            }

            this.noPhysics = false;
            this.setNoGravity(false);
            lookAtTarget = true;
            int waitTime = crush ? 30 : 10;
            if (tick >= waitTime){
                this.jumpBackOnSpawnPath = null;
                return true;
            }
        }

        return false;
    }

    private void jumpEndEarthquakePrepareParticles(Vec3 lastpos, int earthquakesCount, float angle, float radius){

        MalkuthAttackType localType = MalkuthAttackType.FIRE;

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

        MalkuthAttackType localType = MalkuthAttackType.FIRE;

        for (int i = 0; i < earthquakesCount; i++) {

            float currentAngle = i * angle + angle / 2;

            Vec3 v = new Vec3(radius,0,0).yRot(currentAngle);

            MalkuthEarthquake earthquake = MalkuthEarthquake.summon(level(), localType, lastpos, v, 40, angle, 1);
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

            this.slashAttackType = MalkuthAttackType.getRandom(this.getRandom());

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

                MalkuthSlashProjectile malkuthSlashProjectile = MalkuthSlashProjectile.summon(level(),spawnPos,speedv,this.slashAttackType, 1, 2, rotation,0);

            }else if (tick >= 40){
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
            if (stage >= 23){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                return true;
            }else{
                inst.nextStage();
            }
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
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE)
                            .setSpeed(1.5f).build())
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
                MalkuthCrushAttack malkuthCrushAttack = MalkuthCrushAttack.summon(level(), this.position().add(this.getForward().multiply(1,0,1).normalize()), 1);
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),malkuthCrushAttack.position(),120);
            }else if (tick >= 20){
                return true;
            }

        }

        return false;
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



    private boolean pullAndPunch(AttackInstance inst){

        if (true) return true;

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0){

            AABB box = new AABB(-ENRAGE_RADIUS,4,-ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS,ENRAGE_RADIUS).move(this.position());

            List<Player> players = level().getEntitiesOfClass(Player.class, box);

            if (players.isEmpty()){
                return true;
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
            if (tick == 10){
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);
            }else if (tick == 24){

                Vector3f pos = this.getModelPartPosition(this, getMalkuthSwordPlaceBone(MalkuthAttackType.ICE),SERVER_MODEL);

                Vector3f col = getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);

                FDLibCalls.sendParticles((ServerLevel) level(), BallParticleOptions.builder()
                        .size(5f)
                        .scalingOptions(0, 0, 2)
                        .color(col.x,col.y,col.z)
                                .brightness(2)
                        .build(), this.position().add(pos.x,pos.y,pos.z), 60);


                for (var chain : this.level().getEntitiesOfClass(MalkuthChainEntity.class, this.getBoundingBox().inflate(20,20,20))){
                    var passengers = new ArrayList<>(chain.getPassengers());
                    chain.ejectPassengers();
                    chain.setRemoved(RemovalReason.DISCARDED);

                    Vec3 forward = this.getForward().multiply(1,0,1).normalize();

                    for (var e : passengers){
                        if (e instanceof LivingEntity livingEntity){
                            livingEntity.hurt(livingEntity.damageSources().generic(),1);

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
            } else if (tick >= 50){
                return true;
            }
        }


        return false;
    }

    private ProjectileMovementPath jumpOnWallPath = null;

    private boolean jumpAndCommandCannons(AttackInstance attackInstance){

        this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

        this.lookAtTarget = false;


        this.getLookControl().setLookAt(this.position().add(0,0,-100));

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_LAND)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
            this.jumpOnWallPath = this.makeJumpOnWallPath(20,false);
            attackInstance.nextStage();
        }else if (stage == 1){

            if (this.jumpOnWallPath == null){
                this.jumpOnWallPath = this.makeJumpOnWallPath(20,false);
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
                this.setNoGravity(false);
                this.noPhysics = false;
                attackInstance.nextStage();
                this.getAnimationSystem().startAnimation(MAIN_LAYER,AnimationTicker.builder(BossAnims.MALKUTH_SWORD_FORWARD).build());
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

        }else if (stage == 3){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_JUMP_AND_LAND)
                    .nextAnimation(AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build()).build());
            this.jumpOnWallPath = this.makeJumpOnWallPath(20,true);
            attackInstance.nextStage();
        }else if (stage == 4){
            if (this.jumpOnWallPath == null){
                this.jumpOnWallPath = this.makeJumpOnWallPath(20,true);
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
                this.setNoGravity(false);
                this.noPhysics = false;
                attackInstance.nextStage();
                this.setDeltaMovement(0,0,0);
                this.teleportTo(this.spawnPosition.x,this.spawnPosition.y,this.spawnPosition.z);

                this.doJumpStartParticles(0);
            }
        }else if (stage == 5){
            if (tick > 60){
                lookAtTarget = true;
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
                return true;
            }
            return false;
        }


        return false;
    }

    private void shootCannons(){
        int attackPerCombatant = 6;
        var cannons = this.getCannons();
        if (cannons.isEmpty()){
            //summon them
            return;
        }
        var combatants = this.getCombatants(true);

        List<Vec3> positions = new ArrayList<>();
        for (var combatant : combatants){

            float randomAngleStart = FDMathUtil.FPI / 4 * random.nextInt(4);
            Vec3 base = combatant.position().add(0,combatant.getBbHeight()/2,0);


            for (int i = 0; i < attackPerCombatant;i++){
                float angle = randomAngleStart + FDMathUtil.FPI / 4 * i;
                float rad = random.nextFloat() * 5 + 2;
                Vec3 v = new Vec3(1,0,0)
                        .yRot(angle - FDMathUtil.FPI / 4 + FDMathUtil.FPI / 2 * random.nextFloat())
                        .multiply(rad,rad,rad)
                        .add(combatant.position());

                positions.add(v);
            }
            positions.add(base);
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

        for (var entry : cannonTargets.entrySet()){
            var cannon = entry.getKey();
            cannon.shoot(entry.getValue());
        }
    }

    private ProjectileMovementPath makeJumpOnWallPath(int time, boolean reversed){

        Vec3 start = this.spawnPosition;

        Vec3 offset = new Vec3(0, 13.0, 27.85);

        Vec3 end = start.add(offset);

        Vec3 hoffset = offset.multiply(1,0,1);
        Vec3 v1 = start.add(hoffset.multiply(0.25,0.25,0.25).add(0,10,0));
        Vec3 v2 = start.add(hoffset.multiply(0.5,0.5,0.5).add(0,17,0));
        Vec3 v3 = start.add(hoffset.multiply(0.75,0.75,0.75).add(0,15,0));

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

    private void doJumpStartParticles(float verticalOffset){
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

            for (int i = 0; i < slashesAmount;i++){

                Vec3 direction = startVec.yRot(i * angle + angle / 2);

                Vec3 speed = direction.multiply(slashSpeed,slashSpeed,slashSpeed);
                speed = speed.add(0,verticalSpeed,0);


                MalkuthSlashProjectile.summon(level(),this.position().add(0,0.25,0), speed, localCarouselSlash, 1, maxSlashSize, 0, reachDestinationTime);

                if (localCarouselSlash.isFire()){
                    localCarouselSlash = MalkuthAttackType.ICE;
                }else{
                    localCarouselSlash = MalkuthAttackType.FIRE;
                }

            }

            if (currentStartCarouselSlash.isFire()){
                currentStartCarouselSlash = MalkuthAttackType.ICE;
            }else{
                currentStartCarouselSlash = MalkuthAttackType.FIRE;
            }

        }else if (tick >= preparationTime + 20){
            return true;
        }

        return false;
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

    public boolean giantSwordUltimate(AttackInstance inst){

        int stage = inst.stage;
        int tick = inst.tick;

        if (stage == 0) {

            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_GIANT_SWORD_ATTACK)
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .build());

            this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.ANIMATION);

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


            if (tick == swordSpawnTick - 5){

                Vector3f red = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);
                Vector3f blue = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.ICE);

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

                MalkuthGiantSwordSlash.summon(level(), pos1, new Vec3(0,0,-1), MalkuthAttackType.FIRE);
                MalkuthGiantSwordSlash.summon(level(), pos2, new Vec3(0,0,-1), MalkuthAttackType.ICE);


            }else if (tick == MalkuthGiantSwordSlash.TIME_TO_HIT + MalkuthGiantSwordSlash.TIME_TO_RISE + swordSpawnTick - 2){
                ImpactFrame base = new ImpactFrame(0.5f, 0.1f, 1, false);
                FDLibCalls.sendImpactFrames((ServerLevel) level(), this.position(), 30,
                        base,
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true),
                        new ImpactFrame(base).setDuration(1),
                        new ImpactFrame(base).setDuration(1).setInverted(true)
                );

                inst.nextStage();
            }

        }else if (stage == 2){

            if (tick == 0){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.MALKUTH_IDLE).build());
                this.headControllerContainer.setControllersMode(HeadControllerContainer.Mode.LOOK);
                lookAtTarget = true;
            }
            if (tick > 200){
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


    @Override
    public boolean hurt(DamageSource src, float damage) {

        if (!src.is(DamageTypes.GENERIC_KILL) || !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

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
        List<Player> combatants = this.getCombatants(true);
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

    private List<Player> getCombatants(boolean includeCreativeAndSpectator){
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

    private List<MalkuthCannonEntity> getCannons(){
        AABB box = new AABB(-20,-20,-10,20,10,10);
        return this.level().getEntitiesOfClass(MalkuthCannonEntity.class, box.move(this.position()));
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
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
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

    }

    @Override
    public void setSpawnPosition(Vec3 spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    @Override
    public BossSpawnerEntity getSpawner() {
        return null;
    }

    public static String getMalkuthSwordPlaceBone(MalkuthAttackType type){
        if (type.isFire()){
            return "fire_sword_place";
        }else{
            return "ice_sword_place";
        }
    }

}
