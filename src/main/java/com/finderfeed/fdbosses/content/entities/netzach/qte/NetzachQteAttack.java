package com.finderfeed.fdbosses.content.entities.netzach.qte;

import com.finderfeed.fdbosses.BossEvents;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.vanilla_like.SpriteParticleOptions;
import com.finderfeed.fdbosses.content.entities.FDOwnableEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.init.FDEDataSerializers;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetzachQteAttack extends FDOwnableEntity {

    public static final EntityDataAccessor<Vec3> OFFSET = SynchedEntityData.defineId(NetzachQteAttack.class, FDEDataSerializers.VEC3.get());
    private static final EntityDataAccessor<Boolean> RESOLVED = SynchedEntityData.defineId(NetzachQteAttack.class, EntityDataSerializers.BOOLEAN);

    public static final int PARRY_START_TICK = 33;
    public static final int STRIKE_TICK = 38;
    private static final float STRIKE_DAMAGE = 1f;
    public static final int FADE_OUT_TIME = 5;
    public static final int FADE_IN_TIME = 5;

    public static void summon(LivingEntity target, Vec3 offset){
        NetzachQteAttack attack = new NetzachQteAttack(BossEntities.NETZACH_QTE.get(), target.level());
        attack.setPos(target.position().add(offset));
        attack.setOffset(offset);
        attack.setOwner(target);
        attack.lookAt(EntityAnchorArgument.Anchor.FEET, attack.position().add(offset.multiply(1,0,1).scale(-100)));
        target.level().addFreshEntity(attack);
    }

    public NetzachQteAttack(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("MAIN", AnimationTicker.builder(BossAnims.NETZACH_QTE)
                        .setTransitionTime(0)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        var owner = this.getOwner();

        if (owner == null){
            if (!this.level().isClientSide){
                this.discard();
            }
            return;
        }

        Vec3 pos = owner.position().add(this.getOffset());
        this.setPos(pos);

        if (!this.level().isClientSide){
            if (!owner.isAlive() || this.tickCount > BossAnims.NETZACH_QTE.get().getAnimTime() + FADE_OUT_TIME){
                this.setRemoved(RemovalReason.DISCARDED);
            }else if (this.tickCount == STRIKE_TICK){
                if (!this.isResolved()){
                    this.getEntityData().set(RESOLVED, true);
                    owner.hurt(this.damageSources().magic(), STRIKE_DAMAGE);
                    this.shake();
                }
            }
        }else{
            if (this.tickCount == PARRY_START_TICK + 5){
                var slash = SpriteParticleOptions.builder(BossParticles.NETZACH_SLASH)
                        .size(3.5f)
                        .lifetime(3)
                        .xyzRotation(15, 0, 0)
                        .lightenedUp().particleLookDirection(this.getLookAngle()).verticalRendering().build();
                Vec3 ppos = this.getBoundingBox().getCenter();
                level().addParticle(slash,true, ppos.x, ppos.y - 0.1f, ppos.z, 0, 0,0);
            }
        }
    }

    @Override
    public boolean isPickable(){
        return !this.isResolved();
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker){
        if (!(attacker instanceof Player player) || player != this.getOwner()) return true;
        if (!this.level().isClientSide){
            if (!this.isResolved() && this.tickCount >= PARRY_START_TICK && this.tickCount < STRIKE_TICK && player.getMainHandItem().getItem() instanceof TieredItem && player.getAttackStrengthScale(0f) == 1f){
                this.getEntityData().set(RESOLVED, true);
                Vec3 marker = this.getBoundingBox().getCenter().add(this.getLookAngle().scale(this.getBbHeight() / 2));
                BossUtil.netzachQteParry(this.level(), marker);
                this.level().playSound(null, marker.x, marker.y, marker.z, BossSounds.ATTACK_DING.get(), SoundSource.PLAYERS, 1f, 1f);
                this.level().playSound(null, marker.x, marker.y, marker.z, SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.PLAYERS, 1f, 1f);
                this.shake();
            }
        }
        player.resetAttackStrengthTicker();
        return true;
    }

    private void shake(){
        DefaultShakePacket.send((ServerLevel) this.level(), this.position(), 5, FDShakeData.builder()
                .frequency(20f).amplitude(0.5f).inTime(0).stayTime(0).outTime(6).build());
    }

    public boolean isResolved(){
        return this.getEntityData().get(RESOLVED);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        if (this.getOwner() == null) {
            super.lerpTo(x, y, z, yaw, pitch, steps);
        } else {
            this.setRot(yaw, pitch);
        }
    }

    @Override
    public boolean syncsOwner() {
        return true;
    }

    public void setOffset(Vec3 offset){
        this.getEntityData().set(OFFSET, offset);
    }

    public Vec3 getOffset(){
        return this.getEntityData().get(OFFSET);
    }

    public float getAlpha(float partialTick){
        float age = this.tickCount + partialTick;
        float fadeIn = Math.min(age / FADE_IN_TIME, 1f);
        float fadeOut = 1f - Math.clamp((age - BossAnims.NETZACH_QTE.get().getAnimTime()) / FADE_OUT_TIME, 0f, 1f);
        return fadeIn * fadeOut;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
        super.defineSynchedData(p_326003_);
        p_326003_.define(OFFSET, new Vec3(1,1,0));
        p_326003_.define(RESOLVED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        Vec3 offset = this.getOffset();
        tag.putDouble("offsetX", offset.x);
        tag.putDouble("offsetY", offset.y);
        tag.putDouble("offsetZ", offset.z);
    }

}
