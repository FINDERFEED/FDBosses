package com.finderfeed.fdbosses.content.entities.netzach.netzach_ghost_crush;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossModels;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetzachGhostCrush extends Entity implements AutoSerializable {

    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(NetzachGhostCrush.class, EntityDataSerializers.INT);

    private static FDModel bodyModel;
    private static FDModel layerModel;

    public static final int DESCENT_TICKS = 3;
    public static final int FADE_TICKS = 20;
    private static final int DEFAULT_DURATION = 40;

    @SerializableField
    private float radius = 4;

    @SerializableField
    private float damage = 10;

    public static FDModel getBodyModel(){
        if (bodyModel == null){
            bodyModel = new FDModel(BossModels.NETZACH.get());
        }
        return bodyModel;
    }

    public static FDModel getLayerModel(){
        if (layerModel == null){
            layerModel = new FDModel(BossModels.NETZACH_LAYER.get());
        }
        return layerModel;
    }


    public NetzachGhostCrush(EntityType<?> type, Level level){
        super(type, level);
        getLayerModel();
        getBodyModel();
        this.noCulling = true;
    }

    public static NetzachGhostCrush summon(Level level, Vec3 center, Vec3 direction, int duration, float radius, float damage){
        var attack = new NetzachGhostCrush(BossEntities.NETZACH_GHOST_CRUSH.get(), level);

        direction = direction.multiply(1,0,1).normalize();

        attack.setPos(center);
        attack.lookAt(EntityAnchorArgument.Anchor.FEET, center.add(direction.scale(100)));
        attack.entityData.set(DURATION, duration);
        attack.radius = radius;
        attack.damage = damage;
        level.addFreshEntity(attack);

        for (Vec3 dir : new HorizontalCircleRandomDirections(level.random, 4, 0)){
            var arc = new ArcAttackPreparationParticleOptions(dir, radius, FDMathUtil.FPI / 4,
                    duration, Math.round(duration * 0.25f), 10, 1, 0.8f, 0.4f, 0.2f);
            FDLibCalls.sendParticles((ServerLevel) level, arc, attack.position().add(0, 0.05, 0), 120);
        }

        return attack;
    }

    @Override
    public void tick(){
        super.tick();
        if (!level().isClientSide){
            int duration = this.getDuration();

            if (this.tickCount == duration){
                BossUtil.netzachCrush((ServerLevel) level(), this.position(), 60);
                for (var target : FDTargetFinder.getEntitiesInSphere(LivingEntity.class, level(), this.position(), this.radius)){
                    target.hurt(this.damageSources().magic(), this.damage);
                }
            }

            if (this.tickCount >= duration + FADE_TICKS){
                this.discard();
            }

        }
    }

    public int getDuration(){
        return this.entityData.get(DURATION);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){
        builder.define(DURATION, DEFAULT_DURATION);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag){
        this.autoSave(tag);
        tag.putInt("duration", this.getDuration());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag){
        this.autoLoad(tag);
        int duration = tag.getInt("duration");
        this.entityData.set(DURATION, duration > 0 ? duration : DEFAULT_DURATION);
    }
}
