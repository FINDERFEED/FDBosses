package com.finderfeed.fdbosses.content.items.malkuth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.UUID;

public class MalkuthFistDataComponent {

    public static final Codec<MalkuthFistDataComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf("can_use_chain").forGetter(MalkuthFistDataComponent::canUseChain),
                    Codec.BOOL.fieldOf("can_skip_cooldown").forGetter(MalkuthFistDataComponent::canSkipCooldown)
            ).apply(instance, MalkuthFistDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MalkuthFistDataComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, MalkuthFistDataComponent::canUseChain,
                    ByteBufCodecs.BOOL, MalkuthFistDataComponent::canSkipCooldown,
                    MalkuthFistDataComponent::new
            );

    private boolean canUseChain = false;
    private boolean canSkipCooldown = false;


    public MalkuthFistDataComponent(){

    }

    public MalkuthFistDataComponent(boolean canUseChain, boolean canSkipCooldown) {
        this.canUseChain = canUseChain;
        this.canSkipCooldown = canSkipCooldown;
    }

    public void setCanSkipCooldown(boolean canSkipCooldown) {
        this.canSkipCooldown = canSkipCooldown;
    }

    public void setCanUseChain(boolean canUseChain) {
        this.canUseChain = canUseChain;
    }

    public boolean canSkipCooldown() {
        return canSkipCooldown;
    }

    public boolean canUseChain() {
        return canUseChain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MalkuthFistDataComponent that = (MalkuthFistDataComponent) o;
        return canUseChain == that.canUseChain && canSkipCooldown == that.canSkipCooldown;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canUseChain, canSkipCooldown);
    }

}
