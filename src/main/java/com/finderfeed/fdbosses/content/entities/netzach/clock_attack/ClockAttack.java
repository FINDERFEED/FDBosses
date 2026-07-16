package com.finderfeed.fdbosses.content.entities.netzach.clock_attack;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlock;
import com.finderfeed.fdbosses.content.entities.netzach.NetzachEntity;
import com.finderfeed.fdbosses.content.util.AttackTimings;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterData;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.BoundToEntityProcessor;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.client.particles.FDBlockParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ClockAttack extends FDEntity implements AutoSerializable {

    public static final int RADIUS = NetzachEntity.ARENA_RADIUS;

    public static final float ROTATION_SPEED = FDMathUtil.FPI / 14;

    private static FDModel model;

    public static final EntityDataAccessor<AttackTimings> ATTACK_TIMINGS = SynchedEntityData.defineId(ClockAttack.class, BossEntityDataSerializers.ATTACK_TIMINGS.get());
    public static final EntityDataAccessor<Float> ROTATION_ANGLE = SynchedEntityData.defineId(ClockAttack.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> FINISHED_ROTATION = SynchedEntityData.defineId(ClockAttack.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity cachedTarget;
    private UUID target;

    public float previousRotationAngle = -1;

    @SerializableField
    public float rotationAngle;

    @SerializableField
    public int afterRotatedTicks = -1;

    private int passDelay = 5;

    @SerializableField
    private boolean hasRotatedThroughPlayerOnce = false;

    private Vec3 previousTargetPos;

    @SerializableField
    private AttackTimings attackTimings = new AttackTimings()
            .addAttackTiming(40)
            .addAttackTiming(100);

    public static FDModel getCLientModel(){
        if (model == null){
            model = new FDModel(BossModels.CLOCK_ATTACK_ARROW.get());
        }
        return model;
    }

    public static ClockAttack summon(Level level, Vec3 pos, int prepareTime, int rotateTime, LivingEntity target){
        ClockAttack clockAttack = new ClockAttack(BossEntities.CLOCK_ATTACK.get(), level);
        clockAttack.setPos(pos);
        clockAttack.previousTargetPos = target.position();
        clockAttack.cachedTarget = target;
        clockAttack.target = target.getUUID();

        Vec3 b = target.position().subtract(pos);
        var angle = Math.atan2(b.z, b.x) + FDMathUtil.FPI * 2 + FDMathUtil.FPI / 16;
        clockAttack.rotationAngle = (float) angle;
        clockAttack.getEntityData().set(ROTATION_ANGLE, (float) angle);

        clockAttack.setAttackTimings(new AttackTimings()
                .addAttackTiming(prepareTime)
                .addAttackTiming(rotateTime));
        level.addFreshEntity(clockAttack);
        return clockAttack;
    }

    public ClockAttack(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()){

            if (target == null){
                this.setRemoved(RemovalReason.DISCARDED);
                return;
            }

            if (cachedTarget == null){
                cachedTarget = (LivingEntity) ((ServerLevel)level()).getEntity(target);
                if (cachedTarget == null){
                    this.setRemoved(RemovalReason.DISCARDED);
                    return;
                }
                previousTargetPos = cachedTarget.position();
            }


            this.previousRotationAngle = rotationAngle;

            if (this.afterRotatedTicks != -1){
                this.entityData.set(FINISHED_ROTATION, true);
                float rotationPercent = this.attackTimings.getAttackTimingPercent(1, afterRotatedTicks);

                var rp = FDEasings.squareHill(rotationPercent);
                float rotationSpeed = rp * ROTATION_SPEED;
                this.rotationAngle += rotationSpeed;
                this.getEntityData().set(ROTATION_ANGLE, this.rotationAngle);

                if (this.afterRotatedTicks == this.attackTimings.getAttackLength(0)){
                    this.serverFallFX();
                }

                if (rotationSpeed > 0){

                    this.serverRotation(rp);

                    float halfAngle = (this.rotationAngle - this.previousRotationAngle) / 2;

                    Vec3 dir = new Vec3(1,0,0).yRot((this.rotationAngle - halfAngle));
                    var targets = FDTargetFinder.getEntitiesInArc(LivingEntity.class, level(), this.position().add(0,-1,0),
                            new Vec2((float) dir.x, (float) dir.z),
                            this.rotationAngle - this.previousRotationAngle,
                            1.5f,RADIUS
                            );

                    for (var target : targets){
                        target.hurt(level().damageSources().magic(), 1);
                    }

                }

                if (rotationPercent >= 1){
                    this.remove(RemovalReason.DISCARDED);
                }



                afterRotatedTicks++;
            }else{
                this.rotateToTarget(FDMathUtil.FPI / 6);
            }


            previousTargetPos = cachedTarget.position();
        }else{

            previousRotationAngle = rotationAngle;
            rotationAngle = this.getEntityData().get(ROTATION_ANGLE);

            float rotationPercent = this.attackTimings.getAttackTimingPercent(1, afterRotatedTicks);

            if (rotationPercent != 0 && rotationPercent != 1){
                float rotationSpeed = FDEasings.squareHill(rotationPercent) * 0.9f + 0.1f;
                this.rotationFX(rotationSpeed);
            }

            if (this.afterRotatedTicks == this.attackTimings.getAttackLength(0)){
                this.clientFallFX();
            }



            if (this.getEntityData().get(FINISHED_ROTATION)){
                this.afterRotatedTicks++;
            }

        }
    }


    private void rotationFX(float strength){
        Vec3 v = new Vec3(1,0,0).yRot(this.rotationAngle + ROTATION_SPEED * strength / 2);

        for (int i = 0; i < RADIUS / 2; i++) {

            float r = i * 2 + random.nextFloat() * 2;

            Vec3 pos = this.position().add(v.scale(r));

            ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                    .colorStart(new FDColor(1f, 1f, 1f, 1f))
                    .colorEnd(new FDColor(1f, 0.8f, 0.3f, 1f))
                    .maxPointsInTrail(2)
                    .reflectionStrength(0.33f)
                    .gravity(2.5f)
                    .lifetime(-1)
                    .maxJumpAmount(0)
                    .size(0.02f)
                    .build();

            float vspeed = strength * (0.25f + random.nextFloat() * 0.25f);
            float hspeed = strength * (0.15f + random.nextFloat() * 0.35f);
            Vec3 speed = v.yRot(-FDMathUtil.FPI / 2).scale(hspeed).add(0,vspeed,0);

            level().addParticle(options, true, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);

            var state = level().getBlockState(new BlockPos(
                    (int) Math.floor(pos.x),
                    (int) Math.floor(pos.y - 1),
                    (int) Math.floor(pos.z)
            ));

            if (!state.isEmpty()){
                r = i * 2 + random.nextFloat() * 2;
                pos = this.position().add(v.scale(r));
                vspeed = strength * (0.05f + random.nextFloat() * 0.25f);
                hspeed = strength * (0.15f + random.nextFloat() * 0.35f);

                speed = v.yRot(-FDMathUtil.FPI / 2).scale(hspeed).add(0,vspeed,0);

                FDBlockParticleOptions blockParticle = FDBlockParticleOptions.builder()
                        .lifetime(10 + random.nextInt(5))
                        .quadSizeMultiplier(1f)
                        .state(state)
                        .build();
                level().addParticle(blockParticle, true, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);

            }

        }

    }


    private Vec3 cachedPos = null;

    private void rotateToTarget(float rotationSpeed){

        Vec3 pos = this.getApproximatedTargetPos();

        if (cachedPos != null){
            pos = cachedPos;
        }

        Vec3 b = pos.subtract(this.position());

        var angle = (float) -Math.atan2(b.z, b.x);
        var transformedAngle = this.rotationAngle - angle;
        var next = transformedAngle + rotationSpeed;
        Vec3 prevVec = new Vec3(1,0,0).yRot(-transformedAngle);
        Vec3 nextVec = new Vec3(1,0,0).yRot(-next);

        if (this.hasRotatedThroughPlayerOnce){
            if (passDelay <= 0){

                var modded = transformedAngle % (FDMathUtil.FPI * 2);
                var diff = FDMathUtil.FPI * 2 - modded;

                float speedCoefficient = (float) (1 - Math.exp(-1 * diff));

                this.rotationAngle += rotationSpeed * speedCoefficient;

                if (speedCoefficient < 0.001){
                    this.afterRotatedTicks = 0;
                    this.entityData.set(FINISHED_ROTATION, true);

                }else if (speedCoefficient < 0.9 && cachedPos == null){
                    cachedPos = pos;
                }

            }else{
                passDelay--;
                this.rotationAngle = next + angle;
            }
        }else{
            if (prevVec.z <= 0 && nextVec.z >= 0){
                this.hasRotatedThroughPlayerOnce = true;
                passDelay = 5;
            }
            this.rotationAngle = next + angle;
        }

        this.entityData.set(ROTATION_ANGLE, this.rotationAngle);

    }

    private void serverRotation(float strength){
        Vec3 v = new Vec3(1,0,0).yRot(this.rotationAngle);

        Vec3 pos = this.position().add(v.scale(RADIUS / 2f));


        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.MACE_SMASH_AIR, SoundSource.HOSTILE, 3f, 0.25f * strength + 0.75f);

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(10 * strength)
                .amplitude(2 * strength)
                .inTime(0)
                .stayTime(0)
                .outTime(4)
                .build(),pos,40);
    }

    private void serverFallFX(){

        level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 3f, 1f);
        level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND, SoundSource.HOSTILE, 3f, 1f);

        Vec3 v = new Vec3(1,0,0).yRot(this.rotationAngle);

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(10)
                .amplitude(2f)
                .inTime(0)
                .stayTime(0)
                .outTime(10)
                .build(),this.position().add(v.scale(RADIUS / 2f)),100);

        for (int i = 0; i < RADIUS / 2; i++){

            float r = i * 2 + random.nextFloat() * 2;

            Vec3 pos = this.position().add(v.scale(r));

            Vec3 dir = v.yRot(FDMathUtil.FPI / 2 * (i % 2 == 0 ? 1 : -1));

            Vec3 speed = dir.scale(random.nextFloat() * 0.35f + 0.2f).add(0,0.25 + random.nextFloat() * 0.5f,0);

            var state = level().getBlockState(new BlockPos(
                    (int) Math.floor(pos.x),
                    (int) Math.floor(pos.y - 1),
                    (int) Math.floor(pos.z)
            ));


            if (!state.isEmpty()) {
                ChesedFallingBlock chesedFallingBlock = ChesedFallingBlock.summon(level(), state, pos, speed, 0, (float) Player.DEFAULT_BASE_GRAVITY * 0.75f);

                float rnd = random.nextFloat() * 0.05f;
                FDLibCalls.addParticleEmitter(level(), 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                                .color(0.35f - rnd, 0.35f - rnd, 0.35f - rnd)
                                .lifetime(0, 0, 10)
                                .size(1.5f)
                                .build())
                        .lifetime(200)
                        .processor(new BoundToEntityProcessor(chesedFallingBlock.getId(), Vec3.ZERO))
                        .position(pos)
                        .build());
            }
        }

    }

    private void clientFallFX(){

        Vec3 v = new Vec3(1,0,0).yRot(this.rotationAngle);

        for (int i = 0; i < RADIUS * 4; i++){

            float r = i / 4f + random.nextFloat() * 0.25f;

            Vec3 pos = this.position().add(v.scale(r));

            Vec3 dir = v.yRot(FDMathUtil.FPI / 2 * (i % 2 == 0 ? 1 : -1));

            Vec3 speed = dir.scale(random.nextFloat() * 0.35f + 0.2f).add(0,0.25 + random.nextFloat() * 0.5f,0);

            var state = level().getBlockState(new BlockPos(
                    (int) Math.floor(pos.x),
                    (int) Math.floor(pos.y - 1),
                    (int) Math.floor(pos.z)
            ));

            if (!state.isEmpty()) {

                FDBlockParticleOptions blockParticle = FDBlockParticleOptions.builder()
                        .lifetime(10 + random.nextInt(10))
                        .quadSizeMultiplier(1f)
                        .state(state)
                        .build();
                level().addParticle(blockParticle, true, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);

            }
        }

    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (level().isClientSide) {
            this.disappearFX();
        }

    }

    private void disappearFX(){

        Vec3 v = new Vec3(1,0,0).yRot(this.rotationAngle);

        for (int i = 0; i < RADIUS * 4; i++){

            float r = i / 4 + random.nextFloat() * 0.25f;

            Vec3 pos = this.position().add(v.scale(r));

            Vec3 dir = v.yRot(FDMathUtil.FPI / 2 * (i % 2 == 0 ? 1 : -1));

            Vec3 speed = dir.scale(random.nextFloat() * 0.5f + 0.05f).add(0, FDEasings.easeIn(random.nextFloat()) * 0.5f,0);

            SpriteParticleOptions options = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK.get())
                    .lifetime(10 + random.nextInt(10))
                    .alphaDecreasing()
                    .xyzRotation(BossUtil.randomPlusMinus() * 20, 0, 0)
                    .frictionAffectsRotation()
                    .friction(0.6f)
                    .size(0.15f)
                    .lightenedUp()
                    .build();

            level().addParticle(options, true, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);


        }

    }


    public Vec3 getApproximatedTargetPos(){

        Vec3 currentPos = this.cachedTarget.position();
        Vec3 oldPos = this.previousTargetPos;

        Vec3 b = currentPos.subtract(oldPos);

        var scale = this.attackTimings.getAttackLength(0) * 1.9f;

        return currentPos.add(b.scale(scale));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ATTACK_TIMINGS, new AttackTimings()
                .addAttackTiming(40)
                .addAttackTiming(100));
        builder.define(FINISHED_ROTATION, false);
        builder.define(ROTATION_ANGLE, 0f);
    }

    private void setAttackTimings(AttackTimings attackTimings){
        this.attackTimings = attackTimings;
        this.entityData.set(ATTACK_TIMINGS, attackTimings);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_20059_) {
        super.onSyncedDataUpdated(p_20059_);
        if (p_20059_ == ATTACK_TIMINGS) {
            this.attackTimings = this.entityData.get(ATTACK_TIMINGS);
        }else if (p_20059_ == ROTATION_ANGLE){
            if (previousRotationAngle == -1){
                rotationAngle = this.getEntityData().get(ROTATION_ANGLE);
                previousRotationAngle = this.getEntityData().get(ROTATION_ANGLE);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.autoLoad("edata", tag);
        if (tag.contains("target")){
            this.target = tag.getUUID("target");
        }
        this.getEntityData().set(ATTACK_TIMINGS, attackTimings);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.autoSave("edata", tag);
        if (this.target != null) {
            tag.putUUID("target", this.target);
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
    public AABB getBoundingBoxForCulling() {
        return AABB.INFINITE;
    }

}
