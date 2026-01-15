package com.finderfeed.fdbosses.content.items.geburah;

import com.finderfeed.fdbosses.init.BossConfigs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.Objects;

public class DivineGearComponent {

    public static final Codec<DivineGearComponent> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("charge").forGetter(DivineGearComponent::getCharge),
                    Codec.INT.fieldOf("cooldown").forGetter(DivineGearComponent::getCooldown)
            ).apply(instance, DivineGearComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DivineGearComponent> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, DivineGearComponent::getCharge,
                    ByteBufCodecs.INT, DivineGearComponent::getCooldown,
                    DivineGearComponent::new
            );

    private int charge;
    private int cooldown;

    public DivineGearComponent(){
        this.charge = BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearCharges;
    }

    public DivineGearComponent(int charge, int cooldown) {
        this.setCharge(charge);
        this.cooldown = cooldown;
    }

    public DivineGearComponent(DivineGearComponent copy){
        this.charge = copy.charge;
        this.cooldown = copy.cooldown;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = Mth.clamp(charge, 0, BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearCharges);
    }

    public void setCooldown() {
        this.cooldown = BossConfigs.BOSS_CONFIG.get().itemConfig.divineGearChargeReplenishTime;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DivineGearComponent that = (DivineGearComponent) o;
        return charge == that.charge && cooldown == that.cooldown;
    }

    @Override
    public int hashCode() {
        return Objects.hash(charge, cooldown);
    }

}
