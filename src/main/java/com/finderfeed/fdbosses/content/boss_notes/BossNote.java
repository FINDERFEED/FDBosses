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

        CHESED(FDBosses.location("textures/gui/chesed_note.png"), 0x63ffef, 0xffffff),
        MALKUTH(FDBosses.location("textures/gui/malkuth_note.png"), 0x0077de, 0xff860b);

        private final ResourceLocation texture;
        private final int textColor;
        private final int secondaryTextColor;

        Boss(ResourceLocation texture, int textColor, int secondaryTextColor){
            this.texture = texture;
            this.textColor = textColor;
            this.secondaryTextColor = secondaryTextColor;
        }

        public ResourceLocation getTexture(){
            return this.texture;
        }

        public int getTextColor(){
            return this.textColor;
        }

        public int getSecondaryTextColor(){
            return this.secondaryTextColor;
        }

    }

}
