package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.entity_ghost.EntityGhostParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerContextAssignable;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.netzach.netzach_gear_crush.NetzachGearCrushAttack;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackAction;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackOptions;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

public class NetzachEntity extends FDMob implements BossSpawnerContextAssignable, AutoSerializable {

    public static final int ARENA_HEIGHT = 40;
    public static final int ARENA_RADIUS = 40;

    public static final EntityDataAccessor<Boolean> SPAWN_GHOSTS = SynchedEntityData.defineId(NetzachEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(NetzachEntity.class, EntityDataSerializers.FLOAT);

    public static final String MAIN_LAYER = "main";

    public static final String ATTACK_SERIES_1 = "attack_series_1";
    public static final String THROW_GEAR = "throw_gear";
    public static final String ROLLING_GEAR = "rolling_gear";
    public static final String BASIC_ATTACK = "basic_attack";
    public static final String PUSH_AWAY_ATTACK = "push_away_attack";
    public static final String JUMP_CRUSH = "jump_crush";
    public static final String GEAR_CRUSH = "gear_crush";

    public AttackChain attackChain;

    public NetzachEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        attackChain = new AttackChain(level.random)
                .registerAttack(ATTACK_SERIES_1, this::attackSeriesOne)
                .registerAttack(THROW_GEAR, this::throwGear)
                .registerAttack(ROLLING_GEAR, this::throwRollingGear)
                .registerAttack(BASIC_ATTACK, this::basicAttack)
                .registerAttack(PUSH_AWAY_ATTACK, this::pushAway)
                .registerAttack(JUMP_CRUSH, this::jumpAndCrush)
                .registerAttack(GEAR_CRUSH, this::gearCrush)
                .attackListener(this::attackListener)
                .addAttack(0, AttackOptions.chainOptionsBuilder()
                        .addAttack(ATTACK_SERIES_1)
                        .addAttack(BASIC_ATTACK)
                        .addAttack(GEAR_CRUSH)
                        .addAttack(BASIC_ATTACK)
                        .addAttack(JUMP_CRUSH)
                        .addAttack(BASIC_ATTACK)
                        .addAttack(BASIC_ATTACK)
                        .addAttack(GEAR_CRUSH)
                        .build())

        ;

        this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_IDLE).build());

    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.setTarget(this.level().getNearestPlayer(this.getX(),this.getY(),this.getZ(), 120, null));
            this.attackChain.tick();
        }else{

            this.handleGhostSpawning();
        }
    }

    private void handleGhostSpawning(){
        if (!this.isSpawningGhosts()) return;

        Vec3 oldPos = new Vec3(xo, yo, zo);
        Vec3 currentPos = this.position();
        Vec3 b = oldPos.subtract(currentPos);
        Vec3 nb = b.normalize();
        float len = (float) b.length();
        for (float i = 0; i < len; i+=2){
            Vec3 ppos = currentPos.add(nb.scale(i));
            level().addParticle(new EntityGhostParticleOptions(this, 3), true, ppos.x, ppos.y, ppos.z,0,0,0);
        }
    }

    private AttackAction attackListener(String s) {
        if (this.getTarget() == null){
            return AttackAction.WAIT;
        }
        return AttackAction.PROCEED;
    }

    private boolean gearCrush(AttackInstance attackInstance) {

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        if (this.getTarget() == null){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_IDLE)
                    .build());
            return true;
        }

        var target = this.getTarget();
        this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_CAST_SOMETHING)
                            .important()
                            .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                    .build());
            attackInstance.nextStage();
        }else if (stage == 1){

            if (tick == 6){

                var entities = new ArrayList<>(level().getEntitiesOfClass(LivingEntity.class, this.constructArenaBox(), (entity) -> {
                    return entity instanceof Player player && BossUtil.isPlayerInSurvival(player);
                }));
                entities.add(this.getTarget());

                for (var entity : entities){
                    NetzachGearCrushAttack.summon(this, entity, entity.getLookAngle());
                }

            }else if (tick > 25){
                return true;
            }

        }

        return false;
    }

    private AABB constructArenaBox(){
        return new AABB(
                -ARENA_RADIUS,
                -10,
                -ARENA_RADIUS,
                ARENA_RADIUS,
                ARENA_HEIGHT,
                ARENA_RADIUS
        ).move(this.position());
    }

    @SerializableField
    private ProjectileMovementPath jumpAndCrushPath;

    @SerializableField
    private Vec3 jumpFlyTo;

    private boolean jumpAndCrush(AttackInstance instance){

        int tick = instance.tick;
        int stage = instance.stage;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_CRUSH)
                            .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE.get()).build())
                            .important()
                    .build());
            instance.nextStage();
            this.jumpFlyTo = this.position().add(0,7,0);
        } else if (stage == 1){
            if (this.getTarget() != null) {
                this.lookAt(EntityAnchorArgument.Anchor.FEET, this.getTarget().position());
            }

            this.noPhysics = true;


            if (tick < 18) {
                if (this.jumpFlyTo == null) {
                    this.jumpFlyTo = this.position().add(0, 10, 0);
                }

                this.setGravity((float) (Mob.DEFAULT_BASE_GRAVITY / 3));

                Vec3 deltaMovement = this.jumpFlyTo.subtract(this.position()).scale(0.25f);
                this.setDeltaMovement(deltaMovement);
                if (tick > 10){
                    this.setSpawnGhosts(false);
                }else{
                    this.setSpawnGhosts(true);
                }
            }else{


                if (this.getTarget() == null){
                    this.noPhysics = false;
                    return true;
                }else{
                    this.jumpAndCrushPath = new ProjectileMovementPath(2, false)
                            .addPos(this.position())
                            .addPos(this.getTarget().position());
                }

                instance.nextStage();
            }

        }else if (stage == 2){

            if (this.getTarget() == null){
                this.noPhysics = false;
                return true;
            }

            if (this.jumpAndCrushPath == null){
                this.jumpAndCrushPath = new ProjectileMovementPath(2, false)
                        .addPos(this.position())
                        .addPos(this.getTarget().position());
            }

            if (tick > 10){

                this.setSpawnGhosts(true);
                if (this.jumpAndCrushPath.isFinished()){
                    var lastPos = this.jumpAndCrushPath.getPositions().getLast();
                    this.teleportTo(lastPos.x, lastPos.y, lastPos.z);


                    this.setGravity((float) Mob.DEFAULT_BASE_GRAVITY);
                    this.setDeltaMovement(Vec3.ZERO);
                    instance.nextStage();
                    return false;
                }
                this.jumpAndCrushPath.tick(this);
            }

        }else if (stage == 3){


            this.setGravity((float) Mob.DEFAULT_BASE_GRAVITY);

            this.setSpawnGhosts(false);
            this.noPhysics = false;
            if (tick == 0){
                this.setSpawnGhosts(true);

                this.setDeltaMovement(Vec3.ZERO);
            }else if (tick == 2){

                this.setSpawnGhosts(false);
                BossUtil.netzachCrush((ServerLevel) level(), this.position(), 60);

                Vec3 targetPos = this.position().add(1,0,0);
                if (this.getTarget() != null){
                    targetPos = this.getTarget().position();
                }
                Vec3 between = targetPos.subtract(this.position());

                this.spawnRollingGearsAround(between, 8, 1.5f);

                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(10)
                        .amplitude(2.5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(10)
                        .build(),this.position(),30);
            }
            if (tick > 25){

                return true;
            }

        }

        return false;
    }

    private void spawnRollingGearsAround(Vec3 initialDirection, int count, float speed){
        initialDirection = initialDirection.multiply(1,0,1).add(0.001,0,0).normalize();
        float angle = FDMathUtil.FPI * 2 / count;
        for (int i = 0; i < count; i++){
            Vec3 direction = initialDirection.yRot(angle * i);
            NetzachRollingGearAttack netzachRollingGearAttack = NetzachRollingGearAttack.summon(this, this.position(), direction.scale(speed));
        }
    }



    public static final int RANGED_ATTACK_THROW_GEAR = 0;
    public static final int RANGED_ATTACK_ROLLING_GEAR = 1;
    public static final int PUSH_AWAY = 2;

    private int rangedAttackType = RANGED_ATTACK_THROW_GEAR;

    private boolean basicAttack(AttackInstance attackInstance){

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        var target = this.getTarget();

        if (target == null){
            return false;
        }

        if (stage == 0){

            if (this.distanceTo(target) > 7) {

                ClipContext clipContext = new ClipContext(target.position(), target.position().add(0, -2.5, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                var result = level().clip(clipContext);
                if (result.getType() == HitResult.Type.MISS) {
                    rangedAttackType = RANGED_ATTACK_THROW_GEAR;
                } else {
                    if (rangedAttackType == RANGED_ATTACK_THROW_GEAR) {
                        rangedAttackType = RANGED_ATTACK_ROLLING_GEAR;
                    } else {
                        rangedAttackType = RANGED_ATTACK_THROW_GEAR;
                    }
                }

            }else{
                rangedAttackType = PUSH_AWAY;
            }
        }

        if (rangedAttackType == RANGED_ATTACK_ROLLING_GEAR){
            return this.throwRollingGear(attackInstance);
        }else if (rangedAttackType == RANGED_ATTACK_THROW_GEAR){
            return this.throwGear(attackInstance);
        }else if (rangedAttackType == PUSH_AWAY){
          return this.pushAway(attackInstance);
        } else{
            return true;
        }
    }

    private boolean pushAway(AttackInstance attackInstance){

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        if (this.getTarget() == null){
            return true;
        }

        if (stage == 0) {
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_PUSH_AWAY)
                    .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                            .important()
                            .setSpeed(1.25f)
                    .build());
            attackInstance.nextStage();
        } else if (stage == 1){
            if (tick == 14){

                BossUtil.netzachPushAway((ServerLevel) level(), this.position(), 60);

                var targets = FDTargetFinder.getEntitiesInSphere(LivingEntity.class, level(), this.position(), 8, entity -> {
                    return entity != this;
                });

                for (var target : targets){
                    Vec3 pos = this.position();
                    Vec3 tpos = target.position();
                    Vec3 b = tpos.subtract(pos);
                    Vec3 speed = b.multiply(1,0,1).normalize().scale(3f);

                    if (target.onGround()){
                        speed = speed.add(0,0.5f,0);
                    }else{
                        speed = speed.add(0,0.5f,0);
                    }

                    if (target instanceof ServerPlayer serverPlayer){
                        serverPlayer.hasImpulse = true;
                        FDLibCalls.setServerPlayerSpeed(serverPlayer, speed);
                    }else{
                        target.setDeltaMovement(speed);
                    }

                }

            }else if (tick > 30){
                return true;
            }
        }

        return false;
    }

    private boolean isRollingGearLeftAttack;

    private boolean throwRollingGear(AttackInstance attackInstance) {

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        if (this.getTarget() != null) {
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.getTarget().position());
        }else{
            return true;
        }


        if (stage == 0) {
            isRollingGearLeftAttack = !isRollingGearLeftAttack;
            if (isRollingGearLeftAttack) {
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_ROLLING_GEAR_LEFT)
                        .important()
                        .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                        .build());
            }else{
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_ROLLING_GEAR_RIGHT)
                        .important()
                        .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                        .build());
            }
            attackInstance.nextStage();
        }else if (stage == 1){

            if (tick == 9){
                Vec3 fwd = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 left = fwd.yRot(FDMathUtil.FPI / 2);

                if (isRollingGearLeftAttack){
                    Vec3 ppos = this.position().add(0,1,0).add(left.scale(-0.25f));
                    SpriteParticleOptions spriteParticleOptions = SpriteParticleOptions.builder(BossParticles.NETZACH_SLASH)
                            .xyzRotation(30,-40,0)
                            .particleLookDirection(fwd)
                            .verticalRendering()
                            .size(4f)
                            .lightenedUp()
                            .lifetime(4)
                            .build();
                    FDLibCalls.sendParticles((ServerLevel) level(), spriteParticleOptions, ppos, 60);

                }else{
                    Vec3 ppos = this.position().add(0,1,0).add(left.scale(-0.25f));
                    SpriteParticleOptions spriteParticleOptions = SpriteParticleOptions.builder(BossParticles.NETZACH_SLASH)
                            .xyzRotation(-30,40,0)
                            .particleLookDirection(fwd)
                            .verticalRendering()
                            .size(4f)
                            .lightenedUp()
                            .flipSprite()
                            .lifetime(4)
                            .build();
                    FDLibCalls.sendParticles((ServerLevel) level(), spriteParticleOptions, ppos, 60);
                }



            } else if (tick == 10){
                Vec3 fwd = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 startPos = this.position();

                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5)
                        .amplitude(2.5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(5)
                        .build(),this.position(),60);


                SlamParticlesPacket packet = new SlamParticlesPacket(
                        new SlamParticlesPacket.SlamData(this.getOnPos(),this.position().add(0,0.5f,0),fwd)
                                .maxAngle(FDMathUtil.FPI)
                                .maxSpeed(0.5f)
                                .collectRadius(2)
                                .maxParticleLifetime(30)
                                .count(5)
                                .particleSizeMultiplier(0.75f)
                                .maxVerticalSpeedEdges(0.15f)
                                .maxVerticalSpeedCenter(0.4f)
                );
                PacketDistributor.sendToPlayersTrackingEntity(this,packet);

                for (int i = -1; i <= 1; i++) {
                    Vec3 speed = fwd.yRot(FDMathUtil.FPI / 16 * i);
                    NetzachRollingGearAttack.summon(this, startPos, speed.scale(2));
                }

            } else if (tick > 25){
                return true;
            }
        }

        return false;


    }

    private boolean isThrowGearLeftAttack = false;

    private boolean throwGear(AttackInstance attackInstance){

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        if (this.getTarget() != null) {
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.getTarget().position());
        }else{
            return true;
        }

        if (stage == 0) {
            isThrowGearLeftAttack = !isThrowGearLeftAttack;
            if (isThrowGearLeftAttack) {
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_THROW_GEAR_LEFT)
                                .important()
                                .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                        .build());
            }else{
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_THROW_GEAR_RIGHT)
                                .important()
                                .nextAnimation(AnimationTicker.builder(BossAnims.NETZACH_IDLE).build())
                        .build());
            }
            attackInstance.nextStage();
        }else if (stage == 1){

            if (tick == 5){
                Vec3 fwd = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 left = fwd.yRot(FDMathUtil.FPI / 2);
                Vec3 startPos = this.position().add(0,1.5,0);
                Vec3 nextPos;

                if (isThrowGearLeftAttack){
                    nextPos = startPos.add(left.scale(3)).add(0,2,0);
                }else{
                    nextPos = startPos.add(left.scale(-3)).add(0,2,0);
                }
                NetzachAerialGearAttack.summon(this, startPos, Vec3.ZERO, nextPos);
            } else if (tick == 12){
                Vec3 targetPos = this.getTarget().position();
                for (var gear : FDTargetFinder.getEntitiesInSphere(NetzachAerialGearAttack.class, level(), this.position().add(0,2,0), 20)) {
                    if (gear.getFlyTo() != null) {
                        gear.setFlyTo(null);
                        Vec3 b = targetPos.subtract(gear.position());
                        Vec3 hb = b.multiply(1,0,1);
                        Vec3 nhb = hb.normalize();

                        Vec3 aTargetPos;
                        ClipContext clipContext = new ClipContext(targetPos, targetPos.add(0,-2.5,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                        var result = level().clip(clipContext);

                        if (result.getType() == HitResult.Type.MISS){
                            aTargetPos = this.getTarget().position().add(0,this.getTarget().getBbHeight() / 2,0);
                        }else{
                            aTargetPos = result.getLocation().add(nhb.reverse().scale(3));
                        }

                        b = aTargetPos.subtract(gear.position());

                        Vec3 speed = b.normalize().scale(2.5);
                        gear.setDeltaMovement(speed);
                    }
                }
            } else if (tick > 25){
                return true;
            }
        }

        return false;
    }

    private boolean attackSeriesOne(AttackInstance attackInstance) {
        var system = this.getAnimationSystem();

        LivingEntity target = this.getTarget();
        if (target == null){
            this.noPhysics = false;
            system.startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_IDLE)
                    .build());
            return true;
        }

        int tick = attackInstance.tick;
        int stage = attackInstance.stage;

        this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_ATTACK_SERIES_1)
                            .important()
                    .build());
            attackInstance.nextStage();
        }else if (stage == 1){
            this.setSpawnGhosts(true);
            if (this.dashingStage(target, tick, 10, 2, 0.25f)){
                attackInstance.nextStage();
            }
        }else if (stage == 2){
            if (tick == 5){
                Vec3 fwd = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 slashPos = this.position().add(fwd.scale(-0.4)).add(0,1.25,0);
                SpriteParticleOptions options = SpriteParticleOptions.builder(BossParticles.NETZACH_SLASH)
                        .size(3.5f)
                        .lifetime(4)
                        .xyzRotation(15,0,0)
                        .lightenedUp()
                        .particleLookDirection(fwd)
                        .verticalRendering()
                        .build();
                ((ServerLevel)level()).sendParticles(options, slashPos.x, slashPos.y, slashPos.z, 1, 0,0,0,0);

            }else if (tick > 14){
                attackInstance.nextStage();
            }else if (tick >= 3){
                this.setSpawnGhosts(false);
            }
        }else if (stage == 3){
            this.setSpawnGhosts(true);
            if (this.dashingStage(target, tick, 5, 2, 0.5f)){
                Vec3 fwd = this.getLookAngle().multiply(1,0,1).normalize();
                Vec3 left = fwd.yRot(FDMathUtil.FPI / 2);
                Vec3 slashPos = this.position().add(fwd.scale(0.5f)).add(0,1.1,0).add(left.scale(-0.25));
                Vec3 circlePos1 = this.position().add(fwd.scale(0.25f)).add(0,1.1,0).add(left.scale(-0.25));
                SpriteParticleOptions options = SpriteParticleOptions.builder(BossParticles.NETZACH_POKE)
                        .size(2f)
                        .lifetime(4)
                        .lightenedUp()
                        .particleLookDirection(fwd)
                        .verticalRendering()
                        .build();
                SpriteParticleOptions options3 = SpriteParticleOptions.builder(BossParticles.NETZACH_POKE_CIRCLE)
                        .size(1.5f)
                        .lifetime(3)
                        .lightenedUp()
                        .particleLookDirection(fwd)
                        .build();
                ((ServerLevel)level()).sendParticles(options, slashPos.x, slashPos.y, slashPos.z, 1, 0,0,0,0);
                ((ServerLevel)level()).sendParticles(options3, circlePos1.x, circlePos1.y, circlePos1.z, 1, 0,0,0,0);
                attackInstance.nextStage();
            }


        }else if (stage == 4){



            if (tick > 3){
                Vec3 between = target.position().subtract(this.position());
                Vec3 horizontal = between.multiply(-1,0,-1);
                double len = horizontal.length();
                Vec3 pushVector = horizontal.add(0,len / 1.5f,0).normalize();
                this.setDeltaMovement(pushVector.scale(1.5));
                attackInstance.nextStage();
            }else if (tick > 1){
                this.setSpawnGhosts(false);
            }
            
        }else if (stage == 5){
            if (tick == 11) {
                Vec3 gearPos = this.position().add(0,this.getBbHeight() / 2 + 0.5, 0).add(this.getLookAngle());

                Vec3 between = target.position().subtract(gearPos);
                Vec3 hb = between.multiply(1,0,1);

                Vec3 targetPos = target.position().add(hb.normalize().reverse().scale(0));

                Vec3 speed = targetPos.subtract(gearPos).normalize().scale(2.5f);

                NetzachAerialGearAttack.summon(this, gearPos, speed);

            }else if (tick > 20){
                attackInstance.nextStage();
            }
        }else if (stage == 6){
            if (!this.onGround()){
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_FALL).build());
            }else{
                this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_FALL_END).build());
                attackInstance.nextStage();
            }
        }else if (stage == 7){
            this.getAnimationSystem().startAnimation(MAIN_LAYER, AnimationTicker.builder(BossAnims.NETZACH_FALL_END).build());
            if (tick > 10){
                return true;
            }
        }


        return false;
    }

    private boolean dashingStage(LivingEntity target, int tick, int moveStartTime, int dashTime, float distanceFromTarget){
        if (tick >= moveStartTime && tick <= moveStartTime + dashTime){
            this.dashToTarget(target,tick,moveStartTime,dashTime, distanceFromTarget);
            if (tick == moveStartTime + dashTime){
                return true;
            }
        } else if (tick > moveStartTime + dashTime){
            return true;
        }
        return false;
    }


    private void dashToTarget(LivingEntity target, int tick, int moveStartTime, int dashTime, float distanceFromTarget){
        Vec3 targetPos = target.position();
        Vec3 thisPos = this.position();
        Vec3 between = targetPos.subtract(thisPos);
        Vec3 hb = between.multiply(-1,0,-1);
        Vec3 moveToPos = targetPos.add(hb.normalize().scale(distanceFromTarget));

        int time = tick - moveStartTime;
        float p = Mth.clamp((float) time / dashTime,0, 1);
        float movePercent = FDMathUtil.lerp(0.9f,1,p);

        between = moveToPos.subtract(thisPos);

        this.setDeltaMovement(between.scale(movePercent));
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        return src.is(DamageTypes.GENERIC_KILL) && super.hurt(src, damage);
    }

    @Override
    protected double getDefaultGravity() {
        return this.entityData.get(GRAVITY);
    }

    public void setGravity(float gravity){
        this.entityData.set(GRAVITY, gravity);
    }

    public void setSpawnGhosts(boolean state){
        this.entityData.set(SPAWN_GHOSTS, state);
    }

    public boolean isSpawningGhosts(){
        return this.entityData.get(SPAWN_GHOSTS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GRAVITY, (float) Mob.DEFAULT_BASE_GRAVITY);
        builder.define(SPAWN_GHOSTS, false);
    }

    @Override
    public void setSpawnedBy(BossSpawnerEntity bossSpawnerEntity) {

    }

    @Override
    public void setSpawnPosition(Vec3 spawnPosition) {

    }

    @Override
    public BossSpawnerEntity getSpawner() {
        return null;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }
}
