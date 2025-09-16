package com.finderfeed.fdbosses.packets;

import com.finderfeed.fdbosses.BossClientPackets;
import com.finderfeed.fdlib.network.FDPacket;
import com.finderfeed.fdlib.network.RegisterFDPacket;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@RegisterFDPacket("fdbosses:slam_particles")
public class SlamParticlesPacket extends FDPacket {

    private SlamData slamData;

    public SlamParticlesPacket(SlamData data){
        this.slamData = data;
    }
    public SlamParticlesPacket(FriendlyByteBuf buf){
        this.slamData = SlamData.STREAM_CODEC.fromNetwork(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        SlamData.STREAM_CODEC.toNetwork(buf,slamData);
    }

    @Override
    public void clientAction(Supplier<NetworkEvent.Context> context) {
        BossClientPackets.blockProjectileSlamParticles(slamData);
    }

    @Override
    public void serverAction(Supplier<NetworkEvent.Context> context) {

    }

    public static class SlamData {

        public static final NetworkCodec<SlamData> STREAM_CODEC = NetworkCodec.composite(
                NetworkCodec.VEC3,v->v.pos,
                NetworkCodec.VEC3,v->v.direction,
                NetworkCodec.BLOCK_POS,v->v.bPos,
                NetworkCodec.INT,v->v.collectRadius,
                NetworkCodec.FLOAT,v->v.maxSpeed,
                NetworkCodec.FLOAT,v->v.maxVerticalSpeedEdges,
                NetworkCodec.FLOAT,v->v.maxVerticalSpeedCenter,
                NetworkCodec.FLOAT,v->v.perRowDivide,
                NetworkCodec.INT,v->v.count,
                NetworkCodec.FLOAT,v->v.maxAngle,
                NetworkCodec.INT,v->v.maxParticleLifetime,
                NetworkCodec.FLOAT,v->v.particleSizeMult,
                ((pos,direction,bPos,collectRadius,maxSpeed,maxVerticalSpeedEdges,maxVerticalSpeedCenter,perRowDivide,count,maxAngle,maxParticleLifetime,particleSize)->{
                    SlamData data = new SlamData(bPos,pos,direction);
                    data.maxSpeed = maxSpeed;
                    data.collectRadius = collectRadius;
                    data.maxVerticalSpeedEdges = maxVerticalSpeedEdges;
                    data.maxVerticalSpeedCenter = maxVerticalSpeedCenter;
                    data.count = count;
                    data.maxAngle = maxAngle;
                    data.perRowDivide = perRowDivide;
                    data.maxParticleLifetime = maxParticleLifetime;
                    data.particleSizeMult = particleSize;
                    return data;
                })
        );

        public Vec3 pos;
        public Vec3 direction;
        public BlockPos bPos;
        public int collectRadius = 3;
        public float maxSpeed = 0.75f;
        public float maxVerticalSpeedEdges = 0.4f;
        public float maxVerticalSpeedCenter = 0.6f;
        public float particleSizeMult = 2f;
        public float perRowDivide = 0.25f;
        public int count = 30;
        public int maxParticleLifetime = 60;
        public float maxAngle = FDMathUtil.FPI / 3;

        public SlamData(BlockPos bPos,Vec3 pos,Vec3 direction){
            this.pos = pos;
            this.bPos = bPos;
            this.direction = direction;
        }

        public SlamData particleSizeMultiplier(float mult){
            this.particleSizeMult = mult;
            return this;
        }
        public SlamData maxAngle(float maxAngle){
            this.maxAngle = maxAngle;
            return this;
        }

        public SlamData perRowDivide(float perRowDivide){
            this.perRowDivide = perRowDivide;
            return this;
        }

        public SlamData count(int count){
            this.count = count;
            return this;
        }

        public SlamData maxParticleLifetime(int lifetime){
            this.maxParticleLifetime = lifetime;
            return this;
        }

        public SlamData collectRadius(int collectRadius){
            this.collectRadius = collectRadius;
            return this;
        }

        public SlamData maxSpeed(float maxSpeed){
            this.maxSpeed = maxSpeed;
            return this;
        }

        public SlamData maxVerticalSpeedEdges(float speed){
            this.maxVerticalSpeedEdges = speed;
            return this;
        }

        public SlamData maxVerticalSpeedCenter(float speed){
            this.maxVerticalSpeedCenter = speed;
            return this;
        }


    }

}
