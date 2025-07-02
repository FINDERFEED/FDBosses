package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.client.particles.malkuth_slash.MalkuthHorizontalSlashOptions;
import com.finderfeed.fdbosses.client.particles.rush_particle.RushParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBuddy;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.FDHelpers;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.FDProjectile;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class MalkuthSlashProjectile extends FDProjectile implements AutoSerializable {

    public static final float HEIGHT_SIZE_MODIFIER = 0.3461538f;

    public static final EntityDataAccessor<Integer> INCREMENT_SIZE_TIME = SynchedEntityData.defineId(MalkuthSlashProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> MAX_SLASH_SIZE = SynchedEntityData.defineId(MalkuthSlashProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(MalkuthSlashProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<MalkuthAttackType> ATTACK_TYPE = SynchedEntityData.defineId(MalkuthSlashProjectile.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    private int currentIncrementSizeTime = 0;

    private float slashSizeOld = 0;
    private float slashSizeCurrent = 0;

    @SerializableField
    private float damage = 2;

    public MalkuthSlashProjectile(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    public static MalkuthSlashProjectile summon(Level level, Vec3 pos, Vec3 speed, MalkuthAttackType malkuthAttackType, float damage, float slashSize, float rotation, int incrementSizeTime){
        MalkuthSlashProjectile malkuthSlashProjectile = new MalkuthSlashProjectile(BossEntities.MALKUTH_SLASH.get(), level);
        malkuthSlashProjectile.setPos(pos);
        malkuthSlashProjectile.setDeltaMovement(speed);
        malkuthSlashProjectile.setAttackType(malkuthAttackType);

        malkuthSlashProjectile.entityData.set(INCREMENT_SIZE_TIME, incrementSizeTime);
        malkuthSlashProjectile.setMaxSlashSize(slashSize);

        malkuthSlashProjectile.damage = damage;
        malkuthSlashProjectile.setRotation(rotation);
        level.addFreshEntity(malkuthSlashProjectile);
        return malkuthSlashProjectile;
    }

    private double distanceTravelled;
    private double distanceTravelledO;

    @Override
    public void tick() {
        Vec3 oldPos = this.position();
        super.tick();
        this.tickSizeIncrement();
        Vec3 newPos = this.position();
        if (!level().isClientSide){
            this.tickDamage();
        }else{


            distanceTravelledO = distanceTravelled;
            distanceTravelled += newPos.subtract(oldPos).length();

            this.slashParticles();
            this.tickParticles();
        }
    }

    private void tickSizeIncrement(){
        this.slashSizeOld = this.slashSizeCurrent;
        int incrementSizeTime = this.entityData.get(INCREMENT_SIZE_TIME);
        if (incrementSizeTime == 0){
            this.slashSizeCurrent = this.getMaxSlashSize();
            this.slashSizeOld = this.getMaxSlashSize();
            return;
        }else{
            float p = Math.clamp(this.currentIncrementSizeTime / (float) incrementSizeTime,0,1);
            float size = this.getMaxSlashSize() * p;
            this.slashSizeCurrent = size;
            this.currentIncrementSizeTime = Mth.clamp(this.currentIncrementSizeTime + 1,0, incrementSizeTime);
        }

    }

    private void slashParticles() {

        if (this.getSlashSize() == 0 || tickCount < 5) return;

        Vec3 movement = this.getDeltaMovement();
        double distance = distanceTravelled - distanceTravelledO;

        float height = this.getSlashSize() * HEIGHT_SIZE_MODIFIER;

        Vec3 pos = FDMathUtil.interpolateVectors(this.position(),new Vec3(xo,yo,zo),0.5f).add(0,this.getBbHeight()/2,0);

        Vec3 n = movement.normalize().reverse().multiply(0.025f,0.025f,0.025f);

        for (double i = 0; i <= distance - height * movement.length(); i += height) {



            Vec3 nmovement = movement.normalize();


            double offset = height + i;


            pos = pos.add(nmovement.reverse().multiply(offset,offset,offset));

            MalkuthHorizontalSlashOptions options = new MalkuthHorizontalSlashOptions(this.getAttackType(), this.getDeltaMovement(), this.getSlashSize() * 0.9f,this.getRotation(), 5);
            level().addParticle(options, true, pos.x, pos.y, pos.z, -n.x,-n.y,-n.z);
        }
    }

    private void tickParticles(){

        if (this.getSlashSize() == 0) return;

        Vec3 pos = FDMathUtil.interpolateVectors(this.position(),new Vec3(xo,yo,zo),0.5f).add(0,this.getBbHeight()/2,0);
        Vec3 movement = this.getDeltaMovement();

        if (movement.equals(Vec3.ZERO)) return;

        Vec3 left = movement.cross(new Vec3(0,1,0)).normalize();

        float r;
        float g;
        float b;

        Vec3 nmovement = movement.normalize();

        Quaternionf quaternionf = new Quaternionf(new AxisAngle4f(
                (float) Math.toRadians(-this.getRotation()),(float) nmovement.x,(float) nmovement.y,(float) nmovement.z
        ));

        Vector3d leftd = new Vector3d(left.x,left.y,left.z);
        quaternionf.transform(leftd);

        left = new Vec3(leftd.x,leftd.y,leftd.z);




        float slashLeftOffset = 0.3f;

        float height = this.getSlashSize() * HEIGHT_SIZE_MODIFIER;

        for (float i = 0; i <= this.getSlashSize(); i += slashLeftOffset){




            Vec3 offsetPos = pos.add(left.multiply(i,i,i));
            Vec3 offsetPos2 = pos.add(left.multiply(-i,-i,-i));

            float p = FDEasings.easeOut(1 - i / this.getSlashSize());

            for (float l = 0; l < movement.length(); l+= 0.3f) {

                Vec3 noffset = nmovement.multiply(height * p,height * p,height * p);

                Vec3 n = nmovement.multiply(-l,-l,-l).add(noffset).add(nmovement.reverse().multiply(height,height,height));

                float lmult = random.nextFloat() * slashLeftOffset - slashLeftOffset/2;




                Vec3 offsetPos12 = offsetPos.add(n)
                        .add(
                                left.multiply(lmult,lmult,lmult)
                        );
                Vec3 offsetPos22 = offsetPos2.add(n).add(
                        left.multiply(lmult,lmult,lmult)
                );

                ParticleOptions particleOptions;

                if (this.getAttackType() == MalkuthAttackType.FIRE) {
                    r = 0.8f + 0.2f * random.nextFloat();
                    g = 0.3f + 0.2f * random.nextFloat();
                    b = 0.05f + 0.05f * random.nextFloat();
                } else {
                    r = 0.2f + 0.2f * random.nextFloat();
                    g = 0.8f + 0.2f * random.nextFloat();
                    b = 0.8f + 0.2f * random.nextFloat();
                }

                if (random.nextFloat() > 0.1f) {

                    particleOptions = BallParticleOptions.builder()
                            .size(0.05f + random.nextFloat() * 0.05f)
                            .scalingOptions(0, 0, 3)
                            .color(r, g, b)
                            .build();


                }else{

                    particleOptions = new RushParticleOptions(movement.reverse(),
                            new FDColor(r,g,b,1f),
                            random.nextFloat() * 0.5f + 0.25f,
                            0.05f + random.nextFloat() * 0.025f,
                            2 + random.nextInt(2));
                }

                level().addParticle(particleOptions,true, offsetPos12.x, offsetPos12.y, offsetPos12.z,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f
                );
                level().addParticle(particleOptions,true, offsetPos22.x, offsetPos22.y, offsetPos22.z,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f,
                        random.nextFloat() * 0.05f - 0.025f
                );


            }


        }

    }

    private void tickDamage(){

        Vec3 pos = this.position().add(0,this.getBbHeight()/2,0);
        Vec3 movement = this.getDeltaMovement();

        if (movement.equals(Vec3.ZERO)) return;

        Vec3 left = movement.cross(new Vec3(0,1,0)).normalize();

        Vec3 nmovement = movement.normalize();

        Quaternionf quaternionf = new Quaternionf(new AxisAngle4f(
                (float) Math.toRadians(-this.getRotation()),(float) nmovement.x,(float) nmovement.y,(float) nmovement.z
        ));

        Vector3d leftd = new Vector3d(left.x,left.y,left.z);
        quaternionf.transform(leftd);

        left = new Vec3(leftd.x,leftd.y,leftd.z);


        List<Entity> damagedEntities = new ArrayList<>();

        for (float i = 0; i < this.getSlashSize(); i+= 0.2f){

            Vec3 offsetPos = pos.add(left.multiply(i,i,i));
            Vec3 offsetPos2 = pos.add(left.multiply(-i,-i,-i));

            var targets = FDHelpers.traceEntities(level(), offsetPos, offsetPos.add(movement), 0, (e)->{
                return !(e instanceof MalkuthBossBuddy);
            });


            var targets2 = FDHelpers.traceEntities(level(), offsetPos2, offsetPos2.add(movement), 0, (e)->{
                return !(e instanceof MalkuthBossBuddy);
            });

            targets.addAll(targets2);

            for (Entity e : targets){

                if (!damagedEntities.contains(e)){

                    if (e instanceof LivingEntity livingEntity){

                        livingEntity.hurt(level().damageSources().magic(), this.getDamage());

                    }

                    damagedEntities.add(e);
                }

            }

        }

    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        if (tickCount > 3) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public float getMaxSlashSize(){
        return this.entityData.get(MAX_SLASH_SIZE);
    }

    public float getSlashSize(){
        return slashSizeCurrent;
    }

    public float getSlashSize(float partialTicks){
        if (!level().isClientSide){
            return this.getSlashSize();
        }else{
            return FDMathUtil.lerp(this.slashSizeOld, this.getSlashSize(), partialTicks);
        }
    }

    public void setMaxSlashSize(float size){
        this.entityData.set(MAX_SLASH_SIZE,size);
    }

    public float getRotation(){
        return this.entityData.get(ROTATION);
    }

    public void setRotation(float rotation){
        this.entityData.set(ROTATION,rotation);
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType malkuthAttackType){
        this.entityData.set(ATTACK_TYPE,malkuthAttackType);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MAX_SLASH_SIZE, 1f);
        builder.define(INCREMENT_SIZE_TIME, 0);
        builder.define(ROTATION, 0f);
        builder.define(ATTACK_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("slashSize",this.getSlashSize());
        tag.putFloat("rotation",this.getRotation());
        tag.putString("attackType",this.getAttackType().name());
        tag.putInt("incrementSizeTime",this.entityData.get(INCREMENT_SIZE_TIME));
        this.autoSave(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setRotation(tag.getFloat("rotation"));
        this.setMaxSlashSize(tag.getFloat("slashSize"));
        if (tag.contains("attackType")) {
            this.setAttackType(MalkuthAttackType.valueOf(tag.getString("attackType")));
        }
        this.entityData.set(INCREMENT_SIZE_TIME, tag.getInt("incrementSizeTime"));
        this.autoLoad(tag);
    }

}
