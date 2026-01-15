package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class WorldBox {

    public static NetworkCodec<WorldBox> STREAM_CODEC = NetworkCodec.composite(
            NetworkCodec.RESOURCE_LOCATION,v->v.dimension.location(),
            BossByteBufCodecs.AABB,v->v.box,
            ((location, aabb) -> {
                return new WorldBox(ResourceKey.create(Registries.DIMENSION, location), aabb);
            })
    );

    public static Codec<WorldBox> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.STRING.fieldOf("dimension").forGetter(v->v.dimension.location().toString()),
            BossCodecs.AABB.fieldOf("box").forGetter(v->v.box)
    ).apply(p,((location, aabb) -> {
        return new WorldBox(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(location)), aabb);
    })));

    private final ResourceKey<Level> dimension;
    private final AABB box;

    public WorldBox(ResourceKey<Level> dimension, AABB box){
        this.dimension = dimension;
        this.box = box;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public AABB getBox() {
        return box;
    }

    public boolean isEntityInBox(Entity entity){
        return entity.level().dimension().equals(this.dimension) && box.contains(entity.position());
    }



}
