package com.finderfeed.fdbosses.client.particles.particle_processors;

import com.finderfeed.fdbosses.BossModEvents;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.particle.ParticleProcessor;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.finderfeed.fdlib.util.rendering.FDEasings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ChesedRayCircleParticleProcessor implements ParticleProcessor<ChesedRayCircleParticleProcessor> {


    private Vec3 circleAround;



    private boolean forward = true;
    private float circleCount = 1;
    private Vec3 previousPoint = null;
    private Vec3 initialPoint = null;
    private Vector3f axis = null;
    private Vec3 oldSpeed;


    public ChesedRayCircleParticleProcessor(Vec3 circleAround, float circleCount, boolean forward){
        this.circleAround = circleAround;
        this.circleCount = circleCount;
        this.forward = forward;
        this.oldSpeed = Vec3.ZERO;
    }

    @Override
    public ParticleProcessorType<ChesedRayCircleParticleProcessor> type() {
        return BossModEvents.CHESED_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;
    }

    @Override
    public void processParticle(Particle particle) {
        int lifetime = particle.lifetime - 2;
        if (particle.age <= lifetime) {
            float p2 = ((float)particle.age) / (float)lifetime;

            p2 = FDEasings.easeInOut(p2);

            double len = this.initialPoint.subtract(this.circleAround).length();


            float fullAngle = FDMathUtil.FPI * 2 * (float)this.circleCount;
            float targetAngle = fullAngle * p2;
            if (!this.forward) {
                targetAngle = -targetAngle;
            }

            Quaternionf q = new Quaternionf(new AxisAngle4f(targetAngle, this.axis.x, this.axis.y, this.axis.z));
            Vector3d p = q.transform((this.initialPoint.x - this.circleAround.x) / len, (this.initialPoint.y - this.circleAround.y) / len, (this.initialPoint.z - this.circleAround.z) / len, new Vector3d());

            Vec3 targetPoint = this.circleAround.add(p.x * len, p.y * len, p.z * len);

            Vec3 speed = targetPoint.subtract(this.previousPoint);
            this.previousPoint = targetPoint;
            particle.xd -= this.oldSpeed.x;
            particle.yd -= this.oldSpeed.y;
            particle.zd -= this.oldSpeed.z;
            particle.xd += speed.x;
            particle.yd += speed.y;
            particle.zd += speed.z;
            this.oldSpeed = speed;
        }
    }

    @Override
    public void init(Particle particle) {
        this.initialPoint = new Vec3(particle.x, particle.y, particle.z);
        Vec3 b = this.initialPoint.subtract(this.circleAround);
        Vec3 left = b.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 axis = left.cross(b);
        this.axis = (new Vector3f((float)axis.x, (float)axis.y, (float)axis.z)).normalize();
        this.previousPoint = this.initialPoint;
    }

    public static class Type implements ParticleProcessorType<ChesedRayCircleParticleProcessor>{

        public static final NetworkCodec<ChesedRayCircleParticleProcessor> STREAM_CODEC = NetworkCodec.composite(
                NetworkCodec.VEC3,v->v.circleAround,
                NetworkCodec.FLOAT,v->v.circleCount,
                NetworkCodec.BOOL,v->v.forward,
                ChesedRayCircleParticleProcessor::new
        );

        public static final Codec<ChesedRayCircleParticleProcessor> CODEC = RecordCodecBuilder.create(p->p.group(
                FDCodecs.VEC3.fieldOf("circleAround").forGetter(v->v.circleAround),
                Codec.FLOAT.fieldOf("circleCount").forGetter(v->v.circleCount),
                Codec.BOOL.fieldOf("forward").forGetter(v->v.forward)
        ).apply(p, ChesedRayCircleParticleProcessor::new));

        @Override
        public NetworkCodec<ChesedRayCircleParticleProcessor> networkCodec() {
            return STREAM_CODEC;
        }

        @Override
        public Codec<ChesedRayCircleParticleProcessor> codec() {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return FDBosses.location("move_on_a_circle_processor");
        }
    }

}
