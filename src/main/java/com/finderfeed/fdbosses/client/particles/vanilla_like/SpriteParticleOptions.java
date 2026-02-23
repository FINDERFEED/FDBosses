package com.finderfeed.fdbosses.client.particles.vanilla_like;

import com.finderfeed.fdbosses.content.util.BossCodecs;
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
import java.util.function.Supplier;

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
                        VEC3_CODEC.fieldOf("xyz_rotation").forGetter(o -> o.XYZRotation),
                        VEC3_CODEC.fieldOf("rot_speed").forGetter(o -> o.XYZRotationSpeed),
                        Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
                        Codec.FLOAT.fieldOf("size").forGetter(o -> o.size),
                        Codec.FLOAT.fieldOf("friction").forGetter(o -> o.friction),
                        Codec.BYTE.fieldOf("flags").forGetter(o -> o.flags)
                ).apply(instance, (look, xyzrot, rot, life, size, fric, flags) ->
                        new SpriteParticleOptions(type, look, xyzrot, rot, life, size, fric, flags))
        );
    }



    public static StreamCodec<RegistryFriendlyByteBuf, SpriteParticleOptions> streamCodec(ParticleType<?> type) {
        return StreamCodec.of(
                (buf, o) -> {
                    BossCodecs.LIGHT_VECTOR3F_DIRECTION.encode(buf, o.particleLookDirection);
                    ByteBufCodecs.VECTOR3F.encode(buf, o.XYZRotation);
                    ByteBufCodecs.VECTOR3F.encode(buf, o.XYZRotationSpeed);
                    buf.writeInt(o.lifetime);
                    buf.writeFloat(o.size);
                    buf.writeFloat(o.friction);
                    buf.writeByte(o.flags);
                },
                buf -> new SpriteParticleOptions(
                        type,
                        BossCodecs.LIGHT_VECTOR3F_DIRECTION.decode(buf),
                        ByteBufCodecs.VECTOR3F.decode(buf),
                        ByteBufCodecs.VECTOR3F.decode(buf),
                        buf.readInt(),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readByte()
                )
        );
    }




    private final ParticleType<?> particleType;
    private final Vector3f particleLookDirection;
    private final Vector3f XYZRotation;
    private final Vector3f XYZRotationSpeed;
    private final int lifetime;
    private final float size;
    private final float friction;


    public static final byte IS_LIGHTENED_UP = 0b1000000;
    public static final byte FRICTION_AFFECTS_XYZ_ROTATION = 0b0100000;
    public static final byte IS_FLIPPED = 0b0010000;
    private final byte flags;



    private SpriteParticleOptions(
            ParticleType<?> type,
            Vector3f look,
            Vector3f rot,
            Vector3f rotSpeed,
            int lifetime,
            float size,
            float friction,
            byte flags
    ) {
        this.XYZRotation = rot;
        this.particleType = type;
        this.particleLookDirection = look;
        this.XYZRotationSpeed = rotSpeed;
        this.lifetime = lifetime;
        this.size = size;
        this.friction = friction;
        this.flags = flags;
    }

    public Vector3f getXYZRotation() {
        return XYZRotation;
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
        return (flags & IS_LIGHTENED_UP) != 0;
    }

    public boolean isFlipped(){
        return (flags & IS_FLIPPED) != 0;
    }

    public float getFriction() {
        return friction;
    }

    public boolean frictionAffectsXYZRotation() {
        return (flags & FRICTION_AFFECTS_XYZ_ROTATION) != 0;
    }

    public static Builder builder(ParticleType<?> particleType){
        return new Builder(particleType);
    }

    public static <T extends ParticleType<?>> Builder builder(Supplier<T> particleType){
        return builder(particleType.get());
    }

    public static class Builder {

        private final ParticleType<?> type;
        private Vector3f look = new Vector3f();
        private Vector3f xyzRotationSpeed = new Vector3f();
        private Vector3f xyzRotation = new Vector3f();
        private int lifetime = 10;
        private float size = 0.5f;
        private float friction = 1f;

        private byte flags = 0;

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
            this.xyzRotationSpeed = v;
            return this;
        }

        public Builder xyzRotationSpeed(float xd, float yd, float zd) {
            return this.xyzRotationSpeed(new Vector3f(xd, yd, zd));
        }

        public Builder xyzRotationSpeed(Vec3 rotation) {
            return this.xyzRotationSpeed(rotation.toVector3f());
        }

        public Builder xyzRotation(Vector3f v) {
            this.xyzRotation = v;
            return this;
        }

        public Builder xyzRotation(float xd, float yd, float zd) {
            return this.xyzRotation(new Vector3f(xd, yd, zd));
        }

        public Builder xyzRotation(Vec3 rotation) {
            return this.xyzRotation(rotation.toVector3f());
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
            this.flags = (byte) (flags | IS_LIGHTENED_UP);
            return this;
        }

        public Builder friction(float f) {
            this.friction = f;
            return this;
        }

        public Builder frictionAffectsRotation(boolean b) {
            this.flags = (byte) (flags | FRICTION_AFFECTS_XYZ_ROTATION);
            return this;
        }

        public Builder flipSprite(boolean b) {
            this.flags = (byte) (flags | IS_FLIPPED);
            return this;
        }

        public SpriteParticleOptions build() {
            return new SpriteParticleOptions(type, look, xyzRotation, xyzRotationSpeed, lifetime, size, friction, flags);
        }
    }


    @Override
    public ParticleType<?> getType() {
        return particleType;
    }
}
