package com.finderfeed.fdbosses;


import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreens;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.util.FDUtil;
import com.finderfeed.fdlib.util.client.particles.FDBlockParticleOptions;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticle;
import com.finderfeed.fdlib.util.client.particles.ball_particle.BallParticleOptions;
import com.finderfeed.fdlib.util.math.ComplexEasingFunction;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.finderfeed.fdlib.util.rendering.FDRenderUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossClientPackets {

    private static Random random = new Random();

    public static void closeDossierScreen(){
        if (Minecraft.getInstance().screen instanceof BaseBossScreen baseBossScreen){
            Minecraft.getInstance().setScreen(null);
        }
    }

    public static void openBossDossierScreen(BossSpawnerEntity bossSpawner, EntityType<?> bossType){
        BaseBossScreen baseBossScreen = BossScreens.getScreen(bossType,bossSpawner.getId());
        if (baseBossScreen != null){
            Minecraft.getInstance().setScreen(baseBossScreen);
        }
    }

    public static void malkuthSwordCharge(MalkuthAttackType malkuthAttackType, int entityId){

        Level level = FDClientHelpers.getClientLevel();

        if (level.getEntity(entityId) instanceof MalkuthEntity malkuthEntity){

            String name = malkuthAttackType.isFire() ? "fire_sword_place" : "ice_sword_place";

            Matrix4f t = malkuthEntity.getModelPartTransformation(malkuthEntity, name, MalkuthEntity.getClientModel());

            Vector3f v1 = t.transformPosition(new Vector3f());
            Vector3f v2 = t.transformDirection(new Vector3f(0,1,0));
            Vector3d direction = new Vector3d(
                    v2.x,
                    v2.y,
                    v2.z
            );
            Vector3d start = new Vector3d(
                    malkuthEntity.getX() + v1.x,
                    malkuthEntity.getY() + v1.y,
                    malkuthEntity.getZ() + v1.z
            );
            Vector3d end = start.add(direction.mul(3, new Vector3d()),new Vector3d());
            Vector3d left = direction.cross(new Vector3d(0,1,0),new Vector3d());
            Vector3d between = end.sub(start,new Vector3d());

            int steps = 40;

            for (int i = 0; i < steps;i++){

                float p = (i + random.nextFloat()) / (float) steps;


                float r;
                float g;
                float b;

                if (malkuthAttackType.isIce()){
                    r = random.nextFloat() * 0.2f;
                    g = 0.7f + random.nextFloat() * 0.1f;
                    b = 0.9f + random.nextFloat() * 0.1f;
                }else{
                    r = 0.9f + random.nextFloat() * 0.1f;
                    g = 0.2f + random.nextFloat() * 0.2f;
                    b = random.nextFloat() * 0.2f;
                }

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .size(0.2f + random.nextFloat() * 0.1f)
                        .color(r,g,b)
                        .scalingOptions(0,0,10 + random.nextInt(4))
                        .friction(0.6f)
                        .build();

                float rndAngle = FDMathUtil.FPI * 2 * random.nextFloat();

                Quaterniond quaterniond = new Quaterniond(new AxisAngle4d(rndAngle, direction));
                Vector3d particleDirection = quaterniond.transform(left,new Vector3d());

                Vector3d position = start.add(between.mul(p,new Vector3d()),new Vector3d())
                        .add(
                                particleDirection.x * 0.5f,
                                particleDirection.y * 0.5f,
                                particleDirection.z * 0.5f,
                                new Vector3d()
                        );

                level.addParticle(ballParticleOptions,true, position.x,position.y,position.z,
                        particleDirection.x * 0.8f,
                        particleDirection.y * 0.8f,
                        particleDirection.z * 0.8f
                );


            }



        }
    }

    public static void posEvent(Vec3 pos,int event,int data){
        switch (event) {
            case BossUtil.CHESED_GET_BLOCKS_FROM_EARTH_EVENT -> {
                summonBlocksOutOfEarthParticles(pos, 5);
            }
            case BossUtil.RADIAL_EARTHQUAKE_PARTICLES -> {
                radialEarthquakeParticles(pos, data);
            }
            case BossUtil.ROCKFALL_PARTICLES -> {
                rockfallParticles(pos, data);
            }
            case BossUtil.CHESED_RAY_EXPLOSION -> {
                rayExplosion(pos, data);
            }
            case BossUtil.CHESED_RAY_ATTACK_SMOKE -> {
                rayAttackSmoke(pos, data);
            }
            case BossUtil.CHESED_BOOM_PARTICLES -> {
                chesedBoomParticles(pos,data);
            }
            case BossUtil.MALKUTH_CANNON_SHOOT -> {
                malkuthCannonShoot(pos, data);
            }
        }
    }


    public static void malkuthCannonShoot(Vec3 pos, int data){
        MalkuthAttackType malkuthAttackType = (data & 0b1) == 0 ? MalkuthAttackType.ICE : MalkuthAttackType.FIRE;

        int directionData = data >> 1;

        Vec3 direction = FDUtil.decodeDirection(directionData);

        Vec3 left = direction.cross(new Vec3(0,1,0)).normalize();

        Level level = FDClientHelpers.getClientLevel();

        int steps = 20;
        int totalParticlesPerAnglestep = 6;
        float anglestep = FDMathUtil.FPI * 2f / steps;

        for (float angle = 0; angle <= FDMathUtil.FPI * 2; angle += anglestep){

            for (int i = 0; i < totalParticlesPerAnglestep;i++) {

                float p = (i / (float)(totalParticlesPerAnglestep - 1));

                float cangle = angle + (random.nextFloat() - 0.5f) * anglestep;

                Quaternionf quaternionf = new Quaternionf(new AxisAngle4f(cangle,
                        (float) direction.x,
                        (float) direction.y,
                        (float) direction.z
                ));

                Vector3f particleDirection = quaternionf.transform(new Vector3f(
                        (float) left.x,
                        (float) left.y,
                        (float) left.z
                ));

                float col = 0.2f + random.nextFloat() * 0.2f;

                BigSmokeParticleOptions bigSmokeParticleOptions = BigSmokeParticleOptions.builder()
                        .size(1.0f + random.nextFloat() * 0.5f)
                        .color(col,col,col)
                        .lifetime(0,2,30)
                        .friction(0.8f)
                        .minSpeed(0.025f)
                        .build();

                float dirspeed = 0.05f + random.nextFloat() * (0.5f + FDEasings.easeIn(p) * 0.25f); dirspeed *= 1.5f;
                float sidespeed = 0.025f + FDEasings.easeIn(p) * 0.2f; sidespeed *= 1.5f;

                level.addParticle(bigSmokeParticleOptions, true, pos.x,pos.y,pos.z,
                        particleDirection.x * sidespeed + direction.x * dirspeed,
                        particleDirection.y * sidespeed + direction.y * dirspeed,
                        particleDirection.z * sidespeed + direction.z * dirspeed
                );

            }


        }



        int coloredParticlesCount = 30;

        for (int i = 0;i < coloredParticlesCount;i++){

            float p = (float) i / (coloredParticlesCount - 1);

            float r;
            float g;
            float b;

            if (malkuthAttackType.isFire()){
                r = 0.8f + random.nextFloat() * 0.2f;
                g = 0.3f + p * 0.5f;
                b = 0.1f + random.nextFloat() * 0.05f;
            }else{
                r = 0.1f + random.nextFloat() * 0.05f;
                g = 0.8f - p * 0.3f;
                b = 0.8f + random.nextFloat() * 0.2f;
            }

            ParticleOptions options;

            if (random.nextFloat() > 0.75f) {
                options = BigSmokeParticleOptions.builder()
                        .size(1.0f + random.nextFloat() * 0.5f)
                        .color(r, g, b)
                        .lifetime(0, 2, 0)
                        .friction(0.8f)
                        .minSpeed(0.015f)
                        .build();
            }else {
                options = BallParticleOptions.builder()
                        .size(1.0f + random.nextFloat() * 0.5f)
                        .color(r, g, b)
                        .scalingOptions(0, 2, 0)
                        .friction(0.8f)
                        .build();
            }

            Vec3 rnd = new Vec3(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            ).normalize();

            float dirmod = 0.05f + p * 0.5f * 5;
            float rndmod = 0.05f + p * 0.2f * 5;

            Vec3 d = direction.multiply(dirmod,dirmod,dirmod).add(rnd.multiply(rndmod,rndmod,rndmod));

            level.addParticle(options, true, pos.x,pos.y,pos.z,
                    d.x,
                    d.y,
                    d.z
            );
        }


    }


    public static void chesedRayReflectParticles(){

        Window window = Minecraft.getInstance().getWindow();

        float w = window.getGuiScaledWidth();
        float h = window.getGuiScaledHeight();



        float spx = 10f;
        float spy = 40f;
        float friction = 0.4f;
        reflectParticlesPart(400,0,0,spx,spy,250,2,friction,20f);
        reflectParticlesPart(400,w,0,-spx,spy,250,2,friction,20f);
        reflectParticlesPart(400,w,h,-spx,-spy,250,2,friction,20f);
        reflectParticlesPart(400,0,h,spx,-spy,250,2,friction,20f);

    }

    public static void reflectParticlesPart(int count, float x,float y, float xd, float yd,float maxOffset,float maxSpeedMod,float friction,float size){

        Vec3 offsetVector = new Vec3(xd,yd,0).normalize().zRot(FDMathUtil.FPI / 2);

        for (int i = 0; i < count;i++){

            float offset = (random.nextFloat() * 2 - 1) * maxOffset;
            float xoff = offset * (float) offsetVector.x;
            float yoff = offset * (float) offsetVector.y;

            Vec3 sp = new Vec3(xd,yd,0).zRot(FDMathUtil.FPI / 8 * (random.nextFloat() * 2 - 1));

            float speedMod = maxSpeedMod * random.nextFloat();

            float sizeP = (1 - speedMod / maxSpeedMod);

            float frictionP = speedMod / maxSpeedMod; frictionP = frictionP * friction + 0.3f;

            FDTexturedSParticle.create(FDRenderUtil.ParticleRenderTypesS.TEXTURES_BLUR_ADDITIVE, BallParticle.LOCATION)
                    .setPos(x + xoff, y + yoff, true)
                    .setMaxQuadSize(size * sizeP)
                    .setSpeed(sp.x * speedMod,sp.y * speedMod)
                    .setFriction(frictionP)
                    .setColor(
                            0.1f + random.nextFloat() * 0.1f - 0.05f,
                            0.8f + random.nextFloat() * 0.1f - 0.05f,
                            0.8f + random.nextFloat() * 0.1f - 0.05f,
                            0.8f
                    )
                    .setLifetime(15 + random.nextInt(5))
                    .setQuadScaleOptions(ComplexEasingFunction.builder()
                            .addArea(1,FDEasings::reversedEaseOut)
                            .build())
                    .sendToOverlay();
        }

    }

    public static void chesedBoomParticles(Vec3 pos,int radiusFromCenter){
        Level level = Minecraft.getInstance().level;
        int amount = 30;
        int amountPerAmount = 30;
        float angle = FDMathUtil.FPI * 2 / amount;
        for (int i = 0; i < amount;i++){
            for (int c = 0; c < amountPerAmount;c++){

                Vec3 v = new Vec3(36, 0, 0).yRot(angle * i + (random.nextFloat() * 2 - 1) * angle / 2);
                Vec3 direction = v.reverse().normalize();
                float p = c / (float) (amountPerAmount - 1);





                float size = (1 - p) * 2 + 1f;
                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .friction(0.7f)
                        .size(size)
                        .color(100 + random.nextInt(50), 255, 255)
                        .scalingOptions(3,0,100)
                        .build();
                Vec3 ballParticlePos = pos.add(v).add(0,random.nextFloat() * 6,0);

                float power = 4;
                float verticalBallParticleSpeed = power/2 * p * 0.5f;
                float ballParticleSpeedF = power * p * 2;

                Vec3 ballParticleSpeed =
                        new Vec3(direction.x,verticalBallParticleSpeed,direction.z)
                                .multiply(ballParticleSpeedF,1,ballParticleSpeedF)
                                .yRot(FDMathUtil.FPI / 4 * (random.nextFloat() * 2 - 1));
                level.addParticle(ballParticleOptions,true,ballParticlePos.x,ballParticlePos.y,ballParticlePos.z,ballParticleSpeed.x,ballParticleSpeed.y,ballParticleSpeed.z);



                float h = p * p * p * p * 8f;

                float verticalSpeed = (random.nextFloat() * 2 - 1) * 0.1f;


                float horizontalSpeed = (1 - p) * 1.5f;

                Vec3 speed = new Vec3(0,verticalSpeed,0)
                        .add(direction.multiply(horizontalSpeed,horizontalSpeed,horizontalSpeed))
                        .yRot(FDMathUtil.FPI / 4 * (random.nextFloat() * 2 - 1));

                float friction = 0.85f;

                int cr = random.nextInt(50);
                BigSmokeParticleOptions smokeParticleOptions = BigSmokeParticleOptions.builder()
                        .size(8f)
                        .friction(friction)
                        .minSpeed(0)
                        .color(50 + cr,50 + cr,50 + cr)
                        .lifetime(0,60,100 + random.nextInt(20))
                        .build();



                level.addParticle(smokeParticleOptions,true,
                        pos.x + v.x,
                        pos.y + v.y + h,
                        pos.z + v.z,
                        speed.x,
                        speed.y,
                        speed.z
                );

            }
        }

    }

    public static void rayAttackSmoke(Vec3 pos,int data){
        Vec3 directionVector = FDUtil.decodeDirection(data);
        Matrix4f mt = new Matrix4f(); FDRenderUtil.applyMovementMatrixRotations(mt,directionVector);

        int camount = 20;
        float angle = FDMathUtil.FPI * 2 / camount;

        int ramount = 5;

        Level level = Minecraft.getInstance().level;

        for (int i = 0; i < camount;i++){



            for (int k = 0;k < ramount;k++){
                Vector3f direction = new Vector3f(1,0,0).rotateY(i * angle + (random.nextFloat() * 2 - 1) * angle / 2);
                mt.transformPosition(direction);

                float strength = k * 0.95f + (random.nextFloat() * 2 - 1) * 0.35f;
                float friction = 0.65f;

                Vec3 speed = new Vec3(
                        direction.x * strength,
                        direction.y * strength,
                        direction.z * strength
                ).add(directionVector.multiply(0.15 + 1 * random.nextFloat(),0.15 + 1 * random.nextFloat(),0.15 + 1 * random.nextFloat()));

                int cr = random.nextInt(50);

                BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                        .size(5f)
                        .friction(friction)
                        .minSpeed(0.025f)
                        .color(50 + cr,50 + cr,50 + cr)
                        .lifetime(0,20,50)
                        .build();

                level.addParticle(options,true,
                        pos.x + direction.x,
                        pos.y + direction.y,
                        pos.z + direction.z,
                        speed.x,
                        speed.y,
                        speed.z
                );


            }
        }
    }

    public static void rayExplosion(Vec3 pos,int data){

        int dx = (data & 0x00ff0000) >> 16;
        int dy = (data & 0x0000ff00) >> 8;
        int dz = data & 0x000000ff;


        Vec3 direction = new Vec3(
                (dx / (double) 0xff) * 2 - 1,
                (dy / (double) 0xff) * 2 - 1,
                (dz / (double) 0xff) * 2 - 1
        );

        Matrix4f mt = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mt,direction);

        float sizeMod = ((data >> 28) & 0xf) / (float)0xf;


        Level level = Minecraft.getInstance().level;
        int maxCount = ((data >> 24) & 0x0f);
        int maxParticlePerCount = 15;
        float maxVerticalSpeed = 5f;
        float maxHorizontalSpeed = maxVerticalSpeed / 4f;
        float maxFriction = 0.7f;
        float maxSize = 2.5f * sizeMod;

        for (int i = 0; i < maxCount;i++){

            float p = i / 10f;
            float angle = p * FDMathUtil.FPI * 2;
            float rangle = FDMathUtil.FPI * 2f / maxCount / 2;
            for (int g = 0; g < maxParticlePerCount;g++){
                Vector3f dir = new Vector3f(1,0,0).rotateY(angle + random.nextFloat() * rangle * 2 - rangle);

                Vector3f additionPos = new Vector3f(dir);
                mt.transformPosition(additionPos);

                Vector3f ppos = new Vector3f((float)pos.x + additionPos.x,(float)pos.y + additionPos.y,(float)pos.z + additionPos.z);
                float p2 = g / (float) (maxParticlePerCount - 1);

                float size = maxSize / 2 * (1 - p2) + maxSize / 2;
                float yspeed = maxVerticalSpeed * p2 + random.nextFloat() * maxVerticalSpeed / 5;
                float zxspeedAddition = random.nextFloat() * maxHorizontalSpeed * 2 - maxHorizontalSpeed;
                float zxspeed = (maxHorizontalSpeed + zxspeedAddition / 4) * p2;

                BallParticleOptions options = BallParticleOptions.builder()
                        .friction(maxFriction)
                        .size(size)
                        .color(100 + random.nextInt(50), 255, 255)
                        .scalingOptions(3,0,50)
                        .build();

                Vector3f I______Am_Speed = new Vector3f(
                        dir.x * zxspeed,
                        yspeed,
                        dir.z * zxspeed
                );
                mt.transformPosition(I______Am_Speed);
                level.addParticle(options,true,ppos.x,ppos.y,ppos.z,
                        I______Am_Speed.x,
                        I______Am_Speed.y,
                        I______Am_Speed.z
                );



            }
        }
    }

    public static void rockfallParticles(Vec3 tpos,int maxRad){

        for (int rad = 0; rad < maxRad;rad++){
            Vec3 b = new Vec3(rad,0,0);
            float angle;
            if (rad != 0){
                angle = 0.5f / rad;
            }else{
                angle = FDMathUtil.FPI * 2;
            }
            Level level = Minecraft.getInstance().level;


            for (float i = 0; i <= FDMathUtil.FPI * 2;i += angle){
                Vec3 v = b.yRot(i);
                level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,true,
                        tpos.x + v.x + random.nextFloat() - 0.5,
                        tpos.y + v.y + random.nextFloat() * 0.1 - 0.05,
                        tpos.z + v.z + random.nextFloat() - 0.5,

                        random.nextFloat() * 0.025 -  0.0125,
                        -0.015 - random.nextFloat() * 0.015,
                        random.nextFloat() * 0.025 -  0.0125
                        );


            }
        }

    }

    public static void radialEarthquakeParticles(Vec3 tpos,int rad){
        Vec3 b = new Vec3(rad,0,0);
        float angle;
        if (rad != 0){
            angle = 1f / rad;
        }else{
            angle = FDMathUtil.FPI * 2;
        }
        Level level = Minecraft.getInstance().level;

        BlockPos prevPos = null;
        for (float i = 0; i < FDMathUtil.FPI * 2;i += angle){
            Vec3 pos = tpos.add(b.yRot(i));
            BlockPos ppos = FDMathUtil.vec3ToBlockPos(pos);
            if (!ppos.equals(prevPos)){
                Vec3 c = ppos.getCenter();
                Vec3 dir = tpos.subtract(c).multiply(1,0,1).normalize();

                BlockState state = level.getBlockState(ppos);
                if (state.isAir()) continue;


                    Vec3 sppos = new Vec3(
                            c.x + random.nextFloat() * 2 - 1 - dir.x,
                            c.y + 0.1 + random.nextFloat() * 0.19,
                            c.z + random.nextFloat() * 2 - 1 - dir.z
                    );

                    Vec3 speed = dir.yRot(FDMathUtil.FPI / 4 * (random.nextFloat() * 2 - 1)).multiply(0.075,0.,0.075).add(0,0.25f + random.nextFloat() * 0.2f,0);

                    FDBlockParticleOptions options = FDBlockParticleOptions.builder()
                            .lifetime(10 + random.nextInt(5))
                            .state(state)
                            .quadSizeMultiplier(1 + random.nextFloat() * 0.2f)
                            .build();

                    level.addParticle(options,sppos.x,sppos.y,sppos.z,speed.x,speed.y,speed.z);

//                }
            }
            prevPos = ppos;
        }
    }

    public static void summonBlocksOutOfEarthParticles(Vec3 pos,float radius){
        Vec3 p = new Vec3(radius,0,0);
        float rotationAngle = 1 / radius;

        Vec3 poss = pos.add(p);
        for (float i = 0; i <= FDMathUtil.FPI * 2f; i += rotationAngle){
            Vec3 pose = pos.add(p.yRot(i + rotationAngle));
            var bpos = FDMathUtil.vec3ToBlockPos(poss).below();
            BlockState state = Minecraft.getInstance().level.getBlockState(bpos);
            if (state.isAir()) continue;
            Vec3 between = pose.subtract(poss).normalize();
            for (int k = 0; k < 5 + random.nextInt(5);k++){
                FDBlockParticleOptions options = FDBlockParticleOptions.builder()
                        .state(state)
                        .quadSizeMultiplier(1.5f + random.nextFloat() * 0.5f)
                        .lifetime(30 + random.nextInt(20))
                        .build();

                float speedMod = random.nextFloat() + 0.5f;

                Minecraft.getInstance().level.addParticle(options,true,
                        poss.x + random.nextFloat() * 2 - 1,
                        poss.y,
                        poss.z + random.nextFloat() * 2 - 1,
                        between.x * 0.3 * speedMod,
                        random.nextFloat() * 0.8f + 0.1f,
                        between.z * 0.3 * speedMod
                );

            }
            poss = pose;
        }
    }


    public static void blockProjectileSlamParticles(SlamParticlesPacket.SlamData slamData){
        Level level = Minecraft.getInstance().level;
        var states = collectStatesForSlam(level,slamData.collectRadius,slamData.bPos);
        if (states.isEmpty()) return;
        Vec3 horizontal = slamData.direction.multiply(1,0,1).normalize();
        float angle = slamData.maxAngle / slamData.count;
        float half = slamData.maxAngle / 2;
        FDBlockParticleOptions options;

        for (int i = 0; i <= slamData.count;i++){
            float a = -half + angle * i;
            Vec3 rot = horizontal.yRot(a + (random.nextFloat() * 2 - 1) * angle * 0.5f);
            float p = 1 - Math.abs(a) / half;
            p = FDEasings.easeOut(p);
            float verticalSpeed = FDMathUtil.lerp(slamData.maxVerticalSpeedEdges,slamData.maxVerticalSpeedCenter,p);

            int rowParticleCount = Math.round(slamData.maxSpeed / slamData.perRowDivide);

            for (int k = 1; k <= rowParticleCount + 1;k++){
                float percent = (k / (float)rowParticleCount);
                float sp = slamData.maxSpeed * percent + (random.nextFloat() * 2 - 1) * slamData.perRowDivide;
                float vsp = verticalSpeed * percent + random.nextFloat() * verticalSpeed * -0.5f;

                int rnd = slamData.maxParticleLifetime / 4;
                if (rnd != 0){
                    rnd = random.nextInt(rnd);
                }
                int lifetime = slamData.maxParticleLifetime - rnd;

                options = FDBlockParticleOptions.builder()
                        .state(states.get(random.nextInt(states.size())))
                        .lifetime(lifetime)
                        .quadSizeMultiplier(slamData.particleSizeMult)
                        .build();


                level.addParticle(options,true,slamData.pos.x,slamData.pos.y,slamData.pos.z,
                        rot.x * sp,
                        vsp,
                        rot.z * sp
                );

            }
        }

    }

    private static List<BlockState> collectStatesForSlam(Level level, int collectRadius, BlockPos pos){
        List<BlockState> states = new ArrayList<>();
        for (int x = -collectRadius;x <= collectRadius;x++){
            for (int y = -collectRadius;y <= collectRadius;y++){
                for (int z = -collectRadius;z <= collectRadius;z++){
                    BlockPos p = pos.offset(x,y,z);
                    var state = level.getBlockState(p);
                    if (!state.isAir()){
                        states.add(state);
                    }
                }
            }
        }
        return states;
    }

    public static void handleEarthShatterSpawnPacket(int entityId, EarthShatterSettings settings){
        if (Minecraft.getInstance().level.getEntity(entityId) instanceof EarthShatterEntity entity){
            entity.settings = settings;
        }
    }

    public static Player getClientPlayer(){
        return Minecraft.getInstance().player;
    }

}
