package com.finderfeed.fdbosses.content.boss_notes;

import net.minecraft.network.chat.Component;

public class BossNote {

    private final Component component;

    public BossNote(Component component){
        this.component = component;
    }

    public Component getComponent(){
        return this.component;
    }

}
