package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

public class BossCodecs {

    public static final StreamCodec<FriendlyByteBuf, Vector3f> LIGHT_VECTOR3F_DIRECTION = new StreamCodec<FriendlyByteBuf, Vector3f>() {
        @Override
        public Vector3f decode(FriendlyByteBuf b) {
            byte x = b.readByte();
            byte y = b.readByte();
            byte z = b.readByte();
            float vx;
            float vy;
            float vz;

            if (x < 0){
                vx = (float) x / (-Byte.MIN_VALUE);
            }else{
                vx = (float) x / Byte.MAX_VALUE;
            }

            if (y < 0){
                vy = (float) y / (-Byte.MIN_VALUE);
            }else{
                vy = (float) y / Byte.MAX_VALUE;
            }

            if (z < 0){
                vz = (float) z / (-Byte.MIN_VALUE);
            }else{
                vz = (float) z / Byte.MAX_VALUE;
            }

            return new Vector3f(vx,vy,vz);
        }

        @Override
        public void encode(FriendlyByteBuf b, Vector3f vec) {
            Vector3f v = vec.normalize();
            byte x;
            byte y;
            byte z;

            if (v.x < 0){
                x = (byte) FDMathUtil.lerp(Byte.MIN_VALUE, 0, v.x + 1);
            }else{
                x = (byte) FDMathUtil.lerp(0, Byte.MAX_VALUE, v.x);
            }

            if (v.y < 0){
                y = (byte) FDMathUtil.lerp(Byte.MIN_VALUE, 0, v.y + 1);
            }else{
                y = (byte) FDMathUtil.lerp(0, Byte.MAX_VALUE, v.y);
            }

            if (v.z < 0){
                z = (byte) FDMathUtil.lerp(Byte.MIN_VALUE, 0, v.z + 1);
            }else{
                z = (byte) FDMathUtil.lerp(0, Byte.MAX_VALUE, v.z);
            }

            b.writeByte(x);
            b.writeByte(y);
            b.writeByte(z);
        }
    };

    public static final Codec<AABB> AABB = RecordCodecBuilder.create(p->p.group(
            Codec.DOUBLE.fieldOf("minX").forGetter(v->v.minX),
            Codec.DOUBLE.fieldOf("minY").forGetter(v->v.minY),
            Codec.DOUBLE.fieldOf("minZ").forGetter(v->v.minZ),
            Codec.DOUBLE.fieldOf("maxX").forGetter(v->v.maxX),
            Codec.DOUBLE.fieldOf("maxY").forGetter(v->v.maxY),
            Codec.DOUBLE.fieldOf("maxZ").forGetter(v->v.maxZ)
    ).apply(p, AABB::new));

}
