package com.finderfeed.fdbosses.content.entities.netzach.netzach_gear_crush;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
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

import java.util.UUID;

public class NetzachGearCrushAttack extends FDOwnableEntity {

    private static final EntityDataAccessor<Integer> ATTACHED_TO = SynchedEntityData.defineId(NetzachGearCrushAttack.class, EntityDataSerializers.INT);

    public UUID attachedToEntity;

    public static final int PREPARATION_TIME = 15;
    public static final int HIT_TIME = 20;
    public static final int DISAPPEAR_TIME = 20;

    public static NetzachGearCrushAttack summon(LivingEntity owner, LivingEntity target, Vec3 direction){
        NetzachGearCrushAttack attack = new NetzachGearCrushAttack(BossEntities.NETZACH_GEAR_CRUSH.get(), owner.level());
        attack.setOwner(owner);
        attack.attachedToEntity = target.getUUID();
        attack.entityData.set(ATTACHED_TO, target.getId());
        attack.setPos(target.position());
        Vec3 lookAt = target.position().add(direction.scale(100f));
        attack.lookAt(EntityAnchorArgument.Anchor.FEET, lookAt);
        owner.level().addFreshEntity(attack);
        return attack;
    }

    public NetzachGearCrushAttack(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide){
            if (tickCount > HIT_TIME + DISAPPEAR_TIME){
                this.remove(RemovalReason.DISCARDED);
            }else if (tickCount == HIT_TIME + 1){
                PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                        .frequency(5f)
                        .amplitude(2.5f)
                        .inTime(0)
                        .stayTime(0)
                        .outTime(6)
                        .build(), this.position(), 20);
            }


            if (attachedToEntity != null){
                ServerLevel serverLevel = (ServerLevel) level();
                var entity = serverLevel.getEntity(attachedToEntity);
                if (entity != null) {
                    this.entityData.set(ATTACHED_TO, entity.getId());
                    if (tickCount == PREPARATION_TIME - 1) {
                        this.setPos(entity.position());
                    }
                }
            }


        }else{

            if (this.tickCount < PREPARATION_TIME) {
                int attachedTo = this.entityData.get(ATTACHED_TO);
                if (level().getEntity(attachedTo) instanceof Entity entity) {
                    this.setPos(entity.position());
                }
            }

            if (tickCount == HIT_TIME){
                this.hitParticles();
            }

        }

    }

    private void hitParticles(){


        Vec3 ppos = this.position().add(0,1.25,0);

        BallParticleOptions flash = BallParticleOptions.builder()
                .color(1f,0.8f,0.2f)
                .scalingOptions(1,0,2)
                .brightness(4)
                .size(2.5f)
                .build();

        level().addParticle(flash,true,ppos.x,ppos.y,ppos.z,0,0,0);

        for (int i = 0; i < 5; i++) {
            for (var dir : new HorizontalCircleRandomDirections(level().random, 12, 1f)) {


                if (i < 1) {
                    double speed = random.nextFloat() * 0.5f + 0.25f;

                    ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                            .colorStart(new FDColor(1f, 1f, 1f, 1f))
                            .colorEnd(new FDColor(1f, 0.8f, 0.3f, 1f))
                            .maxPointsInTrail(2)
                            .reflectionStrength(0.33f)
                            .gravity(1.5f)
                            .lifetime(-1)
                            .maxJumpAmount(0)
                            .size(0.02f)
                            .build();

                    Vec3 pspeed = dir.add(0,0.1 + random.nextFloat() * 2,0).normalize().scale(speed);

                    level().addParticle(options, true, ppos.x, ppos.y, ppos.z, pspeed.x, pspeed.y, pspeed.z);

                }else{

                    double speed = random.nextFloat() * 0.5f + 0.25f;
                    Vec3 pspeed = dir.add(0,-0.5 + random.nextFloat() * 1.5,0).normalize().scale(speed);

                    var options = SpriteParticleOptions.builder(BossParticles.YELLOW_SPARK)
                            .xyzRotationSpeed(20 * BossUtil.randomPlusMinus(),0,0)
                            .friction(0.8f)
                            .lifetime(random.nextInt(15,25))
                            .size(0.1f + 0.1f * random.nextFloat())
                            .lightenedUp()
                            .quadSizeDecreasing()
                            .quadSizeEaseOut()
                            .build();;

                    level().addParticle(options, true, ppos.x, ppos.y, ppos.z, pspeed.x, pspeed.y, pspeed.z);


                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        super.defineSynchedData(p_326003_);
        p_326003_.define(ATTACHED_TO, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("attachedTo")){
            this.attachedToEntity = tag.getUUID("attachedTo");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (attachedToEntity != null){
            tag.putUUID("attachedTo",attachedToEntity);
        }
    }
}
