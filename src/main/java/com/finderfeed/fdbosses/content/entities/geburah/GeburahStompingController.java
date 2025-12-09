package com.finderfeed.fdbosses.content.entities.geburah;

import com.finderfeed.fdbosses.client.particles.arc_preparation_particle.ArcAttackPreparationParticleOptions;
import com.finderfeed.fdbosses.content.entities.geburah.geburah_earthquake.GeburahEarthquake;
import com.finderfeed.fdbosses.init.BossAnims;
import com.finderfeed.fdbosses.init.BossSounds;
import com.finderfeed.fdlib.FDLibCalls;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.bedrock.animations.Animation;
import com.finderfeed.fdlib.systems.bedrock.animations.animation_system.AnimationTicker;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.systems.shake.PositionedScreenShakePacket;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeburahStompingController implements AutoSerializable {

    @SerializableField
    private float travelSpeed = 1f;

    @SerializableField
    private int stompTime = -1;

    private List<StompInstance> stomps;

    @SerializableField
    private float damage = 1;

    private GeburahEntity geburah;

    private int stompRadius;

    public GeburahStompingController(GeburahEntity geburah, int stompRadius){
        this.geburah = geburah;
        this.stompRadius = stompRadius;
    }

    public void tick(){
        if (stompTime > 0){
            stompTime = Mth.clamp(stompTime - 1,0,Integer.MAX_VALUE);
        }else if (stompTime == 0){
            this.summonStomps();
            stompTime = -1;
            this.stomps = null;
        }
    }

    private void summonStomps(){
        if (stomps == null) return;

        PositionedScreenShakePacket.send((ServerLevel) geburah.level(), FDShakeData.builder()
                        .outTime(8)
                        .amplitude(0.7f)
                        .frequency(10f)
                .build(), geburah.position(), 120);

        for (var stomp : stomps) {
            Vec2 direction = stomp.direction;
            float angle = stomp.angle;
            GeburahEarthquake geburahEarthquake = GeburahEarthquake.summon(geburah.level(), geburah.getOnPos(), 5, stompRadius, travelSpeed, damage,
                    new Vec3(direction.x,0,direction.y),angle);
        }

        if (!stomps.isEmpty()){
            geburah.level().playSound(null, geburah.getX(),geburah.getY(),geburah.getZ(), BossSounds.GEBURAH_STOMP.get(), SoundSource.HOSTILE, 3f, .6f);
        }

    }

    public void stomp(int stompPrepareTime, boolean prepareParticles,float travelSpeed, float damage, StompInstance... stompInstances){
        this.stomp(stompPrepareTime,prepareParticles,travelSpeed,damage, Arrays.stream(stompInstances).toList());
    }

    public void stompFullCircle(int stompPrepareTime, boolean prepareParticles,float travelSpeed, float damage){

        List<StompInstance> stompInstances = new ArrayList<>();

        stompInstances.add(new StompInstance(new Vec2(1,0), FDMathUtil.FPI / 4));
        stompInstances.add(new StompInstance(new Vec2(-1,0), FDMathUtil.FPI / 4));
        stompInstances.add(new StompInstance(new Vec2(0,1), FDMathUtil.FPI / 4));
        stompInstances.add(new StompInstance(new Vec2(0,-1), FDMathUtil.FPI / 4));

        this.stomp(stompPrepareTime,prepareParticles,travelSpeed,damage, stompInstances);
    }

    public void stomp(int stompPrepareTime, boolean prepareParticles,float travelSpeed, float damage, List<StompInstance> stompInstances){
        if (stompTime == -1 && !stompInstances.isEmpty()) {
            this.stompTime = stompPrepareTime;
            this.damage = damage;
            this.travelSpeed = travelSpeed;
            this.causeStompAnimation(stompPrepareTime);
            if (prepareParticles){
                this.sendPrepareParticles(stompPrepareTime, stompInstances);
            }
            this.stomps = new ArrayList<>(stompInstances);
        }
    }

    private void sendPrepareParticles(int stompTime, List<StompInstance> stompInstances){

        for (var stomp : stompInstances) {
            Vec2 direction = stomp.direction;
            float angle = stomp.angle;

            int fadeIn = Math.round(stompTime * 0.25f);
            int fadeOut = 10;

            ArcAttackPreparationParticleOptions options = new ArcAttackPreparationParticleOptions(
                    new Vec3(direction.x,0,direction.y),stompRadius + 2,angle,stompTime, fadeIn, fadeOut, 1f,0.1f,0.1f,0.2f
            );

            FDLibCalls.sendParticles((ServerLevel) geburah.level(), options, geburah.position().add(0,GeburahEntity.STOMP_PREPARATION_PARTICLES_OFFSET,0), 120);

        }

    }

    private void causeStompAnimation(int stompTime){
        if (stompTime != 0) {
            var animSystem = this.geburah.getAnimationSystem();
            Animation animation = BossAnims.GEBURAH_STOMP.get();
            int animLength = animation.getAnimTime();
            float animationSpeed = animLength / (float) stompTime;
            animSystem.startAnimation(GeburahEntity.GEBURAH_STOMPING_LAYER, AnimationTicker.builder(animation)
                    .setSpeed(animationSpeed)
                    .build());

        }
    }

    @Override
    public void autoSave(CompoundTag tag) {
        AutoSerializable.super.autoSave(tag);
        if (stomps != null) {
            int size = this.stomps.size();
            tag.putInt("stomps", size);
            for (int i = 0; i < size; i++) {
                StompInstance stompInstance = this.stomps.get(i);
                Vec2 direction = stompInstance.direction;
                float angle = stompInstance.angle;
                tag.putFloat("stomp_" + i + "_direction_x",direction.x);
                tag.putFloat("stomp_" + i + "_direction_z",direction.y);
                tag.putFloat("stomp_" + i + "_angle",angle);
            }
        }
    }

    @Override
    public void autoLoad(CompoundTag tag) {
        AutoSerializable.super.autoLoad(tag);
        if (tag.contains("stomps")){
            this.stomps = new ArrayList<>();
            int size = tag.getInt("stomps");
            for (int i = 0; i < size; i++){

                Vec2 direction = new Vec2(
                        tag.getFloat("stomp_" + i + "_direction_x"),
                        tag.getFloat("stomp_" + i + "_direction_z")
                );

                float angle = tag.getFloat("stomp_" + i + "_angle");

                this.stomps.add(new StompInstance(direction, angle));

            }
        }
    }

    public static class StompInstance{

        public Vec2 direction;
        public float angle;

        public StompInstance(Vec2 direction, float angle){
            this.direction = direction.normalized();
            this.angle = angle;
        }

    }

}
