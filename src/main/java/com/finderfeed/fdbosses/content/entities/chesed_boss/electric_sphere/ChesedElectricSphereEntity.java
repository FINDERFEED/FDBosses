package com.finderfeed.fdbosses.content.entities.chesed_boss.electric_sphere;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.arc_lightning.ArcLightningOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDLivingEntity;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import net.minecraft.nbt.CompoundTag;
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
    }

    public static ChesedElectricSphereEntity summon(Level level,float damage, ProjectileMovementPath path){

        ChesedElectricSphereEntity sphereEntity = new ChesedElectricSphereEntity(BossEntities.CHESED_ELECTRIC_SPHERE.get(),level);
        sphereEntity.setPos(path.getPositions().getFirst());
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
            this.getSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.ELECTRIC_ORB_IDLE.get()).build());
        }
    }


    private void idleParticles(){

        if (tickCount < 10) return;
        for (int i = 0; i < 1;i++) {
            float offs = 0.25f;
            Vec3 p1 = this.position().add( random.nextFloat() * 0.025 - 0.0125,offs, random.nextFloat() * 0.025 - 0.0125);
            Vec3 p2 = this.position().add(0,this.getBbHeight() - offs,0);

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
                    true, p1.x, p1.y, p1.z, sp.x, sp.y, sp.z
            );
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

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (level().isClientSide){
            this.getSystem().startAnimation("IDLE", AnimationTicker.builder(BossAnims.ELECTRIC_ORB_IDLE.get()).build());
        }
    }

    private void explode(){
        var list = level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(0.2,0.2,0.2),living->{
            return !(living instanceof ChesedElectricSphereEntity) && !(living instanceof ChesedEntity);
        });

        for (LivingEntity entity : list){

            //TODO: damage

        }

        //TODO: FX

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
