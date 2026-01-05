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
                    Codec.BOOL.fieldOf("can_skip_cooldown").forGetter(MalkuthFistDataComponent::canSkipCooldown)
            ).apply(instance, MalkuthFistDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MalkuthFistDataComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, MalkuthFistDataComponent::canSkipCooldown,
                    MalkuthFistDataComponent::new
            );

    private boolean canSkipCooldown = false;


    public MalkuthFistDataComponent(){

    }

    public MalkuthFistDataComponent(boolean canSkipCooldown) {
        this.canSkipCooldown = canSkipCooldown;
    }

    public void setCanSkipCooldown(boolean canSkipCooldown) {
        this.canSkipCooldown = canSkipCooldown;
    }

    public boolean canSkipCooldown() {
        return canSkipCooldown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MalkuthFistDataComponent that = (MalkuthFistDataComponent) o;
        return canSkipCooldown == that.canSkipCooldown;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(canSkipCooldown);
    }

}
