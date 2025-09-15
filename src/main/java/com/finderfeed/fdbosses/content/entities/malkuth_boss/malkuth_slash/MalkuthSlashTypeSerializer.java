package com.finderfeed.fdbosses.content.entities.malkuth_boss.malkuth_slash;

import com.finderfeed.fdbosses.content.entities.malkuth_boss.MalkuthAttackType;
import com.finderfeed.fdlib.systems.stream_codecs.NetworkCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class MalkuthSlashTypeSerializer implements EntityDataSerializer<MalkuthAttackType> {

    public static final NetworkCodec<MalkuthAttackType> CODEC = NetworkCodec.composite(
            NetworkCodec.STRING, Enum::name,
            MalkuthAttackType::valueOf
    );

    @Override
    public void write(FriendlyByteBuf p_135025_, MalkuthAttackType p_135026_) {
        CODEC.toNetwork(p_135025_,p_135026_);
    }

    @Override
    public MalkuthAttackType read(FriendlyByteBuf p_135024_) {
        return CODEC.fromNetwork(p_135024_);
    }

    @Override
    public MalkuthAttackType copy(MalkuthAttackType malkuthAttackType) {
        return malkuthAttackType;
    }

}
