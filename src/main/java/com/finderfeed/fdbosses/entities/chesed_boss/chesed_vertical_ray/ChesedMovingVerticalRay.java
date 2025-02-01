package com.finderfeed.fdbosses.entities.chesed_boss.chesed_vertical_ray;

import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.entities.chesed_boss.ChesedBossBuddy;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class ChesedMovingVerticalRay extends LivingEntity implements AutoSerializable {

    public static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ChesedMovingVerticalRay.class, EntityDataSerializers.FLOAT);

    @SerializableField
    private float height;

    @SerializableField
    private ProjectileMovementPath path;

    @SerializableField
    private float damage;

    public ChesedMovingVerticalRay(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static ChesedMovingVerticalRay summon(Level level, Vec3 pos, ProjectileMovementPath movePath, float damage, float height){
        ChesedMovingVerticalRay attack = new ChesedMovingVerticalRay(BossEntities.CHESED_VERTICAL_RAY_ATTACK.get(),level);
        attack.setPos(pos);
        attack.setDamage(damage);
        attack.setPath(movePath);
        attack.setHeight(height);
        movePath.tick(attack);
        level.addFreshEntity(attack);
        return attack;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide){

            if (path == null){
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            this.path.tick(this);

            if (this.path.isFinished()){
                this.remove(RemovalReason.DISCARDED);
            }

            this.doDamage();

        }else{

            this.particles();

        }
    }

    private void particles(){
        Vec3 pos = this.position();
        for (int i = 0; i < 5;i++){
            BallParticleOptions options = BallParticleOptions.builder()
                    .color(0.3f, 1f, 1f,1f)
                    .scalingOptions(0,0,20 + random.nextInt(3))
                    .physics(false)
                    .size(0.5f)
                    .friction(0.9f)
                    .build();

            Vec3 speed = new Vec3(0.3f,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2)
                    .add(
                            0,
                            random.nextFloat() * 0.1f,
                            0
                    );

            level().addParticle(options,true,pos.x,pos.y,pos.z,speed.x,speed.y,speed.z);
        }

        pos = pos.add(0,this.getHeight(),0);

        float maxl = 2;
        for (int i = 0; i < 2;i++){

            float l = random.nextFloat() * maxl;

            Vec3 add = new Vec3(l,0,0).yRot(random.nextFloat() * FDMathUtil.FPI * 2);

            BallParticleOptions options = BallParticleOptions.builder()
                    .color(0.3f, 1f, 1f,1f)
                    .scalingOptions(0,0,20 + random.nextInt(3))
                    .physics(false)
                    .size(0.25f + (1 - l / maxl) * 2)
                    .build();



            level().addParticle(options,true,pos.x + add.x,pos.y,pos.z + add.z,0,0,0);
        }
    }

    private void doDamage(){

        float damageRadius = 1.5f;
        AABB box = new AABB(-damageRadius,0,-damageRadius,damageRadius,this.getHeight(),damageRadius).move(this.position());
        var list = level().getEntitiesOfClass(LivingEntity.class,box, BossUtil.entityInVerticalRadiusPredicate(this.position(),damageRadius));

        for (LivingEntity entity : list){

            if (entity instanceof ChesedBossBuddy) continue;

            entity.hurt(level().damageSources().magic(),this.getDamage());

        }

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGHT,30f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor == HEIGHT){
            this.height = this.entityData.get(HEIGHT);
        }
    }

    public void setHeight(float height){
        this.entityData.set(HEIGHT,height);
        this.height = height;
    }

    public float getHeight(){
        return this.entityData.get(HEIGHT);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setPath(ProjectileMovementPath path) {
        this.path = path;
    }

    public ProjectileMovementPath getPath() {
        return path;
    }

    public float getDamage() {
        return damage;
    }


    @Override
    public boolean save(CompoundTag tag) {
        this.autoSave(tag);
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.autoLoad(tag);
        this.setHeight(height);
    }

    @Override
    public boolean hurt(DamageSource src, float damage) {
        if (!src.is(DamageTypes.GENERIC_KILL) && !src.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;
        return super.hurt(src,damage);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }
}
