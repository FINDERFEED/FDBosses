package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BossInfo {

    private Either<ResourceLocation, ItemStack> infoIcon;

    private Component infoName;

    private Component infoStats;

    private Component infoDescription;

    public BossInfo(ResourceLocation skillIcon, Component skillName, Component skillStats, Component skillDescription) {
        this.infoIcon = Either.left(skillIcon);
        this.infoName = skillName;
        this.infoStats = skillStats;
        this.infoDescription = skillDescription;
    }

    public BossInfo(ItemStack skillIcon, Component skillStats, Component skillDescription) {
        this.infoIcon = Either.right(skillIcon);
        this.infoName = skillIcon.getHoverName();
        this.infoStats = skillStats;
        this.infoDescription = skillDescription;
    }

    public Component getInfoStats() {
        return infoStats;
    }

    public Component getInfoDescription() {
        return infoDescription;
    }

    public Component getInfoName() {
        return infoName;
    }

    public Either<ResourceLocation, ItemStack> getInfoIcon() {
        return infoIcon;
    }
}
