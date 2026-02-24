package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.BossSimpleProjectile;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.UUID;

public class NetzachAerialGearAttack extends BossSimpleProjectile {

    public static int DISAPPEAR_TICKS = 40;

    public static NetzachAerialGearAttack summon(LivingEntity owner, Vec3 pos, Vec3 speed){
        NetzachAerialGearAttack gearAttack = new NetzachAerialGearAttack(BossEntities.NETZACH_AERIAL_GEAR.get(), owner.level());
        gearAttack.setOwner(owner);
        gearAttack.setDeltaMovement(speed);
        gearAttack.setPos(pos);
        owner.level().addFreshEntity(gearAttack);
        return gearAttack;
    }

    public NetzachAerialGearAttack(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){
            if (this.reachedDestinationTicks > DISAPPEAR_TICKS){
                this.remove(RemovalReason.DISCARDED);
            }
        }else{
            this.particles();
        }
    }

    private void particles(){
        if (!this.hasReachedDestination()) {
            Vec3 base = this.position().add(0, this.getBbHeight() / 2, 0);
            Vec3 fwd = this.getLastKnownDeltaMovement().normalize();

            Vec3 left = fwd.cross(new Vec3(0,1,0)).normalize();
            Vec3 up = left.cross(fwd);

            Vec3 movement = this.getDeltaMovement().scale(0.1);


            for (int i = 0; i < 3; i++) {
                float fwdOffs = random.nextFloat() * -1;
                float leftOffs = random.nextFloat() * 1.5f - 0.75f;
                float upOffset = random.nextFloat() * 0.25f - 0.125f;
                Vec3 ppos = base.add(fwd.scale(fwdOffs)).add(left.scale(leftOffs)).add(up.scale(upOffset));
                SpriteParticleOptions options;
                if (random.nextFloat() > 0.25) {
                    if (Math.abs(leftOffs) > 0.15f) {
                        options = SpriteParticleOptions.builder(BossParticles.SMALL_GEAR)
                                .size(0.25f)
                                .frictionAffectsRotation()
                                .alphaDecreasing()
                                .lifetime(5)
                                .particleLookDirection(up)
                                .lightenedUp()
                                .friction(0.8f)
                                .xyzRotation(
                                        random.nextInt(-5, 5),
                                        random.nextInt(0, 360),
                                        random.nextInt(-5, 5)
                                )
                                .xyzRotationSpeed(0, BossUtil.randomPlusMinus() * 20, 0)
                                .build();
                    } else {
                        options = SpriteParticleOptions.builder(BossParticles.GEAR)
                                .size(0.5f)
                                .frictionAffectsRotation()
                                .alphaDecreasing()
                                .lifetime(5)
                                .particleLookDirection(up)
                                .lightenedUp()
                                .friction(0.8f)
                                .xyzRotation(
                                        random.nextInt(-5, 5),
                                        random.nextInt(0, 360),
                                        random.nextInt(-5, 5)
                                )
                                .xyzRotationSpeed(0, BossUtil.randomPlusMinus() * 20, 0)
                                .build();
                    }
                }else{
                    options = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                            .frictionAffectsRotation()
                            .alphaDecreasing()
                            .size(0.15f)
                            .lifetime(7)
                            .lightenedUp()
                            .friction(0.75f)
                            .xyzRotation(random.nextInt(0,360),0,0)
                            .xyzRotationSpeed(BossUtil.randomPlusMinus() * 20, 0, 0)
                            .build();
                }
                level().addParticle(options, ppos.x, ppos.y, ppos.z, movement.x, movement.y, movement.z);
            }
        }
    }

    @Override
    public void onEntityHit(EntityHitResult entity) {

    }

    @Override
    public void onBlockHit(BlockHitResult result) {
        this.setDeltaMovement(Vec3.ZERO);
        Vec3 location = result.getLocation();
        this.teleportTo(location.x, location.y, location.z);
        this.setReachedDestination(true);
        Vec3 fwd = this.getLastKnownDeltaMovement().multiply(1,0,1).normalize();
        BossUtil.netzachGearSlam((ServerLevel) level(), location.add(0,0.01,0).subtract(fwd.scale(0.5f)), 60, fwd);
        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(5f)
                .amplitude(2.5f)
                .inTime(0)
                .stayTime(0)
                .outTime(6)
                .build(),this.position(),20);

    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }


}
