package com.finderfeed.fdbosses.client.particles.entity_ghost;

import com.finderfeed.fdbosses.client.BossParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public class EntityGhostParticleOptions implements ParticleOptions {

    public static final MapCodec<EntityGhostParticleOptions> CODEC = RecordCodecBuilder.mapCodec(p->p.group(
            Codec.INT.fieldOf("lifetime").forGetter(v->v.lifetime),
            Codec.INT.fieldOf("entity_id").forGetter(v->v.entityId)
    ).apply(p, EntityGhostParticleOptions::new));

    public static final StreamCodec<FriendlyByteBuf, EntityGhostParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,v->v.lifetime,
            ByteBufCodecs.INT,v->v.entityId,
            EntityGhostParticleOptions::new
    );

    public int lifetime;
    public int entityId;

    public EntityGhostParticleOptions(int lifetime, int entityId) {
        this.entityId = entityId;
        this.lifetime = lifetime;
    }

    public EntityGhostParticleOptions(Entity entity, int lifetime) {
        this.entityId = entity.getId();
        this.lifetime = lifetime;
    }

    @Override
    public ParticleType<?> getType() {
        return BossParticles.ENTITY_GHOST.get();
    }

}
