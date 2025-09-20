package com.finderfeed.fdbosses.content.entities.chesed_boss.electric_sphere;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLib;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.client.particles.lightning_particle.LightningParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChesedElectricSphereEntity extends FDLivingEntity implements AutoSerializable, ChesedBossBuddy {

    @SerializableField
    private ProjectileMovementPath path;

    @SerializableField
    private float damage;


    public ChesedElectricSphereEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        if (level().isClientSide){
            this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.ELECTRIC_ORB_IDLE.get()).build());
        }
    }

    public static ChesedElectricSphereEntity summon(Level level,float damage, ProjectileMovementPath path){

        ChesedElectricSphereEntity sphereEntity = new ChesedElectricSphereEntity(BossEntities.CHESED_ELECTRIC_SPHERE.get(),level);
        sphereEntity.setPos(path.getPositions().get(0));
        sphereEntity.path = path;
        sphereEntity.damage = damage;
        level.addFreshEntity(sphereEntity);

        return sphereEntity;
    }

    @Override
    public void tick() {
        noPhysics = true;
        super.tick();
        if (!level().isClientSide) {
            if (this.path != null) {
                if (!path.isFinished()) {
                    this.path.tick(this);
                }else{
                    this.explode();
                    this.discard();
                }
            }
            this.detectEntitiesAndExplode();
        }else{
            this.idleParticles();
            this.getAnimationSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.ELECTRIC_ORB_IDLE.get()).build());
        }
    }


    private void idleParticles(){

        if (tickCount < 10) return;
        if (tickCount % 3 == 0) {
            for (int i = 0; i < 1; i++) {
                float offs = 0.25f;
                Vec3 p1 = this.position().add(random.nextFloat() * 0.025 - 0.0125, offs, random.nextFloat() * 0.025 - 0.0125);
                Vec3 p2 = this.position().add(0, this.getBbHeight() - offs, 0);

                Vec3 sp = this.getDeltaMovement();
                level().addParticle(ArcLightningOptions.builder(BossParticles.ARC_LIGHTNING.get())
                                .end(p2.x, p2.y, p2.z)
                                .endSpeed(sp)
                                .lifetime(2)
                                .color(1 + random.nextInt(40), 183 + random.nextInt(60), 165 + random.nextInt(60))
                                .lightningSpread(0.25f)
                                .width(0.1f)
                                .segments(6)
                                .circleOffset(0.25f)
                                .build(),
                        false, p1.x, p1.y, p1.z, sp.x, sp.y, sp.z
                );
            }
        }

    }


    private void detectEntitiesAndExplode(){
        var list = level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox(),living->{
            return !(living instanceof ChesedBossBuddy);
        });
        if (list.isEmpty()) return;

        this.explode();

        this.discard();
    }

    private void explode(){
        var list = level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(0.2,0.2,0.2),living->{
            if (living instanceof ChesedBossBuddy) return false;
            return true;
        });

        DamageSource source = BossDamageSources.CHESED_ELECTRIC_SPHERE_SOURCE;

        level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.FAST_LIGHTNING_STRIKE.get(), SoundSource.HOSTILE, 1f, 1f);

        FDLibCalls.sendParticles((ServerLevel) level(), BallParticleOptions.builder()
                .size(2f)
                .scalingOptions(0, 0, 2)
                .color(150, 230, 255)
                .build(), this.position(), 30);

        for (LivingEntity entity : list){



            entity.hurt(source,damage);

        }

        if (level() instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(LightningParticleOptions.builder()
                    .color(20, 200 + random.nextInt(50), 255)
                    .lifetime(10)
                    .maxLightningSegments(3)
                    .randomRoll(true)
                    .physics(true)
                    .build(),this.getX(),this.getY() + 0.2f,this.getZ(),30,0.02f,0.02f,0.02f,0.05f);
        }

    }

    @Override
    public boolean hurt(DamageSource src, float damage) {

        if (!src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;

        return super.hurt(src, damage);
    }

    @Override
    protected void onInsideBlock(BlockState p_20005_) {
        super.onInsideBlock(p_20005_);
        this.explode();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }


    @Override
    public boolean save(CompoundTag tag) {
        this.autoSave(tag);
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.autoLoad(tag);
        super.load(tag);
    }
}
