package com.finderfeed.fdbosses.content.util;

import com.finderfeed.fdlib.util.FDByteBufCodecs;
import com.finderfeed.fdlib.util.FDCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class WorldBox {

    public static StreamCodec<RegistryFriendlyByteBuf, WorldBox> STREAM_CODEC = StreamCodec.composite(
            FDByteBufCodecs.RESOURCE_LOCATION,v->v.dimension.location(),
            BossByteBufCodecs.AABB,v->v.box,
            ((location, aabb) -> {
                return new WorldBox(ResourceKey.create(Registries.DIMENSION, location), aabb);
            })
    );

    public static Codec<WorldBox> CODEC = RecordCodecBuilder.create(p->p.group(
            Codec.STRING.fieldOf("dimension").forGetter(v->v.dimension.location().toString()),
            BossCodecs.AABB.fieldOf("box").forGetter(v->v.box)
    ).apply(p,((location, aabb) -> {
        return new WorldBox(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(location)), aabb);
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
