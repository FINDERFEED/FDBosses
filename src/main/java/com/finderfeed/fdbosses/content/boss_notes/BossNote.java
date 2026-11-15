package com.finderfeed.fdbosses.content.boss_notes;

import com.finderfeed.fdbosses.FDBosses;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BossNote {

    private final Component component;

    public BossNote(Component component){
        this.component = component;
    }

    public Component getComponent(){
        return this.component;
    }

    public enum Boss {

        CHESED(FDBosses.location("textures/gui/chesed_note.png"), 0x222222);

        private final ResourceLocation texture;
        private final int textColor;

        Boss(ResourceLocation texture, int textColor){
            this.texture = texture;
            this.textColor = textColor;
        }

        public ResourceLocation getTexture(){
            return this.texture;
        }

        public int getTextColor(){
            return this.textColor;
        }

    }

}
