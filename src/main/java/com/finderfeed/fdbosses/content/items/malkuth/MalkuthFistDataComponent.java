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
                    Codec.BOOL.fieldOf("canSkipCooldown").forGetter(MalkuthFistDataComponent::canSkipCooldown),
                    Codec.INT.fieldOf("entityHookCooldown").forGetter(MalkuthFistDataComponent::getEntityHookCooldown)
            ).apply(instance, MalkuthFistDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MalkuthFistDataComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, MalkuthFistDataComponent::canSkipCooldown,
                    ByteBufCodecs.INT, MalkuthFistDataComponent::getEntityHookCooldown,
                    MalkuthFistDataComponent::new
            );

    private boolean canSkipCooldown = false;
    private int entityHookCooldown = 0;


    public MalkuthFistDataComponent(){

    }

    public MalkuthFistDataComponent(boolean canSkipCooldown, int entityHookCooldown) {
        this.canSkipCooldown = canSkipCooldown;
        this.entityHookCooldown = entityHookCooldown;
    }

    public void setCanSkipCooldown(boolean canSkipCooldown) {
        this.canSkipCooldown = canSkipCooldown;
    }

    public boolean canSkipCooldown() {
        return canSkipCooldown;
    }

    public void setEntityHookCooldown(int entityHookCooldown) {
        this.entityHookCooldown = entityHookCooldown;
    }

    public int getEntityHookCooldown() {
        return entityHookCooldown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MalkuthFistDataComponent that = (MalkuthFistDataComponent) o;
        return canSkipCooldown == that.canSkipCooldown && entityHookCooldown == that.entityHookCooldown;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canSkipCooldown, entityHookCooldown);
    }
}
