package com.finderfeed.fdbosses.content.entities.netzach;

import com.finderfeed.fdbosses.content.util.AttackTimings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class AttackTimingsSerializer implements EntityDataSerializer<AttackTimings> {

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, AttackTimings> codec() {
        return AttackTimings.STREAM_CODEC;
    }

    @Override
    public AttackTimings copy(AttackTimings timings) {
        return new AttackTimings(timings);
    }

}
