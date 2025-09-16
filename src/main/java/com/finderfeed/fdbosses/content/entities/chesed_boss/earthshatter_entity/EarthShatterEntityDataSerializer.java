package com.finderfeed.fdbosses.content.entities.chesed_boss.earthshatter_entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class EarthShatterEntityDataSerializer implements EntityDataSerializer<EarthShatterSettings> {
    @Override
    public void write(FriendlyByteBuf p_135025_, EarthShatterSettings p_135026_) {
        EarthShatterSettings.NETWORK_CODEC.toNetwork(p_135025_,p_135026_);
    }

    @Override
    public EarthShatterSettings read(FriendlyByteBuf p_135024_) {
        return EarthShatterSettings.NETWORK_CODEC.fromNetwork(p_135024_);
    }

    @Override
    public EarthShatterSettings copy(EarthShatterSettings p_135023_) {
        return new EarthShatterSettings(p_135023_);
    }
}
