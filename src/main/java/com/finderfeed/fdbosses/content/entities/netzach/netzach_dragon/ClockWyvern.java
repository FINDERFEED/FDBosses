package com.finderfeed.fdbosses.content.entities.netzach.netzach_dragon;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.ASParticleType;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.netzach.backtrack_entity.BacktrackEntity;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDMob;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackChain;
import com.finderfeed.fdlib.systems.entity.action_chain.AttackInstance;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.FDBlockParticleOptions;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClockWyvern extends FDMob implements AutoSerializable {

    public static final EntityDataAccessor<Vec3> WYVERN_RAY_TARGET = SynchedEntityData.defineId(ClockWyvern.class, FDEDataSerializers.VEC3.get());

    public static final String MAIN = "main";
    public static final String TURNING = "turning";

    public static final String DO_NOTHING = "do_nothing";
    public static final String FLY_UP_AND_SLAM = "fly_up_and_slam";
    public static final String TIMED_RAY_ATTACK = "delayed_ray_attack";

    public static FDModel animatedModel;
    public static FDModel model;

    private AttackChain attackChain;

    public ClockWyvern(EntityType<? extends FDMob> type, Level level) {
        super(type, level);
        if (animatedModel == null){
            animatedModel = new FDModel(BossModels.CLOCK_WYVERN.get());
            model = new FDModel(BossModels.CLOCK_WYVERN.get());
        }
        attackChain = new AttackChain(level.random);
        attackChain.registerAttack(DO_NOTHING, (e) -> this.doNothing(e, 60));
        attackChain.registerAttack(FLY_UP_AND_SLAM, this::flyUpAndSlamAttack);
        attackChain.registerAttack(TIMED_RAY_ATTACK, this::timedRayAttack);
        attackChain.addAttack(0, DO_NOTHING);
//        attackChain.addAttack(1, FLY_UP_AND_SLAM);
        attackChain.addAttack(2, TIMED_RAY_ATTACK);


        this.getAnimationSystem().setAnimationsApplyListener(this::onAnimationsApply);
        this.lookControl = new ClockWyvernLookControl(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            this.getAnimationSystem().startAnimation(TURNING, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_SPINNING_GEARS.get())
                    .setTransitionTime(5)
                    .build());

            if (tickCount % 500 < 250){
                this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE)
                        .build());
            }else{
                this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLYING)
                        .build());
            }

            this.setTarget(this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 100, false));
