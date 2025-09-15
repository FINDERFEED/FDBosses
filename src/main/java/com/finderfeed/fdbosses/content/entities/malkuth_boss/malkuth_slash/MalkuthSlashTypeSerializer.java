package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.NetworkCodec;
import net.minecraft.network.codec.NetworkCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class MalkuthSlashTypeSerializer implements EntityDataSerializer<MalkuthAttackType> {

    public static final NetworkCodec<FriendlyByteBuf, MalkuthAttackType> CODEC = NetworkCodec.composite(
            NetworkCodec.STRING_UTF8, Enum::name,
            MalkuthAttackType::valueOf
    );

    @Override
    public NetworkCodec<? super FriendlyByteBuf, MalkuthAttackType> codec() {
        return CODEC;
    }

    @Override
    public MalkuthAttackType copy(MalkuthAttackType malkuthAttackType) {
        return malkuthAttackType;
    }

}
