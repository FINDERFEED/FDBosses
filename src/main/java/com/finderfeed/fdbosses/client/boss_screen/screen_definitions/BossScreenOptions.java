package com.finderfeed.fdbosses.client.boss_screen.screen_definitions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class BossScreenOptions {

    private List<BossInfo> skills = new ArrayList<>();
    private Component TLDRComponent = null;

    private Component bossDescription = Component.empty();

    private EntityType<?> entityType;

    public BossScreenOptions(){
    }

    public BossScreenOptions setBossDescription(Component bossDescription) {
        this.bossDescription = bossDescription;
        return this;
    }

    public BossScreenOptions setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        return this;
    }

    public BossScreenOptions addSkill(BossInfo bossSkill){
        this.skills.add(bossSkill);
        return this;
    }


    public BossScreenOptions setTLDRComponent(Component component){
        this.TLDRComponent = component;
        return this;
    }

    public List<BossInfo> getSkills() {
        return skills;
    }

    public Component getBossDescription() {
        return bossDescription;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Component getTLDRComponent() {
        return TLDRComponent.copy();
    }
}
