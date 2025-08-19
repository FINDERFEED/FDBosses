package com.finderfeed.fdbosses.content.projectiles;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthDamageSource;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush.MalkuthCrushAttack;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_fireball.MalkuthFireball;
import com.finderfeed.fdbosses.init.*;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthPlayerFireIceBall extends FDProjectile implements AutoSerializable {

    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthPlayerFireIceBall.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    private ItemStack itemStack;

    public static MalkuthPlayerFireIceBall summon(LivingEntity owner, Vec3 pos, Vec3 speed, MalkuthAttackType attackType, ItemStack tool){
        MalkuthPlayerFireIceBall malkuthPlayerFireIceBall = new MalkuthPlayerFireIceBall(BossEntities.MALKUTH_PLAYER_FIREBALL.get(), owner.level());
        malkuthPlayerFireIceBall.setOwner(owner);
        malkuthPlayerFireIceBall.setPos(pos);
        malkuthPlayerFireIceBall.setDeltaMovement(speed);
        malkuthPlayerFireIceBall.setAttackType(attackType);
        malkuthPlayerFireIceBall.itemStack = tool;
        owner.level().playSound(null, pos.x,pos.y,pos.z, BossSounds.MALKUTH_FIREBALL_LAUNCH.get(), SoundSource.PLAYERS, 1f, 1f);
        owner.level().addFreshEntity(malkuthPlayerFireIceBall);
        return malkuthPlayerFireIceBall;
    }

    public MalkuthPlayerFireIceBall(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }


    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (tickCount > 1000 || this.itemStack == null || this.itemStack.isEmpty()) {
                this.remove(RemovalReason.DISCARDED);
            }
        }else{
            this.particles();
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide && result.getEntity() instanceof LivingEntity livingEntity) {

            if (livingEntity != this.getOwner()) {

                Vec3 location = result.getLocation();

                Direction direction = null;

                if (Math.abs(location.y - livingEntity.getY()) < 3 && livingEntity.onGround()){
                    location = new Vec3(location.x,livingEntity.getY(),location.z);
                    direction = Direction.UP;
                }

                this.explode(location, direction);
            }

        }

    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            Direction direction = result.getDirection();

            this.explode(result.getLocation(),direction);
        }
    }

    public void explode(Vec3 pos, Direction crushDirectionOrNoCrush){

        if (this.getAttackType().isIce()) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), BossSounds.MALKUTH_ICE_FIREBALL_EXPLOSION.get(), SoundSource.HOSTILE, 3f, 1f);
        }else{
            level().playSound(null,this.getX(),this.getY(),this.getZ(), BossSounds.MALKUTH_FIREBALL_EXPLOSION.get(), SoundSource.HOSTILE, 3f, 1f);
        }

        BossUtil.malkuthPlayerFireballExplosionParticles((ServerLevel) level(), pos, this.getAttackType());
        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(50)
                .amplitude(1.5f)
                .inTime(0)
                .stayTime(0)
                .outTime(10)
                .build(),pos,40);

        this.remove(RemovalReason.DISCARDED);

        if (crushDirectionOrNoCrush != null){
            MalkuthCrushAttack.summon(level(), pos, 0, crushDirectionOrNoCrush);
            level().playSound(null, pos.x,pos.y,pos.z, BossSounds.ROCK_IMPACT.get(), SoundSource.PLAYERS, 3f, 0.8f);
            level().playSound(null, pos.x,pos.y,pos.z, BossSounds.MALKUTH_SWORD_EARTH_IMPACT.get(), SoundSource.PLAYERS, 3f, 0.8f);
        }

        DamageSource damageSource = level().damageSources().generic();

        if (this.getOwner() instanceof LivingEntity livingEntity){
            damageSource = level().damageSources().mobAttack(livingEntity);
        }

        float dmgModifier = BossConfigs.BOSS_CONFIG.get().itemConfig.playerMalkuthFireballToolDamagePercent / 100f;

        for (var target : BossTargetFinder.getEntitiesInSphere(LivingEntity.class, level(), pos, 3)){
            double damage = 5;
            if (this.getOwner() instanceof LivingEntity livingEntity){
                damage = BossUtil.getToolDamage(livingEntity, target, this.itemStack) * dmgModifier;
            }

            target.hurt(damageSource, (float) damage);
        }

    }


    private void particles(){


        Vec3 between = new Vec3(
                this.getX() - xo,
                this.getY() - yo,
                this.getZ() - zo
        );

        double length = between.length();


        Vec3 pos = this.position().add(0, this.getBbHeight()  / 2, 0);


        float maxrad = 0.5f;

        for (float g = -0.001f; g < length * 0.8f; g+= maxrad) {


            for (int i = 0; i < 10; i++) {

                Vector3f color = MalkuthEntity.getAndRandomizeColor(this.getAttackType(), random);

                ParticleOptions particleOptions;

                if (random.nextFloat() > 0.1f) {
                    particleOptions = BallParticleOptions.builder()
                            .color(color.x, color.y, color.z)
                            .scalingOptions(0, 0, 4 + random.nextInt(4))
                            .brightness(1)
                            .build();
                }else{
                    if (this.getAttackType().isFire()){
                        particleOptions = ParticleTypes.FLAME;
                    }else{
                        particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 5 + random.nextInt(4),0.5f + random.nextFloat() * 0.2f,0,2f,true);
                    }
                }

                float rndOut = maxrad / 2 + random.nextFloat() * maxrad / 2;

                Vec3 rndOffs = new Vec3(
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1,
                        random.nextFloat() * 2 - 1
                ).normalize().multiply(rndOut, rndOut, rndOut);

                Vec3 distOffset = between.normalize().multiply(g,g,g);

                Vec3 ppos = pos.add(rndOffs)
                        .subtract(distOffset);

                float vspeed = length == 0 ? -random.nextFloat() * 0.1f : 0;

                level().addParticle(particleOptions, true, ppos.x, ppos.y, ppos.z, 0, vspeed, 0);

            }
        }

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType type){
        this.entityData.set(ATTACK_TYPE,type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_TYPE, MalkuthAttackType.ICE);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("item", this.itemStack.save(level().registryAccess()));
        tag.putString("mtype", this.entityData.get(ATTACK_TYPE).name());
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mtype")) {
            this.entityData.set(ATTACK_TYPE, MalkuthAttackType.valueOf(tag.getString("mtype")));
        }

        if (tag.contains("item")){
            this.itemStack = ItemStack.parse(level().registryAccess(), tag.getCompound("item")).get();
        }

        this.autoLoad(tag);
    }


}
