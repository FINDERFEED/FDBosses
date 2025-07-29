package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_repair_crystal;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_cannon.MalkuthCannonEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MalkuthRepairEntity extends Entity {

    public static final EntityDataAccessor<Integer> RANDOMIZED_DIRECTION = SynchedEntityData.defineId(MalkuthRepairEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthRepairEntity.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    private ProjectileMovementPath movementPath;

    private MalkuthCannonEntity flyingTo = null;

    public static MalkuthRepairEntity summon(Level level, Vec3 pos, MalkuthAttackType repairEntityType){
        MalkuthRepairEntity repairEntity = new MalkuthRepairEntity(BossEntities.MALKUTH_REPAIR_ENTITY.get(), level);
        repairEntity.setPos(pos);
        repairEntity.entityData.set(ATTACK_TYPE, repairEntityType);
        level.addFreshEntity(repairEntity);
        return repairEntity;
    }

    public MalkuthRepairEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.position().add(this.getDeltaMovement()));
        if (!level().isClientSide){
            this.tickMovementPath();
        }else{

            Vector3f color = MalkuthEntity.getAndRandomizeColor(this.entityData.get(ATTACK_TYPE), level().random);

            BallParticleOptions options = BallParticleOptions.builder()
                    .color(color.x,color.y,color.z)
                    .brightness(2)
                    .scalingOptions(0,0,10)
                    .size(0.15f)
                    .build();

            level().addParticle(options, true, this.xo,this.yo, this.zo, 0,0,0);

        }
    }

    private void tickMovementPath(){

        if (this.movementPath == null){
            if (!this.initMovementPath()){
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        }


        if (!this.movementPath.isFinished()) {
            this.movementPath.tick(this);
        }else{
            flyingTo.repairWithMaterial();
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private boolean initMovementPath(){

        var type = this.entityData.get(ATTACK_TYPE);

        var cannons = BossTargetFinder.getEntitiesInSphere(MalkuthCannonEntity.class, level(), this.position(), 30, cannon -> {
            return cannon.getCannonType().equals(type) && cannon.isPlayerControlled();
        });

        if (cannons.isEmpty()) return false;

        flyingTo = cannons.getFirst();

        var startPos = this.position();
        var endPos = flyingTo.position().add(0,1,0);

        var b = endPos.subtract(startPos);

        var left = b.cross(new Vec3(0,1,0)).normalize();

        if (random.nextFloat() > 0.5f){
            left = left.reverse();
            this.entityData.set(RANDOMIZED_DIRECTION, -1);
        }

        var bp1 = startPos.add(b.multiply(0.25f,0.25f,0.25f)).add(left.multiply(1.5,1.5,1.5));
        var bp2 = startPos.add(b.multiply(0.5f,0.5f,0.5f)).add(left.multiply(2,2,2));
        var bp3 = startPos.add(b.multiply(0.75f,0.75f,0.75f)).add(left.multiply(1.5,1.5,1.5));

        List<Vector3f> positions = new ArrayList<>(List.of(
                startPos,bp1,bp2,bp3,endPos
        )).stream().map(Vec3::toVector3f).toList();

        movementPath = new ProjectileMovementPath(20, false);

        int segments = 20;

        for (int k = 0;k < segments; k++){

            float p = k / (segments - 1f);

            Vector3f p1 = FDMathUtil.catmullRom(positions, p);

            movementPath.addPos(new Vec3(p1));
        }

        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p) {
        p.define(ATTACK_TYPE, MalkuthAttackType.FIRE);
        p.define(RANDOMIZED_DIRECTION, 1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("attackType")) {
            this.entityData.set(ATTACK_TYPE, MalkuthAttackType.valueOf(tag.getString("attackType")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("attackType", this.entityData.get(ATTACK_TYPE).name());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return super.shouldRender(p_20296_, p_20297_, p_20298_);
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    public int getTeamColor() {
        return this.entityData.get(ATTACK_TYPE).isFire() ? 0xffbb11 : 0x11aaff;
    }

}
