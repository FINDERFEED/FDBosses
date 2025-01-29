package com.finderfeed.fdbosses.client.particles.particle_processors;

import com.finderfeed.fdbosses.BossModEvents;
import com.finderfeed.fdbosses.FDBosses;
import com.finderfeed.fdlib.systems.particle.ParticleProcessor;
import com.finderfeed.fdlib.systems.particle.ParticleProcessorType;
import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class MoveOnACircleParticleProcessor implements ParticleProcessor<MoveOnACircleParticleProcessor> {


    private Vec3 circleAround;



    private boolean forward = true;
    private int circleCount = 1;
    private Vec3 previousPoint = null;
    private Vec3 initialPoint = null;
    private Vector3f axis = null;
    private Vec3 oldSpeed;


    public MoveOnACircleParticleProcessor(Vec3 circleAround,int circleCount,boolean forward){
        this.circleAround = circleAround;
        this.circleCount = circleCount;
        this.forward = forward;
        this.oldSpeed = Vec3.ZERO;
    }

    @Override
    public ParticleProcessorType<MoveOnACircleParticleProcessor> type() {
        return BossModEvents.MOVE_ON_A_CIRCLE_PARTICLE_PROCESSOR_PARTICLE_PROCESSOR_TYPE;
    }

    @Override
    public void processParticle(Particle particle) {
        if (particle.age <= particle.lifetime) {
            float p2 = ((float)particle.age + 1) / (float)particle.lifetime;
            double len = this.initialPoint.subtract(this.circleAround).length();


            float fullAngle = FDMathUtil.FPI * 2 * (float)this.circleCount;
            float targetAngle = fullAngle * p2;
            if (!this.forward) {
                targetAngle = -targetAngle;
            }

            Quaternionf q = new Quaternionf(new AxisAngle4f(targetAngle, this.axis.x, this.axis.y, this.axis.z));
            Vector3d p = q.transform((this.initialPoint.x - this.circleAround.x) / len, (this.initialPoint.y - this.circleAround.y) / len, (this.initialPoint.z - this.circleAround.z) / len, new Vector3d());

            Vec3 targetPoint = this.circleAround.add(p.x * len, p.y * len, p.z * len);

            Minecraft.getInstance().level.addParticle(ParticleTypes.FLAME,true,previousPoint.x,previousPoint.y,previousPoint.z,0,0,0);
            Minecraft.getInstance().level.addParticle(ParticleTypes.END_ROD,true,targetPoint.x,targetPoint.y,targetPoint.z,0,0,0);
            Minecraft.getInstance().level.addParticle(ParticleTypes.ANGRY_VILLAGER,true,particle.x,particle.y,particle.z,0,0,0);

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

    public static class Type implements ParticleProcessorType<MoveOnACircleParticleProcessor>{

        public static final StreamCodec<FriendlyByteBuf,MoveOnACircleParticleProcessor> STREAM_CODEC = StreamCodec.composite(
                FDByteBufCodecs.VEC3,v->v.circleAround,
                ByteBufCodecs.INT,v->v.circleCount,
                ByteBufCodecs.BOOL,v->v.forward,
                MoveOnACircleParticleProcessor::new
        );

        public static final Codec<MoveOnACircleParticleProcessor> CODEC = RecordCodecBuilder.create(p->p.group(
                FDCodecs.VEC3.fieldOf("circleAround").forGetter(v->v.circleAround),
                Codec.INT.fieldOf("circleCount").forGetter(v->v.circleCount),
                Codec.BOOL.fieldOf("forward").forGetter(v->v.forward)
        ).apply(p,MoveOnACircleParticleProcessor::new));

        @Override
        public StreamCodec<FriendlyByteBuf, MoveOnACircleParticleProcessor> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public Codec<MoveOnACircleParticleProcessor> codec() {
            return CODEC;
        }

        @Override
        public ResourceLocation id() {
            return FDBosses.location("move_on_a_circle_processor");
        }
    }

}
