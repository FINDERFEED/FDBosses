package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_crush;

import com.finderfeed.fdbosses.BossTargetFinder;
import com.finderfeed.fdbosses.BossUtil;
import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthBossBuddy;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossDamageSources;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.entity.FDEntity;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;

public class MalkuthCrushAttack extends FDEntity implements AutoSerializable {

    public static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(MalkuthCrushAttack.class, EntityDataSerializers.DIRECTION);
    public static final EntityDataAccessor<Byte> VISUAL_APPEARANCE = SynchedEntityData.defineId(MalkuthCrushAttack.class, EntityDataSerializers.BYTE);

    @SerializableField
    private float damage;

    public static MalkuthCrushAttack summon(Level level, Vec3 pos, float damage){
        return summon(level, pos, damage, Direction.UP, null);
    }

    public static MalkuthCrushAttack summon(Level level, Vec3 pos, float damage, Direction direction, MalkuthAttackType visualAppearance){
        MalkuthCrushAttack malkuthCrushAttack = new MalkuthCrushAttack(BossEntities.MALKUTH_CRUSH.get(), level);
        malkuthCrushAttack.setPos(pos);
        malkuthCrushAttack.damage = damage;
        malkuthCrushAttack.entityData.set(DIRECTION, direction);

        if (visualAppearance != null) {
            if (visualAppearance.isFire()) {
                malkuthCrushAttack.entityData.set(VISUAL_APPEARANCE, (byte) 1);
            }else{
                malkuthCrushAttack.entityData.set(VISUAL_APPEARANCE, (byte) 2);
            }
        }

        level.addFreshEntity(malkuthCrushAttack);
        return malkuthCrushAttack;
    }

    public MalkuthCrushAttack(EntityType<?> type, Level level) {
        super(type, level);
        this.getAnimationSystem().startAnimation("ATTACK", AnimationTicker.builder(BossAnims.MALKUTH_CRUSH_ATTACK_CRUSH).setSpeed(1.25f).build());
    }

    @Override
    public void tick() {

        if (firstTick){
            if (level().isClientSide) {
                this.doParticles();
            }else{
                this.doDamage();
            }
        }
        super.tick();
        if (!level().isClientSide){
            if (tickCount > BossAnims.MALKUTH_CRUSH_ATTACK_CRUSH.get().getAnimTime() - 10){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    public boolean isBothFireAndIce(){
        return this.entityData.get(VISUAL_APPEARANCE) == 0;
    }

    public boolean isFire(){
        return this.entityData.get(VISUAL_APPEARANCE) == 1;
    }

    public boolean isIce(){
        return this.entityData.get(VISUAL_APPEARANCE) == 2;
    }

    private void doDamage(){

        if (damage == 0) return;

        float radius = 3f;

        var targets = BossTargetFinder.getEntitiesInCylinder(LivingEntity.class, level(), this.position(), 2, radius, v->!(v instanceof MalkuthBossBuddy));


        for (var target : targets){

            target.hurt(BossDamageSources.MALKUTH_EARTHSHATTER_SOURCE,damage);

            Vec3 speed = target.position().subtract(this.position())
                    .multiply(1,0,1)
                    .normalize()
                    .multiply(2,0,2)
                    .add(0,1.5,0);

            if (target instanceof ServerPlayer player){
                FDLibCalls.setServerPlayerSpeed(player, speed);
            }else{
                target.setDeltaMovement(speed);
            }

        }

    }

    private void doParticles(){

        int count = 30;

        Vec3i nrm = this.getEntityData().get(DIRECTION).getNormal();

        Vec3 v = new Vec3(nrm.getX(),nrm.getY(),nrm.getZ());
        Matrix4f mat = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mat,v);

        float angle = FDMathUtil.FPI * 2 / count;

        for (int i = 0; i < count;i++){

            float currentAngle = angle * i;

            // ball particles
            for (float dist = 0; dist < 3;dist+= 0.5f){
                float m = dist + random.nextFloat() * 0.25f;

                Vector2d randomAngle = this.randomAngle(angle,currentAngle);

                float r;
                float g;
                float b;

                if (this.isBothFireAndIce()) {
                    if (random.nextFloat() > 0.5f) {
                        r = random.nextFloat() * 0.2f;
                        g = 0.7f + random.nextFloat() * 0.1f;
                        b = 0.9f + random.nextFloat() * 0.1f;
                    } else {
                        r = 0.9f + random.nextFloat() * 0.1f;
                        g = 0.2f + random.nextFloat() * 0.2f;
                        b = random.nextFloat() * 0.2f;
                    }
                }else if (this.isFire()){
                    r = 0.9f + random.nextFloat() * 0.1f;
                    g = 0.2f + random.nextFloat() * 0.2f;
                    b = random.nextFloat() * 0.2f;
                }else{
                    r = random.nextFloat() * 0.2f;
                    g = 0.7f + random.nextFloat() * 0.1f;
                    b = 0.9f + random.nextFloat() * 0.1f;
                }

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(0.2f + random.nextFloat() * 0.2f)
                        .color(r,g,b)
                        .scalingOptions(0,0,10 + random.nextInt(4))
                        .friction(0.6f)
                        .build();

                float hspeed = (0.1f + random.nextFloat() * 0.2f) * 2;
                float vspeed = (0.05f + random.nextFloat() * 0.5f) * 2;

                Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                        randomAngle.x * hspeed,
                        vspeed,
                        randomAngle.y * hspeed
                ));

                Vec3 spOffset = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                        randomAngle.x * m,
                        0,
                        randomAngle.y * m
                ));

                level().addParticle(ballParticleOptions,true,
                        this.getX() + spOffset.x,
                        this.getY() + spOffset.y,
                        this.getZ() + spOffset.z,
                        speed.x,speed.y,speed.z
                );
            }

