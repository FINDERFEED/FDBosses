package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BossSkill {

    private ResourceLocation skillIcon;

    private Component skillName;

    private Component skillStats;

    private Component skillDescription;

    public BossSkill(ResourceLocation skillIcon, Component skillName, Component skillStats, Component skillDescription) {
        this.skillIcon = skillIcon;
        this.skillName = skillName;
        this.skillStats = skillStats;
        this.skillDescription = skillDescription;
    }

    public Component getSkillStats() {
        return skillStats;
    }

    public Component getSkillDescription() {
        return skillDescription;
    }

    public Component getSkillName() {
        return skillName;
    }

    public ResourceLocation getSkillIcon() {
        return skillIcon;
    }
}
