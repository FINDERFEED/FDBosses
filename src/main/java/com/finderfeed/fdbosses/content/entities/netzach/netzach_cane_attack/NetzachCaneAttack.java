package com.finderfeed.fdbosses.content.entities.netzach.netzach_cane_attack;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlock;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterData;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.BoundToEntityProcessor;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NetzachCaneAttack extends Entity implements AutoSerializable {

    private static final EntityDataAccessor<Integer> CHARGE_TICKS = SynchedEntityData.defineId(NetzachCaneAttack.class, EntityDataSerializers.INT);

    private static final int DEFAULT_CHARGE_TICKS = 40;
    static final int FALL_TICKS = 5;
    static final int FADE_TICKS = 20;

    private static FDModel caneModel;
    private static FDModel caneLayerModel;

    @SerializableField
    private float damage = 10;

    @SerializableField
    private float radius = 6;

    protected float tilt;

    public NetzachCaneAttack(EntityType<?> type, Level level){
        super(type, level);
        this.noCulling = true;
        this.tilt = level.isClientSide ? (5 + this.random.nextFloat() * 7) * (this.random.nextBoolean() ? 1 : -1) : 0;
    }

    public static FDModel getCaneModel(){
        if (caneModel == null){
            caneModel = new FDModel(BossModels.NETZACH_CANE.get());
        }
        return caneModel;
    }

    public static FDModel getCaneLayerModel(){
        if (caneLayerModel == null){
            caneLayerModel = new FDModel(BossModels.NETZACH_CANE_LAYER.get());
        }
        return caneLayerModel;
    }

    public static NetzachCaneAttack summon(Level level, Vec3 center, Vec3 direction, float damage, float radius, int chargeTicks){
        var attack = new NetzachCaneAttack(BossEntities.NETZACH_CANE_ATTACK.get(), level);
        attack.setPos(center);
        attack.lookAt(EntityAnchorArgument.Anchor.FEET, center.add(direction.multiply(1, 0, 1).normalize().scale(100)));
        attack.damage = damage;
        attack.radius = radius;
        attack.entityData.set(CHARGE_TICKS, chargeTicks);
        level.addFreshEntity(attack);
        return attack;
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.level().isClientSide){
            if (this.tickCount == FALL_TICKS && this.getChargeTicks() > 0){
                this.showPreparation();
            }

            if (this.tickCount == this.getExplosionTick()){
                this.explode();
            }else if (this.tickCount >= this.getExplosionTick() + FADE_TICKS){
                this.discard();
            }else if (this.tickCount == FALL_TICKS){
                PositionedScreenShakePacket.send((ServerLevel) this.level(), FDShakeData.builder()
                                .frequency(10f)
                                .amplitude(7)
                                .inTime(0)
                                .stayTime(0)
                                .outTime(6).build(), this.position(),
                        60);
            }

        }
    }

    private void showPreparation(){
        int chargeTicks = this.getChargeTicks();
        for (Vec3 direction : new HorizontalCircleRandomDirections(this.random, 4, 0)){
            var arc = new ArcAttackPreparationParticleOptions(direction, this.radius, FDMathUtil.FPI / 4, chargeTicks, Math.max(1, Math.round(chargeTicks * 0.25f)), 10, 1, 0.05f, 0.05f, 0.3f);
            FDLibCalls.sendParticles((ServerLevel) this.level(), arc, this.position().add(0, 0.05, 0), 120);
        }
    }

    private void explode(){
        Vec3 center = this.position();
        for (var target : FDTargetFinder.getEntitiesInCylinder(LivingEntity.class, this.level(), center, this.radius * 2, this.radius)){
            target.hurt(this.damageSources().magic(), this.damage);
        }
        BossUtil.netzachCaneStrike(this.level(), center, this.radius);
        PositionedScreenShakePacket.send((ServerLevel) this.level(), FDShakeData.builder()
                .frequency(10f)
                .amplitude(15)
                .inTime(0)
                .stayTime(0)
                .outTime(6).build(), center,
                60);
        this.spawnDebris();
    }

    private void spawnDebris(){
        Level level = this.level();

        for (Vec3 direction : new HorizontalCircleRandomDirections(this.random, 24, 1f)){
            Vec3 sample = this.position().add(direction);
            var hit = level.clip(new ClipContext(sample.add(0, 1, 0), sample.add(0, -2, 0),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            if (hit.getType() == HitResult.Type.MISS) continue;

            float horizontal = 0.1f + random.nextFloat() * 0.3f;
            float vertical = 1f + random.nextFloat() * 1f;
            Vec3 velocity = direction.scale(horizontal).add(0, vertical, 0);

            var block = ChesedFallingBlock.summon(level, level.getBlockState(hit.getBlockPos()), this.position().add(direction)
                    .add(0,0.05f,0), velocity, 0, 0.075f);

            block.lessParticles = true;
            block.softerSound = true;

            float color = random.nextFloat() * 0.1f + 0.2f;
            FDLibCalls.addParticleEmitter(level, 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                            .color(color, color, color).lifetime(0, 0, 25).size(1.5f).build())
                    .lifetime(200).processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                    .position(this.position()).build());
        }


        for (int i = 2; i < radius; i++){

            float p = i / (radius - 1);
            float md = p * p;
            int count = (int) Math.ceil(md * FDMathUtil.lerp(radius, radius * 3,1 - md));

            for (var direction : new HorizontalCircleRandomDirections(random, count, 1f)){
                Vec3 scaledDir = direction.scale(i + random.nextFloat() * 0.25f);

                Vec3 sample = this.position().add(scaledDir);
                var hit = level.clip(new ClipContext(sample.add(0, 1, 0), sample.add(0, -2, 0),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

                if (hit.getType() == HitResult.Type.MISS) continue;

                float horizontal = 0.1f + random.nextFloat() * FDMathUtil.lerp(0.1f, 0.75f,1 - md);
                float vertical = 0.05f + random.nextFloat() * FDMathUtil.lerp(0.05f, 1.25f, 1 - md);
                Vec3 velocity = direction.scale(horizontal).add(0, vertical, 0);

                var block = ChesedFallingBlock.summon(level, level.getBlockState(hit.getBlockPos()), this.position().add(scaledDir)
                        .add(0,0.05f,0), velocity, 0, 0.075f);

                block.lessParticles = true;
                block.softerSound = true;

                float color = random.nextFloat() * 0.1f + 0.2f;
                FDLibCalls.addParticleEmitter(level, 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                                .color(color, color, color).lifetime(0, 0, 25).size(1.5f).build())
                        .lifetime(200).processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                        .position(this.position()).build());
            }

        }

    }

    private int getChargeTicks(){
        return this.entityData.get(CHARGE_TICKS);
    }

    public int getExplosionTick(){
        return FALL_TICKS + this.getChargeTicks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){
        builder.define(CHARGE_TICKS, DEFAULT_CHARGE_TICKS);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        this.autoSave(tag);
        tag.putInt("charge_ticks", this.getChargeTicks());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        this.autoLoad(tag);
        this.entityData.set(CHARGE_TICKS, tag.getInt("charge_ticks"));
    }
}
