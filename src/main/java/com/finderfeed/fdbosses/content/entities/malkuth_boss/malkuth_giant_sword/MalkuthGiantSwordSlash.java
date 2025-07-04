package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_giant_sword;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
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
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.FDMathUtil;
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

        }else{
            if (tickCount == TIME_TO_HIT + TIME_TO_RISE){
                this.impactBlocks();

            }else if (tickCount >= TIME_TO_HIT + TIME_TO_RISE + DISSOLVE_TIME){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void impactBlocks(){

        float start = 10;
        float end = 40;

        Vec3 direction = new Vec3(0,0,1).yRot(-(float)Math.toRadians(this.getYRot()));



        PositionedScreenShakePacket.send((ServerLevel) level(),FDShakeData.builder()
                .frequency(20)
                .amplitude(5f)
                .inTime(0)
                .stayTime(0)
                .outTime(10)
                .build(),this.position().add(direction.x * ( (end - start) / 2),direction.y * ( (end - start) / 2),direction.z * ( (end - start) / 2)),100);

        float step = 0.5f;

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
