package com.finderfeed.fdbosses;


import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.client.boss_screen.BaseBossScreen;
import com.finderfeed.fdbosses.client.boss_screen.screen_definitions.BossScreens;
import com.finderfeed.fdbosses.client.particles.GravityParticleOptions;
import com.finderfeed.fdbosses.client.particles.colored_jumping_particles.ColoredJumpingParticleOptions;
import com.finderfeed.fdbosses.client.particles.smoke_particle.BigSmokeParticleOptions;
import com.finderfeed.fdbosses.client.particles.stripe_particle.StripeParticleOptions;
import com.finderfeed.fdbosses.content.entities.base.BossSpawnerEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterEntity;
import com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity.EarthShatterSettings;
import com.finderfeed.fdbosses.content.entities.chesed_boss.radial_earthquake.RadialEarthquakeEntity;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahEntity;
import com.finderfeed.fdbosses.content.entities.geburah.GeburahRenderer;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffect;
import com.finderfeed.fdbosses.content.entities.geburah.distortion_sphere.DistortionSphereEffectHandler;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_earthquake.GeburahEarthquake;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.GeburahWeaponRotationController;
import com.finderfeed.fdbosses.content.entities.geburah.rotating_weapons.rotations.GeburahWeaponRotation;
import com.finderfeed.fdbosses.content.entities.geburah.sins.ScreenFlashEffect;
import com.finderfeed.fdbosses.content.entities.geburah.sins.ScreenFlashEffectData;
import com.finderfeed.fdbosses.content.entities.geburah.sins.attachment.PlayerSins;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthEntity;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthWeaknessHandler;
import com.finderfeed.fdbosses.content.items.chesed.PhaseSphereHandler;
import com.finderfeed.fdbosses.content.util.HorizontalCircleRandomDirections;
import com.finderfeed.fdbosses.init.BossConfigs;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdbosses.packets.SlamParticlesPacket;
import com.finderfeed.fdlib.ClientMixinHandler;
import com.finderfeed.fdlib.FDClientHelpers;
import com.finderfeed.fdlib.systems.bedrock.models.FDModel;
import com.finderfeed.fdlib.systems.broken_screen_effect.ShatteredScreenEffectHandler;
import com.finderfeed.fdlib.systems.broken_screen_effect.ShatteredScreenSettings;
import com.finderfeed.fdlib.systems.particle.CircleParticleProcessor;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterData;
import com.finderfeed.fdlib.systems.particle.particle_emitter.ParticleEmitterHandler;
import com.finderfeed.fdlib.systems.particle.particle_emitter.processors.CircleSpawnProcessor;
import com.finderfeed.fdlib.systems.screen.screen_effect.ScreenEffectOverlay;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDScreenParticle;
import com.finderfeed.fdlib.systems.screen.screen_particles.FDTexturedSParticle;
import com.finderfeed.fdlib.systems.shake.DefaultShake;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.FDColor;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class BossClientPackets {

    private static Random random = new Random();

    public static void geburahSyncRayVisuals(int geburah, boolean rayState){
        if (FDClientHelpers.getClientLevel().getEntity(geburah) instanceof GeburahEntity geburah1){
            geburah1.setLaserVisualsState(rayState);
        }
    }

    public static void forceAttackEntity(int entityId){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof LivingEntity livingEntity){
            Minecraft.getInstance().gameMode.attack(Minecraft.getInstance().player, livingEntity);
            FDClientHelpers.getClientPlayer().swing(InteractionHand.MAIN_HAND);
        }
    }

    public static void summonParticle(ParticleOptions particleOptions, double x,double y, double z,  double xd,double yd, double zd, int lifetime){
        var engine = Minecraft.getInstance().particleEngine;
        var particle = engine.createParticle(particleOptions,x,y,z,xd,yd,zd);
        particle.lifetime = lifetime;
    }

    public static void chesedItemUse(CompoundTag update, boolean noPhysics, boolean startedUsing){
        var player = FDClientHelpers.getClientPlayer();
        player.getAbilities().loadSaveData(update);
        player.noPhysics = noPhysics;
        PhaseSphereHandler.isUsingChesedItem = startedUsing;
    }

    public static void geburahScalesControllerSetDisplacement(int entityId, int displacement, int displacementTime){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){
            geburah.getScalesController().setCurrentDisplacement(displacement, displacementTime);
        }
    }

    public static void geburahSinnedPacket(int entityId){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){
            geburah.sinnedTicks = GeburahEntity.SINNED_CLIENT_ANIM_DURATION;
        }
    }

    public static void setPlayerMalkuthWeaknessAmount(int amount) {
        if (FDClientHelpers.getClientPlayer() != null) {
            MalkuthWeaknessHandler.setCurrentWeakness(FDClientHelpers.getClientPlayer(), amount);
        }
    }

    public static void closeDossierScreen(){
        if (Minecraft.getInstance().screen instanceof BaseBossScreen baseBossScreen){
            Minecraft.getInstance().setScreen(null);
        }
    }

    public static void syncPlayerSinsPacket(PlayerSins playerSins){
        PlayerSins.setPlayerSins(FDClientHelpers.getClientPlayer(), playerSins);
    }

    public static void launchGeburahLaserPrepare(int geburah, int timeUntilAttack){
        if (FDClientHelpers.getClientLevel().getEntity(geburah) instanceof GeburahEntity geburah1){
            geburah1.laserAttackPreparator.launchPreparation(timeUntilAttack);
        }
    }

    public static void triggerSinEffect(float soundPitch){

        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(BossSounds.GEBURAH_SIN.get(),soundPitch * (0.95f + random.nextFloat() * 0.1f),0.5f));
        ClientMixinHandler.addShake(new DefaultShake(FDShakeData.builder()
                .amplitude(0.5f)
                .outTime(10)
                .inTime(0)
                .stayTime(0)
                .build()));

        ResourceLocation data = ShatteredScreenSettings.DATA_1_GLASSY;

        ShatteredScreenEffectHandler.setCurrentEffect(new ShatteredScreenSettings(
                data,
                0,0,40,0.1f,0.05f,true
        ));

        ScreenEffectOverlay.addScreenEffect(new ScreenFlashEffect(
                new ScreenFlashEffectData(new FDColor(1f,1f,1f,0.5f),0.3f),1,0,20
        ));

    }

    public static void stopWeaponRotation(int entityId){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){
            geburah.getWeaponRotationController().stopRotation();
        }
    }

    public static void startGeburahWeaponRotation(int entityId, GeburahWeaponRotation geburahWeaponRotation){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){
            var handler = geburah.getWeaponRotationController();
            geburahWeaponRotation.setRotatingWeaponsHandler(handler);
            handler.rotateWeapons(geburahWeaponRotation);
        }
    }

    public static void handleGeburahWeaponRotationSync(int entityId, float currentRotation){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){
            GeburahWeaponRotationController rotationWeapons = geburah.getWeaponRotationController();
            float rot = rotationWeapons.getCurrentRotation();
            if (Math.abs(rot - currentRotation) > 0.1f) {
                rotationWeapons.setRotation(currentRotation);
            }
        }
    }

    public static void startGeburahDistortionEffect(int entityId){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEntity geburah){

            Vec3 corePos = geburah.getCorePosition();
            Vec3 pos = geburah.position();

            DistortionSphereEffectHandler.setDistortionSphereEffect(new DistortionSphereEffect(
                    corePos, 60, GeburahEntity.ARENA_RADIUS * 2,2, (float)pos.y
            ));

        }
    }

    public static void openBossDossierScreen(int spawnerId, List<Item> drops){
        Level level = FDClientHelpers.getClientLevel();
        if (level.getEntity(spawnerId) instanceof BossSpawnerEntity bossSpawner) {
            BaseBossScreen baseBossScreen = BossScreens.getScreen(bossSpawner.getBossEntityType(), spawnerId, drops);
            if (baseBossScreen != null) {
                Minecraft.getInstance().setScreen(baseBossScreen);
            }
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


    public static void geburahEarthquakeSpawnEarthshatters(int entityId,Vec3 direction, int radius, float angle){

        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof GeburahEarthquake geburahEarthquake){
            geburahEarthquake.spawnEarthShattersOnRadius(radius,direction,angle);
            geburahEarthquakeParticles(geburahEarthquake.position(),radius, direction, angle);
        }

    }


    public static void geburahEarthquakeParticles(Vec3 tpos,int rad, Vec3 direction, float arcAngle){
        Vec3 b = new Vec3(rad,0,0);
        float angle;
        if (rad != 0){
            angle = 3f / rad;
        }else{
            angle = FDMathUtil.FPI * 2;
        }
        Level level = Minecraft.getInstance().level;

        BlockPos prevPos = null;
        for (float i = 0; i < FDMathUtil.FPI * 2;i += angle){

            Vec3 vec = b.yRot(i);

            if (FDMathUtil.angleBetweenVectors(vec, direction) > arcAngle) continue;

            Vec3 pos = tpos.add(vec);

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
            case BossUtil.MALKUTH_SWORD_CHARGE_PARTICLES -> {
                malkuthSwordChargeParticles(pos, data);
            }
            case BossUtil.MALKUTH_FLOAT_PARTICLES -> {
                malkuthFloat(data);
            }
            case BossUtil.MALKUTH_FIREBALL_EXPLODE -> {
                malkuthFireballExplode(pos, data);
            }
            case BossUtil.MALKUTH_VOLCANO_ERRUPTION -> {
                volcanoErruptionParticles(pos,data);
            }
            case BossUtil.MALKUTH_SWORD_INSERT_PARTICLES -> {
                malkuthSwordInsertParticles(data);
            }
            case BossUtil.MALKUTH_PLAYER_FIREBALL_EXPLODE -> {
                malkuthPlayerFireballExplode(pos,data);
            }
            case BossUtil.CHESED_ADD_ROCKFALL_PARTICLE_EMITTER -> {
                chesedAddEmitter(pos,data);
            }
            case BossUtil.GEBURAH_RAY_PARTICLES -> {
                geburahRayParticles(pos, data);
            }
            case BossUtil.GEBURAH_RAY_CHARGE_PARTICLES -> {
                geburahRayChargeParticles(pos, data);
            }
            case BossUtil.GEBURAH_WEAPONS_START_LASER -> {
                geburahWeaponsStartLaser(pos, data);
            }
            case BossUtil.JUDGEMENT_BIRD_RAY_PARTICLES -> {
                judgementBirdRayParticles(pos, data);
            }
            case BossUtil.TRIGGER_GEBURAH_SIN_PUNISHMENT_ATTACK_EFFECT -> {
                geburahTriggerSinPunishmentAttackEffect(pos, data);
            }
            case BossUtil.TRIGGER_GEBURAH_SIN_PUNISHMENT_ATTACK_IMPACT -> {
                sinPunishmentParticles(pos, data);
            }
            case BossUtil.GEBURAH_PREPARE_RAYS -> {
                geburahPrepareParticles(pos, data);
            }
            case BossUtil.GEBURAH_PREPARE_RAYS_FRAMES -> {
                geburahPrepareRoundAndRoundRays(pos, data);
            }
            case BossUtil.DIVINE_GEAR_RAY_PARTICLES -> {
                divineGearRayParticles(pos,data);
            }
        }
    }

    public static void divineGearRayParticles(Vec3 pos, int data){


        //1f,0.8f,0.2f
        Level level = FDClientHelpers.getClientLevel();

        BallParticleOptions flash = BallParticleOptions.builder()
                .color(1f,0.8f,0.2f)
                .scalingOptions(1,0,2)
                .brightness(2)
                .size(7f)
                .build();

        level.addParticle(flash,true,pos.x,pos.y,pos.z,0,0,0);


        Vec3 direction = FDUtil.decodeDirection(data);

        Matrix4f mat = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mat, direction);


        float maxParticleSize = 1.5f;
        float minParticleSize = 0.1f;
        float particleFriction = 0.7f;
        int maxParticleRows = 10;

        int particleTravelTime = 100;

        float cumulativeSize = 0;

        float travelDistWindow = 0.75f;
        float randomXZSpread = 0.15f;


        //MAIN
        for (int i = 0; i < maxParticleRows;i++) {

            float ip = i / (maxParticleRows - 1f);

            float size = FDMathUtil.lerp(minParticleSize, maxParticleSize, 1 - ip);
            int repetitionCount = Math.round(FDMathUtil.lerp(1,3,ip));
            int directionsCount = Math.round(FDMathUtil.lerp(1,7,ip));

            float r = FDMathUtil.lerp(1f,1f,ip);
            float gr = FDMathUtil.lerp(0.5f,1f,ip);
            float b = FDMathUtil.lerp(0.0f,1f,ip * ip);

            for (int g = 0; g < repetitionCount; g++) {
                for (var dir : new HorizontalCircleRandomDirections(level.random, directionsCount, 1f)) {

                    //---------------------------RAY-----------------------------

                    float travelDistance = cumulativeSize + travelDistWindow * random.nextFloat();

                    float particleSpeed = calculateParticleSpeed(travelDistance, particleFriction, particleTravelTime);

                    Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                            dir.x * 0.5 + random.nextFloat() * randomXZSpread * 2 - randomXZSpread,
                            3 - random.nextFloat() * 0.5f,
                            dir.z * 0.5 + random.nextFloat() * randomXZSpread * 2 - randomXZSpread
                    ).normalize().multiply(particleSpeed, particleSpeed, particleSpeed));

                    Vec3 spawnOffset = BossUtil.matTransformDirectionVec3(mat, dir.multiply(0.5f, 0.5f, 0.5f));

                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .brightness(2)
                            .size(size)
                            .color(r, gr, b, 1f)
                            .friction(particleFriction)
                            .scalingOptions(0, 0, 20 + random.nextInt(10))
                            .build();

                    level.addParticle(ballParticleOptions, true, pos.x + spawnOffset.x, pos.y + spawnOffset.y, pos.z + spawnOffset.z, speed.x, speed.y, speed.z);


                }
            }

            cumulativeSize += travelDistWindow;

        }



        //STRIPES
        for (var dir : new HorizontalCircleRandomDirections(level.random, 12, 0)){

            float randomOffset = FDMathUtil.FPI / 8 + FDMathUtil.FPI / 8 * level.random.nextFloat();
            randomOffset *= level.random.nextBoolean() ? 1 : -1;

            float offset1 = 0.5f;
            float offset2 = 1.5f;

            Vec3 p1 = Vec3.ZERO;
            Vec3 p2 = BossUtil.matTransformDirectionVec3(mat, dir.yRot(randomOffset).add(dir.x * offset1,3,dir.z * offset1)
                    .add(
                            random.nextFloat() * 0.5 - 0.25f,
                            random.nextFloat() * 2 - 1,
                            random.nextFloat() * 0.5 - 0.25f
                    ));
            Vec3 p3 = BossUtil.matTransformDirectionVec3(mat, dir.yRot(-randomOffset).add(dir.x * offset2,7,dir.z * offset2)
                    .add(
                            random.nextFloat() * 0.5 - 0.25f,
                            random.nextFloat() * 2 - 1,
                            random.nextFloat() * 0.5 - 0.25f
                    ));

            StripeParticleOptions options = StripeParticleOptions.builder()
                    .startColor(new FDColor(1f,0.4f,0.1f,1f))
                    .endColor(new FDColor(1f,0.8f,0.8f,1f))
                    .lifetime(10 + random.nextInt(5))
                    .lod(25)
                    .scale(0.1f)
                    .stripePercentLength(0.5f)
                    .endOutPercent(0.1f)
                    .startInPercent(0.1f)
                    .offsets(p1,p2,p3)
                    .build();

            level.addParticle(options, true, pos.x, pos.y,pos.z,0,0,0);

        }

    }

    public static void geburahPrepareRoundAndRoundRays(Vec3 p, int data){
        if (FDClientHelpers.getClientLevel().getEntity(data) instanceof GeburahEntity geburah){
            geburah.triggerRaysShotPreparationEffect();
        }
    }

    public static void geburahPrepareParticles(Vec3 p, int data){

        Level level = FDClientHelpers.getClientLevel();
        if (!(level.getEntity(data) instanceof GeburahEntity geburah)) return;

        var positionsAndDirections = geburah.getCannonsPositionAndDirection();

        for (var posAndDir : positionsAndDirections) {

            Vec3 pos = posAndDir.first;
            Vec3 direction = posAndDir.second;

            Matrix4f mat = new Matrix4f();
            FDRenderUtil.applyMovementMatrixRotations(mat, direction);

            for (var dir : new HorizontalCircleRandomDirections(level.random, 6, 1f)) {

                dir = BossUtil.matTransformDirectionVec3(mat, dir);

                Vec3 startPos = pos.add(dir.scale(1.5)).add(direction.reverse().scale(2));

                Vec3 speed = direction.scale(2).add(dir.reverse()).scale(0.3f);

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .color(0.2f, 0.5f, 1f)
                        .brightness(2)
                        .friction(0.8f)
                        .size(0.25f)
                        .in(10)
                        .out(1)
                        .build();
                level.addParticle(ballParticleOptions, true, startPos.x, startPos.y, startPos.z, speed.x,speed.y,speed.z);

            }
        }

    }

    public static void sinPunishmentParticles(Vec3 pos, int radius){

        Level level = FDClientHelpers.getClientLevel();

        float length = FDMathUtil.FPI * 2 * radius;
        float step = 300 / length;
        float smokeDirStep = FDMathUtil.FPI / 8;

        float minSmokeSize = 3f;
        float maxSmokeSize = 5f;
        float smokeFriction = 0.7f;



        for (float i = 0; i < length; i+=step){

            float p = i / length;

            Vec3 direction = new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * p + random.nextFloat() * FDMathUtil.FPI / 16);
            Vec3 between = direction.scale(radius);
            Vec3 reversedDir = direction.reverse();
            Vec3 ppos = pos.add(between);

            //SMOKE
            float smokeTravelDistance = 0.5f;
            int smokeCount = 3;
            for (int c = 0; c < smokeCount; c++) {
                float smokeP = c / (smokeCount - 1f);
                float particleSize = FDMathUtil.lerp(minSmokeSize, maxSmokeSize, 1 - smokeP);
                for (int dirswitch = -1; dirswitch <= 1; dirswitch += 2) {
                    for (int k = -4; k < 4; k++) {

                        Vec3 smokeDir = direction.scale(dirswitch).yRot(k * smokeDirStep);
                        float smokeSpeed = calculateParticleSpeed(smokeTravelDistance + random.nextFloat() * particleSize, smokeFriction, 40);
                        Vec3 speed = smokeDir.scale(smokeSpeed);
                        float color = random.nextFloat() * 0.2f + 0.2f;

                        BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                                .color(color,color,color)
                                .friction(smokeFriction)
                                .minSpeed(0.01f)
                                .size(particleSize)
                                .lifetime(0,2,100 + random.nextInt(60))
                                .build();

                        level.addParticle(options, ppos.x,ppos.y,ppos.z, speed.x,random.nextFloat() * (1 - p) * 1f,speed.z);

                    }
                }
                smokeTravelDistance += particleSize;
            }

            //BALLS (not that balls, no, NO!)

            for (int count = 0; count < 10; count++){

                for (int dirswitch = -1; dirswitch <= 1; dirswitch += 2){

                    Vec3 randomOffset = direction.scale(dirswitch * random.nextFloat() * radius / 2)
                            .yRot(FDMathUtil.FPI / 4 * random.nextFloat());
                    float len = (float) randomOffset.length();
                    float pb = len / (radius / 2f);
                    float ballSize = FDMathUtil.lerp(1f,1.5f,1 - pb);
                    float ballYSpeed = FDMathUtil.lerp(
                            0.1f + random.nextFloat() * 0.2f,
                            0.3f + random.nextFloat() * 0.6f,FDEasings.easeOut(1 - pb));


                    Vec3 actualppos = ppos.add(randomOffset);


                    BallParticleOptions options = BallParticleOptions.builder()
                            .color(0.3f + random.nextFloat() * 0.1f,0.8f, 1f)
                            .brightness(2)
                            .size(ballSize)
                            .friction(0.6f + random.nextFloat() * 0.2f)
                            .scalingOptions(2,0,20 + random.nextInt(40))
                            .build();

                    Vec3 vspeed = direction.scale(dirswitch).scale(FDEasings.easeIn(pb));

                    level.addParticle(options, actualppos.x,actualppos.y,actualppos.z,vspeed.x,ballYSpeed, vspeed.z);

                }

            }

        }

        //JUMPING PARTICLES

        int hammersCount = GeburahRenderer.HAMMER_AMOUNT;
        float angle = FDMathUtil.FPI * 2 / hammersCount;
        for (int i = 0; i < hammersCount; i++){

            Vec3 hammerOffset = new Vec3(1,0,0).yRot(angle * i);

            Vec3 hammerImpactPos = pos.add(hammerOffset.scale(radius));

            for (var dir : new HorizontalCircleRandomDirections(level.random, 24, 0.5f)){

                ColoredJumpingParticleOptions options = ColoredJumpingParticleOptions.builder()
                        .colorStart(new FDColor(0.5f,0.8f,1f,1f))
                        .colorEnd(new FDColor(1f,1f,1f,1f))
                        .maxJumpAmount(1)
                        .maxPointsInTrail(3)
                        .reflectionStrength(0.5f)
                        .size(0.05f)
                        .gravity(2)
                        .lifetime(-1)
                        .build();

                float randomXZSpeed = random.nextFloat() * 0.3f + 0.3f;

                Vec3 speed = new Vec3(dir.x * randomXZSpeed,0.4f + random.nextFloat() * 0.2f, dir.z * randomXZSpeed);

                level.addParticle(options, hammerImpactPos.x + dir.x,hammerImpactPos.y,hammerImpactPos.z + dir.z,
                        speed.x,speed.y,speed.z);

            }

        }


    }

    public static void geburahTriggerSinPunishmentAttackEffect(Vec3 pos, int data){
        if (!(FDClientHelpers.getClientLevel().getEntity(data) instanceof GeburahEntity geburah)) return;
        geburah.sinPunishmentAttackTicker = GeburahEntity.SIN_PUNISHMENT_ATTACK_DURATION;
    }

    public static void judgementBirdRayParticles(Vec3 pos, int data){

        Level level = FDClientHelpers.getClientLevel();

        BallParticleOptions flash = BallParticleOptions.builder()
                .color(0.5f,0.8f,1f)
                .scalingOptions(1,0,2)
                .brightness(4)
                .size(8f)
                .build();

        level.addParticle(flash,true,pos.x,pos.y,pos.z,0,0,0);


        Vec3 direction = FDUtil.decodeDirection(data);
        Vec3 horizontalReversedDirection = direction.multiply(-1,0,-1).normalize();

        Matrix4f mat = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mat, direction);


        float maxParticleSize = 0.5f;
        float minParticleSize = 0.1f;
        float particleFriction = 0.7f;
        int maxParticleRows = 6;

        int particleTravelTime = 100;

        float cumulativeSize = 0;
        float cumulativeSize2 = 0;

        float travelDistWindow = 0.5f;
        float travelDistWindow2 = 0.5f;
        float randomXZSpread = 0.1f;

        float angleSlamEarth = FDMathUtil.FPI / 4;

        //MAIN
        for (int i = 0; i < maxParticleRows;i++) {

            float ip = i / (maxParticleRows - 1f);

            float size = FDMathUtil.lerp(minParticleSize, maxParticleSize, 1 - ip);
            int repetitionCount = Math.round(FDMathUtil.lerp(1,5,ip));
            int directionsCount = Math.round(FDMathUtil.lerp(1,6,ip));

            float r = FDMathUtil.lerp(0.0f,1f,ip * ip);
            float gr = FDMathUtil.lerp(0.5f,1f,ip);
            float b = FDMathUtil.lerp(1f,1f,ip);
            //FDMathUtil.lerp(0.0f,1f,ip * ip);

            for (int g = 0; g < repetitionCount; g++) {
                for (var dir : new HorizontalCircleRandomDirections(level.random, directionsCount, 1f)) {

                    //---------------------------RAY-----------------------------

                    float travelDistance = cumulativeSize + travelDistWindow * random.nextFloat();

                    float particleSpeed = calculateParticleSpeed(travelDistance, particleFriction, particleTravelTime);
                    particleSpeed = particleSpeed + random.nextFloat() * 0.05f;

                    Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                            dir.x + random.nextFloat() * randomXZSpread * 2 - randomXZSpread,
                            3 - random.nextFloat() * 0.5f,
                            dir.z + random.nextFloat() * randomXZSpread * 2 - randomXZSpread
                    ).normalize().multiply(particleSpeed, particleSpeed, particleSpeed));

                    Vec3 spawnOffset = BossUtil.matTransformDirectionVec3(mat, dir.multiply(0.5f, 0.5f, 0.5f));

                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .brightness(2)
                            .size(size)
                            .color(r, gr, b, 1f)
                            .friction(particleFriction)
                            .scalingOptions(0, 0, 20 + random.nextInt(10))
                            .build();

                    level.addParticle(ballParticleOptions, true, pos.x + spawnOffset.x, pos.y + spawnOffset.y, pos.z + spawnOffset.z, speed.x, speed.y, speed.z);


                    //--------------------------------------EARTH SLAM---------------------------------------------

                    double angleBetween = FDMathUtil.angleBetweenVectors(horizontalReversedDirection, dir);

                    if (angleBetween > angleSlamEarth) continue;

                    BallParticleOptions ballParticleOptions2 = BallParticleOptions.builder()
                            .brightness(2)
                            .size(size * 2)
                            .color(r,gr,b, 1f)
                            .friction(particleFriction)
                            .scalingOptions(0, 0, 20 + random.nextInt(10))
                            .build();

                    float travelDistance2 = cumulativeSize2 + travelDistWindow2 * random.nextFloat();

                    float particleSpeed2 = calculateParticleSpeed(travelDistance2,particleFriction,particleTravelTime);


                    Vec3 speed2 = new Vec3(
                            dir.x + random.nextFloat() * randomXZSpread * 2 - randomXZSpread,
                            0.1f + random.nextFloat() * 0.5f,
                            dir.z + random.nextFloat() * randomXZSpread * 2 - randomXZSpread
                    ).normalize().scale(particleSpeed2);

                    Vec3 spawnPos2 = pos.add(new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat()));


                    level.addParticle(ballParticleOptions2, true, spawnPos2.x,spawnPos2.y,spawnPos2.z, speed2.x, speed2.y, speed2.z);

                }
            }

            cumulativeSize += travelDistWindow;
            cumulativeSize2 += travelDistWindow2;

        }



        //SMOKE
        int smokesCount = 10;
        float smokesFriction = 0.7f;
        int smokesTravelTime = 20;

        for (int i = 0; i < smokesCount; i++){

            float p = i / (float) (smokesCount - 1);
            int count = Math.round(FDMathUtil.lerp(6,12,p));
            float distance = 0.3f * i;

            for (var dir : new HorizontalCircleRandomDirections(level.random, count, 1f)){

                float particleSpeed = (distance * (1 - smokesFriction) / (1 - (float) Math.pow(smokesFriction, smokesTravelTime)));

                Vec3 pspeed = dir.multiply(particleSpeed,0,particleSpeed);

                float col = 0.2f + random.nextFloat() * 0.2f;

                BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                        .color(col,col,col)
                        .friction(smokesFriction)
                        .size(1f)
                        .minSpeed(0.025f)
                        .lifetime(0,0,30)
                        .build();

                level.addParticle(options, true, pos.x,pos.y,pos.z,pspeed.x,pspeed.y,pspeed.z);


            }

        }


        //JUMPING PARTICLES
        for (var dir : new HorizontalCircleRandomDirections(level.random, 12, 1f)){

            ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                    .colorStart(new FDColor(1f, 1f, 1f, 1f))
                    .colorEnd(new FDColor(0.3f, 0.8f, 1f, 1f))
                    .maxPointsInTrail(5)
                    .reflectionStrength(0.33f)
                    .gravity(1.5f)
                    .lifetime(-1)
                    .maxJumpAmount(1)
                    .size(0.03f)
                    .build();

            float horizontalSpeed = random.nextFloat() * 0.25f + 0.1f;

            float verticalSpeed = random.nextFloat() * 0.1f + 0.25f;

            dir = dir.subtract(direction.scale(0.75));

            level.addParticle(options,pos.x,pos.y,pos.z,dir.x * horizontalSpeed, verticalSpeed,dir.z * horizontalSpeed);
        }

        //MAIN 2





    }

    public static void geburahWeaponsStartLaser(Vec3 pos, int data){

        Level level = FDClientHelpers.getClientLevel();

        if (level.getEntity(data) instanceof GeburahEntity geburah){

            float particleFriction = 0.8f;

            float r = 0.3f;
            float g = 0.7f;
            float b = 1f;

            float rowWidth = 0.5f;

            for (var cannon : geburah.getCannonsPositionAndDirection()){

                Vec3 cannonPos = cannon.first;
                Vec3 cannonDirection = cannon.second;

                Matrix4f mat = new Matrix4f();

                FDRenderUtil.applyMovementMatrixRotations(mat, cannonDirection);

                int rowCount = 5;

                float offs = 0;

                for (int i = 0; i < rowCount; i++) {

                    for (var dir : new HorizontalCircleRandomDirections(geburah.getRandom(), 12, 1f)) {

                        float pspeed = calculateParticleSpeed(offs + random.nextFloat() * rowWidth, particleFriction, 10);
                        Vec3 desiredSpeed = new Vec3(
                                dir.x + random.nextFloat() * 0.8 - 0.4,
                                3,
                                dir.z + random.nextFloat() * 0.8 - 0.4
                        ).normalize().scale(pspeed);

                        Vec3 sppos = BossUtil.matTransformDirectionVec3(mat, dir.scale(0.25));

                        desiredSpeed = BossUtil.matTransformDirectionVec3(mat, desiredSpeed);

                        BallParticleOptions options = BallParticleOptions.builder()
                                .friction(particleFriction)
                                .size(0.5f)
                                .brightness(2)
                                .scalingOptions(0,0,10)
                                .color(r,g,b)
                                .build();


                        level.addParticle(options, true, cannonPos.x + sppos.x,cannonPos.y + sppos.y,cannonPos.z + sppos.z, desiredSpeed.x,desiredSpeed.y, desiredSpeed.z);

                    }

                    offs += rowWidth;
                }

            }

        }
    }


    public static void geburahRayChargeParticles(Vec3 pos, int data){

        if (FDClientHelpers.getClientLevel().getEntity(data) instanceof GeburahEntity geburah) {

            pos = geburah.getCorePosition();
            int count = 3;

            float randomYRot = random.nextFloat() * FDMathUtil.FPI * 2;
            float randomXRot = random.nextFloat() * FDMathUtil.FPI * 2;

            float r = 1f;
            float gr = 0.49f;
            float b = 0.2f;

            Level level = FDClientHelpers.getClientLevel();

            for (int x = -count; x <= count; x++) {
                for (int y = -count; y <= count; y++) {
                    for (int z = -count; z <= count; z++) {

                        if (random.nextFloat() > 0.05f) continue;

                        Vec3 v = new Vec3(x, y, z).normalize()
                                .yRot(randomYRot).xRot(randomXRot);

                        BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                                .brightness(2)
                                .size(0.5f)
                                .color(r, gr, b, 1f)
                                .friction(1.4f)
                                .scalingOptions(10, 0, 0)
                                .build();

                        Vec3 ppos = pos.add(v.scale(2));

                        float speed = 0.05f;

                        level.addParticle(ballParticleOptions, true, ppos.x, ppos.y, ppos.z, -v.x * speed, -v.y * speed, -v.z * speed);

                    }
                }
            }
        }
    }

    private static float calculateParticleSpeed(float travelDistance, float particleFriction, int particleTravelTime){
        return (travelDistance * (1 - particleFriction) / (1 - (float) Math.pow(particleFriction, particleTravelTime)));
    }

    public static void geburahRayParticles(Vec3 pos, int data){


        //1f,0.8f,0.2f
        Level level = FDClientHelpers.getClientLevel();

        BallParticleOptions flash = BallParticleOptions.builder()
                .color(1f,0.8f,0.2f)
                .scalingOptions(1,0,2)
                .brightness(4)
                .size(15f)
                .build();

        level.addParticle(flash,true,pos.x,pos.y,pos.z,0,0,0);


        Vec3 direction = FDUtil.decodeDirection(data);
        Vec3 horizontalReversedDirection = direction.multiply(-1,0,-1).normalize();

        Matrix4f mat = new Matrix4f();
        FDRenderUtil.applyMovementMatrixRotations(mat, direction);


        float maxParticleSize = 2f;
        float minParticleSize = 0.1f;
        float particleFriction = 0.7f;
        int maxParticleRows = 10;

        int particleTravelTime = 100;

        float cumulativeSize = 0;
        float cumulativeSize2 = 0;

        float travelDistWindow = 1f;
        float travelDistWindow2 = 1f;
        float randomXZSpread = 0.25f;

        float angleSlamEarth = FDMathUtil.FPI / 4;

        //MAIN
        for (int i = 0; i < maxParticleRows;i++) {

            float ip = i / (maxParticleRows - 1f);

            float size = FDMathUtil.lerp(minParticleSize, maxParticleSize, 1 - ip);
            int repetitionCount = Math.round(FDMathUtil.lerp(1,3,ip));
            int directionsCount = Math.round(FDMathUtil.lerp(1,7,ip));

            float r = FDMathUtil.lerp(1f,1f,ip);
            float gr = FDMathUtil.lerp(0.5f,1f,ip);
            float b = FDMathUtil.lerp(0.0f,1f,ip * ip);

            for (int g = 0; g < repetitionCount; g++) {
                for (var dir : new HorizontalCircleRandomDirections(level.random, directionsCount, 1f)) {

                    //---------------------------RAY-----------------------------

                    float travelDistance = cumulativeSize + travelDistWindow * random.nextFloat();

                    float particleSpeed = calculateParticleSpeed(travelDistance, particleFriction, particleTravelTime);

                    Vec3 speed = BossUtil.matTransformDirectionVec3(mat, new Vec3(
                            dir.x + random.nextFloat() * randomXZSpread * 2 - randomXZSpread,
                            3 - random.nextFloat() * 0.5f,
                            dir.z + random.nextFloat() * randomXZSpread * 2 - randomXZSpread
                    ).normalize().multiply(particleSpeed, particleSpeed, particleSpeed));

                    Vec3 spawnOffset = BossUtil.matTransformDirectionVec3(mat, dir.multiply(0.5f, 0.5f, 0.5f));

                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .brightness(2)
                            .size(size)
                            .color(r, gr, b, 1f)
                            .friction(particleFriction)
                            .scalingOptions(0, 0, 20 + random.nextInt(10))
                            .build();

                    level.addParticle(ballParticleOptions, true, pos.x + spawnOffset.x, pos.y + spawnOffset.y, pos.z + spawnOffset.z, speed.x, speed.y, speed.z);


                    //--------------------------------------EARTH SLAM---------------------------------------------

                    double angleBetween = FDMathUtil.angleBetweenVectors(horizontalReversedDirection, dir);

                    if (angleBetween > angleSlamEarth) continue;

                    BallParticleOptions ballParticleOptions2 = BallParticleOptions.builder()
                            .brightness(2)
                            .size(size * 2)
                            .color(r,gr,b, 1f)
                            .friction(particleFriction)
                            .scalingOptions(0, 0, 20 + random.nextInt(10))
                            .build();

                    float travelDistance2 = cumulativeSize2 + travelDistWindow2 * random.nextFloat();

                    float particleSpeed2 = calculateParticleSpeed(travelDistance2,particleFriction,particleTravelTime);


                    Vec3 speed2 = new Vec3(
                            dir.x + random.nextFloat() * randomXZSpread * 2 - randomXZSpread,
                            0.1f + random.nextFloat() * 0.5f,
                            dir.z + random.nextFloat() * randomXZSpread * 2 - randomXZSpread
                    ).normalize().scale(particleSpeed2);

                    Vec3 spawnPos2 = pos.add(new Vec3(1,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat()));


                    level.addParticle(ballParticleOptions2, true, spawnPos2.x,spawnPos2.y,spawnPos2.z, speed2.x, speed2.y, speed2.z);

                }
            }

            cumulativeSize += travelDistWindow;
            cumulativeSize2 += travelDistWindow2;

        }


        //SMOKE
        int smokesCount = 10;
        float smokesFriction = 0.8f;
        int smokesTravelTime = 20;

        for (int i = 0; i < smokesCount; i++){

            float p = i / (float) (smokesCount - 1);
            int count = Math.round(FDMathUtil.lerp(12,15,p));
            float distance = 0.8f * i;

            for (var dir : new HorizontalCircleRandomDirections(level.random, count, 1f)){

                float particleSpeed = (distance * (1 - smokesFriction) / (1 - (float) Math.pow(smokesFriction, smokesTravelTime)));

                Vec3 pspeed = dir.multiply(particleSpeed,0,particleSpeed);

                float col = 0.2f + random.nextFloat() * 0.2f;

                BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                        .color(col,col,col)
                        .friction(smokesFriction)
                        .size(4f)
                        .minSpeed(0.025f)
                        .lifetime(0,0,30)
                        .build();

                level.addParticle(options, true, pos.x,pos.y,pos.z,pspeed.x,pspeed.y,pspeed.z);


            }

        }

        //STRIPES
        for (var dir : new HorizontalCircleRandomDirections(level.random, 12, 0)){

            float randomOffset = FDMathUtil.FPI / 8 + FDMathUtil.FPI / 8 * level.random.nextFloat();
            randomOffset *= level.random.nextBoolean() ? 1 : -1;

            float offset1 = 2;
            float offset2 = 3;

            Vec3 p1 = Vec3.ZERO;
            Vec3 p2 = BossUtil.matTransformDirectionVec3(mat, dir.yRot(randomOffset).add(dir.x * offset1,5,dir.z * offset1)
                    .add(
                            random.nextFloat() * 0.5 - 0.25f,
                            random.nextFloat() * 2 - 1,
                            random.nextFloat() * 0.5 - 0.25f
                    ));
            Vec3 p3 = BossUtil.matTransformDirectionVec3(mat, dir.yRot(-randomOffset).add(dir.x * offset2,10,dir.z * offset2)
                    .add(
                            random.nextFloat() * 0.5 - 0.25f,
                            random.nextFloat() * 2 - 1,
                            random.nextFloat() * 0.5 - 0.25f
                    ));

            StripeParticleOptions options = StripeParticleOptions.builder()
                    .startColor(new FDColor(1f,0.4f,0.1f,1f))
                    .endColor(new FDColor(1f,0.8f,0.8f,1f))
                    .lifetime(10 + random.nextInt(5))
                    .lod(25)
                    .scale(0.1f)
                    .stripePercentLength(0.5f)
                    .endOutPercent(0.1f)
                    .startInPercent(0.1f)
                    .offsets(p1,p2,p3)
                    .build();

            level.addParticle(options, true, pos.x, pos.y,pos.z,0,0,0);

        }

        //JUMPING PARTICLES
        for (var dir : new HorizontalCircleRandomDirections(level.random, 13, 1f)){

            ColoredJumpingParticleOptions options = new ColoredJumpingParticleOptions.Builder()
                    .colorStart(new FDColor(1f, 1f, 1f, 1f))
                    .colorEnd(new FDColor(1f, 0.8f, 0.3f, 1f))
                    .maxPointsInTrail(5)
                    .reflectionStrength(0.33f)
                    .gravity(1.5f)
                    .lifetime(-1)
                    .maxJumpAmount(1)
                    .size(0.03f)
                    .build();

            float horizontalSpeed = random.nextFloat() * 0.5f + 0.2f;

            float verticalSpeed = random.nextFloat() * 0.3f + 0.3f;

            dir = dir.subtract(direction.scale(0.75));

            level.addParticle(options,pos.x,pos.y,pos.z,dir.x * horizontalSpeed, verticalSpeed,dir.z * horizontalSpeed);
        }

        //MAIN 2





    }

    public static void chesedAddEmitter(Vec3 pos, int entityId){

        if (BossConfigs.BOSS_CONFIG_CLIENT.get().lessParticles) return;

        var emitterData = ParticleEmitterData.builder(BigSmokeParticleOptions.builder()
                        .color(0.25f, 0.25f, 0.25f)
                        .lifetime(0, 0, 100)
                        .size(1f)
                        .build())
                .lifetime(400)
                .particlesPerTick(10)
                .processor(new CircleSpawnProcessor(new Vec3(0,-1,0),0.05f,0.1f,36))
                .position(pos)
                .build();

        ParticleEmitterHandler.addParticleEmitter(emitterData);

    }

    public static void malkuthSwordInsertParticles(int malkuthEntityId){

        Level level = FDClientHelpers.getClientLevel();

        if (!(level.getEntity(malkuthEntityId) instanceof MalkuthEntity malkuth)) return;

        Matrix4f iceSwordTransform = malkuth.getModelPartTransformation(malkuth, MalkuthEntity.getMalkuthSwordPlaceBone(MalkuthAttackType.ICE),MalkuthEntity.getClientModel());
        Matrix4f fireSwordTransform = malkuth.getModelPartTransformation(malkuth, MalkuthEntity.getMalkuthSwordPlaceBone(MalkuthAttackType.FIRE),MalkuthEntity.getClientModel());

        Vector3f v1 = iceSwordTransform.transformPosition(0,0,0, new Vector3f());
        Vector3f v2 = fireSwordTransform.transformPosition(0,0,0, new Vector3f());

        Vector3f vd1 = iceSwordTransform.transformDirection(0,1,0, new Vector3f());
        Vector3f vd2 = fireSwordTransform.transformDirection(0,1,0, new Vector3f());

        Vec3 posIce = malkuth.position().add(v1.x,v1.y,v1.z);
        Vec3 posFire = malkuth.position().add(v2.x,v2.y,v2.z);

        Vec3 dirIce = new Vec3(vd1.x,vd1.y,vd1.z);
        Vec3 dirFire = new Vec3(vd2.x,vd2.y,vd2.z);

        swordInsertParticles(MalkuthAttackType.ICE, posIce, dirIce);
        swordInsertParticles(MalkuthAttackType.FIRE, posFire, dirFire);

    }

    public static void swordInsertParticles(MalkuthAttackType type, Vec3 pos, Vec3 direction){

        Matrix4f t = new Matrix4f();

        FDRenderUtil.applyMovementMatrixRotations(t, direction);

        Level level = FDClientHelpers.getClientLevel();

        float maxSize = 0.35f;
        float minSize = 0.1f;

        for (int i = 0; i < 200;i++){

            float angle = FDMathUtil.FPI * 2 * random.nextFloat();

            float hoffset = random.nextFloat() * 0.5f;

            Vector3f v = new Vector3f(hoffset,0,0).rotateY(angle);

            v = t.transformDirection(v);

            Vec3 p = pos.add(v.x,v.y,v.z);

            float size = minSize + random.nextFloat() * (maxSize - minSize);

            Vector3f color = MalkuthEntity.getAndRandomizeColor(type, level.random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .scalingOptions(0,0,35 + random.nextInt(10))
                    .size(size)
                    .color(color.x,color.y,color.z)
                    .brightness(2)
                    .friction(0.7f)
                    .build();

            float sizep = FDEasings.easeOut(1 - size / maxSize);

            double speed = 0.2f + sizep * 2f * random.nextFloat();

            Vec3 sp = direction.normalize().multiply(speed,speed,speed);

            level.addParticle(ballParticleOptions,true, p.x,p.y,p.z,
                    sp.x + random.nextFloat() * 0.5f - 0.25f,
                    sp.y + random.nextFloat() * 0.5f - 0.25f,
                    sp.z + random.nextFloat() * 0.5f - 0.25f
            );

            if (level.random.nextFloat() < 0.2f) {

                ParticleType<?> ptype = type.isFire() ? BossParticles.FLAME_WITH_STONE.get() : BossParticles.ICE_CHUNK.get();

                GravityParticleOptions gravityParticleOptions = new GravityParticleOptions(ptype, 20 + random.nextInt(4), 0.6f + random.nextFloat() * 0.6f,
                        (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);

                level.addParticle(gravityParticleOptions, true, p.x, p.y, p.z,
                        sp.x + random.nextFloat() * 0.5f - 0.25f,
                        sp.y + random.nextFloat() * 0.5f - 0.25f,
                        sp.z + random.nextFloat() * 0.5f - 0.25f
                );
            }

        }

    }

    public static void volcanoErruptionParticles(Vec3 pos, int data){

        Level level = FDClientHelpers.getClientLevel();

        float radius = data / 16f;

        int amount = 30;

        float angle = FDMathUtil.FPI * 2 / amount;

        float maxBallSize = 1f;

        for (int i = 0; i < amount;i++){

            float rndAdd = random.nextFloat() * angle;
            float rndAdd2 = random.nextFloat() * angle;

            Vec3 offs = new Vec3(1,0,0).yRot(rndAdd + angle * i);
            Vec3 offs2 = new Vec3(1,0,0).yRot(rndAdd2 + angle * i);

            float aradius = radius - random.nextFloat() * 0.5f * radius;

            Vec3 ppos = pos.add(offs.multiply(aradius,aradius,aradius));
            Vec3 ppos2 = pos.add(offs2.multiply(radius,radius,radius));

            float size = 0.1f + random.nextFloat() * (maxBallSize - 0.1f);

            float sizeP = FDEasings.easeInOut(size / maxBallSize);

            Vector3f color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.FIRE, level.random);

            BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                    .brightness(2)
                    .size(size)
                    .scalingOptions(0,0,20 + random.nextInt(10))
                    .friction(0.9f - 0.1f * sizeP)
                    .color(color.x,color.y,color.z)
                    .build();

            level.addParticle(ballParticleOptions,true, ppos.x,ppos.y,ppos.z,
                    offs.x * (0.3f + random.nextFloat() * 0.2f),
                    1 + random.nextFloat() * 0.5,
                    offs.z * (0.3f + random.nextFloat() * 0.2f)
            );

            if (random.nextBoolean()) {
                GravityParticleOptions options = new GravityParticleOptions(BossParticles.FLAME_WITH_STONE.get(), 20 + random.nextInt(4), 0.6f + random.nextFloat() * 0.6f,
                        (float) Mob.DEFAULT_BASE_GRAVITY * 20, 2f, true);

                level.addParticle(options, true, ppos2.x, ppos2.y, ppos2.z,
                        offs2.x * (0.5f + random.nextFloat() * 0.2f),
                        0.5 + random.nextFloat() * 0.5,
                        offs2.z * (0.5f + random.nextFloat() * 0.2f)
                );
            }

            if (random.nextFloat() > 0.8f) {
                float col = 0.2f + random.nextFloat() * 0.1f;
                BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                        .lifetime(0, 0, 20 + random.nextInt(10))
                        .friction(0.8f)
                        .size(2f + random.nextFloat() * 2f)
                        .minSpeed(0.0025f)
                        .color(col,col,col)
                        .build();

                level.addParticle(options, true, ppos2.x, ppos2.y, ppos2.z,
                        offs2.x * (0.5f + random.nextFloat() * 0.2f),
                        0.5 + random.nextFloat() * 0.5,
                        offs2.z * (0.5f + random.nextFloat() * 0.2f)
                );
            }




        }

        Vec3 randomDir = new Vec3(radius,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());
        Vec3 randomDirN = randomDir.normalize();

        Vector3f colFire = MalkuthEntity.getMalkuthAttackPreparationParticleColor(MalkuthAttackType.FIRE);

        FDColor fireColorStart = new FDColor(colFire.x,colFire.y - random.nextFloat() * 0.1f - 0.3f,colFire.z,0.5f);
        FDColor fireColor = new FDColor(colFire.x,colFire.y + random.nextFloat() * 0.1f,colFire.z,1f);

        float firstMultiplier = 2;
        float secondMultiplier = 8f;
        StripeParticleOptions stripeParticleOptions = StripeParticleOptions.builder()
                .startColor(fireColorStart)
                .endColor(fireColor)
                .lifetime(10 + random.nextInt(10))
                .lod(50)
                .scale(0.1f)
                .stripePercentLength(0.5f)
                .endOutPercent(0.2f)
                .startInPercent(0.2f)
                .offsets(
                        Vec3.ZERO,
                        randomDirN.multiply(firstMultiplier,firstMultiplier,firstMultiplier).add(random.nextFloat() * 2 - 1,2 + random.nextFloat(),random.nextFloat() * 2 - 1),
                        randomDirN.multiply(secondMultiplier,secondMultiplier,secondMultiplier).add(random.nextFloat() * 2 - 1,7 + random.nextFloat() * 3f,random.nextFloat() * 2 - 1)
                        )
                .build();

        Vec3 stripePos = pos.add(randomDir);

        level.addParticle(stripeParticleOptions,true,stripePos.x,stripePos.y,stripePos.z,0,0,0);


    }

    public static void malkuthPlayerFireballExplode(Vec3 pos, int type){

        MalkuthAttackType attackType = type == 1 ? MalkuthAttackType.ICE : MalkuthAttackType.FIRE;

        Level level = FDClientHelpers.getClientLevel();

        for (int i = 0; i < 200;i++){
            Vector3f color = MalkuthEntity.getAndRandomizeColor(attackType, level.random);

            ParticleOptions options;

            float speedMd = 1f;

            if (level.random.nextFloat() > 0.5f){
                options = BallParticleOptions.builder()
                        .brightness(3)
                        .size(0.3f + random.nextFloat() * 0.2f)
                        .color(color.x,color.y,color.z)
                        .friction(0.7f)
                        .scalingOptions(0,0,10 + random.nextInt(10))
                        .build();
            }else{
                speedMd = 0.5f;
                if (attackType.isFire()){
                    if (random.nextFloat() > 0.3) {
                        options = ParticleTypes.FLAME;
                    }else {
                        options = ParticleTypes.LAVA;
                    }
                }else{
                    options = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),20 + random.nextInt(4),0.25f + random.nextFloat() * 0.2f,
                            (float) Mob.DEFAULT_BASE_GRAVITY * 10,2f,true);
                }
            }

            float rnds = random.nextFloat() * 0.5f + 0.5f;

            Vec3 speed = new Vec3(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1).normalize().multiply(speedMd * rnds,speedMd * rnds,speedMd * rnds);

            float rnd = random.nextFloat();

            Vec3 ppos = pos.add(speed.normalize().multiply(rnd,rnd,rnd));

            level.addParticle(options,ppos.x,ppos.y,ppos.z,speed.x,speed.y,speed.z);


        }

        Vector3f color = MalkuthEntity.getAndRandomizeColor(attackType, level.random);

        level.addParticle(BallParticleOptions.builder()
                .scalingOptions(1,0,2)
                .brightness(2)
                .size(10f)
                .color(color.x,color.y,color.z)
                .build(),true, pos.x,pos.y,pos.z,0,0,0);

        for (int i = 0; i < 100;i++){

            float col = random.nextFloat() * 0.1f + 0.3f;

            BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                    .color(col,col,col,1f)
                    .size(1f + random.nextFloat() * 1)
                    .minSpeed(0.01f)
                    .friction(0.7f)
                    .lifetime(0,0,25 + random.nextInt(5))
                    .build();

            float rnd = random.nextFloat() * 1f + 0.5f;

            Vec3 speed = new Vec3(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1).normalize().multiply(rnd,rnd,rnd);

            Vec3 ppos = pos.add(speed.normalize().multiply(0.15f,0.15f,0.15f));

            level.addParticle(options, ppos.x,ppos.y,ppos.z, speed.x,speed.y,speed.z);

        }


    }

    public static void malkuthFireballExplode(Vec3 pos, int type){

        MalkuthAttackType attackType = type == 1 ? MalkuthAttackType.ICE : MalkuthAttackType.FIRE;

        Level level = FDClientHelpers.getClientLevel();

        for (int i = 0; i < 100;i++){

            Vector3f color = MalkuthEntity.getAndRandomizeColor(attackType, level.random);

            float vspeed = random.nextFloat() * 1.5f;

            float hspeed = FDEasings.easeOut(1.5f-vspeed) * 0.5f;





            ParticleOptions options;

            if (level.random.nextFloat() > 0.5f){
                options = BallParticleOptions.builder()
                        .brightness(3)
                        .size(0.1f + random.nextFloat() * 0.1f)
                        .color(color.x,color.y,color.z)
                        .friction(0.7f)
                        .scalingOptions(0,0,20 + random.nextInt(10))
                        .build();
            }else{
                if (attackType.isFire()){
                    vspeed = vspeed * 0.25f;
                    hspeed = hspeed * 0.25f;
                    if (random.nextFloat() > 0.3) {
                        options = ParticleTypes.FLAME;
                    }else {
                        options = ParticleTypes.LAVA;
                    }
                }else{
                    vspeed = vspeed * 0.75f;
                    hspeed = hspeed * 0.75f;
                    options = new GravityParticleOptions(BossParticles.ICE_CHUNK.get(),20 + random.nextInt(4),0.25f + random.nextFloat() * 0.2f,
                            (float) Mob.DEFAULT_BASE_GRAVITY * 10,2f,true);
                }
            }

            Vec3 speed = new Vec3(vspeed,0,0).yRot(level.random.nextFloat() * FDMathUtil.FPI * 2).add(0,hspeed,0);

            float rnd = random.nextFloat();

            Vec3 ppos = pos.add(speed.normalize().multiply(rnd,rnd,rnd));

            level.addParticle(options,ppos.x,ppos.y,ppos.z,speed.x,speed.y,speed.z);

        }

        for (int i = 0; i < 30;i++){

            float col = random.nextFloat() * 0.1f + 0.3f;

            BigSmokeParticleOptions options = BigSmokeParticleOptions.builder()
                    .color(col,col,col,1f)
                    .size(1f + random.nextFloat() * 1)
                    .minSpeed(0.01f)
                    .friction(0.7f)
                    .lifetime(0,0,25 + random.nextInt(5))
                    .build();

            Vec3 speed = new Vec3(0.5f + level.random.nextFloat() * 1f,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

            double hspeed = Math.sqrt(speed.x * speed.x + speed.z * speed.z);

            Vec3 ppos = pos.add(speed.normalize().multiply(0.15f,0.15f,0.15f));

            level.addParticle(options, ppos.x,ppos.y,ppos.z, speed.x,speed.y + (1.5f - hspeed) * random.nextFloat(),speed.z);

        }


    }

    public static void malkuthFloat(int id){
        ClientLevel clientLevel = (ClientLevel) FDClientHelpers.getClientLevel();

        if (clientLevel.getEntity(id) instanceof MalkuthEntity malkuth){

            Vec3 pos = malkuth.position();
            Vec3 old = new Vec3(
                    malkuth.xo,
                    malkuth.yo,
                    malkuth.zo
            );

            Vec3 between = old.subtract(pos);

            Vec3 nrm = between.normalize();

            double dist = between.length();

            for (float i = -1; i < dist; i+=1f){

                Vec3 startPos = pos.add(nrm.multiply(i,i,i)).add(0,2,0);

                for (int g = 0; g < 10; g++) {

                    Vector3f color = MalkuthEntity.getAndRandomizeColor(MalkuthAttackType.getRandom(clientLevel.random), clientLevel.random);


                    float v = random.nextFloat() * 1.5f + 0.1f;


                    BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                            .size(0.3f - random.nextFloat() * 0.1f)
                            .scalingOptions(5,0,random.nextInt(10) + 10)
                            .brightness(2)
                            .color(color.x,color.y,color.z)
                            .particleProcessor(new CircleParticleProcessor(startPos.add(0,-v,0), true, true, 1))
                            .build();

                    Vec3 horizontalOffset = new Vec3(1.6f - v,0,0).yRot(FDMathUtil.FPI * 2 * random.nextFloat());

                    Vec3 ppos = startPos.add(horizontalOffset).add(0,-v,0);

                    clientLevel.addParticle(ballParticleOptions, true, ppos.x,ppos.y,ppos.z,0,-random.nextFloat() * 0.1f,0);

                }
            }


        }

    }

    public static void malkuthSwordChargeParticles(Vec3 encodedMalkuthAttackTypeISAIDDONTJUDGEME, int entityId){
        Level level = FDClientHelpers.getClientLevel();
        if (level.getEntity(entityId) instanceof MalkuthEntity malkuthEntity) {

            encodedMalkuthAttackTypeISAIDDONTJUDGEME = encodedMalkuthAttackTypeISAIDDONTJUDGEME.subtract(malkuthEntity.position());

            MalkuthAttackType malkuthAttackType;
            if (encodedMalkuthAttackTypeISAIDDONTJUDGEME.dot(new Vec3(1, 0, 0)) > 0) {
                malkuthAttackType = MalkuthAttackType.FIRE;
            } else {
                malkuthAttackType = MalkuthAttackType.ICE;
            }

            Vector3f color = MalkuthEntity.getMalkuthAttackPreparationParticleColor(malkuthAttackType);

            String boneName = MalkuthEntity.getMalkuthSwordPlaceBone(malkuthAttackType);

            FDModel clientModel = MalkuthEntity.getClientModel();

            Matrix4f boneTransform = malkuthEntity.getModelPartTransformation(malkuthEntity, boneName, clientModel);

            Vector3f swordDir = boneTransform.transformDirection(0,1,0,new Vector3f());

            Vector3f swordDirUp = boneTransform.transformDirection(0,0,-1,new Vector3f());

            Vector3f swordPos = boneTransform.transformPosition(0,0,0,new Vector3f());

            int totalParticles = 15;

            float startHeight = 0.5f;
            float swordBladeHeight = 1.75f;
            float particleSize = 0.15f;

            Vec3 swordWorldPosition = malkuthEntity.position().add(
                    swordPos.x,swordPos.y,swordPos.z
            );

            for (int i = 0; i < totalParticles;i++){

                float p = i / (float) (totalParticles - 1);

                float height = startHeight + p * swordBladeHeight + random.nextFloat() * 0.5f - 0.25f;

                Vec3 center = swordWorldPosition.add(
                        swordDir.x * startHeight + swordDir.x * height,
                        swordDir.y * startHeight + swordDir.y * height,
                        swordDir.z * startHeight + swordDir.z * height
                );

                Quaternionf quaternionf = new Quaternionf(new AxisAngle4f(FDMathUtil.FPI * 2 * random.nextFloat(), swordDir.x,swordDir.y,swordDir.z));

                Vector3f ppos = quaternionf.transform(swordDirUp,new Vector3f()).mul(1.5f).add(
                        (float)center.x,(float)center.y,(float)center.z
                );

                float r = FDMathUtil.clamp(color.x + random.nextFloat() * 0.2f, 0, 1);
                float g = FDMathUtil.clamp(color.y + random.nextFloat() * 0.4f, 0, 1);
                float b = FDMathUtil.clamp(color.z + random.nextFloat() * 0.2f, 0, 1);

                BallParticleOptions ballParticleOptions = BallParticleOptions.builder()
                        .color(r,g,b)
                        .size(particleSize)
                        .scalingOptions(10,0,5)
                        .brightness(2)
                        .particleProcessor(new CircleParticleProcessor(center, true, true, 1))
                        .build();

                level.addParticle(ballParticleOptions, true, ppos.x,ppos.y,ppos.z,0,0,0);

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

    public static void spawnRadialEarthquakeShatters(int entityId, int radius){
        if (FDClientHelpers.getClientLevel().getEntity(entityId) instanceof RadialEarthquakeEntity radialEarthquakeEntity){
            radialEarthquakeEntity.spawnEarthShattersOnRadius(radius);
        }
    }

    public static void radialEarthquakeParticles(Vec3 tpos,int rad){

        if (BossConfigs.BOSS_CONFIG_CLIENT.get().lessParticles) return;

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
