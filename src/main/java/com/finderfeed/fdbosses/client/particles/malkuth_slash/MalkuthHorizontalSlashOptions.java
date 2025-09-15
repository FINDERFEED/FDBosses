package com.finderfeed.fdbosses.client.particles.malkuth_slash;

import com.finderfeed.fdbosses.client.BossParticles;
import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.finderfeed.fdlib.util.FDCodecs;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class MalkuthHorizontalSlashOptions implements ParticleOptions {

    public static final Deserializer<MalkuthHorizontalSlashOptions> DESERIALIZER = new Deserializer<MalkuthHorizontalSlashOptions>() {
        @Override
        public MalkuthHorizontalSlashOptions fromCommand(ParticleType<MalkuthHorizontalSlashOptions> p_123733_, StringReader p_123734_) throws CommandSyntaxException {
            return new MalkuthHorizontalSlashOptions(MalkuthAttackType.FIRE,null,1,1,1);
        }

        @Override
        public MalkuthHorizontalSlashOptions fromNetwork(ParticleType<MalkuthHorizontalSlashOptions> p_123735_, FriendlyByteBuf p_123736_) {
            return STREAM_CODEC.fromNetwork(p_123736_);
        }
    };

    public static final NetworkCodec<MalkuthHorizontalSlashOptions> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.STRING,v->v.attackType.name(),
            NetworkCodec.VEC3,v->v.slashDirection,
            NetworkCodec.FLOAT,v->v.slashWidth,
            NetworkCodec.INT,v->v.lifetime,
            NetworkCodec.FLOAT,v->v.rotation,
            (type,dir,width,lifetime,rotation)->{
                MalkuthHorizontalSlashOptions options = new MalkuthHorizontalSlashOptions(
                        MalkuthAttackType.valueOf(type),
                        dir,width,rotation,lifetime
                );
                return options;
            }
    );

    public static final Codec<MalkuthHorizontalSlashOptions> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.STRING.fieldOf("attackType").forGetter(v->v.attackType.name()),
            FDCodecs.VEC3.fieldOf("slashDirection").forGetter(v->v.slashDirection),
            Codec.FLOAT.fieldOf("slashWidth").forGetter(v->v.slashWidth),
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.FLOAT.fieldOf("rotation").forGetter(v->v.rotation)
    ).apply(p,(type,dir,width,lifetime,rotation)->{
        MalkuthHorizontalSlashOptions options = new MalkuthHorizontalSlashOptions(
                MalkuthAttackType.valueOf(type),
                dir,width,rotation,lifetime
        );
        return options;
    }));

    private MalkuthAttackType attackType;
    private Vec3 slashDirection;
    private float slashWidth;
    private float rotation;
    private int lifetime;


    public MalkuthHorizontalSlashOptions(MalkuthAttackType malkuthAttackType, Vec3 slashDirection, float slashWidth, float rotation,int lifetime){
        this.attackType = malkuthAttackType;
        this.slashDirection = slashDirection;
        this.slashWidth = slashWidth;
        this.lifetime = lifetime;
        this.rotation = rotation;
    }

    public float getRotation() {
        return rotation;
    }

    public int getLifetime() {
        return lifetime;
    }

    public float getSlashWidth() {
        return slashWidth;
    }

    public MalkuthAttackType getAttackType() {
        return attackType;
    }

    public Vec3 getSlashDirection() {
        return slashDirection;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.MALKUTH_HORIZONTAL_SLASH.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        STREAM_CODEC.toNetwork(p_123732_,this);
    }

    @Override
    public String writeToString() {
        return "zhopa";
    }
}
