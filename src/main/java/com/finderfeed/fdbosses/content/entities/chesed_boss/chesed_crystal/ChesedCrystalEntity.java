package com.finderfeed.fdbosses.content.entities.chesed_boss.chesed_crystal;

import com.finderfeed.fdbosses.content.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossEffects;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChesedCrystalEntity extends FDEntity implements ChesedBossBuddy {

    private static final float MAX_HEALTH = 20.0f;

    private boolean dead = false;
    private int deathTime = 20;
    private float health = MAX_HEALTH;
    @Nullable
    private Player lastHurtByPlayer;

    public ChesedCrystalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("SPAWN", AnimationTicker.builder(BossAnims.CHESED_CRYSTAL_SPAWN.get())
                .setToNullTransitionTime(0)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && dead && deathTime-- < 0) {
            this.setRemoved(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    public void die(DamageSource src) {
        if (dead) {
            return;
        }
        if (!level().isClientSide) {
            if (this.lastHurtByPlayer != null) {
                this.lastHurtByPlayer.addEffect(new MobEffectInstance(BossEffects.CHESED_ENERGIZED, 400, 1, false, true));
            }
            dead = true;
            deathTime = 10;
            health = 0;
            this.getAnimationSystem().startAnimation("DEATH", AnimationTicker.builder(BossAnims.CHESED_CRYSTAL_SPAWN.get())
                    .setLoopMode(Animation.LoopMode.HOLD_ON_LAST_FRAME)
                    .reversed()
                    .setSpeed(2f)
                    .build());
            SoundEvent sound = this.getDeathSound();
            if (sound != null) {
                level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, 1f, 1f);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (dead) {
            return false;
        }

        if (src.getEntity() instanceof Player player) {
            this.lastHurtByPlayer = player;
            float modifier = player.getMainHandItem().getItem() instanceof AxeItem ? 1f : 0.05f;
            return this.damageCrystal(src, amount * modifier);
        } else if (src.is(DamageTypes.GENERIC_KILL) || src.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            this.lastHurtByPlayer = null;
            return this.damageCrystal(src, amount);
        } else {
            this.lastHurtByPlayer = null;
            return false;
        }
    }

    private boolean damageCrystal(DamageSource src, float amount) {
        if (level().isClientSide) {
            return true;
        }
        if (amount <= 0) {
            return false;
        }
        SoundEvent sound = this.getHurtSound(src);
        if (sound != null) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, 1f, 1f);
        }
        this.gameEvent(GameEvent.ENTITY_DAMAGE);
        health -= amount;
        if (health <= 0) {
            this.die(src);
        }
        return true;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource src) {
        if (src.is(DamageTypes.GENERIC_KILL)) {
            return null;
        }
        return BossSounds.CHESED_CRYSTAL_HIT.get();
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return BossSounds.CHESED_CRYSTAL_HIT.get();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public void push(Vec3 movement) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    public void push(double x, double y, double z) {
    }

    public Vec3 getCrystalFacingDirection() {
        return Vec3.directionFromRotation(this.getXRot(), this.getYRot());
    }

    public void setCrystalFacingDirection(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-6) {
            return;
        }
        Vec3 normalized = direction.normalize();
        double horizontalDistance = Math.sqrt(normalized.x * normalized.x + normalized.z * normalized.z);
        this.setXRot((float) (-(Mth.atan2(normalized.y, horizontalDistance) * Mth.RAD_TO_DEG)));
        this.setYRot((float) (Mth.atan2(-normalized.x, normalized.z) * Mth.RAD_TO_DEG));
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        dead = tag.getBoolean("dead");
        deathTime = tag.contains("deathTime") ? tag.getInt("deathTime") : 20;
        health = tag.contains("health") ? tag.getFloat("health") : MAX_HEALTH;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("dead", dead);
        tag.putInt("deathTime", deathTime);
        tag.putFloat("health", health);
    }
}
