package com.finderfeed.fdbosses.client.particles.vanilla_like;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class SpriteParticleOptions implements ParticleOptions {

    private static final Codec<Vector3f> VEC3_CODEC = Codec.FLOAT.listOf().comapFlatMap(list -> {
        if (list.size() != 3)
            return DataResult.error(() -> "Vector3f must have exactly 3 elements");
        return DataResult.success(new Vector3f(list.get(0), list.get(1), list.get(2)));
    }, vec -> List.of(vec.x(), vec.y(), vec.z()));


    public static MapCodec<SpriteParticleOptions> codec(ParticleType<?> type) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        VEC3_CODEC.fieldOf("look_dir").forGetter(o -> o.particleLookDirection),
                        VEC3_CODEC.fieldOf("rot_speed").forGetter(o -> o.XYZRotationSpeed),
                        Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
                        Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                        Codec.BOOL.fieldOf("light").forGetter(o -> o.isLightenedUp),
                        Codec.FLOAT.fieldOf("friction").forGetter(o -> o.friction),
                        Codec.BOOL.fieldOf("friction_affects_rot").forGetter(o -> o.frictionAffectsXYZRotation)
                ).apply(instance, (look, rot, life, size, light, fric, fricRot) ->
                        new SpriteParticleOptions(type, look, rot, life, size, light, fric, fricRot))
        );
    }


    public static StreamCodec<RegistryFriendlyByteBuf, SpriteParticleOptions> streamCodec(ParticleType<?> type) {
        return StreamCodec.of(
                (buf, o) -> {
                    ByteBufCodecs.VECTOR3F.encode(buf, o.particleLookDirection);
                    ByteBufCodecs.VECTOR3F.encode(buf, o.XYZRotationSpeed);
                    buf.writeInt(o.lifetime);
                    buf.writeFloat(o.size);
                    buf.writeBoolean(o.isLightenedUp);
                    buf.writeFloat(o.friction);
                    buf.writeBoolean(o.frictionAffectsXYZRotation);
                },
                buf -> new SpriteParticleOptions(
                        type,
                        ByteBufCodecs.VECTOR3F.decode(buf),
                        ByteBufCodecs.VECTOR3F.decode(buf),
                        buf.readInt(),
                        buf.readFloat(),
                        buf.readBoolean(),
                        buf.readFloat(),
                        buf.readBoolean()
                )
        );
    }




    private final ParticleType<?> particleType;
    private final Vector3f particleLookDirection;
    private final Vector3f XYZRotationSpeed;
    private final int lifetime;
    private final float size;
    private final boolean isLightenedUp;
    private final float friction;
    private final boolean frictionAffectsXYZRotation;


    private SpriteParticleOptions(
            ParticleType<?> type,
            Vector3f look,
            Vector3f rot,
            int lifetime,
            float size,
            boolean light,
            float friction,
            boolean frictionRot
    ) {
        this.particleType = type;
        this.particleLookDirection = look;
        this.XYZRotationSpeed = rot;
        this.lifetime = lifetime;
        this.size = size;
        this.isLightenedUp = light;
        this.friction = friction;
        this.frictionAffectsXYZRotation = frictionRot;
    }

    public Vector3f getParticleLookDirection() {
        return particleLookDirection;
    }

    public Vector3f getXYZRotationSpeed() {
        return XYZRotationSpeed;
    }

    public int getLifetime() {
        return lifetime;
    }

    public float getSize() {
        return size;
    }

    public boolean isLightenedUp() {
        return isLightenedUp;
    }

    public float getFriction() {
        return friction;
    }

    public boolean frictionAffectsXYZRotation() {
        return frictionAffectsXYZRotation;
    }

    public static Builder create(ParticleType<?> particleType){
        return new Builder(particleType);
    }

    public static class Builder {

        private final ParticleType<?> type;
        private Vector3f look = new Vector3f();
        private Vector3f xyzRotation = new Vector3f();
        private int lifetime = 10;
        private float size = 0.5f;
        private boolean light = false;
        private float friction = 1f;
        private boolean frictionAffectsRotation = true;

        public Builder(ParticleType<?> type) {
            this.type = type;
        }

        public Builder particleLookDirection(Vector3f v) {
            this.look = v;
            return this;
        }

        public Builder particleLookDirection(float x, float y, float z) {
            return this.particleLookDirection(new Vector3f(x,y,z));
        }

        public Builder particleLookDirection(Vec3 look) {
            return this.particleLookDirection(look.toVector3f());
        }

        public Builder xyzRotationSpeed(Vector3f v) {
            this.xyzRotation = v;
            return this;
        }

        public Builder xyzRotationSpeed(float xd, float yd, float zd) {
            return this.xyzRotationSpeed(new Vector3f(xd, yd, zd));
        }

        public Builder xyzRotationSpeed(Vec3 rotation) {
            return this.xyzRotationSpeed(rotation.toVector3f());
        }

        public Builder lifetime(int t) {
            this.lifetime = t;
            return this;
        }

        public Builder size(float s) {
            this.size = s;
            return this;
        }

        public Builder lightenedUp(boolean is) {
            this.light = is;
            return this;
        }

        public Builder friction(float f) {
            this.friction = f;
            return this;
        }

        public Builder frictionAffectsRotation(boolean b) {
            this.frictionAffectsRotation = b;
            return this;
        }

        public SpriteParticleOptions build() {
            return new SpriteParticleOptions(type, look, xyzRotation, lifetime, size, light, friction, frictionAffectsRotation);
        }
    }


    @Override
    public ParticleType<?> getType() {
        return particleType;
    }
}
