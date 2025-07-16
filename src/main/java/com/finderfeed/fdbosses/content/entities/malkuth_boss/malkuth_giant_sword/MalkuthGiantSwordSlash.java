package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.chesed_boss.falling_block.ChesedFallingBlock;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.init.BossEntities;
import com.finderfeed.fdbosses.init.BossEntityDataSerializers;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterData;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.BoundToEntityProcessor;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.FDColor;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MalkuthGiantSwordSlash extends Entity {

    public static int TIME_TO_RISE = 60;
    public static int TIME_TO_HIT = 30;
    public static int DISSOLVE_TIME = 30;


    public static final EntityDataAccessor<MalkuthAttackType> MALKUTH_ATTACK_TYPE = SynchedEntityData.defineId(MalkuthGiantSwordSlash.class, BossEntityDataSerializers.MALKUTH_ATTACK_TYPE.get());

    public static MalkuthGiantSwordSlash summon(Level level, Vec3 pos, Vec3 direction, MalkuthAttackType attackType){

        MalkuthGiantSwordSlash slash = new MalkuthGiantSwordSlash(BossEntities.MALKUTH_GIANT_SWORD.get(), level);

        direction = direction.multiply(1,0,1).normalize();
        slash.setAttackType(attackType);
        slash.setPos(pos);
        slash.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(direction));

        level.addFreshEntity(slash);

        return slash;
    }

    public MalkuthGiantSwordSlash(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){

            if (tickCount == TIME_TO_HIT + TIME_TO_RISE){
                this.impactParticles();
            }

            if (tickCount > 3 && tickCount < TIME_TO_RISE - 20){
                this.riseParticles();
            }

        }else{
            if (tickCount == TIME_TO_HIT + TIME_TO_RISE){
                this.impactBlocks();

            }else if (tickCount >= TIME_TO_HIT + TIME_TO_RISE + DISSOLVE_TIME){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void riseParticles(){

        for (int i = 0; i < 10; i++){

            Vector3f color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(this.getAttackType());

            color.x = Math.clamp(color.x + 0.2f,0,1);
            color.y = Math.clamp(color.y + 0.2f,0,1);
            color.z = Math.clamp(color.z + 0.2f,0,1);


            float rndRadius = FDEasings.easeOut(random.nextFloat()) * 4f;
            Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

            float speedf = 0.05f + random.nextFloat() * 0.8f;

            Vec3 speed = rnd.normalize().multiply(speedf,0,speedf).add(0,speedf * 1.5f,0);

            Vec3 ppos = this.position().add(rnd);

            BallParticleOptions options = BallParticleOptions.builder()
                    .scalingOptions(5,0,10)
                    .color(color.x,color.y,color.z)
                    .size(0.15f + (1 - speedf / 0.85f) * 0.5f)
                    .friction(0.8f)
                    .brightness(2)
                    .build();

            level().addParticle(options,true,ppos.x,ppos.y,ppos.z,speed.x,speed.y,speed.z);

        }



        float rndRadius = 7 + FDEasings.easeOut(random.nextFloat()) * 3f;
        Vec3 rnd = new Vec3(rndRadius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

        Vec3 dir = rnd.normalize();

        float startOffsetRand = 3f + random.nextFloat() * 2f;
        Vec3 startOffset = dir.multiply(startOffsetRand,startOffsetRand,startOffsetRand);

        Vec3 stripePos = this.position().add(startOffset);

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(this.getAttackType());

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstPointOffset = 2 + random.nextFloat() * 2;
        float secondPointOffset = 6 + random.nextFloat() * 2;

        StripeParticleOptions stripeParticleOptions = StripeParticleOptions.builder()
                .startColor(fireColorStart)
                .endColor(fireColor)
                .lifetime(20)
                .lod(50)
                .scale(0.24f)
                .stripePercentLength(0.5f)
                .endOutPercent(0.2f)
                .startInPercent(0.2f)
                .offsets(new Vec3(0.01f,0,0),
                        dir.multiply(firstPointOffset,0,firstPointOffset).add(0,3,0),
                        dir.multiply(secondPointOffset,0,secondPointOffset).add(0,7,0),
                        rnd.add(0,12,0))
                .build();

        level().addParticle(stripeParticleOptions, true, stripePos.x,stripePos.y,stripePos.z,0,0,0);


    }

    private void impactBlocks(){

        float start = 10;
        float end = 40;

        Vec3 direction = new Vec3(0,0,1).yRot(-(float)Math.toRadians(this.getYRot()));

        PositionedScreenShakePacket.send((ServerLevel) level(), FDShakeData.builder()
                .frequency(50)
                .amplitude(3f)
                .inTime(0)
                .stayTime(0)
                .outTime(50)
                .build(), this.position().add(direction.x * ( (end - start) / 2),direction.y * ( (end - start) / 2),direction.z * ( (end - start) / 2)), 60);

        float step = 0.4f;

        for (float i = start; i <= end;i += step){

            Vec3 offset = direction.multiply(i,i,i);

            Vec3 pos = this.position().add(offset);

            BlockState state = this.randomParticleBlock();

            float verticalSpeed = random.nextFloat() * 0.5f + 0.25f;
            float horizontalSpeed = 0.05f + random.nextFloat() * 0.8f;
            int m = random.nextInt(2) == 1 ? -1 : 1;
            Vec3 rndVec = direction.yRot((FDMathUtil.FPI / 2 - FDMathUtil.FPI / 4 * random.nextFloat()) * m).multiply(horizontalSpeed,horizontalSpeed,horizontalSpeed);

            Vec3 speed = rndVec.add(0,verticalSpeed,0);

            ChesedFallingBlock block = ChesedFallingBlock.summon(level(), state, pos, speed,0);

            block.softerSound = true;




            float rnd = random.nextFloat() * 0.05f;
            FDLibCalls.addParticleEmitter(level(), 120, ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                            .color(0.35f - rnd, 0.35f - rnd, 0.35f - rnd)
                            .lifetime(0, 0, 25)
                            .size(1.5f)
                            .build())
                    .lifetime(200)
                    .processor(new BoundToEntityProcessor(block.getId(), Vec3.ZERO))
                    .position(this.position())
                    .build());

        }

    }

    private void impactParticles(){

        float start = 10;
        float end = 40;

        Vec3 direction = new Vec3(0,0,1).yRot(-(float)Math.toRadians(this.getYRot()));

        int bigsmokerows = 4;

        float step = 0.5f;

        for (float i = start; i <= end;i += step){

            Vec3 offset = direction.multiply(i,i,i);

            Vec3 pos = this.position().add(offset);




            //balls
            for (int g = 0; g <= 4; g++) {


                float verticalSpeed = random.nextFloat() * 0.5f + 0.05f;
                float horizontalSpeed = 0.05f + random.nextFloat() * 0.8f;
                int m = random.nextInt(2) == 1 ? -1 : 1;
                Vec3 rndVec = direction.yRot(FDMathUtil.FPI / 2 * m).multiply(horizontalSpeed,horizontalSpeed,horizontalSpeed);

                Vec3 speed = rndVec.add(0,verticalSpeed,0);

                float v = random.nextFloat() * step - step/2;

                Vec3 p = pos.add(direction.multiply(v,v,v));

                Vector3f col = MalkuthEntity.getMalkuthAttackPreparationParticleColor(this.getAttackType());

                float size = 0.5f + random.nextFloat() * 2;

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(size)
                        .color(col.x,col.y + 0.2f,col.z)
                        .friction(0.8f)
                        .brightness(2)
                        .scalingOptions(0,0, 40 + random.nextInt(20))
                        .build();

                float speedm = 2;

                level().addParticle(ballParticleOptions, true, p.x,p.y,p.z, speed.x * speedm,speed.y * speedm,speed.z * speedm);

            }

            //particles
            for (int g = 0; g <= 3; g++) {


                float verticalSpeed = random.nextFloat() * 0.6f + 0.05f;
                float horizontalSpeed = 0.05f + random.nextFloat() * 0.4f;
                int m = random.nextInt(2) == 1 ? -1 : 1;
                Vec3 rndVec = direction.yRot(FDMathUtil.FPI / 2 * m).multiply(horizontalSpeed,horizontalSpeed,horizontalSpeed);

                Vec3 speed = rndVec.add(0,verticalSpeed,0);

                float v = random.nextFloat() * step - step/2;

                Vec3 p = pos.add(direction.multiply(v,v,v));

                ParticleOptions particleOptions = this.randomParticle();

                float speedm = 2;

                level().addParticle(particleOptions, true, p.x,p.y,p.z, speed.x * speedm,speed.y * speedm,speed.z * speedm);

            }

            for (int g = 0; g <= bigsmokerows; g++){

                float p = g / (bigsmokerows - 1f);

                Vec3 dir1 = direction.yRot(FDMathUtil.FPI/2);
                Vec3 dir2 = direction.yRot(-FDMathUtil.FPI/2);

                float v = random.nextFloat() * step - step/2;
                float v2 = random.nextFloat() * step - step/2;

                Vec3 ppos = pos.add(direction.multiply(v,v,v));
                Vec3 ppos2 = pos.add(direction.multiply(v2,v2,v2));

                ParticleOptions options;
                if (random.nextFloat() > 0.1f) {

                    float col = random.nextFloat() * 0.4f + 0.2f;

                    options = BigSmokeParticleOptions.builder()
                            .lifetime(0, 0, 60 + random.nextInt(20))
                            .friction(0.9f - p * 0.1f)
                            .size(3f - p * 1.5f)
                            .color(col, col, col)
                            .minSpeed(0.05f)
                            .build();
                }else {

                    Vector3f col = MalkuthEntity.getMalkuthAttackPreparationParticleColor(this.getAttackType());

                    options = BallParticleOptions.builder()
                            .size(3f - p * 1.5f)
                            .color(col.x, col.y + 0.2f, col.z)
                            .friction(0.9f - p * 0.1f)
                            .scalingOptions(0, 0, 40 + random.nextInt(20))
                            .build();
                }

                float speed = p * 2f + 0.15f + random.nextFloat() * 1.5f * (p + 0.5f);
                float rndy = random.nextFloat() * (1 - p) * 0.25f;
                float rndy2 = random.nextFloat() * (1 - p) * 0.25f;
                level().addParticle(options, true, ppos.x,ppos.y + rndy,ppos.z, dir1.x * speed,dir1.y * speed,dir1.z * speed);
                level().addParticle(options, true, ppos2.x,ppos2.y + rndy2,ppos2.z, dir2.x * speed,dir2.y * speed,dir2.z * speed);


            }


        }

    }

    private ParticleOptions randomParticle(){

        if (this.getAttackType().isFire()){
            if (this.random.nextFloat() > 0.5f){
                ParticleOptions particleOptions = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(),40 + random.nextInt(4),0.75f + random.nextFloat() * 0.25f,
                        (float) Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
                return particleOptions;
            }else{
                return ParticleTypes.LAVA;
            }
        }else{
            ParticleOptions particleOptions = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),40 + random.nextInt(4),0.75f + random.nextFloat() * 0.25f,
                    (float) Mob.DEFAULT_BASE_GRAVITY * 20,2f,true);
            return particleOptions;
        }
    }

    private BlockState randomParticleBlock(){
        if (random.nextFloat() > 0.5f) {
            if (this.getAttackType() == MalkuthAttackType.ICE) {
                return Blocks.BLUE_ICE.defaultBlockState();
            } else {
                return Blocks.MAGMA_BLOCK.defaultBlockState();
            }
        }else{
            return Blocks.BLACKSTONE.defaultBlockState();
        }
    }

    public float getCurrentMoveUpTime(float pticks){
        return Math.clamp(this.tickCount + pticks, 0, TIME_TO_RISE);
    }

    public float getCurrentHitTime(float pticks){
        return Math.clamp(this.tickCount - TIME_TO_RISE + pticks, 0, TIME_TO_HIT);
    }

    public float getDissolveTime(float pticks){
        return Math.clamp(this.tickCount - TIME_TO_RISE - TIME_TO_HIT + pticks,0, DISSOLVE_TIME);
    }

    public MalkuthAttackType getAttackType(){
        return this.entityData.get(MALKUTH_ATTACK_TYPE);
    }

    public void setAttackType(MalkuthAttackType type){
        this.entityData.set(MALKUTH_ATTACK_TYPE, type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MALKUTH_ATTACK_TYPE, MalkuthAttackType.FIRE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tag.putString("mtype",this.getAttackType().name());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("mtype")) {
            this.setAttackType(MalkuthAttackType.valueOf(tag.getString("mtype")));
        }
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