            //lava and shit
            for (float dist = 1; dist < 3.5f; dist += 1f){
                float m = dist + random.nextFloat() * 0.5f;
                Vector2d randomAngle = this.randomAngle(angle,currentAngle);


                if (random.nextFloat() > 0.66f) {
                    if (!this.isIce()) {
                        level().addParticle(ParticleTypes.LAVA, true,
                                this.getX() + randomAngle.x * m,
                                this.getY(),
                                this.getZ() + randomAngle.y * m,
                                0, 0, 0
                        );
                    }
                }else{

                    ParticleOptions particleOptions;

                    if (this.isBothFireAndIce()) {
                        if (random.nextFloat() > 0.5f) {
                            particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                    (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                        } else {
                            particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                    (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                        }
                    }else if (this.isFire()){
                        particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                    }else{
                        particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(), 20 + random.nextInt(4), 0.5f + random.nextFloat() * 0.2f,
                                (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);
                    }

                    float hspeed = (0.1f + random.nextFloat() * 0.05f);
                    float vspeed = (0.4f + random.nextFloat() * 0.25f);


                    Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                            randomAngle.x * hspeed,
                            vspeed,
                            randomAngle.y * hspeed
                    ));

                    Vec3 spOffset = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                            randomAngle.x * m,
                            0,
                            randomAngle.y * m
                    ));

                    level().addParticle(particleOptions,true,
                            this.getX() + spOffset.x,
                            this.getY() + spOffset.y,
                            this.getZ() + spOffset.z,
                            speed.x,speed.y,speed.z
                    );

                }

            }




            Vector2d randomAngle = this.randomAngle(angle,currentAngle);

            int cr = random.nextInt(50);

            BigSmokeParticleOptions smokeParticleOptions = BigSmokeParticleOptions.builder()
                    .size(1f + random.nextFloat() * 0.5f)
                    .friction(0.75f)
                    .minSpeed(0.025f)
                    .color(50 + cr,50 + cr,50 + cr)
                    .lifetime(0,5,30 + random.nextInt(5))
                    .build();

            float rndOffs = random.nextFloat() * 0.5f;
            float rndSpeed = random.nextFloat() * 1 + 0.25f;


            Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                    randomAngle.x * rndSpeed,
                    random.nextFloat() * 0.025f + 0.025f,
                    randomAngle.y * rndSpeed
            ));

            Vec3 spOffset = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                    randomAngle.x * rndOffs,
                    random.nextFloat() * 0.5f,
                    randomAngle.y * rndOffs
            ));

            level().addParticle(smokeParticleOptions, true,
                    this.getX() + spOffset.x,
                    this.getY() + spOffset.y,
                    this.getZ() + spOffset.z,
                    speed.x,speed.y,speed.z
            );





        }





        for (int i = 0; i < 15;i++) {
            if (this.isBothFireAndIce()) {
                this.stripeParticles(mat, MalkuthAttackType.FIRE);
                this.stripeParticles(mat, MalkuthAttackType.ICE);
            }else if (this.isFire()){
                this.stripeParticles(mat, MalkuthAttackType.FIRE);
                this.stripeParticles(mat, MalkuthAttackType.FIRE);
            }else{
                this.stripeParticles(mat, MalkuthAttackType.ICE);
                this.stripeParticles(mat, MalkuthAttackType.ICE);
            }
        }
    }

    private void stripeParticles(Matrix4f mat, MalkuthAttackType type){


        float rndRadius = 1 + FDEasings.easeOut(random.nextFloat()) * 4f;
        Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

        Vec3 dir = rnd.normalize();

        float startOffsetRand = 0.1f + random.nextFloat() * 0.5f;
        Vec3 startOffset = dir.multiply(startOffsetRand,startOffsetRand,startOffsetRand);

        Vec3 stripePos = this.position().add(BossUtil.matTransformDirectionVec3(mat, startOffset));

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(type);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstPointOffset = 2f + random.nextFloat() * 1f;

        StripeParticleOptions stripeParticleOptions = new StripeParticleOptions(fireColorStart,fireColor, 5 + random.nextInt(10), 50, 0.05f, 0.75f,
                BossUtil.matTransformDirectionVec3(mat, new Vec3(0.01f,0,0)),
                BossUtil.matTransformDirectionVec3(mat, dir.multiply(firstPointOffset,0,firstPointOffset).add(0,0.5,0)),
                BossUtil.matTransformDirectionVec3(mat, rnd.add(0,1.5f + random.nextFloat() * 2,0))
        );

        level().addParticle(stripeParticleOptions, true, stripePos.x,stripePos.y,stripePos.z,0,0,0);

    }

    private Vector2d randomAngle(float angle, float currentAngle){
        float a = currentAngle + (random.nextFloat() * 2 - 1) * angle / 2;
        return new Vector2d(
                Math.sin(a),
                Math.cos(a)
        );
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder data) {
        data.define(DIRECTION, Direction.UP);
        data.define(VISUAL_APPEARANCE, (byte)0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.autoSave(tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        this.autoLoad(tag);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return dist <= 60 * 60;
    }
}
