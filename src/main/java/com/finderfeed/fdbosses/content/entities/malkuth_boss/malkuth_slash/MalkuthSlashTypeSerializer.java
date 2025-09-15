package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class MalkuthSlashTypeSerializer implements EntityDataSerializer<MalkuthAttackType> {

    public static final StreamCodec<FriendlyByteBuf, MalkuthAttackType> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Enum::name,
            MalkuthAttackType::valueOf
    );

    @Override
    public StreamCodec<? super FriendlyByteBuf, MalkuthAttackType> codec() {
        return CODEC;
    }

    @Override
    public MalkuthAttackType copy(MalkuthAttackType malkuthAttackType) {
        return malkuthAttackType;
    }

}