//            attackChain.tick();
            this.setYRot(this.yBodyRot);
            this.rotationAnimations();
        }else{
            this.chargeTimedRayAttackBallParticles();
        }

    }

    private boolean timedRayAttack(AttackInstance inst){
        if (this.getTarget() == null){
            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE.get())
                    .setTransitionTime(5)
                    .build());
            return true;
        }

        int stage = inst.stage;
        int tick = inst.tick;

        int backtrackTime = 117;

        if (stage == 0) {
            var target = this.getTarget();

            Vec3 groundPos = BossUtil.targetGroundPos(level(), target.position(), 60);
            BacktrackEntity.summon(target, backtrackTime, groundPos);
            this.setWyvernRayTarget(groundPos);

            if (target instanceof ServerPlayer player) {
                BossUtil.netzachBacktrackEffectFor(player, backtrackTime, 40);
            }

            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_TIMED_RAY_ATTACK)
                    .setTransitionTime(5)
                    .nextAnimation(AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE)
                            .setTransitionTime(5)
                            .build())
                    .build());
            inst.nextStage();
        }else if (stage == 1) {
            this.getLookControl().setLookAt(this.getWyvernRayTarget());
            if (tick > backtrackTime){
                inst.nextStage();
            }
        }else if (stage == 2){
            if (tick > 10){
                return true;
            }
        }

        return false;
    }

    private void chargeTimedRayAttackBallParticles(){
        var ticker = this.getAnimationSystem().getTicker(ClockWyvern.MAIN);
        if (ticker == null) return;
        var anim = ticker.getAnimation();

        if (anim != BossAnims.CLOCK_WYVERN_TIMED_RAY_ATTACK.get()) return;

        var time = ticker.getTime(0);

        if (time > 25 && time < 100){

            Matrix4f t = this.getModelPartTransformation(this, "head", animatedModel, 0);
            Vector3f pos = t.transformPosition(new Vector3f(ClockWyvernRenderer.BALL_OFFSET));
            Vec3 ballCenter = this.position().add(pos.x, pos.y, pos.z);

            SpriteParticleOptions spriteParticleOptions = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                    .lifetime(9)
                    .lightenedUp()
                    .friction(1.18f)
                    .size(0.2f + random.nextFloat() * 0.1f)
                    .xyzRotationSpeed(20 * BossUtil.randomPlusMinus(),0,0)
                    .quadSizeIncreasing()
                    .build();

            for (int i = 0; i < 2; i++){

                Vec3 offs = new Vec3(
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1
                ).normalize().scale(2);
                float speed = 0.05f;
                level().addParticle(spriteParticleOptions,
                        true,
                        ballCenter.x + offs.x,
                        ballCenter.y + offs.y,
                        ballCenter.z + offs.z,
                        -offs.x * speed,
                        -offs.y * speed,
                        -offs.z * speed
                );

            }



        }else if (time >= 120 && time <= 135){
            Vec3 explosionPos = this.getWyvernRayTarget();

            if (time == 120){
                BallParticleOptions flash = BallParticleOptions.builder()
                        .color(1f,0.8f,0.4f)
                        .scalingOptions(1,0,1)
                        .brightness(2)
                        .size(10f)
                        .build();
                level().addParticle(flash,true,explosionPos.x,explosionPos.y,explosionPos.z,0,0,0);
            }

            BallParticleOptions flash = BallParticleOptions.builder()
                    .color(1f,0.8f,0.4f)
                    .scalingOptions(1,0,1)
                    .brightness(2)
                    .size(3f)
                    .build();
            level().addParticle(flash,true,explosionPos.x,explosionPos.y,explosionPos.z,0,0,0);

            int smokesCount = 20;
            float smokesFriction = 0.8f;

            float maxSize = 2f;
            float minSize = 0.5f;

            for (int i = 0; i < smokesCount; i++){

                float smokeColor = 0.2f - random.nextFloat() * 0.1f;
                float size = minSize + FDEasings.easeIn(random.nextFloat()) * (maxSize - minSize);
                BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                        .color(new FDColor(smokeColor, smokeColor, smokeColor, 1f))
                        .friction(smokesFriction)
                        .size(size)
                        .minSpeed(0.02f)
                        .lifetime(2, 0,10)
                        .build();

                Vec3 randomDir = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                float speed = (1 - size / (maxSize - minSize)) * 2;
                level().addParticle(options, true,
                        explosionPos.x,
                        explosionPos.y + size / 4,
                        explosionPos.z,
                        randomDir.x * speed,
                        randomDir.y,
                        randomDir.z * speed
                );

            }

            Vec3 dir = explosionPos.subtract(this.position()).multiply(1,0,1).normalize();

            for (int i = 0; i < 10; i++){
                SpriteParticleOptions spark = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                        .quadSizeDecreasing()
                        .xyzRotation(10 * BossUtil.randomPlusMinus(),0,0)
                        .lifetime(10 + random.nextInt(5))
                        .friction(0.8f)
                        .size(0.3f + random.nextFloat() * 0.5f)
                        .build();

                Vec3 randomDir = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                float speed = 1f;
                float verticalSpeed = level().random.nextFloat() * 0.7f + 0.2f;
                float circleOffset = 0.5f;
                level().addParticle(spark, true,
                        explosionPos.x,
                        explosionPos.y,
                        explosionPos.z,
                        (randomDir.x + dir.x * circleOffset) * speed,
                        (randomDir.y + dir.y * circleOffset) * speed + verticalSpeed,
                        (randomDir.z + dir.z * circleOffset) * speed
                );

            }

            BlockPos pos = new BlockPos(
                    (int) Math.floor(explosionPos.x),
                    (int) Math.floor(explosionPos.y - 0.5f),
                    (int) Math.floor(explosionPos.z)
            );
            BlockState state = level().getBlockState(pos);

            if (!state.isAir()){
                for (int i = 0; i < 3; i++){
                    FDBlockParticleOptions blockParticleOptions = FDBlockParticleOptions.builder()
                            .quadSizeMultiplier(0.5f + random.nextFloat())
                            .state(state)
                            .lifetime(40)
                            .build();

                    Vec3 randomDir = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                    float speed = 0.25f + random.nextFloat() * 0.25f;
                    float verticalSpeed = level().random.nextFloat() * 0.25f + 0.2f;
                    float circleOffset = 0.5f;
                    level().addParticle(blockParticleOptions, true,
                            explosionPos.x,
                            explosionPos.y,
                            explosionPos.z,
                            (randomDir.x + dir.x * circleOffset) * speed,
                            (randomDir.y + dir.y * circleOffset) * speed + verticalSpeed,
                            (randomDir.z + dir.z * circleOffset) * speed
                    );

                }
            }

        }


    }

    @SerializableField
    private ProjectileMovementPath flyUpAndSlamPath;

    private boolean flyUpAndSlamAttack(AttackInstance attackInstance){
        if (this.getTarget() == null){
            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE.get())
                    .setTransitionTime(5)
                    .build());
            this.noPhysics = false;
            return true;
        }

        int stage = attackInstance.stage;
        int tick = attackInstance.tick;

        if (stage == 0){
            this.getAnimationSystem().startAnimation(MAIN, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_FLY_AND_SLAM.get())
                            .setTransitionTime(5)
                            .nextAnimation(AnimationTicker.builder(BossAnims.CLOCK_WYVERN_IDLE.get())
                                    .setTransitionTime(5)
                                    .build())
                    .build());
            this.getLookControl().setLookAt(this.getTarget());
            if (tick > 7){
                this.noPhysics = true;
                flyUpAndSlamPath = new ProjectileMovementPath(20, false)
                        .addPos(this.position())
                        .addPos(this.position().add(0,20,0))
                ;
                flyUpAndSlamPath.tick(this);
                attackInstance.nextStage();
            }
        }else if (stage == 1){
            this.getLookControl().setLookAt(this.getTarget());
            this.noPhysics = true;
            if (!flyUpAndSlamPath.isFinished()){
                flyUpAndSlamPath.tick(this);
            }else{
                attackInstance.nextStage();
            }
        }else if (stage == 2){
            if (tick >= 10){
                attackInstance.nextStage();
            }
        }else if (stage == 3){
            var target = this.getTarget();
            if (this.distanceTo(target) < 100){
                Vec3 groundPos = BossUtil.targetGroundPos(level(), target.position(), 40);
                ClipContext clipContext = new ClipContext(this.position(), groundPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                BlockHitResult result = level().clip(clipContext);
                Vec3 location = result.getLocation();
                flyUpAndSlamPath = new ProjectileMovementPath(8, false)
                        .addPos(this.position())
                        .addPos(location);
                attackInstance.nextStage();
            }else{
                this.noPhysics = false;
                return true;
            }
        }else if (stage == 4){
            Vec3 pos = this.flyUpAndSlamPath.getPositions().getLast();
            this.getLookControl().setLookAt(pos);
            this.noPhysics = true;
            if (!flyUpAndSlamPath.isFinished()){
                flyUpAndSlamPath.tick(this);
            }else{
                this.noPhysics = false;
                this.setDeltaMovement(0, 0, 0);
                this.teleportTo(pos.x, pos.y, pos.z);
                attackInstance.nextStage();
            }
        }else{
            this.noPhysics = false;
            if (tick > 20){
                return true;
            }else if (tick == 1){
                this.slam();
            }
        }
        return false;
    }

    private void rotationAnimations(){
        var mainAnimation = this.getAnimationSystem().getTickerAnimation(MAIN);
        if (mainAnimation == BossAnims.CLOCK_WYVERN_IDLE.get()){
            float rot = yRotO - this.getYRot();
            float terminalRotation = 0.4f;
            if (rot > terminalRotation){
                this.getAnimationSystem().startAnimation(TURNING, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_TURN_LEFT)
                        .setLoopMode(Animation.LoopMode.ONCE)
                        .setTransitionTime(5)
                        .build());
            }else if (rot < -terminalRotation){
                this.getAnimationSystem().startAnimation(TURNING, AnimationTicker.builder(BossAnims.CLOCK_WYVERN_TURN_RIGHT)
                        .setTransitionTime(5)
                        .setLoopMode(Animation.LoopMode.ONCE)
                        .build());
            }
        }
    }

    private void onAnimationsApply(FDModel fdModel, Float pticks) {
        var ticker = this.getAnimationSystem().getTicker(MAIN);
        if (ticker != null && ticker.getAnimation() == BossAnims.CLOCK_WYVERN_FLY_AND_SLAM.get()){
            float time = ticker.getTime(pticks);
            float startTime = 2.0f * 20;
            float endTime = 3f * 20;
            float p = Mth.clamp((time - startTime) / (endTime - startTime), 0, 1);
            if (p >= 0){
                float sqHill = FDEasings.squareHill(p);
                float xrot = this.getViewXRot(pticks);
                this.getAnimationSystem().setVariable("variable.body_rotation", sqHill * xrot);
            }
        }else{
            this.getAnimationSystem().setVariable("variable.body_rotation", 0);
        }
    }

    private boolean doNothing(AttackInstance inst, int time){
        return inst.tick > time;
    }

    private void slam(){
        BossUtil.clockWyvernSlam((ServerLevel) level(), this.position());
        level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 4f, 1f);
        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(10)
                .amplitude(10f)
                .inTime(0)
                .stayTime(0)
                .outTime(10)
                .build(),this.position(),40);

    }

    public void setWyvernRayTarget(Vec3 pos){
        this.entityData.set(WYVERN_RAY_TARGET, pos);
    }

    public Vec3 getWyvernRayTarget(){
        return this.entityData.get(WYVERN_RAY_TARGET);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WYVERN_RAY_TARGET, Vec3.ZERO);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new ClockWyvernBodyRotationControl(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave(tag);
        Vec3 rayTarget = this.getWyvernRayTarget();
        tag.putDouble("ray_target_x", rayTarget.x);
        tag.putDouble("ray_target_y", rayTarget.y);
        tag.putDouble("ray_target_z", rayTarget.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad(tag);
        this.setWyvernRayTarget(new Vec3(
                tag.getDouble("ray_target_x"),
                tag.getDouble("ray_target_y"),
                tag.getDouble("ray_target_z")
        ));
    }

    @Override
    public int getHeadRotSpeed() {
        return 10;
    }

}
