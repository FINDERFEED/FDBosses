package com.finderfeed.fdbosses.content.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.AABB;

public class BossCodecs {

    public static final Codec<AABB> AABB = RecordCodecBuilder.create(p->p.group(
            Codec.DOUBLE.fieldOf("minX").forGetter(v->v.minX),
            Codec.DOUBLE.fieldOf("minY").forGetter(v->v.minY),
            Codec.DOUBLE.fieldOf("minZ").forGetter(v->v.minZ),
            Codec.DOUBLE.fieldOf("maxX").forGetter(v->v.maxX),
            Codec.DOUBLE.fieldOf("maxY").forGetter(v->v.maxY),
            Codec.DOUBLE.fieldOf("maxZ").forGetter(v->v.maxZ)
    ).apply(p, AABB::new));

}
