package com.finderfeed.fdbosses.content.entities.netzach.netzach_clock_pendulum;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticle;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.content.util.AttackTimings;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class NetzachClockPendulum extends FDEntity {

    public static final int PENDULUM_APPEAR = 0;
    public static final int PENDULUM_WAIT_1 = 1;
    public static final int PENDULUM_ATTACK = 2;
    public static final int PENDULUM_WAIT_2 = 3;
    public static final int PENDULUM_DISAPPEAR = 4;

    public static final EntityDataAccessor<Float> PENDULUM_ATTACK_LENGTH = SynchedEntityData.defineId(NetzachClockPendulum.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<AttackTimings> PENDULUM_ATTACK_TIMINGS = SynchedEntityData.defineId(NetzachClockPendulum.class, BossEntityDataSerializers.ATTACK_TIMINGS.get());

    public static void summon(Level level, Vec3 startPos, Vec3 direction, float attackLength, int attackDuration){
        NetzachClockPendulum pendulum = new NetzachClockPendulum(BossEntities.NETZACH_CLOCK_PENDULUM.get(), level);
        pendulum.setAttackLength(attackLength);
        pendulum.setPos(startPos);
        pendulum.lookAt(EntityAnchorArgument.Anchor.FEET, startPos.add(direction));

        AttackTimings attackTimings = new AttackTimings(10)
                .addAttackTiming(10)
                .addAttackTiming(attackDuration)
                .addAttackTiming(10)
                .addAttackTiming(30);

        pendulum.getEntityData().set(PENDULUM_ATTACK_TIMINGS, attackTimings);

        level.addFreshEntity(pendulum);
    }

    public NetzachClockPendulum(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        var timings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);

        if (!level().isClientSide){
            if (tickCount > timings.getFullTiming()){
                this.remove(RemovalReason.DISCARDED);
            }

            this.tickDamage();

            this.impactBlocks(timings);
        }else{
            this.impactParticles(timings);
        }
    }

    private void impactParticles(AttackTimings timings){
        int tick = timings.getAttackTimingTick(PENDULUM_WAIT_1, tickCount);
        if (tick == 0){


            Vec3 startPos = this.getCurrentPendulumWorldPos();

            for (var dir : new HorizontalCircleRandomDirections(level().random, 20, 1f)){

                ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                        .colorStart(new FDColor(1f, 1f, 1f, 1f))
                        .colorEnd(new FDColor(1f, 0.8f, 0.3f, 1f))
                        .maxPointsInTrail(2)
                        .reflectionStrength(0.33f)
                        .gravity(1f)
                        .lifetime(-1)
                        .maxJumpAmount(0)
                        .size(0.02f)
                        .build();

                float horizontalSpeed = random.nextFloat() * 0.5f + 0.05f;

                float verticalSpeed = random.nextFloat() * 0.3f + 0.2f;


                Vec3 ppos = startPos.add(
                        dir.scale(0.5f)
                );

                level().addParticle(options,ppos.x,ppos.y,ppos.z,dir.x * horizontalSpeed, verticalSpeed,dir.z * horizontalSpeed);
            }

        }
    }

    private void impactBlocks(AttackTimings timings){
        int tick = timings.getAttackTimingTick(PENDULUM_WAIT_1, tickCount);
        if (tick == 0){
            int c = 4;
            float angle = FDMathUtil.FPI * 2 / c;

            Vec3 startPos = this.getCurrentPendulumWorldPos();

            for (int i = 0; i < c; i++ ){
                Vec3 d = new Vec3(1,0,0).yRot(angle * i);
                BossUtil.createOnEarthBlockExplosionEffect(level(), startPos, d, 2, 0.6f,Blocks.STONE.defaultBlockState());
            }
        }
    }

    private Vec3 getCurrentPendulumWorldPos(){
        var timings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);
        float p1 = FDEasings.easeInOut(timings.getAttackTimingPercent(PENDULUM_ATTACK, tickCount));

        Vec3 dir = this.getLookAngle().multiply(1,0,1).normalize();
        var length = this.getAttackLength();
        float l1 = FDMathUtil.lerp(-length,length,p1);
        Vec3 startPos = dir.scale(l1).add(this.position());
        return startPos;
    }

    private void tickDamage(){
        var timings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);
        if (timings.isTimeForAttack(PENDULUM_ATTACK, tickCount)){

            float p1 = FDEasings.easeInOut(timings.getAttackTimingPercent(PENDULUM_ATTACK, tickCount));
            float p2 = FDEasings.easeInOut(timings.getAttackTimingPercent(PENDULUM_ATTACK, tickCount + 1));

            Vec3 dir = this.getLookAngle().multiply(1,0,1).normalize();

            var length = this.getAttackLength();

            float l1 = FDMathUtil.lerp(-length,length,p1);
            float l2 = FDMathUtil.lerp(-length,length,p2);

            float width = 6f;
            Vec3 startPos = dir.scale(l1 - width / 2).add(this.position().add(0,-1,0));


            var entities = FDTargetFinder.getEntitiesInHorizontalBox(LivingEntity.class, level(), startPos, new Vec2((float)dir.x, (float) dir.z), l2 - l1 + width, width, 5f, (e)->{
                return true;
            });

            for (var entity : entities){
                entity.hurt(level().damageSources().magic(), 1);
            }

        }
    }

    private AttackTimings defaultTimings(){
        return new AttackTimings(20)
                .addAttackTiming(10)
                .addAttackTiming(100)
                .addAttackTiming(10)
                .addAttackTiming(20);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PENDULUM_ATTACK_LENGTH, 10f);
        builder.define(PENDULUM_ATTACK_TIMINGS,
                this.defaultTimings()
        );
    }

    public void setAttackLength(float attackLength){
        this.getEntityData().set(PENDULUM_ATTACK_LENGTH, attackLength);
    }

    public float getAttackLength(){
        return this.getEntityData().get(PENDULUM_ATTACK_LENGTH);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("attackLength", this.getAttackLength());

        AttackTimings attackTimings = this.getEntityData().get(PENDULUM_ATTACK_TIMINGS);
        CompoundTag compoundTag = new CompoundTag();
        attackTimings.autoSave(compoundTag);
        tag.put("attackTimings", compoundTag);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAttackLength(tag.getFloat("attackLength"));

        if (tag.contains("attackTimings")) {
            AttackTimings attackTimings = new AttackTimings();
            attackTimings.autoLoad(tag.getCompound("attackTimings"));
            this.getEntityData().set(PENDULUM_ATTACK_TIMINGS, attackTimings);
        }else{
            this.getEntityData().set(PENDULUM_ATTACK_TIMINGS, this.defaultTimings());
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

}
